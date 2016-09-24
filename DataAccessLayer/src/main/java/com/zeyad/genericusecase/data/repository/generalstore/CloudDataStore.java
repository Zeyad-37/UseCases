package com.zeyad.genericusecase.data.repository.generalstore;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.gson.Gson;
import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.exceptions.NetworkConnectionException;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.network.RestApi;
import com.zeyad.genericusecase.data.services.GenericGCMService;
import com.zeyad.genericusecase.data.services.GenericJobService;
import com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService;
import com.zeyad.genericusecase.data.utils.Constants;
import com.zeyad.genericusecase.data.utils.ModelConverters;
import com.zeyad.genericusecase.data.utils.Utils;
import com.zeyad.genericusecase.domain.interactors.FileIORequest;
import com.zeyad.genericusecase.domain.interactors.PostRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.DOWNLOAD_FILE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.UPLOAD_FILE;

public class CloudDataStore implements DataStore {

    static final String TAG = com.zeyad.genericusecase.data.repository.generalstore.CloudDataStore.class.getName();
    @NonNull
    private final Observable<Object> mErrorObservablePersisted, mErrorObservableNotPersisted, mQueueFileIO;
    private final RestApi mRestApi;
    private final boolean mIsCharging;
    private final GcmNetworkManager mGcmNetworkManager;
    private GoogleApiAvailability mGoogleApiAvailability;
    private boolean mIsOnWifi;
    private boolean mHasLollipop;
    final EntityMapper mEntityDataMapper;
    final DataBaseManager mRealmManager;
    final Context mContext;

    /**
     * Construct a {@link DataStore} based on connections to the api (Cloud).
     *
     * @param restApi      The {@link RestApi} implementation to use.
     * @param realmManager A {@link DataBaseManager} to cache data retrieved from the api.
     */
    public CloudDataStore(RestApi restApi, DataBaseManager realmManager, EntityMapper entityDataMapper) {
        this(restApi, realmManager, entityDataMapper
                , GcmNetworkManager.getInstance(realmManager.getContext().getApplicationContext()));
    }

    /**
     * Construct a {@link DataStore} based on connections to the api (Cloud).
     *
     * @param restApi      The {@link RestApi} implementation to use.
     * @param realmManager A {@link DataBaseManager} to cache data retrieved from the api.
     */
    CloudDataStore(RestApi restApi, DataBaseManager realmManager, EntityMapper entityDataMapper, GcmNetworkManager gcmNetworkManager) {
        mRestApi = restApi;
        mEntityDataMapper = entityDataMapper;
        mRealmManager = realmManager;
        mContext = mRealmManager.getContext().getApplicationContext();
        mGoogleApiAvailability = GoogleApiAvailability.getInstance();
        mErrorObservablePersisted = Observable.error(new NetworkConnectionException(Constants.NETWORK_ERROR_PERSISTED));
        mErrorObservableNotPersisted = Observable.error(new NetworkConnectionException(Constants.NETWORK_ERROR_NOT_PERSISTED));
        mQueueFileIO = Observable.empty();
        mIsCharging = Utils.isCharging();
        mGcmNetworkManager = gcmNetworkManager;
    }

    /**
     * Construct a {@link DataStore} based on connections to the api (Cloud).
     *
     * @param restApi           The {@link RestApi} implementation to use.
     * @param realmManager      A {@link DataBaseManager} to cache data retrieved from the api.
     * @param isCharging
     * @param isOnWifi
     * @param gcmNetworkManager
     */
    CloudDataStore(RestApi restApi, DataBaseManager realmManager, EntityMapper entityDataMapper
            , boolean isCharging, boolean isOnWifi, GcmNetworkManager gcmNetworkManager) {
        mRestApi = restApi;
        mEntityDataMapper = entityDataMapper;
        mRealmManager = realmManager;
        mContext = mRealmManager.getContext().getApplicationContext();
        mGoogleApiAvailability = GoogleApiAvailability.getInstance();
        mErrorObservablePersisted = Observable.error(new NetworkConnectionException(Constants.NETWORK_ERROR_PERSISTED));
        mErrorObservableNotPersisted = Observable.error(new NetworkConnectionException(Constants.NETWORK_ERROR_NOT_PERSISTED));
        mQueueFileIO = Observable.empty();
        mIsCharging = isCharging;
        mIsOnWifi = isOnWifi;
        mGcmNetworkManager = gcmNetworkManager;
    }

