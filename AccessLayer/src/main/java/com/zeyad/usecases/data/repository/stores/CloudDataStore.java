package com.zeyad.usecases.data.repository.stores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.zeyad.usecases.Config.NONE;
import static com.zeyad.usecases.data.requests.PostRequest.DELETE;
import static com.zeyad.usecases.data.requests.PostRequest.POST;

public class CloudDataStore implements DataStore {

    public static final String FILE_IO_TAG = "fileIOObject", POST_TAG = "postObject", APPLICATION_JSON = "application/json",
            MULTIPART_FORMDATA = "multipart/form-data";
    static final String TAG = CloudDataStore.class.getName();
    private static final int COUNTER_START = 1, ATTEMPTS = 3;
    final DataBaseManager mDataBaseManager;
    private final EntityMapper mEntityDataMapper;
    private final Context mContext;
    @NonNull
    private final Observable<Object> mErrorObservableNotPersisted, mQueueFileIO;
    private final RestApi mRestApi;
    private final boolean mCanPersist;
    private final FirebaseJobDispatcher mDispatcher;

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
        mCanPersist = Config.getInstance().getDBType() > NONE;
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
                        new SaveGenericToDBAction(dataClass, idColumnName).call(object);
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
                        new SaveAllGenericsToDBAction(dataClass).call(list);
                })
                .map(entities -> mEntityDataMapper.transformAllToDomain(entities, domainClass));
    }

    @NonNull
    @Override
    public Observable<?> dynamicPostObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                           Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            final SaveGenericToDBAction cacheAction = new SaveGenericToDBAction(dataClass, idColumnName);
            if (isEligibleForPersistenceIfNetworkNotAvailable(queuable)) {
                queuePost(POST, url, idColumnName, jsonObject, persist);
                cacheAction.call(jsonObject);
                return Observable.empty();
            } else if (!Utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicPostObject(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    ModelConverters.convertToString(jsonObject)))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(object -> {
                        if (willPersist(persist))
                            cacheAction.call(object);
                    })
                    .doOnError(throwable -> {
                        if (persist)
                            new SaveGenericToDBAction(dataClass, idColumnName).call(jsonObject);
                        if (isNetworkFailure(throwable))
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
            final SaveGenericToDBAction cacheAction = new SaveGenericToDBAction(dataClass, idColumnName);
            if (isEligibleForPersistenceIfNetworkNotAvailable(queuable)) {
                cacheAction.call(jsonArray);
                queuePost(POST, url, idColumnName, jsonArray, persist);
                return Observable.empty();
            } else if (!Utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicPostList(url,
                    RequestBody.create(MediaType.parse(APPLICATION_JSON), jsonArray.toString()))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(list -> {
                        if (persist)
                            cacheAction.call(list);
                    })
                    .doOnError(throwable -> {
                        if (persist)
                            cacheAction.call(jsonArray);
                        if (isNetworkFailure(throwable))
                            queuePost(POST, url, idColumnName, jsonArray, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                          Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            final SaveGenericToDBAction cacheAction = new SaveGenericToDBAction(dataClass, idColumnName);
            if (isEligibleForPersistenceIfNetworkNotAvailable(queuable)) {
                cacheAction.call(jsonObject);
                queuePost(PostRequest.PUT, url, idColumnName, jsonObject, persist);
                return Observable.empty();
            } else if (!Utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicPutObject(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    ModelConverters.convertToString(jsonObject)))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(object -> {
                        if (willPersist(persist))
                            cacheAction.call(object);
                    })
                    .doOnError(throwable -> {
                        if (willPersist(persist))
                            new SaveGenericToDBAction(dataClass, idColumnName).call(jsonObject);
                        if (isNetworkFailure(throwable))
                            queuePost(PostRequest.PUT, url, idColumnName, jsonObject, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutList(String url, String idColumnName, @NonNull JSONArray jsonArray,
                                        Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            final SaveGenericToDBAction cacheAction = new SaveGenericToDBAction(dataClass, idColumnName);
            if (willPersist(persist))
                cacheAction.call(jsonArray);
            if (isEligibleForPersistenceIfNetworkNotAvailable(queuable)) {
                queuePost(PostRequest.PUT, url, idColumnName, jsonArray, persist);
                return Observable.empty();
            } else if (!Utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicPutList(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    ModelConverters.convertToString(jsonArray)))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(list -> {
                        if (willPersist(persist))
                            cacheAction.call(list);
                    })
                    .doOnError(throwable -> {
                        if (willPersist(persist))
                            cacheAction.call(jsonArray);
                        if (isNetworkFailure(throwable))
                            queuePost(PostRequest.PUT, url, idColumnName, jsonArray, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicDeleteCollection(final String url, String idColumnName,
                                                 @NonNull final JSONArray jsonArray,
                                                 Class dataClass, boolean persist, boolean queuable) {
        return Observable.defer(() -> {
            List<Long> ids = ModelConverters.convertToListOfId(jsonArray);
            if (willPersist(persist))
                new DeleteCollectionGenericsFromDBAction(dataClass, idColumnName).call(ids);
            if (isEligibleForPersistenceIfNetworkNotAvailable(queuable)) {
                queuePost(DELETE, url, idColumnName, jsonArray, persist);
                return Observable.empty();
            } else if (!Utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicDeleteObject(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    ModelConverters.convertToString(jsonArray)))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(object -> {
                        if (willPersist(persist))
                            new DeleteCollectionGenericsFromDBAction(dataClass, idColumnName).call(ids);
                    })
                    .doOnError(throwable -> {
                        if (willPersist(persist))
                            new DeleteCollectionGenericsFromDBAction(dataClass, idColumnName).call(ids);
                        if (isNetworkFailure(throwable))
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
    public Observable<?> dynamicUploadFile(String url, @NonNull File file, String key, HashMap<String, Object> parameters, boolean onWifi, boolean whileCharging,
                                           boolean queuable, Class domainClass) {
        return Observable.defer(() -> {
            if (isEligibleForPersistenceIfNetworkNotAvailable(queuable) && Utils.isOnWifi(mContext) == onWifi
                    && Utils.isChargingReqCompatible(Utils.isCharging(mContext), whileCharging)) {
                queueIOFile(url, file, true, whileCharging, false);
                return mQueueFileIO;
            } else if (!Utils.isNetworkAvailable(mContext))
                return mErrorObservableNotPersisted;
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse(MULTIPART_FORMDATA), file);
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put(key, requestFile);
            if (parameters != null && !parameters.isEmpty()) {
                for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                    RequestBody data = Utils.createPartFromString(entry.getValue());
                    map.put(entry.getKey(), data);
                }
            }

            return mRestApi.upload(url, map, MultipartBody.Part.createFormData(key, file.getName(), requestFile))
                    .doOnError(throwable -> {
                        throwable.printStackTrace();
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
            if (isEligibleForPersistenceIfNetworkNotAvailable(queuable) && Utils.isOnWifi(mContext) == onWifi
                    && Utils.isChargingReqCompatible(Utils.isCharging(mContext), whileCharging)) {
                queueIOFile(url, file, onWifi, whileCharging, true);
                return mQueueFileIO;
            } else
                return mRestApi.dynamicDownload(url)
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
        return Observable.error(new Exception(mContext.getString(R.string.search_disk_error_cloud)));
    }

    @NonNull
    @Override
    public Observable<List> searchDisk(RealmQuery query, Class domainClass) {
        return Observable.error(new Exception(mContext.getString(R.string.search_disk_error_cloud)));
    }

    private <T> Observable.Transformer<T, T> applyExponentialBackoff() {
        return observable -> observable.retryWhen(attempts -> {
            ConnectionQuality cq = ConnectionClassManager.getInstance().getCurrentBandwidthQuality();
            if (cq.compareTo(ConnectionQuality.MODERATE) >= 0)
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

    private boolean isEligibleForPersistenceIfNetworkNotAvailable(boolean queuable) {
        return queuable && !Utils.isNetworkAvailable(mContext);
    }

    private boolean queueIOFile(String url, File file, boolean onWifi, boolean whileCharging, boolean isDownload) {
        FileIORequest fileIORequest = new FileIORequest.FileIORequestBuilder(url, file)
                .onWifi(onWifi)
                .whileCharging(whileCharging)
                .build();
        Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class)
                        && f.getDeclaredClass().equals(RealmModel.class)
                        && f.getDeclaringClass().equals(RealmList.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        Utils.queueFileIOCore(mDispatcher, isDownload, fileIORequest, gson);
        return true;
    }

    private boolean queuePost(String method, String url, String idColumnName, JSONArray jsonArray,
                              boolean persist) {
        return queuePostCore(new PostRequest.PostRequestBuilder(null, persist)
                .idColumnName(idColumnName)
                .payLoad(jsonArray)
                .url(url)
                .method(method)
                .build());
    }

    private boolean queuePost(String method, String url, String idColumnName, JSONObject jsonObject,
                              boolean persist) {
        return queuePostCore(new PostRequest.PostRequestBuilder(null, persist)
                .idColumnName(idColumnName)
                .payLoad(jsonObject)
                .url(url)
                .method(method)
                .build());
    }

    private boolean queuePostCore(PostRequest postRequest) {
        Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class)
                        && f.getDeclaredClass().equals(RealmModel.class)
                        && f.getDeclaringClass().equals(RealmList.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        Utils.queuePostCore(mDispatcher, postRequest, gson);
        return true;
    }

    private static class SimpleSubscriber extends Subscriber<Object> {
        private final Object mObject;

        SimpleSubscriber(Object object) {
            mObject = object;
        }

        @Override
        public void onCompleted() {
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

    private final class SaveGenericToDBAction implements Action1<Object> {

        private Class mDataClass;
        private String mIdColumnName;

        SaveGenericToDBAction(Class dataClass, String idColumnName) {
            mDataClass = dataClass;
            mIdColumnName = idColumnName;
        }

        @Override
        public void call(Object object) {
            if (object instanceof File)
                return;
            Object mappedObject = null;
            Observable<?> observable = null;
            if (mDataBaseManager instanceof RealmManager) {
                try {
                    if (!(object instanceof JSONArray) && !(object instanceof Map))
                        mappedObject = mEntityDataMapper.transformToRealm(object, mDataClass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mappedObject instanceof RealmObject)
                    observable = mDataBaseManager.put((RealmObject) mappedObject, mDataClass);
                else if (mappedObject instanceof RealmModel)
                    observable = mDataBaseManager.put((RealmModel) mappedObject, mDataClass);
                else try {
                        if (object instanceof JSONArray) {
                            observable = mDataBaseManager.putAll((JSONArray) object, mIdColumnName, mDataClass);
                        } else if (object instanceof List) {
                            mDataBaseManager.putAll((List<RealmObject>) mEntityDataMapper
                                    .transformAllToRealm((List) object, mDataClass), mDataClass);
                        } else {
                            JSONObject jsonObject;
                            if (object instanceof Map) {
                                jsonObject = new JSONObject(((Map) object));
                            } else if (object instanceof String) {
                                jsonObject = new JSONObject((String) object);
                            } else if (object instanceof JSONObject) {
                                jsonObject = ((JSONObject) object);
                            } else
                                jsonObject = new JSONObject(new Gson().toJson(object, mDataClass));
                            observable = mDataBaseManager.put(jsonObject, mIdColumnName, mDataClass);
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
    }

    private final class SaveAllGenericsToDBAction implements Action1<List> {

        private Class mDataClass;

        SaveAllGenericsToDBAction(Class dataClass) {
            mDataClass = dataClass;
        }

        @Override
        public void call(List collection) {
            mDataBaseManager.putAll(mEntityDataMapper.transformAllToRealm(collection, mDataClass),
                    mDataClass);
        }
    }

    private final class DeleteCollectionGenericsFromDBAction implements Action1<List> {

        private Class mDataClass;
        private String mIdFieldName;

        DeleteCollectionGenericsFromDBAction(Class dataClass, String idFieldName) {
            mDataClass = dataClass;
            mIdFieldName = idFieldName;
        }

        @Override
        public void call(List collection) {
            for (int i = 0, collectionSize = collection.size(); i < collectionSize; i++)
                mDataBaseManager.evictById(mDataClass, mIdFieldName, (long) collection.get(i));
        }
    }
}
