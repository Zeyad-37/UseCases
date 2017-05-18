package com.zeyad.usecases.stores;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.R;
import com.zeyad.usecases.db.DataBaseManager;
import com.zeyad.usecases.db.RealmManager;
import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.exceptions.NetworkConnectionException;
import com.zeyad.usecases.mapper.DAOMapper;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.network.RestApi;
import com.zeyad.usecases.requests.FileIORequest;
import com.zeyad.usecases.requests.PostRequest;
import com.zeyad.usecases.utils.Utils;

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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;
import rx.Subscription;
import st.lowlevel.storo.Storo;

import static com.zeyad.usecases.requests.PostRequest.DELETE;
import static com.zeyad.usecases.requests.PostRequest.PATCH;
import static com.zeyad.usecases.requests.PostRequest.POST;
import static com.zeyad.usecases.requests.PostRequest.PUT;

public class CloudDataStore implements DataStore {

    public static final String APPLICATION_JSON = "application/json";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final String TAG = CloudDataStore.class.getSimpleName();
    private static final String NO_INTERNET_NOT_PERSISTED = "Could not " +
            "reach server and could not persist request to queue!\\nGoogle play services not " +
            "available and android version less than 5.0!";
    private static final int COUNTER_START = 1, ATTEMPTS = 3;
    private final DataBaseManager mDataBaseManager;
    private final DAOMapper mEntityDataMapper;
    private final Context mContext;
    private final Observable<Object> mErrorObservableNotPersisted;
    private final ApiConnection mApiConnection;
    private final FirebaseJobDispatcher mDispatcher;
    private final Utils utils;
    boolean mCanPersist;

    /**
     * Construct a {@link DataStore} based on connections to the api (Cloud).
     *
     * @param apiConnection   The {@link RestApi} implementation to use.
     * @param dataBaseManager A {@link DataBaseManager} to cache data retrieved from the api.
     */
    CloudDataStore(ApiConnection apiConnection, DataBaseManager dataBaseManager, DAOMapper entityDataMapper) {
        mApiConnection = apiConnection;
        mEntityDataMapper = entityDataMapper;
        mDataBaseManager = dataBaseManager;
        mContext = Config.getInstance().getContext();
        mErrorObservableNotPersisted = Observable.error(new NetworkConnectionException(NO_INTERNET_NOT_PERSISTED));
        mCanPersist = Config.isWithRealm();
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
        utils = Utils.getInstance();
        Config.setCloudDataStore(this);
    }

    CloudDataStore(ApiConnection apiConnection, DataBaseManager dataBaseManager, DAOMapper entityDataMapper,
                   Context context) {
        mApiConnection = apiConnection;
        mEntityDataMapper = entityDataMapper;
        mDataBaseManager = dataBaseManager;
        mContext = context;
        mErrorObservableNotPersisted = Observable.error(new NetworkConnectionException(NO_INTERNET_NOT_PERSISTED));
        mCanPersist = Config.isWithRealm();
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
        utils = Utils.getInstance();
        Config.setCloudDataStore(this);
    }

    @NonNull
    @Override
    public Observable<Object> dynamicGetObject(String url, String idColumnName, int itemId, Class dataClass,
                                               boolean persist, boolean shouldCache) {
        return mApiConnection.dynamicGetObject(url, shouldCache)
                .doOnNext(object -> {
                    if (willPersist(persist))
                        persistGeneric(object, idColumnName, dataClass);
                })
                .map(entity -> mEntityDataMapper.mapTo(entity, dataClass));
    }

    @NonNull
    @Override
    public <M> Observable<List<M>> dynamicGetList(String url, Class dataClass, boolean persist, boolean shouldCache) {
        return mApiConnection.dynamicGetList(url, shouldCache)
                .doOnNext(list -> {
                    if (willPersist(persist))
                        persistAllGenerics(list, dataClass);
                })
                .map(entities -> mEntityDataMapper.mapAllTo(entities, dataClass));
    }

