package com.zeyad.usecases.data.repository.stores;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.R;
import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.db.RealmManager;
import com.zeyad.usecases.data.exceptions.NetworkConnectionException;
import com.zeyad.usecases.data.mappers.EntityMapper;
import com.zeyad.usecases.data.network.RestApi;
import com.zeyad.usecases.data.requests.FileIORequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.data.utils.ModelConverters;
import com.zeyad.usecases.data.utils.Utils;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import st.lowlevel.storo.Storo;

import static com.zeyad.usecases.data.requests.PostRequest.DELETE;
import static com.zeyad.usecases.data.requests.PostRequest.POST;
import static com.zeyad.usecases.data.requests.PostRequest.PUT;
import static com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory.NONE;

public class CloudDataStore implements DataStore {

    public static final String APPLICATION_JSON = "application/json";
    static final String TAG = CloudDataStore.class.getSimpleName();
    private static final int COUNTER_START = 1, ATTEMPTS = 3;
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    final DataBaseManager mDataBaseManager;
    private final EntityMapper mEntityDataMapper;
    private final Context mContext;
    private final Observable<Object> mErrorObservableNotPersisted, mQueueFileIO;
    private final RestApi mRestApi;
    private final FirebaseJobDispatcher mDispatcher;
    private final boolean mCanPersist;

    /**
     * Construct a {@link DataStore} based on connections to the api (Cloud).
     *
     * @param restApi         The {@link RestApi} implementation to use.
     * @param dataBaseManager A {@link DataBaseManager} to cache data retrieved from the api.
     */
    CloudDataStore(RestApi restApi, DataBaseManager dataBaseManager, EntityMapper entityDataMapper) {
        mRestApi = restApi;
        mEntityDataMapper = entityDataMapper;
        mDataBaseManager = dataBaseManager;
        mContext = Config.getInstance().getContext();
        mErrorObservableNotPersisted = Observable.error(new NetworkConnectionException(mContext
                .getString(R.string.exception_network_error_not_persisted)));
        mQueueFileIO = Observable.empty();
        mCanPersist = DataUseCaseFactory.getDBType() > NONE;
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
    }

    @NonNull
    @Override
    public Observable<?> dynamicGetObject(String url, String idColumnName, int itemId, Class domainClass,
                                          Class dataClass, boolean persist, boolean shouldCache) {
        return mRestApi.dynamicGetObject(url, shouldCache)
                //.compose(applyExponentialBackoff())
                .doOnNext(object -> {
                    if (willPersist(persist))
                        persistGeneric(object, idColumnName, dataClass);
                })
                .map(entity -> mEntityDataMapper.transformToDomain(entity, domainClass));
    }

    @NonNull
    @Override
    public Observable<List> dynamicGetList(String url, Class domainClass, Class dataClass, boolean persist,
                                           boolean shouldCache) {
        return mRestApi.dynamicGetList(url, shouldCache)
                //.compose(applyExponentialBackoff())
                .doOnNext(list -> {
                    if (willPersist(persist))
                        persistAllGenerics(list, dataClass);
                })
                .map(entities -> mEntityDataMapper.transformAllToDomain(entities, domainClass));
    }

