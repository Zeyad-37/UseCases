package com.zeyad.usecases.api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.requests.FileIORequest;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;
import com.zeyad.usecases.stores.DataStoreFactory;
import com.zeyad.usecases.utils.ReplayingShare;
import com.zeyad.usecases.utils.Utils;

import org.json.JSONException;

import java.io.File;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.Single;

/**
 * @author by ZIaDo on 5/9/17.
 */
class DataService implements IDataService {

    private static final String CACHE_HIT = "cache Hit ", CACHE_MISS = "cache Miss ",
            GET_LIST_OFFLINE_FIRST = "getListOffLineFirst", GET_OBJECT_OFFLINE_FIRST = "getObjectOffLineFirst";
    private final DataStoreFactory mDataStoreFactory;
    private final Scheduler mPostExecutionThread;
    private final Scheduler mBackgroundThread;
    private final boolean mPostThreadExist;

    DataService(DataStoreFactory dataStoreFactory, Scheduler postExecutionThread, Scheduler backgroundThread) {
        mBackgroundThread = backgroundThread;
        mDataStoreFactory = dataStoreFactory;
        mPostExecutionThread = postExecutionThread;
        mPostThreadExist = mPostExecutionThread != null;
    }

    @Override
    public <M> Flowable<List<M>> getList(@NonNull GetRequest getListRequest) {
        Flowable<List<M>> result;
        try {
            Class dataClass = getListRequest.getDataClass();
            String url = getListRequest.getUrl();
            String simpleName = dataClass.getSimpleName();
            boolean shouldCache = getListRequest.isShouldCache();
            Flowable<List<M>> dynamicGetList = mDataStoreFactory.dynamically(url, dataClass)
                    .dynamicGetList(url, getListRequest.getIdColumnName(), dataClass,
                            getListRequest.isPersist(), shouldCache);
            if (Utils.getInstance().withCache(shouldCache)) {
                result = mDataStoreFactory.memory().<M>getAllItems(dataClass)
                        .doOnSuccess(m -> Log.d("getList", CACHE_HIT + simpleName))
                        .doOnError(throwable -> Log.d("getList", CACHE_MISS + simpleName))
                        .toFlowable()
                        .onErrorResumeNext(t -> dynamicGetList);
            } else {
                result = dynamicGetList;
            }
        } catch (IllegalAccessException e) {
            result = Flowable.error(e);
        }
        return result.compose(applySchedulers());
    }

    @Override
    public <M> Flowable<M> getObject(@NonNull GetRequest getRequest) {
        Flowable<M> result;
        try {
            Object itemId = getRequest.getItemId();
            Class dataClass = getRequest.getDataClass();
            boolean shouldCache = getRequest.isShouldCache();
            String url = getRequest.getUrl();
            String simpleName = dataClass.getSimpleName();
            Flowable<M> dynamicGetObject = mDataStoreFactory.dynamically(url, dataClass)
                                                            .dynamicGetObject(url, getRequest.getIdColumnName(), itemId, getRequest.getIdType(),
                                                                    dataClass, getRequest.isPersist(), shouldCache);
            if (Utils.getInstance().withCache(shouldCache)) {
                result = mDataStoreFactory.memory()
                        .<M> getItem(String.valueOf(itemId), dataClass)
                        .doOnSuccess(m -> Log.d("getObject", CACHE_HIT + simpleName))
                        .doOnError(throwable -> Log.d("getObject", CACHE_MISS + simpleName))
                        .toFlowable()
                        .onErrorResumeNext(t -> dynamicGetObject);
            } else {
                result = dynamicGetObject;
            }
        } catch (IllegalAccessException e) {
            result = Flowable.error(e);
        }
        return result.compose(applySchedulers());
    }