    @NonNull
    @Override
    public Observable<Object> dynamicPatchObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                                 Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            if (willPersist(persist))
                persistGeneric(jsonObject, idColumnName, dataClass);
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(PATCH, url, idColumnName, jsonObject, persist);
                return Observable.empty();
            } else if (!utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mApiConnection.dynamicPatch(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonObject.toString()))
                    .map(object -> mEntityDataMapper.mapTo(object, dataClass))
                    .onErrorResumeNext(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                            queuePost(PATCH, url, idColumnName, jsonObject, persist);
                            return Observable.empty();
                        }
                        return Observable.error(throwable);
                    });
        });
    }

    @NonNull
    @Override
    public Observable<Object> dynamicPostObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                                Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            if (willPersist(persist))
                persistGeneric(jsonObject, idColumnName, dataClass);
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(POST, url, idColumnName, jsonObject, persist);
                return Observable.empty();
            } else if (!utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mApiConnection.dynamicPost(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonObject.toString()))
                    .map(object -> mEntityDataMapper.mapTo(object, dataClass))
                    .onErrorResumeNext(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                            queuePost(POST, url, idColumnName, jsonObject, persist);
                            return Observable.empty();
                        }
                        return Observable.error(throwable);
                    });
        });
    }

    @NonNull
    @Override
    public Observable<Object> dynamicPostList(String url, String idColumnName, @NonNull JSONArray jsonArray,
                                              Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            if (willPersist(persist))
                persistGeneric(jsonArray, idColumnName, dataClass);
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(POST, url, idColumnName, jsonArray, persist);
                return Observable.empty();
            } else if (!utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mApiConnection.dynamicPost(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonArray.toString()))
                    .map(object -> mEntityDataMapper.mapTo(object, dataClass))
                    .onErrorResumeNext(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                            queuePost(POST, url, idColumnName, jsonArray, persist);
                            return Observable.empty();
                        }
                        return Observable.error(throwable);
                    });
        });
    }

    @NonNull
    @Override
    public Observable<Object> dynamicPutObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                               Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            if (willPersist(persist))
                persistGeneric(jsonObject, idColumnName, dataClass);
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(PUT, url, idColumnName, jsonObject, persist);
                return Observable.empty();
            } else if (!utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mApiConnection.dynamicPut(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonObject.toString()))
                    .map(object -> mEntityDataMapper.mapTo(object, dataClass))
                    .onErrorResumeNext(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                            queuePost(PUT, url, idColumnName, jsonObject, persist);
                            return Observable.empty();
                        }
                        return Observable.error(throwable);
                    });
        });
    }

    @NonNull
    @Override
    public Observable<Object> dynamicPutList(String url, String idColumnName, @NonNull JSONArray jsonArray,
                                             Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            if (willPersist(persist))
                persistGeneric(jsonArray, idColumnName, dataClass);
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(PUT, url, idColumnName, jsonArray, persist);
                return Observable.empty();
            } else if (!utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mApiConnection.dynamicPut(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonArray.toString()))
                    .map(object -> mEntityDataMapper.mapTo(object, dataClass))
                    .onErrorResumeNext(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                            queuePost(PUT, url, idColumnName, jsonArray, persist);
                            return Observable.empty();
                        }
                        return Observable.error(throwable);
                    });
        });
    }

    @NonNull
    @Override
    public Observable<Object> dynamicDeleteCollection(String url, String idColumnName, JSONArray jsonArray,
                                                      Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            List<Long> ids = Utils.getInstance().convertToListOfId(jsonArray);
            if (willPersist(persist))
                deleteFromPersistence(ids, idColumnName, dataClass);
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(DELETE, url, idColumnName, jsonArray, persist);
                return Observable.empty();
            } else if (!utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mApiConnection.dynamicDelete(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonArray.toString()))
                    .map(object -> mEntityDataMapper.mapTo(object, dataClass))
                    .onErrorResumeNext(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                            queuePost(PostRequest.DELETE, url, idColumnName, jsonArray, persist);
                            return Observable.empty();
                        }
                        return Observable.error(throwable);
                    });
        });
    }

    @NonNull
    @Override
    public Completable dynamicDeleteAll(Class dataClass) {
        return Completable.error(new IllegalStateException(mContext.getString(R.string.delete_all_error_cloud)));
    }

    @NonNull
    @Override
    public <M> Observable dynamicUploadFile(String url, @NonNull File file, String key, HashMap<String, Object> parameters,
                                            boolean onWifi, boolean whileCharging, boolean queuable, Class dataClass) {
        return Observable.defer(() -> {
            if (isQueuableIfOutOfNetwork(queuable) && isOnWifi(mContext) == onWifi
                    && isChargingReqCompatible(isCharging(mContext), whileCharging)) {
                queueIOFile(url, file, true, whileCharging, false);
                return Observable.empty();
            } else if (!utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            RequestBody requestFile = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put(key, requestFile);
            if (parameters != null && !parameters.isEmpty())
                for (Map.Entry<String, Object> entry : parameters.entrySet())
                    map.put(entry.getKey(), utils.createPartFromString(entry.getValue()));
            return mApiConnection.dynamicUpload(url, map, MultipartBody.Part.createFormData(key,
                    file.getName(), requestFile))
                    .map(object -> mEntityDataMapper.mapTo(object, dataClass))
                    .onErrorResumeNext(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                            queueIOFile(url, file, true, whileCharging, false);
                            return Observable.empty();
                        }
                        return Observable.error(throwable);
                    });
        });
    }

    @NonNull
    @Override
    public Observable dynamicDownloadFile(String url, @NonNull File file, boolean onWifi,
                                          boolean whileCharging, boolean queuable) {
        return Observable.defer(() -> {
            if (isQueuableIfOutOfNetwork(queuable) && isOnWifi(mContext) == onWifi
                    && isChargingReqCompatible(isCharging(mContext), whileCharging)) {
                queueIOFile(url, file, onWifi, whileCharging, true);
                return Observable.empty();
            } else if (!utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mApiConnection.dynamicDownload(url)
                    .onErrorResumeNext(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                            queueIOFile(url, file, true, whileCharging, false);
                            return Observable.empty();
                        }
                        return Observable.error(throwable);
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
    public <M> Observable<List<M>> queryDisk(RealmQueryProvider queryFactory) {
        return Observable.error(new IllegalAccessException(mContext.getString(R.string.search_disk_error_cloud)));
    }

    private <T> Observable.Transformer<T, T> applyExponentialBackoff() {
        return observable -> observable.retryWhen(attempts -> {
            return attempts.zipWith(Observable.range(COUNTER_START, ATTEMPTS), (n, i) -> i)
                    .flatMap(i -> {
                        Log.d(TAG, "delay retry by " + i + " second(s)");
                        return Observable.timer(i, TimeUnit.SECONDS);
                    });
        });
    }

    private boolean willPersist(boolean persist) {
        return persist && mCanPersist;
    }

    private boolean isNetworkFailure(Throwable throwable) {
        return throwable instanceof UnknownHostException || throwable instanceof ConnectException
                || throwable instanceof IOException;
    }

    private boolean isQueuableIfOutOfNetwork(boolean queuable) {
        return queuable && !utils.isNetworkAvailable(mContext);
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
        utils.queueFileIOCore(mDispatcher, isDownload, new FileIORequest.Builder(url, file)
                .onWifi(onWifi)
                .whileCharging(whileCharging)
                .build());
    }

    private void queuePost(String method, String url, String idColumnName, JSONArray jsonArray, boolean persist) {
        queuePostCore(new PostRequest.Builder(null, persist)
                .idColumnName(idColumnName)
                .payLoad(jsonArray)
                .url(url)
                .method(method)
                .build());
    }

    private void queuePost(String method, String url, String idColumnName, JSONObject jsonObject, boolean persist) {
        queuePostCore(new PostRequest.Builder(null, persist)
                .idColumnName(idColumnName)
                .payLoad(jsonObject)
                .url(url)
                .method(method)
                .build());
    }

    private void queuePostCore(PostRequest postRequest) {
        utils.queuePostCore(mDispatcher, postRequest);
    }

    private void persistGeneric(Object object, String idColumnName, Class dataClass) {
        if (object instanceof File)
            return;
        Object mappedObject = null;
        Completable completable
                = null;
        if (mDataBaseManager instanceof RealmManager) {
            try {
                if (!(object instanceof JSONArray) && !(object instanceof Map))
                    mappedObject = mEntityDataMapper.mapTo(object, dataClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mappedObject instanceof RealmObject)
                completable = mDataBaseManager.put((RealmObject) mappedObject, dataClass);
            else if (mappedObject instanceof RealmModel)
                completable = mDataBaseManager.put((RealmModel) mappedObject, dataClass);
            else try {
                    if (object instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) object;
                        completable = mDataBaseManager.putAll(jsonArray, idColumnName, dataClass)
                                .doOnEach(objectNotification -> {
                                    JSONObject jsonObject;
                                    for (int i = 0, size = jsonArray.length(); i < size; i++) {
                                        jsonObject = jsonArray.optJSONObject(i);
                                        Storo.put(dataClass.getSimpleName()
                                                        + jsonObject.optString(idColumnName),
                                                gson.fromJson(jsonObject.toString(), dataClass)).execute();
                                    }
                                });
                    } else if (object instanceof List) {
                        completable = mDataBaseManager.putAll(mEntityDataMapper
                                .mapAllTo((List) object, dataClass), dataClass);
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
                        completable = mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                                .doOnEach(objectNotification -> {
                                    if (Config.isWithCache())
                                        Storo.put(dataClass.getSimpleName() + jsonObject.optString(idColumnName),
                                                gson.fromJson(jsonObject.toString(), dataClass))
                                                .setExpiry(Config.getCacheAmount(), Config.getCacheTimeUnit())
                                                .execute();
                                });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    completable = Completable.error(e);
                }
        }
        if (completable != null)
            completable.subscribeOn(Config.getBackgroundThread())
                    .subscribe(new SimpleSubscriber(dataClass));
    }

    private void persistAllGenerics(List collection, Class dataClass) {
        mDataBaseManager.putAll(mEntityDataMapper.mapAllTo(collection, dataClass), dataClass)
                .subscribeOn(Config.getBackgroundThread())
                .subscribe(new SimpleSubscriber(dataClass));
    }

    private void deleteFromPersistence(List collection, String idColumnName, Class dataClass) {
        for (int i = 0, collectionSize = collection.size(); i < collectionSize; i++) {
            mDataBaseManager.evictById(dataClass, idColumnName, (long) collection.get(i));
            if (Config.isWithCache())
                Storo.delete(dataClass.getSimpleName() + (long) collection.get(i));
        }
    }

    private static class SimpleSubscriber implements CompletableSubscriber {
        private final Class mClass;
        private Subscription subscription;

        SimpleSubscriber(Class aClass) {
            mClass = aClass;
        }

        @Override
        public void onCompleted() {
            subscription.unsubscribe();
            Log.d(TAG, mClass.getSimpleName() + " persisted!");
        }

        @Override
        public void onError(@NonNull Throwable e) {
            e.printStackTrace();
            subscription.unsubscribe();
        }

        @Override
        public void onSubscribe(Subscription d) {
            subscription = d;
        }
    }
}
