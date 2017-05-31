package com.zeyad.usecases.stores;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.zeyad.usecases.Config;
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

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.disposables.Disposable;
import io.realm.RealmModel;
import io.realm.RealmObject;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import st.lowlevel.storo.Storo;

import static com.zeyad.usecases.requests.PostRequest.DELETE;
import static com.zeyad.usecases.requests.PostRequest.PATCH;
import static com.zeyad.usecases.requests.PostRequest.POST;
import static com.zeyad.usecases.requests.PostRequest.PUT;

public class CloudDataStore implements DataStore {

    public static final String APPLICATION_JSON = "application/json";
    private static final String TAG = CloudDataStore.class.getSimpleName(), MULTIPART_FORM_DATA = "multipart/form-data";
    private static final int COUNTER_START = 1, ATTEMPTS = 3;
    private final DataBaseManager mDataBaseManager;
    @NonNull
    private final DAOMapper mEntityDataMapper;
    private final Context mContext;
    private final ApiConnection mApiConnection;
    @NonNull
    private final FirebaseJobDispatcher mDispatcher;
    private final Utils utils;
    boolean mCanPersist;

    /**
     * Construct a {@link DataStore} based on connections to the api (Cloud).
     *
     * @param apiConnection   The {@link RestApi} implementation to use.
     * @param dataBaseManager A {@link DataBaseManager} to cache data retrieved from the api.
     */
    CloudDataStore(
            ApiConnection apiConnection,
            DataBaseManager dataBaseManager,
            @NonNull DAOMapper entityDataMapper,
            Context context) {
        mApiConnection = apiConnection;
        mEntityDataMapper = entityDataMapper;
        mDataBaseManager = dataBaseManager;
        mContext = context;
        mCanPersist = Config.isWithRealm() || Config.isWithSQLite();
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
        utils = Utils.getInstance();
        Config.setCloudDataStore(this);
    }