    @Override
    public <M> Flowable<M> patchObject(@NonNull PostRequest postRequest) {
        Flowable<M> result;
        try {
            result = mDataStoreFactory.dynamically(postRequest.getUrl(), postRequest.getRequestType())
                    .dynamicPatchObject(postRequest.getUrl(), postRequest.getIdColumnName(),
                            postRequest.getIdType(), postRequest.getObjectBundle(),
                            postRequest.getRequestType(), postRequest.getResponseType(),
                            postRequest.isPersist(), postRequest.isCache(), postRequest.isQueuable());
        } catch (IllegalAccessException | JSONException e) {
            result = Flowable.error(e);
        }
        return result.compose(applySchedulers());
    }

    @Override
    public <M> Flowable<M> postObject(@NonNull PostRequest postRequest) {
        Flowable<M> result;
        try {
            result = mDataStoreFactory.dynamically(postRequest.getUrl(), postRequest.getRequestType())
                    .dynamicPostObject(postRequest.getUrl(), postRequest.getIdColumnName(),
                            postRequest.getIdType(), postRequest.getObjectBundle(),
                            postRequest.getRequestType(), postRequest.getResponseType(),
                            postRequest.isPersist(), postRequest.isCache(), postRequest.isQueuable());
        } catch (IllegalAccessException | JSONException e) {
            result = Flowable.error(e);
        }
        return result.compose(applySchedulers());
    }

    @Override
    public <M> Flowable<M> postList(@NonNull PostRequest postRequest) {
        Flowable<M> result;
        try {
            result = mDataStoreFactory.dynamically(postRequest.getUrl(), postRequest.getRequestType())
                    .dynamicPostList(postRequest.getUrl(), postRequest.getIdColumnName(),
                            postRequest.getIdType(), postRequest.getArrayBundle(),
                            postRequest.getRequestType(), postRequest.getResponseType(),
                            postRequest.isPersist(), postRequest.isCache(), postRequest.isQueuable());
        } catch (IllegalAccessException | JSONException e) {
            result = Flowable.error(e);
        }
        return result.compose(applySchedulers());
    }

    @Override
    public <M> Flowable<M> putObject(@NonNull PostRequest postRequest) {
        Flowable<M> result;
        try {
            result = mDataStoreFactory.dynamically(postRequest.getUrl(), postRequest.getRequestType())
                    .dynamicPutObject(postRequest.getUrl(), postRequest.getIdColumnName(),
                            postRequest.getIdType(), postRequest.getObjectBundle(),
                            postRequest.getRequestType(), postRequest.getResponseType(),
                            postRequest.isPersist(), postRequest.isCache(), postRequest.isQueuable());
        } catch (IllegalAccessException | JSONException e) {
            result = Flowable.error(e);
        }
        return result.compose(applySchedulers());
    }

    @Override
    public <M> Flowable<M> putList(@NonNull PostRequest postRequest) {
        Flowable<M> result;
        try {
            result = mDataStoreFactory.dynamically(postRequest.getUrl(), postRequest.getRequestType())
                    .dynamicPutList(postRequest.getUrl(), postRequest.getIdColumnName(),
                            postRequest.getIdType(), postRequest.getArrayBundle(),
                            postRequest.getRequestType(), postRequest.getResponseType(),
                            postRequest.isPersist(), postRequest.isCache(), postRequest.isQueuable());
        } catch (IllegalAccessException | JSONException e) {
            result = Flowable.error(e);
        }
        return result.compose(applySchedulers());
    }

    @Override
    public <M> Flowable<M> deleteItemById(@NonNull PostRequest request) {
        PostRequest.Builder builder = new PostRequest
                .Builder(request.getRequestType(), request.isPersist())
                .payLoad(Collections.singletonList(request.getObject()))
                .idColumnName(request.getIdColumnName(), request.getIdType())
                .responseType(request.getResponseType())
                .fullUrl(request.getUrl());
        //        if (request.isQueuable()) {
        //            builder.queuable(request.isOnWifi(), request.isWhileCharging());
        //        }
        return deleteCollectionByIds(builder.build());
    }

