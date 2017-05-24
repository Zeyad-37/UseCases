package com.zeyad.usecases.api;

import android.support.annotation.NonNull;

import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.requests.FileIORequest;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;
import com.zeyad.usecases.stores.DataStoreFactory;
import com.zeyad.usecases.utils.ReplayingShare;

import java.io.File;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

/**
 * @author by ZIaDo on 5/9/17.
 */
class DataService implements IDataService {

    private static DataService sDataService;
    private final DataStoreFactory mDataStoreFactory;
    private final Scheduler mPostExecutionThread;
    private final Scheduler mBackgroundThread;
    private final boolean mPostThreadExist;

    DataService(DataStoreFactory dataStoreFactory, Scheduler postExecutionThread, Scheduler backgroundThread) {
        mBackgroundThread = backgroundThread;
        mDataStoreFactory = dataStoreFactory;
        mPostExecutionThread = postExecutionThread;
        sDataService = this;
        mPostThreadExist = mPostExecutionThread != null;
    }

    public static DataService getInstance() {
        if (sDataService == null)
            throw new NullPointerException("DataUseCase#initRealm must be called before calling getInstance()");
        return sDataService;
    }

    @Override
    public <M> Flowable<List<M>> getListRoom(Class dataClass) {
        return null;
//                new RoomManager(Config.getAppDatabase(), new DAOMapper()).getAll(dataClass)
//                .compose(applySchedulers());
    }

    @Override
    public <M> Completable putListRoom(List<M> items) {
        return null;

//        new RoomManager(Config.getAppDatabase(), new DAOMapper()).putAll(items)
//                .compose(mPostThreadExist ? completable -> completable.subscribeOn(mBackgroundThread)
//                        .observeOn(mPostExecutionThread)
//                        .unsubscribeOn(mBackgroundThread) : completable -> completable.subscribeOn(mBackgroundThread)
//                        .unsubscribeOn(mBackgroundThread));
    }

    @Override
    public <M> Flowable<List<M>> getList(@NonNull GetRequest getListRequest) {
        try {
            return mDataStoreFactory.dynamically(getListRequest.getUrl())
                    .<M>dynamicGetList(getListRequest.getUrl(), getListRequest.getDataClass(),
                            getListRequest.isPersist(), getListRequest.isShouldCache())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Flowable.error(e);
        }
    }

    @Override
    public <M> Flowable<M> getObject(@NonNull GetRequest getRequest) {
        try {
            return mDataStoreFactory.dynamically(getRequest.getUrl())
                    .<M>dynamicGetObject(getRequest.getUrl(), getRequest.getIdColumnName(),
                            getRequest.getItemId(), getRequest.getDataClass(), getRequest.isPersist(),
                            getRequest.isShouldCache())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Flowable.error(e);
        }
    }

    @Override
    public <M> Flowable<M> patchObject(@NonNull PostRequest postRequest) {
        try {
            return mDataStoreFactory.dynamically(postRequest.getUrl())
                    .<M>dynamicPatchObject(postRequest.getUrl(), postRequest.getIdColumnName(),
                            postRequest.getJsonObject(), postRequest.getDataClass(), postRequest.isPersist(),
                            postRequest.isQueuable())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Flowable.error(e);
        }
    }

    @Override
    public <M> Flowable<M> postObject(@NonNull PostRequest postRequest) {
        try {
            return mDataStoreFactory.dynamically(postRequest.getUrl())
                    .<M>dynamicPostObject(postRequest.getUrl(), postRequest.getIdColumnName(),
                            postRequest.getJsonObject(), postRequest.getDataClass(), postRequest.isPersist(),
                            postRequest.isQueuable())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Flowable.error(e);
        }
    }

    @Override
    public <M> Flowable<M> postList(@NonNull PostRequest postRequest) {
        try {
            return mDataStoreFactory.dynamically(postRequest.getUrl())
                    .<M>dynamicPostList(postRequest.getUrl(), postRequest.getIdColumnName(),
                            postRequest.getJsonArray(), postRequest.getDataClass(), postRequest.isPersist(),
                            postRequest.isQueuable())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Flowable.error(e);
        }
    }

    @Override
    public <M> Flowable<M> putObject(@NonNull PostRequest postRequest) {
        try {
            return mDataStoreFactory.dynamically(postRequest.getUrl())
                    .<M>dynamicPutObject(postRequest.getUrl(), postRequest.getIdColumnName(), postRequest.getJsonObject(),
                            postRequest.getDataClass(), postRequest.isPersist(), postRequest.isQueuable())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Flowable.error(e);
        }
    }

    @Override
    public <M> Flowable<M> putList(@NonNull PostRequest postRequest) {
        try {
            return mDataStoreFactory.dynamically(postRequest.getUrl())
                    .<M>dynamicPutList(postRequest.getUrl(), postRequest.getIdColumnName(),
                            postRequest.getJsonArray(), postRequest.getDataClass(), postRequest.isPersist(),
                            postRequest.isQueuable())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Flowable.error(e);
        }
    }

