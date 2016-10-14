package com.zeyad.genericusecase.data.repository.stores;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.ContentValues;
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
import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.R;
import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.db.RealmManager;
import com.zeyad.genericusecase.data.exceptions.NetworkConnectionException;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.network.RestApi;
import com.zeyad.genericusecase.data.services.GenericGCMService;
import com.zeyad.genericusecase.data.services.GenericJobService;
import com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService;
import com.zeyad.genericusecase.data.utils.ModelConverters;
import com.zeyad.genericusecase.data.utils.Utils;
import com.zeyad.genericusecase.domain.interactors.requests.FileIORequest;
import com.zeyad.genericusecase.domain.interactors.requests.PostRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
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

import static com.zeyad.genericusecase.Config.NONE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.DOWNLOAD_FILE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.UPLOAD_FILE;
import static com.zeyad.genericusecase.domain.interactors.requests.PostRequest.POST;

public class CloudDataStore implements DataStore {

    public static final String FILE_IO_TAG = "fileIOObject", POST_TAG = "postObject", APPLICATION_JSON = "application/json";
    private static final int COUNTER_START = 1, ATTEMPTS = 3;
    private static final String TAG = com.zeyad.genericusecase.data.repository.stores.CloudDataStore.class.getName();
    private final EntityMapper mEntityDataMapper;
    private final DataBaseManager mDataBaseManager;
    private final Context mContext;
    @NonNull
    private final Observable<Object> mErrorObservablePersisted, mErrorObservableNotPersisted, mQueueFileIO;
    private final RestApi mRestApi;
    private final GcmNetworkManager mGcmNetworkManager;
    private final boolean mCanPersist;
    private GoogleApiAvailability mGoogleApiAvailability;
    private boolean mHasLollipop;

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
        mGoogleApiAvailability = GoogleApiAvailability.getInstance();
        mErrorObservablePersisted = Observable.error(new NetworkConnectionException(mContext.getString(R.string.exception_network_error_persisted)));
        mErrorObservableNotPersisted = Observable.error(new NetworkConnectionException(mContext.getString(R.string.exception_network_error_not_persisted)));
        mQueueFileIO = Observable.empty();
        mGcmNetworkManager = GcmNetworkManager.getInstance(mContext);
        mHasLollipop = Utils.hasLollipop();
        mCanPersist = Config.getInstance().getDBType() > NONE;
    }

    @Nullable
    private static String getMimeType(String uri) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri);
        if (extension != null)
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return type;
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
    public Observable<?> dynamicPostObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                           Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            final SaveGenericToDBAction cacheAction = new SaveGenericToDBAction(dataClass, idColumnName);
            if (isEligibleForPersistenceIfNetworkNotAvailable()) {
                queuePost(POST, url, idColumnName, jsonObject, dataClass, persist);
                if (willPersist(persist))
                    cacheAction.call(jsonObject);
                return mErrorObservablePersisted;
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicPostObject(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    ModelConverters.convertToString(jsonObject)))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(object -> {
                        if (willPersist(persist))
                            cacheAction.call(object);
                    })
                    .doOnError(throwable -> {
                        if (throwable instanceof UnknownHostException || throwable instanceof ConnectException)
                            queuePost(POST, url, idColumnName, jsonObject, dataClass, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @Override
    public Observable<?> dynamicPostObject(String url, String idColumnName, ContentValues contentValues,
                                           Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            final SaveGenericToDBAction cacheAction = new SaveGenericToDBAction(dataClass, idColumnName);
            if (isEligibleForPersistenceIfNetworkNotAvailable()) {
                queuePost(POST, url, idColumnName, ModelConverters.contentValueToJSONObject(contentValues),
                        dataClass, persist);
                if (willPersist(persist))
                    cacheAction.call(contentValues);
                return mErrorObservablePersisted;
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicPostObject(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    ModelConverters.convertToString(ModelConverters.contentValueToJSONObject(contentValues))))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(object -> {
                        if (willPersist(persist))
                            cacheAction.call(object);
                    })
                    .doOnError(throwable -> {
                        JSONObject jsonObject = ModelConverters.contentValueToJSONObject(contentValues);
                        if (willPersist(persist))
                            new SaveGenericToDBAction(dataClass, idColumnName).call(jsonObject);
                        if (isNetworkFailure(throwable))
                            queuePost(POST, url, idColumnName, jsonObject, dataClass, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<List> dynamicPostList(String url, String idColumnName, @NonNull JSONArray jsonArray,
                                            Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            if (isEligibleForPersistenceIfNetworkNotAvailable()) {
                queuePost(POST, url, idColumnName, jsonArray, dataClass, persist);
                if (willPersist(persist))
                    new SaveGenericToDBAction(dataClass, idColumnName).call(jsonArray);
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.exception_network_error_persisted)));
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.exception_network_error_persisted)));
            else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.exception_network_error_not_persisted)));
            return mRestApi.dynamicPostList(url,
                    RequestBody.create(MediaType.parse(APPLICATION_JSON), jsonArray.toString()))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(list -> {
                        if (willPersist(persist))
                            new SaveGenericToDBAction(dataClass, idColumnName).call(list);
                    })
                    .doOnError(throwable -> {
                        if (willPersist(persist))
                            new SaveGenericToDBAction(dataClass, idColumnName).call(jsonArray);
                        if (isNetworkFailure(throwable))
                            queuePost(POST, url, idColumnName, jsonArray, dataClass, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel, domainClass));
        });
    }

    @Override
    public Observable<?> dynamicPostList(String url, String idColumnName, ContentValues[] contentValues,
                                         Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            if (isEligibleForPersistenceIfNetworkNotAvailable()) {
                queuePost(POST, url, idColumnName, ModelConverters.contentValuesToJSONArray(contentValues),
                        dataClass, persist);
                if (willPersist(persist))
                    new SaveGenericToDBAction(dataClass, idColumnName).call(contentValues);
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.exception_network_error_persisted)));
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.exception_network_error_persisted)));
            else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.exception_network_error_not_persisted)));
            return mRestApi.dynamicPostList(url,
                    RequestBody.create(MediaType.parse(APPLICATION_JSON), ModelConverters
                            .convertToString(ModelConverters.contentValuesToJSONArray(contentValues))))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(list -> {
                        if (willPersist(persist))
                            new SaveGenericToDBAction(dataClass, idColumnName).call(list);
                    })
                    .doOnError(throwable -> {
                        JSONArray jsonArray = ModelConverters.contentValuesToJSONArray(contentValues);
                        if (willPersist(persist))
                            new SaveGenericToDBAction(dataClass, idColumnName).call(jsonArray);
                        if (isNetworkFailure(throwable))
                            queuePost(POST, url, idColumnName, jsonArray, dataClass, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicDeleteCollection(final String url, String idColumnName,
                                                 @NonNull final JSONArray jsonArray,
                                                 Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            List<Long> ids = ModelConverters.convertToListOfId(jsonArray);
            if (isEligibleForPersistenceIfNetworkNotAvailable()) {
                queuePost(PostRequest.DELETE, url, idColumnName, jsonArray, dataClass, persist);
                if (willPersist(persist))
                    new DeleteCollectionGenericsFromDBAction(dataClass, idColumnName).call(ids);
                return mErrorObservablePersisted;
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
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
                            queuePost(PostRequest.DELETE, url, idColumnName, jsonArray, dataClass, persist);
                    });
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                          Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            final SaveGenericToDBAction cacheAction = new SaveGenericToDBAction(dataClass, idColumnName);
            if (isEligibleForPersistenceIfNetworkNotAvailable()) {
                queuePost(PostRequest.PUT, url, idColumnName, jsonObject, dataClass, persist);
                if (willPersist(persist))
                    cacheAction.call(jsonObject);
                return mErrorObservablePersisted;
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
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
                            queuePost(PostRequest.PUT, url, idColumnName, jsonObject, dataClass, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @Override
    public Observable<?> dynamicPutObject(String url, String idColumnName, ContentValues contentValues,
                                          Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            final SaveGenericToDBAction cacheAction = new SaveGenericToDBAction(dataClass, idColumnName);
            if (isEligibleForPersistenceIfNetworkNotAvailable()) {
                queuePost(PostRequest.PUT, url, idColumnName, ModelConverters.contentValueToJSONObject(contentValues),
                        dataClass, persist);
                if (willPersist(persist))
                    cacheAction.call(contentValues);
                return mErrorObservablePersisted;
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return mErrorObservableNotPersisted;
            return mRestApi.dynamicPutObject(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    ModelConverters.convertToString(ModelConverters.contentValueToJSONObject(contentValues))))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(object -> {
                        if (willPersist(persist))
                            cacheAction.call(object);
                    })
                    .doOnError(throwable -> {
                        JSONObject jsonObject = ModelConverters.contentValueToJSONObject(contentValues);
                        if (willPersist(persist))
                            new SaveGenericToDBAction(dataClass, idColumnName).call(jsonObject);
                        if (isNetworkFailure(throwable))
                            queuePost(PostRequest.PUT, url, idColumnName, jsonObject, dataClass, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicUploadFile(String url, @NonNull File file, boolean onWifi, boolean whileCharging,
                                           Class domainClass) {
        return Observable.defer(() -> {
            if (isEligibleForPersistenceIfNetworkNotAvailable() && Utils.isOnWifi() == onWifi
                    && Utils.isChargingReqCompatible(Utils.isCharging(), whileCharging)) {
                queueIOFile(url, file, true, whileCharging, false);
                return mQueueFileIO;
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return mErrorObservableNotPersisted;
            return mRestApi.upload(url, RequestBody.create(MediaType.parse(getMimeType(file.getPath())), file))
                    .doOnError(throwable -> {
                        throwable.printStackTrace();
                        queueIOFile(url, file, true, whileCharging, false);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutList(String url, String idColumnName, @NonNull JSONArray jsonArray,
                                        Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            final SaveGenericToDBAction cacheAction = new SaveGenericToDBAction(dataClass, idColumnName);
            if (isEligibleForPersistenceIfNetworkNotAvailable()) {
                queuePost(PostRequest.PUT, url, idColumnName, jsonArray, dataClass, persist);
                if (willPersist(persist))
                    cacheAction.call(jsonArray);
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.exception_network_error_persisted)));
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.exception_network_error_not_persisted)));
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
                            queuePost(PostRequest.PUT, url, idColumnName, jsonArray, dataClass, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel, domainClass));
        });
    }

    @Override
    public Observable<?> dynamicPutList(String url, String idColumnName, ContentValues[] contentValues,
                                        Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            final SaveGenericToDBAction cacheAction = new SaveGenericToDBAction(dataClass, idColumnName);
            if (isEligibleForPersistenceIfNetworkNotAvailable()) {
                queuePost(PostRequest.PUT, url, idColumnName, ModelConverters.contentValuesToJSONArray(contentValues),
                        dataClass, persist);
                if (willPersist(persist))
                    cacheAction.call(contentValues);
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.exception_network_error_persisted)));
            } else if (isEligibleForThrowErrorIfNetworkNotAvailable())
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.exception_network_error_not_persisted)));
            return mRestApi.dynamicPutList(url, RequestBody.create(MediaType.parse(APPLICATION_JSON),
                    ModelConverters.convertToString(ModelConverters.contentValuesToJSONArray(contentValues))))
                    //.compose(applyExponentialBackoff())
                    .doOnNext(list -> {
                        if (willPersist(persist))
                            cacheAction.call(list);
                    })
                    .doOnError(throwable -> {
                        JSONArray jsonArray = ModelConverters.contentValuesToJSONArray(contentValues);
                        if (willPersist(persist))
                            cacheAction.call(jsonArray);
                        if (isNetworkFailure(throwable))
                            queuePost(PostRequest.PUT, url, idColumnName, jsonArray, dataClass, persist);
                    })
                    .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel, domainClass));
        });
    }

    @NonNull
    @Override
    public Observable<?> dynamicDeleteAll(String url, Class dataClass, boolean persist) {
        return Observable.error(new Exception(mContext.getString(R.string.delete_all_error_cloud)));
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

    @NonNull
    @Override
    public Observable<?> dynamicDownloadFile(String url, @NonNull File file, boolean onWifi, boolean whileCharging) {
        return Observable.defer(() -> {
            if (isEligibleForPersistenceIfNetworkNotAvailable() && Utils.isOnWifi() == onWifi
                    && Utils.isChargingReqCompatible(Utils.isCharging(), whileCharging)) {
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

    private boolean isGooglePlayServicesAvailable() {
        return getGoogleApiInstance() != null && getGooglePlayServicesAvailable() == ConnectionResult.SUCCESS;
    }

    private int getGooglePlayServicesAvailable() {
        return getGoogleApiInstance().isGooglePlayServicesAvailable(mContext);
    }

    private GoogleApiAvailability getGoogleApiInstance() {
        return mGoogleApiAvailability;
    }

    GoogleApiAvailability getGoogleApiAvailability() {
        return mGoogleApiAvailability;
    }

    void setGoogleApiAvailability(GoogleApiAvailability googleApiAvailability) {
        mGoogleApiAvailability = googleApiAvailability;
    }

    boolean isHasLollipop() {
        return mHasLollipop;
    }

    void setHasLollipop(boolean hasLollipop) {
        mHasLollipop = hasLollipop;
    }

    private boolean isEligibleForPersistenceIfNetworkNotAvailable() {
        return !Utils.isNetworkAvailable(mContext) && (mHasLollipop || isGooglePlayServicesAvailable());
    }

    private boolean isEligibleForThrowErrorIfNetworkNotAvailable() {
        return !Utils.isNetworkAvailable(mContext) && !(mHasLollipop || isGooglePlayServicesAvailable());
    }

    private boolean queueIOFile(String url, File file, boolean onWifi, boolean whileCharging, boolean isDownload) {
        FileIORequest fileIORequest = new FileIORequest.UploadRequestBuilder(url, file)
                .onWifi(onWifi)
                .whileCharging(whileCharging)
                .build();
        if (isGooglePlayServicesAvailable()) {
            Bundle extras = new Bundle();
            extras.putString(GenericNetworkQueueIntentService.JOB_TYPE, isDownload ? DOWNLOAD_FILE : UPLOAD_FILE);
            extras.putString(GenericNetworkQueueIntentService.PAYLOAD, new Gson().toJson(fileIORequest));
            mGcmNetworkManager.schedule(new OneoffTask.Builder()
                    .setService(GenericGCMService.class)
                    .setRequiredNetwork(onWifi ? OneoffTask.NETWORK_STATE_UNMETERED : OneoffTask.NETWORK_STATE_CONNECTED)
                    .setRequiresCharging(whileCharging)
                    .setUpdateCurrent(false)
                    .setPersisted(true)
                    .setExtras(extras)
                    .setTag(FILE_IO_TAG)
                    .setExecutionWindow(0, 30)
                    .build());
            Log.d(TAG, mContext.getString(R.string.queued, "GCM Network Manager", String.valueOf(true)));
            return true;
        } else if (Utils.hasLollipop()) {
            PersistableBundle persistableBundle = new PersistableBundle();
            persistableBundle.putString(GenericNetworkQueueIntentService.JOB_TYPE, isDownload ? DOWNLOAD_FILE : UPLOAD_FILE);
            persistableBundle.putString(GenericNetworkQueueIntentService.PAYLOAD, new Gson().toJson(fileIORequest));
            boolean isScheduled = Utils.scheduleJob(mContext, new JobInfo.Builder(1,
                    new ComponentName(mContext, GenericJobService.class))
                    .setRequiredNetworkType(onWifi ? JobInfo.NETWORK_TYPE_UNMETERED : JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresCharging(whileCharging)
                    .setPersisted(true)
                    .setExtras(persistableBundle)
                    .build());
            Log.d(TAG, mContext.getString(R.string.queued, "JobScheduler", String.valueOf(isScheduled)));
            return isScheduled;
        }
        return false;
    }

    private boolean queuePost(String method, String url, String idColumnName, JSONArray jsonArray,
                              Class dataClass, boolean persist) {
        return queuePostCore(new PostRequest.PostRequestBuilder(dataClass, persist)
                .idColumnName(idColumnName)
                .jsonArray(jsonArray)
                .url(url)
                .method(method)
                .build());
    }

    private boolean queuePost(String method, String url, String idColumnName, JSONObject jsonObject,
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
                    .setTag(POST_TAG)
                    .setExecutionWindow(0, 30)
                    .build());
            Log.d(TAG, mContext.getString(R.string.queued, "GCM Network Manager", String.valueOf(true)));
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
            Log.d(TAG, mContext.getString(R.string.queued, "JobScheduler", String.valueOf(isScheduled)));
            return isScheduled;
        }
        return false;
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
            Observable<?> observable = null;
            if (mDataBaseManager instanceof RealmManager) {
                try {
                    //we need to check object is not instance of JsonArray,Map since
                    //if we pass on to this method, unexpected and unwanted results are produced.
                    //we need to skip if object is instance of JsonArray,Map
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
                        if ((object instanceof JSONArray)) {
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
            } else if (object instanceof ContentValues)
                observable = mDataBaseManager.put((ContentValues) object, mDataClass);
            else if (object instanceof ContentValues[])
                observable = mDataBaseManager.putAll((ContentValues[]) object, mDataClass);
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
            mDataBaseManager.putAll(mEntityDataMapper.transformAllToRealm(collection, mDataClass), mDataClass);
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

    private final class DeleteAllGenericsFromDBAction implements Action1<List> {

        private Class mDataClass;

        public DeleteAllGenericsFromDBAction(Class dataClass) {
            mDataClass = dataClass;
        }

        @Override
        public void call(@NonNull List collection) {
            mDataBaseManager.evictAll(mDataClass)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Object>() {
                        @Override
                        public void onCompleted() {
                            unsubscribe();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Object object) {
                            Log.d(TAG, collection.getClass().getName() + "s deleted!");
                        }
                    });
        }
    }
}