    @Override
    public <M> Flowable<M> deleteCollectionByIds(@NonNull PostRequest deleteRequest) {
        Flowable<M> result;
        try {
            result = mDataStoreFactory.dynamically(deleteRequest.getUrl(), deleteRequest.getRequestType())
                    .<M>dynamicDeleteCollection(deleteRequest.getUrl(), deleteRequest.getIdColumnName(),
                            deleteRequest.getIdType(), deleteRequest.getArrayBundle(),
                            deleteRequest.getRequestType(), deleteRequest.getResponseType(),
                            deleteRequest.isPersist(), deleteRequest.isCache(), deleteRequest.isQueuable())
                    .compose(applySchedulers());
        } catch (IllegalAccessException | JSONException e) {
            result = Flowable.error(e);
        }
        return result.compose(applySchedulers());
    }

    @Override
    public Single<Boolean> deleteAll(@NonNull PostRequest deleteRequest) {
        Single<Boolean> result;
        try {
            result = mDataStoreFactory.disk(deleteRequest.getRequestType())
                    .dynamicDeleteAll(deleteRequest.getRequestType());
        } catch (IllegalAccessException e) {
            result = Single.error(e);
        }
        return result.compose(upstream -> mPostThreadExist ? upstream.subscribeOn(mBackgroundThread)
                .observeOn(mPostExecutionThread).unsubscribeOn(mBackgroundThread) :
                upstream.subscribeOn(mBackgroundThread).unsubscribeOn(mBackgroundThread));
    }

    @Override
    public <M> Flowable<List<M>> queryDisk(RealmQueryProvider realmQueryProvider) {
        Flowable<List<M>> result;
        try {
            result = mDataStoreFactory.disk(Object.class).<M> queryDisk(realmQueryProvider)
                    .compose(ReplayingShare.instance());
        } catch (IllegalAccessException e) {
            result = Flowable.error(e);
        }
        return result.compose(applySchedulers());
    }

    @Override
    public <M> Flowable<List<M>> getListOffLineFirst(@NonNull GetRequest getRequest) {
        Flowable<List<M>> result;
        try {
            Utils utils = Utils.getInstance();
            Class dataClass = getRequest.getDataClass();
            String simpleName = dataClass.getSimpleName();
            String idColumnName = getRequest.getIdColumnName();
            boolean persist = getRequest.isPersist();
            boolean shouldCache = getRequest.isShouldCache();
            boolean withDisk = utils.withDisk(persist);
            boolean withCache = utils.withCache(shouldCache);
            Flowable<List<M>> cloud = mDataStoreFactory.cloud(dataClass)
                                                       .dynamicGetList(getRequest.getUrl(), idColumnName, dataClass, persist, shouldCache);
            Flowable<List<M>> disk = mDataStoreFactory.disk(dataClass)
                    .<M> dynamicGetList("", idColumnName, dataClass, persist, shouldCache)
                    .doOnNext(m -> Log.d(GET_LIST_OFFLINE_FIRST, "Disk Hit " + simpleName))
                    .doOnError(throwable -> Log.e(GET_LIST_OFFLINE_FIRST, "Disk Miss " + simpleName,
                            throwable))
                    .flatMap(m -> m.isEmpty() ? cloud : Flowable.just(m))
                    .onErrorResumeNext(t -> cloud);
            Flowable<List<M>> memory = mDataStoreFactory.memory().<M> getAllItems(dataClass)
                    .doOnSuccess(m -> Log.d(GET_LIST_OFFLINE_FIRST, CACHE_HIT + simpleName))
                    .doOnError(throwable -> Log.d(GET_LIST_OFFLINE_FIRST, CACHE_MISS + simpleName))
                    .toFlowable()
                    .onErrorResumeNext(throwable -> withDisk ? disk : cloud);
            if (withCache) {
                result = memory;
            } else if (withDisk) {
                result = disk;
            } else {
                result = cloud;
            }
        } catch (IllegalAccessException e) {
            result = Flowable.error(e);
        }
        return result.compose(ReplayingShare.instance()).compose(applySchedulers());
    }