    private <M> Flowable<M> getErrorFlowableNotPersisted() {
        return Flowable.error(new NetworkConnectionException("Could not reach server and could not persist to queue!"));
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicGetObject(
            String url,
            String idColumnName,
            Long itemIdL,
            String itemIdS,
            @NonNull Class dataClass,
            boolean persist,
            boolean shouldCache) {
        return mApiConnection
                .<M>dynamicGetObject(url, shouldCache)
                .doOnNext(
                        m -> {
                            if (willPersist(persist)) {
                                persistGeneric(m, idColumnName, dataClass);
                            }
                        })
                .map(entity -> mEntityDataMapper.<M>mapTo(entity, dataClass));
    }

    @NonNull
    @Override
    public <M> Flowable<List<M>> dynamicGetList(
            String url, @NonNull Class dataClass, boolean persist, boolean shouldCache) {
        return mApiConnection
                .dynamicGetList(url, shouldCache)
                .map(entities -> mEntityDataMapper.<List<M>>mapAllTo(entities, dataClass))
                .doOnNext(
                        list -> {
                            if (willPersist(persist)) {
                                persistAllGenerics(list, dataClass);
                            }
                        });
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicPatchObject(
            String url,
            String idColumnName,
            @NonNull JSONObject jsonObject,
            @NonNull Class dataClass,
            Class responseType,
            boolean persist,
            boolean queuable) {
        return Flowable.defer(
                () -> {
                    if (willPersist(persist)) {
                        persistGeneric(jsonObject, idColumnName, dataClass);
                    }
                    if (isQueuableIfOutOfNetwork(queuable)) {
                        queuePost(PATCH, url, idColumnName, jsonObject, persist);
                        return Flowable.empty();
                    } else if (!utils.isNetworkAvailable(mContext)) {
                        return getErrorFlowableNotPersisted();
                    }
                    return mApiConnection
                            .<M>dynamicPatch(
                                    url,
                                    RequestBody.create(
                                            MediaType.parse(APPLICATION_JSON),
                                            jsonObject.toString()))
                            .map(object -> daoMapHelper(responseType, object))
                            .onErrorResumeNext(
                                    throwable -> {
                                        if (isQueuableIfOutOfNetwork(queuable)
                                                && isNetworkFailure(throwable)) {
                                            queuePost(
                                                    PATCH, url, idColumnName, jsonObject, persist);
                                            return Flowable.empty();
                                        }
                                        return Flowable.error(throwable);
                                    });
                });
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicPostObject(
            String url,
            String idColumnName,
            @NonNull JSONObject jsonObject,
            @NonNull Class dataClass,
            Class responseType,
            boolean persist,
            boolean queuable) {
        return Flowable.defer(
                () -> {
                    if (willPersist(persist)) {
                        persistGeneric(jsonObject, idColumnName, dataClass);
                    }
                    if (isQueuableIfOutOfNetwork(queuable)) {
                        queuePost(POST, url, idColumnName, jsonObject, persist);
                        return Flowable.empty();
                    } else if (!utils.isNetworkAvailable(mContext)) {
                        return getErrorFlowableNotPersisted();
                    }
                    return mApiConnection
                            .<M>dynamicPost(
                                    url,
                                    RequestBody.create(
                                            MediaType.parse(APPLICATION_JSON),
                                            jsonObject.toString()))
                            .map(object -> daoMapHelper(responseType, object))
                            .onErrorResumeNext(
                                    throwable -> {
                                        if (isQueuableIfOutOfNetwork(queuable)
                                                && isNetworkFailure(throwable)) {
                                            queuePost(POST, url, idColumnName, jsonObject, persist);
                                            return Flowable.empty();
                                        }
                                        return Flowable.error(throwable);
                                    });
                });
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicPostList(String url, String idColumnName, @NonNull JSONArray jsonArray,
                                           @NonNull Class dataClass, Class responseType,
                                           boolean persist, boolean queuable) {
        return Flowable.defer(() -> {
            if (willPersist(persist)) {
                persistGeneric(jsonArray, idColumnName, dataClass);
            }
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(POST, url, idColumnName, jsonArray, persist);
                return Flowable.empty();
            } else if (!utils.isNetworkAvailable(mContext)) {
                return getErrorFlowableNotPersisted();
            }
            return mApiConnection.<M>dynamicPost(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonArray.toString()))
                    .map(object -> daoMapHelper(responseType, object))
                    .onErrorResumeNext(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                            queuePost(POST, url, idColumnName, jsonArray, persist);
                            return Flowable.empty();
                        }
                        return Flowable.error(throwable);
                    });
        });
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicPutObject(
            String url,
            String idColumnName,
            @NonNull JSONObject jsonObject,
            @NonNull Class dataClass,
            Class responseType,
            boolean persist,
            boolean queuable) {
        return Flowable.defer(
                () -> {
                    if (willPersist(persist)) {
                        persistGeneric(jsonObject, idColumnName, dataClass);
                    }
                    if (isQueuableIfOutOfNetwork(queuable)) {
                        queuePost(PUT, url, idColumnName, jsonObject, persist);
                        return Flowable.empty();
                    } else if (!utils.isNetworkAvailable(mContext)) {
                        return getErrorFlowableNotPersisted();
                    }
                    return mApiConnection
                            .<M>dynamicPut(
                                    url,
                                    RequestBody.create(
                                            MediaType.parse(APPLICATION_JSON),
                                            jsonObject.toString()))
                            .map(object -> daoMapHelper(responseType, object))
                            .onErrorResumeNext(
                                    throwable -> {
                                        if (isQueuableIfOutOfNetwork(queuable)
                                                && isNetworkFailure(throwable)) {
                                            queuePost(PUT, url, idColumnName, jsonObject, persist);
                                            return Flowable.empty();
                                        }
                                        return Flowable.error(throwable);
                                    });
                });
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicPutList(
            String url,
            String idColumnName,
            @NonNull JSONArray jsonArray,
            @NonNull Class dataClass,
            Class responseType,
            boolean persist,
            boolean queuable) {
        return Flowable.defer(
                () -> {
                    if (willPersist(persist)) {
                        persistGeneric(jsonArray, idColumnName, dataClass);
                    }
                    if (isQueuableIfOutOfNetwork(queuable)) {
                        queuePost(PUT, url, idColumnName, jsonArray, persist);
                        return Flowable.empty();
                    } else if (!utils.isNetworkAvailable(mContext)) {
                        return getErrorFlowableNotPersisted();
                    }
                    return mApiConnection
                            .<M>dynamicPut(
                                    url,
                                    RequestBody.create(
                                            MediaType.parse(APPLICATION_JSON),
                                            jsonArray.toString()))
                            .map(object -> daoMapHelper(responseType, object))
                            .onErrorResumeNext(
                                    throwable -> {
                                        if (isQueuableIfOutOfNetwork(queuable)
                                                && isNetworkFailure(throwable)) {
                                            queuePost(PUT, url, idColumnName, jsonArray, persist);
                                            return Flowable.empty();
                                        }
                                        return Flowable.error(throwable);
                                    });
                });
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicDeleteCollection(String url, String idColumnName, @NonNull JSONArray jsonArray,
                                                   @NonNull Class dataClass, Class responseType,
                                                   boolean persist, boolean queuable) {
        return Flowable.defer(() -> {
            List<Long> ids = Utils.getInstance().convertToListOfId(jsonArray);
            if (willPersist(persist)) {
                deleteFromPersistence(ids, idColumnName, dataClass);
            }
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(DELETE, url, idColumnName, jsonArray, persist);
                return Flowable.empty();
            } else if (!utils.isNetworkAvailable(mContext)) {
                return getErrorFlowableNotPersisted();
            }
            return mApiConnection.<M>dynamicDelete(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    jsonArray.toString()))
                    .map(object -> daoMapHelper(responseType, object))
                    .onErrorResumeNext(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                            queuePost(DELETE, url, idColumnName, jsonArray, persist);
                            return Flowable.empty();
                        }
                        return Flowable.error(throwable);
                    });
        });
    }

    @NonNull
    @Override
    public Completable dynamicDeleteAll(Class dataClass) {
        return Completable.error(new IllegalStateException("Can not delete all from cloud data store!"));
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicUploadFile(String url, @NonNull File file, @NonNull String key, @Nullable Map<String, Object> parameters,
                                             boolean onWifi, boolean whileCharging, boolean queuable, @NonNull Class dataClass) {
        return Flowable.defer(() -> {
            if (isQueuableIfOutOfNetwork(queuable) && isOnWifi(mContext) == onWifi
                    && isChargingReqCompatible(isCharging(mContext), whileCharging)) {
                queueIOFile(url, file, true, whileCharging, false);
                return Flowable.empty();
            } else if (!utils.isNetworkAvailable(mContext)) {
                return getErrorFlowableNotPersisted();
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put(key, requestFile);
            if (parameters != null && !parameters.isEmpty()) {
                for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                    map.put(entry.getKey(), RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA),
                            String.valueOf(entry.getValue())));
                }
            }
            return mApiConnection.<M>dynamicUpload(url, map, MultipartBody.Part.createFormData(key,
                    file.getName(), requestFile))
                    .map(object -> daoMapHelper(dataClass, object))
                    .onErrorResumeNext(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                            queueIOFile(url, file, true, whileCharging, false);
                            return Flowable.empty();
                        }
                        return Flowable.error(throwable);
                    });
        });
    }

    @NonNull
    @Override
    public Flowable<File> dynamicDownloadFile(String url, @NonNull File file, boolean onWifi,
                                              boolean whileCharging, boolean queuable) {
        return Flowable.defer(() -> {
            if (isQueuableIfOutOfNetwork(queuable) && isOnWifi(mContext) == onWifi
                    && isChargingReqCompatible(isCharging(mContext), whileCharging)) {
                queueIOFile(url, file, onWifi, whileCharging, true);
                return Flowable.empty();
            } else if (!utils.isNetworkAvailable(mContext)) {
                return getErrorFlowableNotPersisted();
            }
            return mApiConnection.dynamicDownload(url)
                    .onErrorResumeNext(throwable -> {
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                            queueIOFile(url, file, true, whileCharging, false);
                            return Flowable.empty();
                        }
                        return Flowable.error(throwable);
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
                                    if (read == -1) {
                                        break;
                                    }
                                    outputStream.write(fileReader, 0, read);
                                    fileSizeDownloaded += read;
                                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                                }
                                outputStream.flush();
                            } catch (IOException e) {
                                Log.e(TAG, "", e);
                            } finally {
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                                if (outputStream != null) {
                                    outputStream.close();
                                }
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "", e);
                        }
                        return file;
                    });
        });
    }

    @NonNull
    @Override
    public <M> Flowable<List<M>> queryDisk(RealmQueryProvider queryFactory) {
        return Flowable.error(new IllegalAccessException("Can not search disk in cloud data store!"));
    }

    private <M> FlowableTransformer<M, M> applyExponentialBackoff() {
        return observable ->
                observable.retryWhen(
                        attempts -> {
                            return attempts.zipWith(
                                    Flowable.range(COUNTER_START, ATTEMPTS), (n, i) -> i)
                                    .flatMap(
                                            i -> {
                                                Log.d(TAG, "delay retry by " + i + " second(s)");
                                                return Flowable.timer(5 * i, TimeUnit.SECONDS);
                                            });
                        });
    }

    @Nullable
    private <M> M daoMapHelper(@NonNull Class dataClass, M object) {
        return object instanceof List
                ? mEntityDataMapper.mapAllTo((List) object, dataClass)
                : mEntityDataMapper.mapTo(object, dataClass);
    }

    private boolean willPersist(boolean persist) {
        return persist && mCanPersist;
    }

    private boolean isNetworkFailure(Throwable throwable) {
        return throwable instanceof UnknownHostException
                || throwable instanceof ConnectException
                || throwable instanceof IOException;
    }

    private boolean isQueuableIfOutOfNetwork(boolean queuable) {
        return queuable && !utils.isNetworkAvailable(mContext);
    }

    private boolean isChargingReqCompatible(boolean isChargingCurrently, boolean doWhileCharging) {
        return !doWhileCharging || isChargingCurrently;
    }

    private boolean isCharging(@NonNull Context context) {
        boolean charging = false;
        final Intent batteryIntent =
                context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent != null) {
            int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean batteryCharge = status == BatteryManager.BATTERY_STATUS_CHARGING;
            int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            if (batteryCharge) {
                charging = true;
            }
            if (usbCharge) {
                charging = true;
            }
            if (acCharge) {
                charging = true;
            }
        }
        return charging;
        //        Intent intent = Config.getInstance().getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        //        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    private boolean isOnWifi(@NonNull Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo()
                .getType()
                == ConnectivityManager.TYPE_WIFI;
    }

    private void queueIOFile(
            String url, File file, boolean onWifi, boolean whileCharging, boolean isDownload) {
        utils.queueFileIOCore(
                mDispatcher,
                isDownload,
                new FileIORequest.Builder(url, file)
                        .onWifi(onWifi)
                        .whileCharging(whileCharging)
                        .build());
    }

    private void queuePost(
            String method, String url, String idColumnName, JSONArray jsonArray, boolean persist) {
        queuePostCore(
                new PostRequest.Builder(null, persist)
                        .idColumnName(idColumnName)
                        .payLoad(jsonArray)
                        .url(url)
                        .method(method)
                        .build());
    }

    private void queuePost(
            String method,
            String url,
            String idColumnName,
            JSONObject jsonObject,
            boolean persist) {
        queuePostCore(
                new PostRequest.Builder(null, persist)
                        .idColumnName(idColumnName)
                        .payLoad(jsonObject)
                        .url(url)
                        .method(method)
                        .build());
    }

    private void queuePostCore(@NonNull PostRequest postRequest) {
        utils.queuePostCore(mDispatcher, postRequest);
    }

    private void persistGeneric(Object object, String idColumnName, @NonNull Class dataClass) {
        if (object instanceof File) {
            return;
        }
        Object mappedObject = null;
        Completable completable = null;
        if (mDataBaseManager instanceof RealmManager) {
            try {
                if (!(object instanceof JSONArray) && !(object instanceof Map)) {
                    mappedObject = mEntityDataMapper.mapTo(object, dataClass);
                }
            } catch (Exception e) {
                Log.e(TAG, "persistGeneric", e);
            }
            if (mappedObject instanceof RealmObject) {
                completable = mDataBaseManager.put((RealmObject) mappedObject, dataClass);
            } else if (mappedObject instanceof RealmModel) {
                completable = mDataBaseManager.put((RealmModel) mappedObject, dataClass);
            } else {
                try {
                    if (object instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) object;
                        completable = mDataBaseManager.putAll(jsonArray, idColumnName, dataClass)
                                .doOnEvent(objectNotification -> {
                                    JSONObject jsonObject;
                                    int size = jsonArray.length();
                                    for (int i = 0; i < size; i++) {
                                        jsonObject = jsonArray.optJSONObject(i);
                                        Storo.put(dataClass.getSimpleName()
                                                        + jsonObject.optString(idColumnName),
                                                gson.fromJson(jsonObject.toString(), dataClass)).execute();
                                    }
                                });
                    } else if (object instanceof List) {
                        completable =
                                mDataBaseManager.putAll(
                                        mEntityDataMapper.mapAllTo((List) object, dataClass),
                                        dataClass);
                    } else {
                        JSONObject jsonObject;
                        if (object instanceof Map) {
                            jsonObject = new JSONObject(((Map) object));
                        } else if (object instanceof String) {
                            jsonObject = new JSONObject((String) object);
                        } else if (object instanceof JSONObject) {
                            jsonObject = ((JSONObject) object);
                        } else {
                            jsonObject = new JSONObject(gson.toJson(object, dataClass));
                        }
                        completable =
                                mDataBaseManager
                                        .put(jsonObject, idColumnName, dataClass)
                                        .doOnEvent(
                                                objectNotification -> {
                                                    if (Config.isWithCache()) {
                                                        Storo.put(
                                                                dataClass.getSimpleName()
                                                                        + jsonObject
                                                                        .optString(
                                                                                idColumnName),
                                                                gson.fromJson(
                                                                        jsonObject
                                                                                .toString(),
                                                                        dataClass))
                                                                .setExpiry(
                                                                        Config.getCacheAmount(),
                                                                        Config.getCacheTimeUnit())
                                                                .execute();
                                                    }
                                                });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "persistGeneric", e);
                    completable = Completable.error(e);
                }
            }
        }
        if (completable != null) {
            completable
                    .subscribeOn(Config.getBackgroundThread())
                    .subscribe(new SimpleSubscriber(dataClass));
        }
    }

    private void persistAllGenerics(List collection, Class dataClass) {
        mDataBaseManager
                .putAll(collection, dataClass)
                .subscribeOn(Config.getBackgroundThread())
                .subscribe(new SimpleSubscriber(dataClass));
    }

    private void deleteFromPersistence(@NonNull List collection, String idColumnName, @NonNull Class dataClass) {
        int collectionSize = collection.size();
        for (int i = 0; i < collectionSize; i++) {
            mDataBaseManager.evictById(dataClass, idColumnName, (long) collection.get(i));
            if (Config.isWithCache()) {
                Storo.delete(dataClass.getSimpleName() + (long) collection.get(i));
            }
        }
    }

    private static class SimpleSubscriber implements CompletableObserver {
        private final Class mClass;
        private Disposable subscription;

        SimpleSubscriber(Class aClass) {
            mClass = aClass;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            subscription = d;
        }

        @Override
        public void onComplete() {
            subscription.dispose();
            Log.d(TAG, mClass.getSimpleName() + " persisted!");
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(TAG, "", e);
            subscription.dispose();
        }
    }
}