    @NonNull
    @Override
    public Observable<List> dynamicGetList(String url, Class domainClass, Class dataClass, boolean persist,
                                           boolean shouldCache) {
        return mRestApi.dynamicGetList(url, shouldCache)
                //.compose(applyExponentialBackoff())
//                .doOnError(Throwable::printStackTrace)
                .doOnNext(list -> {
                    if (persist)
                        new SaveAllGenericsToDBAction(dataClass).call(list);
                })
                .map(entities -> mEntityDataMapper.transformAllToDomain(entities, domainClass));
    }

    @NonNull
    @Override
    public Observable<?> dynamicGetObject(String url, String idColumnName, int itemId, Class domainClass,
                                          Class dataClass, boolean persist, boolean shouldCache) {
        return mRestApi.dynamicGetObject(url, shouldCache)
                //.compose(applyExponentialBackoff())
//                .doOnError(Throwable::printStackTrace)
                .doOnNext(object -> {
                    if (persist)
                        new SaveGenericToDBAction(dataClass, idColumnName).call(object);
                })
                .map(entity -> mEntityDataMapper.transformToDomain(entity, domainClass));
    }

    @NonNull
    @Override
    public Observable<?> dynamicPostObject(String url, String idColumnName,
                                           @NonNull JSONObject jsonObject, Class domainClass,
                                           Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            final SaveGenericToDBAction cacheAction = new SaveGenericToDBAction(dataClass, idColumnName);
            if (isEligibleForPersistenceIfNetworkNotAvailable()) {
//                new QueuePost(PostRequest.POST, url, idColumnName, keyValuePairs, dataClass, persist).call();
                if (persist)
                    cacheAction.call(jsonObject);
                return mErrorObservablePersisted;
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicPostObject(url, RequestBody.create(MediaType.parse(Constants.APPLICATION_JSON),
                    ModelConverters.convertToString(jsonObject)))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(object -> {
                        if (persist)
                            cacheAction.call(object);
                    })
//                    .doOnError(throwable -> new QueuePost(PostRequest.POST, url, idColumnName, keyValuePairs,
//                            dataClass, persist).call())
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<List> dynamicPostList(String url, String idColumnName, @NonNull JSONArray jsonArray,
                                            Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            if (isEligibleForPersistenceIfNetworkNotAvailable()) {
//                queuePost.call(object);
                if (persist)
                    new SaveGenericToDBAction(dataClass, idColumnName).call(jsonArray);
                return Observable.error(new NetworkConnectionException(Constants.NETWORK_ERROR_PERSISTED));
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return Observable.error(new NetworkConnectionException(Constants.NETWORK_ERROR_PERSISTED));
            else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return Observable.error(new NetworkConnectionException(Constants.NETWORK_ERROR_NOT_PERSISTED));
            return mRestApi.dynamicPostList(url,
                    RequestBody.create(MediaType.parse(Constants.APPLICATION_JSON), jsonArray.toString()))
                    //.compose(applyExponentialBackoff())
//                    .doOnError(Throwable::printStackTrace)
                    .doOnNext(list -> {
                        if (persist)
                            new SaveGenericToDBAction(dataClass, idColumnName).call(list);
                    })
                    .doOnError(throwable -> {
                        if (persist)
                            new SaveGenericToDBAction(dataClass, idColumnName).call(jsonArray);
//                        queuePost.call(object);
                    })
                    .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicDeleteCollection(final String url, String idColumnName,
                                                 @NonNull final JSONArray jsonArray,
                                                 Class dataClass, boolean persist) {
        List<Long> ids = ModelConverters.convertToListOfId(jsonArray);
        return Observable.defer(() -> {
            if (isEligibleForPersistenceIfNetworkNotAvailable()) {
//                queueDeleteCollection.call(list);
                if (persist)
                    new DeleteCollectionGenericsFromDBAction(dataClass).call(ids);
                return mErrorObservablePersisted;
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicDeleteObject(url, RequestBody.create(MediaType.parse(Constants.APPLICATION_JSON),
                    ModelConverters.convertToString(jsonArray)))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(object -> {
                        if (persist)
                            new DeleteCollectionGenericsFromDBAction(dataClass).call(ids);
                    })
                    .doOnError(throwable -> {
//                        queueDeleteCollection.call(list);
                        if (persist)
                            new DeleteCollectionGenericsFromDBAction(dataClass).call(ids);
                    });
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutObject(String url, String idColumnName,
                                          @NonNull JSONObject keyValuePairs,
                                          Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            final SaveGenericToDBAction cacheAction = new SaveGenericToDBAction(dataClass, idColumnName);
            if (isEligibleForPersistenceIfNetworkNotAvailable()) {
//                queuePut.call(object);
                if (persist)
                    cacheAction.call(keyValuePairs);
                return mErrorObservablePersisted;
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicPutObject(url, RequestBody.create(MediaType.parse(Constants.APPLICATION_JSON),
                    ModelConverters.convertToString(keyValuePairs)))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(object -> {
                        if (persist)
                            cacheAction.call(object);
                    })
                    .doOnError(throwable -> {
//                        queuePut.call(object);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicUploadFile(String url, @NonNull File file, boolean onWifi, boolean whileCharging,
                                           Class domainClass) {
        return Observable.defer(() -> {
            mIsOnWifi = Utils.isOnWifi();
            if (isEligibleForPersistenceIfNetworkNotAvailable() && mIsOnWifi == onWifi
                    && Utils.isChargingReqCompatible(mIsCharging, whileCharging)) {
                queueIOFile(url, file, true, whileCharging, false, mContext);
                return mQueueFileIO;
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return mErrorObservableNotPersisted;
            return mRestApi.upload(url, RequestBody.create(MediaType.parse(getMimeType(file.getPath())), file))
                    .doOnError(throwable -> {
                        throwable.printStackTrace();
                        queueIOFile(url, file, true, whileCharging, false, mContext);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutList(String url, String idColumnName,
                                        @NonNull JSONArray keyValuePairs, Class domainClass,
                                        Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            final SaveGenericToDBAction cacheAction = new SaveGenericToDBAction(dataClass, idColumnName);
            if (isEligibleForPersistenceIfNetworkNotAvailable()) {
//                queuePut.call(object);
                if (persist)
                    cacheAction.call(keyValuePairs);
                return Observable.error(new NetworkConnectionException(Constants.NETWORK_ERROR_PERSISTED));
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return Observable.error(new NetworkConnectionException(Constants.NETWORK_ERROR_NOT_PERSISTED));
            return mRestApi.dynamicPutList(url, RequestBody.create(MediaType.parse(Constants.APPLICATION_JSON),
                    ModelConverters.convertToString(keyValuePairs)))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(list -> {
                        if (persist)
                            cacheAction.call(list);
                    })
                    .doOnError(throwable -> {
                        if (persist)
                            cacheAction.call(keyValuePairs);
//                        queuePut.call(object);
                    })
                    .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<Boolean> dynamicDeleteAll(String url, Class dataClass, boolean persist) {
        return Observable.error(new Exception("cant delete all from cloud data store"));
    }

    @NonNull
    @Override
    public Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant search disk in cloud data store"));
    }

    @NonNull
    @Override
    public Observable<List> searchDisk(RealmQuery query, Class domainClass) {
        return Observable.error(new Exception("cant search disk in cloud data store"));
    }

    @NonNull
    @Override
    public Observable<?> dynamicDownloadFile(String url, @NonNull File file, boolean onWifi, boolean whileCharging) {
        return Observable.defer(() -> {
            mIsOnWifi = Utils.isOnWifi();
            if (isEligibleForPersistenceIfNetworkNotAvailable() && mIsOnWifi == onWifi
                    && Utils.isChargingReqCompatible(mIsCharging, whileCharging)) {
                queueIOFile(url, file, onWifi, whileCharging, true, mContext);
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
                                        Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
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

    private <T> Observable.Transformer<T, T> applyExponentialBackoff() {
        return observable -> observable.retryWhen(attempts -> {
            ConnectionQuality cq = ConnectionClassManager.getInstance().getCurrentBandwidthQuality();
            if (cq.compareTo(ConnectionQuality.MODERATE) >= 0)
                return attempts.zipWith(Observable.range(Constants.COUNTER_START,
                        Constants.ATTEMPTS), (n, i) -> i)
                        .flatMap(i -> {
                            Log.d(TAG, "delay retry by " + i + " second(s)");
                            return Observable.timer(i, TimeUnit.SECONDS);
                        });
            else return null;
        });
    }

    @Nullable
    private static String getMimeType(String uri) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri);
        if (extension != null)
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return type;
    }

    boolean isGooglePlayServicesAvailable() {
        return getGoogleApiInstance() != null && getGooglePlayServicesAvailable() == ConnectionResult.SUCCESS;
    }

    private int getGooglePlayServicesAvailable() {
        return getGoogleApiInstance().isGooglePlayServicesAvailable(mContext);
    }

    private GoogleApiAvailability getGoogleApiInstance() {
        return mGoogleApiAvailability;
    }

    void setGoogleApiAvailability(GoogleApiAvailability googleApiAvailability) {
        mGoogleApiAvailability = googleApiAvailability;
    }

    void setHasLollipop(boolean hasLollipop) {
        mHasLollipop = hasLollipop;
    }

    GoogleApiAvailability getGoogleApiAvailability() {
        return mGoogleApiAvailability;
    }

    boolean isHasLollipop() {
        return mHasLollipop;
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
                //since file object save is not supported by Realm.
                return;
            Object mappedObject = null;
            try {
                //we need to check object is not instance of JsonArray,Map since
                //if we pass on to this method, unexpected and unwanted results are produced.
                //we need to skip if object is instance of JsonArray,Map
                if (!(object instanceof JSONArray) && !(object instanceof Map))
                    mappedObject = mEntityDataMapper.transformToRealm(object, mDataClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Observable<?> observable = null;
            if (mappedObject instanceof RealmObject)
                observable = mRealmManager.put((RealmObject) mappedObject, mDataClass);
            else if (mappedObject instanceof RealmModel)
                observable = mRealmManager.put((RealmModel) mappedObject, mDataClass);
            else
                try {
                    if ((object instanceof JSONArray)) {
                        observable = mRealmManager.putAll((JSONArray) object, mIdColumnName, mDataClass);
                    } else if (object instanceof List) {
                        mRealmManager.putAll((List<RealmObject>) mEntityDataMapper.transformAllToRealm((List) object, mDataClass), mDataClass);
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
                        observable = mRealmManager.put(jsonObject, mIdColumnName, mDataClass);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    observable = Observable.error(e);
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
            List<RealmObject> realmObjectCollection = new ArrayList<>();
            realmObjectCollection.addAll(mEntityDataMapper.transformAllToRealm(collection, mDataClass));
            mRealmManager.putAll(realmObjectCollection, mDataClass);
        }
    }

    private final class DeleteCollectionGenericsFromDBAction implements Action1<List> {

        private Class mDataClass;

        DeleteCollectionGenericsFromDBAction(Class dataClass) {
            mDataClass = dataClass;
        }

        @Override
        public void call(List collection) {
            List<RealmObject> realmObjectList = new ArrayList<>();
            realmObjectList.addAll(mEntityDataMapper.transformAllToRealm(collection, mDataClass));
            for (RealmObject realmObject : realmObjectList)
                mRealmManager.evict(realmObject, mDataClass);
        }
    }

    private final class DeleteAllGenericsFromDBAction implements Action1<List> {

        private Class mDataClass;

        public DeleteAllGenericsFromDBAction(Class dataClass) {
            mDataClass = dataClass;
        }

        @Override
        public void call(@NonNull List collection) {
            mRealmManager.evictAll(mDataClass)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(Boolean object) {
                            Log.d(TAG, collection.getClass().getName() + "s deleted!");
                        }
                    });
        }
    }

    private boolean isEligibleForPersistenceIfNetworkNotAvailable() {
        return !Utils.isNetworkAvailable(mContext) && (mHasLollipop || isGooglePlayServicesAvailable());
    }

    private boolean isEligibleForThrowErrorIfNetworkNotAvailable() {
        return !Utils.isNetworkAvailable(mContext) && !(mHasLollipop || isGooglePlayServicesAvailable());
    }

    private boolean queueIOFile(String url, File file, boolean onWifi, boolean whileCharging, boolean isDownload, Context context) {
        FileIORequest fileIORequest = new FileIORequest.UploadRequestBuilder(url, file)
                .onWifi(onWifi)
                .whileCharging(whileCharging)
                .build();
        if (isGooglePlayServicesAvailable()) {
            Bundle extras = new Bundle();
            extras.putString(GenericNetworkQueueIntentService.JOB_TYPE, isDownload ? DOWNLOAD_FILE : UPLOAD_FILE);
            extras.putString(GenericNetworkQueueIntentService.PAYLOAD, new Gson().toJson(fileIORequest));
            mGcmNetworkManager
                    .schedule(new OneoffTask.Builder()
                            .setService(GenericGCMService.class)
                            .setRequiredNetwork(onWifi ? OneoffTask.NETWORK_STATE_UNMETERED : OneoffTask.NETWORK_STATE_CONNECTED)
                            .setRequiresCharging(whileCharging)
                            .setUpdateCurrent(false)
                            .setPersisted(true)
                            .setExtras(extras)
                            .setTag(Constants.FILE_IO_TAG)
                            .setExecutionWindow(0, 30)
                            .build());
            Log.d(TAG, "QueuePost scheduled through GcmNetworkManager: " + true);
            return true;
        } else if (Utils.hasLollipop()) {
            PersistableBundle persistableBundle = new PersistableBundle();
            persistableBundle.putString(GenericNetworkQueueIntentService.JOB_TYPE, isDownload ? DOWNLOAD_FILE : UPLOAD_FILE);
            persistableBundle.putString(GenericNetworkQueueIntentService.PAYLOAD, new Gson().toJson(fileIORequest));
            boolean isScheduled = Utils.scheduleJob(context, new JobInfo.Builder(1,
                    new ComponentName(context, GenericJobService.class))
                    .setRequiredNetworkType(onWifi ? JobInfo.NETWORK_TYPE_UNMETERED : JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresCharging(whileCharging)
                    .setPersisted(true)
                    .setExtras(persistableBundle)
                    .build());
            Log.d(TAG, "QueuePost scheduled through JobScheduler: " + isScheduled);
            return isScheduled;
        }
        return false;
    }

    public boolean queuePost(PostRequest postRequest) {
        return queuePostCore(postRequest);
    }

    public boolean queuePost(String method, String url, String idColumnName, HashMap<String, Object> keyValuePairs,
                             Class dataClass, boolean persist) {
        return queuePostCore(new PostRequest.PostRequestBuilder(dataClass, persist)
                .idColumnName(idColumnName)
                .hashMap(keyValuePairs)
                .url(url)
                .method(method)
                .build());
    }

    public boolean queuePost(String method, String url, String idColumnName, JSONArray jsonArray,
                             Class dataClass, boolean persist) {
        return queuePostCore(new PostRequest.PostRequestBuilder(dataClass, persist)
                .idColumnName(idColumnName)
                .jsonArray(jsonArray)
                .url(url)
                .method(method)
                .build());
    }

    public boolean queuePost(String method, String url, String idColumnName, JSONObject jsonObject,
                             Class dataClass, boolean persist) {
        return queuePostCore(new PostRequest.PostRequestBuilder(dataClass, persist)
                .idColumnName(idColumnName)
                .jsonObject(jsonObject)
                .url(url)
                .method(method)
                .build());
    }

    private boolean queuePostCore(PostRequest postRequest) {
        if (isGooglePlayServicesAvailable()) {
            Bundle extras = new Bundle();
            extras.putString(GenericNetworkQueueIntentService.JOB_TYPE, GenericNetworkQueueIntentService.POST);
            extras.putString(GenericNetworkQueueIntentService.PAYLOAD, new Gson().toJson(postRequest));
            mGcmNetworkManager.schedule(new OneoffTask.Builder()
                    .setService(GenericGCMService.class)
                    .setRequiredNetwork(OneoffTask.NETWORK_STATE_CONNECTED)
                    .setRequiresCharging(false)
                    .setUpdateCurrent(false)
                    .setPersisted(true)
                    .setExtras(extras)
                    .setTag(Constants.POST_TAG)
                    .setExecutionWindow(0, 30)
                    .build());
            Log.d(TAG, "QueuePost scheduled through GcmNetworkManager: " + true);
            return true;
        } else if (Utils.hasLollipop()) {
            PersistableBundle persistableBundle = new PersistableBundle();
            persistableBundle.putString(GenericNetworkQueueIntentService.JOB_TYPE, GenericNetworkQueueIntentService.POST);
            persistableBundle.putString(GenericNetworkQueueIntentService.PAYLOAD, new Gson().toJson(postRequest));
            boolean isScheduled = Utils.scheduleJob(mContext, new JobInfo.Builder(1,
                    new ComponentName(mContext, GenericJobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .setExtras(persistableBundle)
                    .build());
            Log.d(TAG, "QueuePost scheduled through JobScheduler: " + isScheduled);
            return isScheduled;
        }
        return false;
    }
}