    @Override
    public <M> Flowable<M> getObjectOffLineFirst(@NonNull GetRequest getRequest) {
        Flowable<M> result;
        try {
            Utils utils = Utils.getInstance();
            Object itemId = getRequest.getItemId();
            Class dataClass = getRequest.getDataClass();
            Class idType = getRequest.getIdType();
            String idColumnName = getRequest.getIdColumnName();
            String simpleName = dataClass.getSimpleName();
            boolean persist = getRequest.isPersist();
            boolean shouldCache = getRequest.isShouldCache();
            boolean withDisk = utils.withDisk(persist);
            boolean withCache = utils.withCache(shouldCache);
            Flowable<M> cloud = mDataStoreFactory.cloud(dataClass)
                    .<M> dynamicGetObject(getRequest.getUrl(), idColumnName, itemId, idType, dataClass,
                            persist, shouldCache)
                    .doOnNext(m -> Log.d(GET_OBJECT_OFFLINE_FIRST, "Cloud Hit " + simpleName));
            Flowable<M> disk = mDataStoreFactory.disk(dataClass)
                    .<M> dynamicGetObject("", idColumnName, itemId, idType, dataClass, persist, shouldCache)
                    .doOnNext(m -> Log.d(GET_OBJECT_OFFLINE_FIRST, "Disk Hit " + simpleName))
                    .doOnError(throwable -> Log.e(GET_OBJECT_OFFLINE_FIRST, "Disk Miss " + simpleName,
                            throwable))
                    .onErrorResumeNext(t -> cloud);
            Flowable<M> memory = mDataStoreFactory.memory()
                    .<M> getItem(String.valueOf(itemId), dataClass)
                    .doOnSuccess(m -> Log.d(GET_OBJECT_OFFLINE_FIRST, CACHE_HIT + simpleName))
                    .doOnError(throwable -> Log.d(GET_OBJECT_OFFLINE_FIRST, CACHE_MISS + simpleName))
                    .toFlowable()
                    .onErrorResumeNext(t -> withDisk ? disk : cloud);
            if (withCache) {
                result = memory;
            } else if (withDisk) {
                result = disk;
            } else {
                result = cloud;
            }
        } catch (IllegalAccessException e) {
            result = Flowable.error(e);
        }
        return result.compose(ReplayingShare.instance()).compose(applySchedulers());
    }

    @Override
    public <M> Flowable<M> uploadFile(@NonNull FileIORequest fileIORequest) {
        return mDataStoreFactory.cloud(fileIORequest.getDataClass())
                .<M> dynamicUploadFile(fileIORequest.getUrl(), fileIORequest.getKeyFileMap(),
                        fileIORequest.getParameters(), fileIORequest.isOnWifi(),
                        fileIORequest.isWhileCharging(), fileIORequest.isQueuable(),
                        fileIORequest.getDataClass())
                .compose(applySchedulers());
    }

    @Override
    public Flowable<File> downloadFile(@NonNull FileIORequest fileIORequest) {
        return mDataStoreFactory.cloud(fileIORequest.getDataClass())
                    .dynamicDownloadFile(fileIORequest.getUrl(), fileIORequest.getFile(),
                            fileIORequest.isOnWifi(), fileIORequest.isWhileCharging(),
                            fileIORequest.isQueuable()).compose(applySchedulers());
    }

    /**
     * Apply the default android schedulers to a observable
     *
     * @param <M> the current observable
     * @return the transformed observable
     */
    @NonNull
    private <M> FlowableTransformer<M, M> applySchedulers() {
        return mPostThreadExist ? observable -> observable.subscribeOn(mBackgroundThread)
                .observeOn(mPostExecutionThread).unsubscribeOn(mBackgroundThread) :
                observable -> observable.subscribeOn(mBackgroundThread).unsubscribeOn(mBackgroundThread);
    }
}