    @NonNull
    @Override
    public Observable<?> dynamicPostObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                           Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            if (willPersist(persist))
                persistGeneric(jsonObject, idColumnName, dataClass);
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(POST, url, idColumnName, jsonObject, persist);
                return Observable.empty();
            } else if (!Utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicPost(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonObject.toString()))
                    //.compose(applyExponentialBackoff())
                    .doOnError(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable))
                            queuePost(POST, url, idColumnName, jsonObject, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicPostList(String url, String idColumnName, @NonNull JSONArray jsonArray,
                                         Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            if (willPersist(persist))
                persistGeneric(jsonArray, idColumnName, dataClass);
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(POST, url, idColumnName, jsonArray, persist);
                return Observable.empty();
            } else if (!Utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicPost(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonArray.toString()))
                    //.compose(applyExponentialBackoff())
                    .doOnError(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable))
                            queuePost(POST, url, idColumnName, jsonArray, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                          Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            if (willPersist(persist))
                persistGeneric(jsonObject, idColumnName, dataClass);
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(PUT, url, idColumnName, jsonObject, persist);
                return Observable.empty();
            } else if (!Utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicPut(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonObject.toString()))
                    //.compose(applyExponentialBackoff())
                    .doOnError(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable))
                            queuePost(PUT, url, idColumnName, jsonObject, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutList(String url, String idColumnName, @NonNull JSONArray jsonArray,
                                        Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            if (willPersist(persist))
                persistGeneric(jsonArray, idColumnName, dataClass);
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(PUT, url, idColumnName, jsonArray, persist);
                return Observable.empty();
            } else if (!Utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicPut(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonArray.toString()))
                    //.compose(applyExponentialBackoff())
                    .doOnError(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable))
                            queuePost(PUT, url, idColumnName, jsonArray, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<Object> dynamicDeleteCollection(final String url, String idColumnName,
                                                      @NonNull final JSONArray jsonArray,
                                                      Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            List<Long> ids = ModelConverters.convertToListOfId(jsonArray);
            if (willPersist(persist))
                deleteFromPersistence(ids, idColumnName, dataClass);
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(DELETE, url, idColumnName, jsonArray, persist);
                return Observable.empty();
            } else if (!Utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicDelete(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonArray.toString()))
                    //.compose(applyExponentialBackoff())
                    .doOnError(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable))
                            queuePost(PostRequest.DELETE, url, idColumnName, jsonArray, persist);
                    });
        });
    }

    @NonNull
    @Override
    public Observable<Boolean> dynamicDeleteAll(String url, Class dataClass, boolean persist) {
        return Observable.error(new Exception(mContext.getString(R.string.delete_all_error_cloud)));
    }

    @NonNull
    @Override
    public Observable<?> dynamicUploadFile(String url, @NonNull File file, String key, HashMap<String, Object> parameters,
                                           boolean onWifi, boolean whileCharging, boolean queuable, Class domainClass) {
        return Observable.defer(() -> {
            if (isQueuableIfOutOfNetwork(queuable) && isOnWifi(mContext) == onWifi
                    && isChargingReqCompatible(isCharging(mContext), whileCharging)) {
                queueIOFile(url, file, true, whileCharging, false);
                return mQueueFileIO;
            } else if (!Utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            RequestBody requestFile = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put(key, requestFile);
            if (parameters != null && !parameters.isEmpty()) {
                for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                    map.put(entry.getKey(), Utils.createPartFromString(entry.getValue()));
                }
            }
            return mRestApi.upload(url, map, MultipartBody.Part.createFormData(key, file.getName(), requestFile))
                    .doOnError(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable))
                            queueIOFile(url, file, true, whileCharging, false);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicDownloadFile(String url, @NonNull File file, boolean onWifi,
                                             boolean whileCharging, boolean queuable) {
        return Observable.defer(() -> {
            if (isQueuableIfOutOfNetwork(queuable) && isOnWifi(mContext) == onWifi
                    && isChargingReqCompatible(isCharging(mContext), whileCharging)) {
                queueIOFile(url, file, onWifi, whileCharging, true);
                return mQueueFileIO;
            } else
                return mRestApi.dynamicDownload(url)
                        .doOnError(throwable -> {
                            if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable))
                                queueIOFile(url, file, true, whileCharging, false);
                        })
                        .map(responseBody -> {
                            try {
                                InputStream inputStream = null;
                                OutputStream outputStream = null;
                                try {
                                    byte[] fileReader = new byte[4096];
                                    long fileSize = responseBody.contentLength();
                                    long fileSizeDownloaded = 0;
                                    inputStream = responseBody.byteStream();
                                    outputStream = new FileOutputStream(file);
                                    while (true) {
                                        int read = inputStream.read(fileReader);
                                        if (read == -1)
                                            break;
                                        outputStream.write(fileReader, 0, read);
                                        fileSizeDownloaded += read;
                                        Log.d(TAG, "file download: " + fileSizeDownloaded + " of "
                                                + fileSize);
                                    }
                                    outputStream.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (inputStream != null)
                                        inputStream.close();
                                    if (outputStream != null)
                                        outputStream.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return file;
                        });
        });
    }

    @NonNull
    @Override
    public Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass) {
        return Observable.error(new IllegalAccessException(mContext.getString(R.string.search_disk_error_cloud)));
    }

    @NonNull
    @Override
    public Observable<List> searchDisk(RealmQuery query, Class domainClass) {
        return Observable.error(new IllegalAccessException(mContext.getString(R.string.search_disk_error_cloud)));
    }

    private <T> Observable.Transformer<T, T> applyExponentialBackoff() {
        return observable -> observable.retryWhen(attempts -> {
            if (ConnectionClassManager.getInstance().getCurrentBandwidthQuality()
                    .compareTo(ConnectionQuality.MODERATE) >= 0)
                return attempts.zipWith(Observable.range(COUNTER_START, ATTEMPTS), (n, i) -> i)
                        .flatMap(i -> {
                            Log.d(TAG, "delay retry by " + i + " second(s)");
                            return Observable.timer(i, TimeUnit.SECONDS);
                        });
            else return null;
        });
    }

    private boolean willPersist(boolean persist) {
        return persist && mCanPersist;
    }

    private boolean isNetworkFailure(Throwable throwable) {
        return throwable instanceof UnknownHostException || throwable instanceof ConnectException;
    }

    private boolean isQueuableIfOutOfNetwork(boolean queuable) {
        return queuable && !Utils.isNetworkAvailable(mContext);
    }

    private boolean isChargingReqCompatible(boolean isChargingCurrently, boolean doWhileCharging) {
        return !doWhileCharging || isChargingCurrently;
    }

    private boolean isCharging(Context context) {
        boolean charging = false;
        final Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent != null) {
            int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean batteryCharge = status == BatteryManager.BATTERY_STATUS_CHARGING;
            int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            if (batteryCharge) charging = true;
            if (usbCharge) charging = true;
            if (acCharge) charging = true;
        }
        return charging;
//        Intent intent = Config.getInstance().getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
//        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    private boolean isOnWifi(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    private void queueIOFile(String url, File file, boolean onWifi, boolean whileCharging, boolean isDownload) {
        Utils.queueFileIOCore(mDispatcher, isDownload, new FileIORequest.FileIORequestBuilder(url, file)
                .onWifi(onWifi)
                .whileCharging(whileCharging)
                .build(), gson);
    }

    private void queuePost(String method, String url, String idColumnName, JSONArray jsonArray,
                           boolean persist) {
        queuePostCore(new PostRequest.PostRequestBuilder(null, persist)
                .idColumnName(idColumnName)
                .payLoad(jsonArray)
                .url(url)
                .method(method)
                .build());
    }

    private void queuePost(String method, String url, String idColumnName, JSONObject jsonObject,
                           boolean persist) {
        queuePostCore(new PostRequest.PostRequestBuilder(null, persist)
                .idColumnName(idColumnName)
                .payLoad(jsonObject)
                .url(url)
                .method(method)
                .build());
    }

    private void queuePostCore(PostRequest postRequest) {
        Utils.queuePostCore(mDispatcher, postRequest, gson);
    }

    private void persistGeneric(Object object, String idColumnName, Class dataClass) {
        if (object instanceof File)
            return;
        Object mappedObject = null;
        Observable<?> observable = null;
        if (mDataBaseManager instanceof RealmManager) {
            try {
                if (!(object instanceof JSONArray) && !(object instanceof Map))
                    mappedObject = mEntityDataMapper.transformToRealm(object, dataClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mappedObject instanceof RealmObject)
                observable = mDataBaseManager.put((RealmObject) mappedObject, dataClass);
            else if (mappedObject instanceof RealmModel)
                observable = mDataBaseManager.put((RealmModel) mappedObject, dataClass);
            else try {
                    if (object instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) object;
                        observable = mDataBaseManager.putAll(jsonArray, idColumnName, dataClass)
                                .flatMap(o -> {
                                    JSONObject jsonObject;
                                    for (int i = 0, size = jsonArray.length(); i < size; i++) {
                                        jsonObject = jsonArray.optJSONObject(i);
                                        Storo.put(dataClass.getSimpleName()
                                                        + jsonObject.optString(idColumnName),
                                                gson.fromJson(jsonObject.toString(), dataClass)).execute();
                                    }
                                    return Observable.just(true);
                                });
                    } else if (object instanceof List) {
                        observable = mDataBaseManager.putAll((List<RealmObject>) mEntityDataMapper
                                .transformAllToRealm((List) object, dataClass), dataClass);
                    } else {
                        JSONObject jsonObject;
                        if (object instanceof Map) {
                            jsonObject = new JSONObject(((Map) object));
                        } else if (object instanceof String) {
                            jsonObject = new JSONObject((String) object);
                        } else if (object instanceof JSONObject) {
                            jsonObject = ((JSONObject) object);
                        } else
                            jsonObject = new JSONObject(gson.toJson(object, dataClass));
                        observable = mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                                .flatMap(o -> Observable.just(Storo.put(dataClass.getSimpleName()
                                                + jsonObject.optString(idColumnName),
                                        gson.fromJson(jsonObject.toString(), dataClass)).execute()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    observable = Observable.error(e);
                }
        }
        if (observable != null)
            observable.subscribeOn(Schedulers.io())
                    .subscribe(new SimpleSubscriber(object));
    }

    private void persistAllGenerics(List collection, Class dataClass) {
        mDataBaseManager.putAll(mEntityDataMapper.transformAllToRealm(collection, dataClass), dataClass)
                .subscribeOn(Schedulers.io())
                .subscribe(new SimpleSubscriber(collection));
    }

    private void deleteFromPersistence(List collection, String idColumnName, Class dataClass) {
        for (int i = 0, collectionSize = collection.size(); i < collectionSize; i++) {
            mDataBaseManager.evictById(dataClass, idColumnName, (long) collection.get(i));
            Storo.delete(dataClass.getSimpleName() + (long) collection.get(i));
        }
    }

    private static class SimpleSubscriber extends Subscriber<Object> {
        private final Object mObject;

        SimpleSubscriber(Object object) {
            mObject = object;
        }

        @Override
        public void onCompleted() {
            unsubscribe();
            Log.d(TAG, mObject.getClass().getName() + " completed!");
        }

        @Override
        public void onError(@NonNull Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(Object o) {
            Log.d(TAG, mObject.getClass().getName() + " added!");
        }
    }
}