    @Override
    public <M> Flowable<M> deleteItemById(@NonNull PostRequest request) {
        PostRequest.Builder builder = new PostRequest.Builder(request.getDataClass(), request.isPersist())
                .payLoad(Collections.singleton((Long) request.getObject()))
                .queuable()
                .idColumnName(request.getIdColumnName())
                .fullUrl(request.getUrl());
        if (request.isQueuable())
            builder.queuable();
        return deleteCollectionByIds(builder.build());
    }

    @Override
    public <M> Flowable<M> deleteCollectionByIds(@NonNull PostRequest deleteRequest) {
        try {
            return mDataStoreFactory.dynamically(deleteRequest.getUrl())
                    .<M>dynamicDeleteCollection(deleteRequest.getUrl(), deleteRequest.getIdColumnName(),
                            deleteRequest.getJsonArray(), deleteRequest.getDataClass(),
                            deleteRequest.isPersist(), deleteRequest.isQueuable())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Flowable.error(e);
        }
    }

    @Override
    public Completable deleteAll(@NonNull PostRequest deleteRequest) {
        try {
            return mDataStoreFactory.disk().dynamicDeleteAll(deleteRequest.getDataClass())
                    .compose(mPostThreadExist ? completable -> completable.subscribeOn(mBackgroundThread)
                            .observeOn(mPostExecutionThread)
                            .unsubscribeOn(mBackgroundThread) : completable -> completable.subscribeOn(mBackgroundThread)
                            .unsubscribeOn(mBackgroundThread));
        } catch (IllegalAccessException e) {
            return Completable.error(e);
        }
    }

    @Override
    public <M> Flowable<List<M>> queryDisk(RealmQueryProvider realmQueryProvider) {
        try {
            return mDataStoreFactory.disk().<M>queryDisk(realmQueryProvider)
                    .compose(ReplayingShare.instance())
                    .compose(applySchedulers());
        } catch (IllegalAccessException e) {
            return Flowable.error(e);
        }
    }

    @Override
    public <M> Flowable<List<M>> getListOffLineFirst(@NonNull GetRequest getRequest) {
        try {
            Flowable<List<M>> online = mDataStoreFactory.cloud()
                    .dynamicGetList(getRequest.getUrl(), getRequest.getDataClass(),
                            getRequest.isPersist(), getRequest.isShouldCache());
            return mDataStoreFactory.disk()
                    .<M>dynamicGetList("", getRequest.getDataClass(),
                            getRequest.isPersist(), getRequest.isShouldCache())
                    .flatMap(new Function<List<M>, Flowable<List<M>>>() {
                        @Override
                        public Flowable<List<M>> apply(@io.reactivex.annotations.NonNull List<M> list) throws Exception {
                            if (list != null && !list.isEmpty())
                                return Flowable.just(list);
                            else return online;
                        }
                    })
                    .compose(ReplayingShare.instance())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Flowable.error(e);
        }
    }

    @Override
    public <M> Flowable<M> getObjectOffLineFirst(@NonNull GetRequest getRequest) {
        try {
            Flowable<M> online = mDataStoreFactory.cloud()
                    .dynamicGetObject(getRequest.getUrl(), getRequest.getIdColumnName(),
                            getRequest.getItemId(), getRequest.getDataClass(), getRequest.isPersist(),
                            getRequest.isShouldCache());
            return mDataStoreFactory.disk()
                    .<M>dynamicGetObject("", getRequest.getIdColumnName(), getRequest.getItemId(),
                            getRequest.getDataClass(), getRequest.isPersist(), getRequest.isShouldCache())
                    .flatMap(object -> object != null ? Flowable.just(object) : online)
                    .onErrorResumeNext(throwable -> online)
                    .compose(ReplayingShare.instance())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Flowable.error(e);
        }
    }

    @Override
    public <M> Flowable<M> uploadFile(@NonNull FileIORequest fileIORequest) {
        return mDataStoreFactory.cloud()
                .<M>dynamicUploadFile(fileIORequest.getUrl(), fileIORequest.getFile(), fileIORequest.getKey(),
                        fileIORequest.getParameters(), fileIORequest.onWifi(), fileIORequest.isWhileCharging(),
                        fileIORequest.isQueuable(), fileIORequest.getDataClass())
                .compose(applySchedulers());
    }

    @Override
    public Flowable<File> downloadFile(@NonNull FileIORequest fileIORequest) {
        return mDataStoreFactory.cloud()
                .dynamicDownloadFile(fileIORequest.getUrl(), fileIORequest.getFile(), fileIORequest.onWifi(),
                        fileIORequest.isWhileCharging(), fileIORequest.isQueuable())
                .compose(applySchedulers());
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
                .observeOn(mPostExecutionThread)
                .unsubscribeOn(mBackgroundThread) : observable -> observable.subscribeOn(mBackgroundThread)
                .unsubscribeOn(mBackgroundThread);
    }
}
