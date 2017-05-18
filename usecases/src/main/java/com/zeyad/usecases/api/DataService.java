package com.zeyad.usecases.api;

import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.requests.FileIORequest;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;
import com.zeyad.usecases.stores.DataStoreFactory;
import com.zeyad.usecases.utils.ReplayingShare;
import com.zeyad.usecases.utils.Utils;

import java.util.Collections;
import java.util.List;

import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;

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
    public <M> Observable<List<M>> getList(GetRequest getListRequest) {
        try {
            return mDataStoreFactory.dynamically(getListRequest.getUrl())
                    .<M>dynamicGetList(getListRequest.getUrl(), getListRequest.getDataClass(),
                            getListRequest.isPersist(), getListRequest.isShouldCache())
                    .compose(ReplayingShare.instance())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public <M> Observable<M> getObject(GetRequest getRequest) {
        try {
            return mDataStoreFactory.dynamically(getRequest.getUrl())
                    .<M>dynamicGetObject(getRequest.getUrl(), getRequest.getIdColumnName(),
                            getRequest.getItemId(), getRequest.getDataClass(), getRequest.isPersist(),
                            getRequest.isShouldCache())
                    .compose(ReplayingShare.instance())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable patchObject(PostRequest postRequest) {
        try {
            return mDataStoreFactory.dynamically(postRequest.getUrl())
                    .dynamicPatchObject(postRequest.getUrl(), postRequest.getIdColumnName(),
                            postRequest.getJsonObject(), postRequest.getDataClass(), postRequest.isPersist(),
                            postRequest.isQueuable())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable postObject(PostRequest postRequest) {
        try {
            return mDataStoreFactory.dynamically(postRequest.getUrl())
                    .dynamicPostObject(postRequest.getUrl(), postRequest.getIdColumnName(),
                            postRequest.getJsonObject(), postRequest.getDataClass(), postRequest.isPersist(),
                            postRequest.isQueuable())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable<?> postList(PostRequest postRequest) {
        try {
            return mDataStoreFactory.dynamically(postRequest.getUrl())
                    .dynamicPostList(postRequest.getUrl(), postRequest.getIdColumnName(),
                            postRequest.getJsonArray(), postRequest.getDataClass(), postRequest.isPersist(),
                            postRequest.isQueuable())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable putObject(PostRequest postRequest) {
        try {
            return mDataStoreFactory.dynamically(postRequest.getUrl())
                    .dynamicPutObject(postRequest.getUrl(), postRequest.getIdColumnName(), postRequest.getJsonObject(),
                            postRequest.getDataClass(), postRequest.isPersist(), postRequest.isQueuable())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable putList(PostRequest postRequest) {
        try {
            return mDataStoreFactory.dynamically(postRequest.getUrl())
                    .dynamicPutList(postRequest.getUrl(), postRequest.getIdColumnName(),
                            postRequest.getJsonArray(), postRequest.getDataClass(), postRequest.isPersist(),
                            postRequest.isQueuable())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable deleteItemById(PostRequest request) {
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
    public Observable deleteCollectionByIds(PostRequest deleteRequest) {
        try {
            return mDataStoreFactory.dynamically(deleteRequest.getUrl())
                    .dynamicDeleteCollection(deleteRequest.getUrl(), deleteRequest.getIdColumnName(),
                            deleteRequest.getJsonArray(), deleteRequest.getDataClass(),
                            deleteRequest.isPersist(), deleteRequest.isQueuable())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public Completable deleteAll(PostRequest deleteRequest) {
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
    public <M> Observable<List<M>> queryDisk(RealmQueryProvider realmQueryProvider) {
        try {
            return mDataStoreFactory.disk().<M>queryDisk(realmQueryProvider)
                    .compose(ReplayingShare.instance())
                    .compose(applySchedulers());
        } catch (IllegalAccessException e) {
            return Observable.error(e);
        }
    }

    @Override
    public <M> Observable<List<M>> getListOffLineFirst(GetRequest getRequest) {
        try {
            Observable<List<M>> online = mDataStoreFactory.cloud()
                    .dynamicGetList(getRequest.getUrl(), getRequest.getDataClass(),
                            getRequest.isPersist(), getRequest.isShouldCache());
            return mDataStoreFactory.disk()
                    .<M>dynamicGetList("", getRequest.getDataClass(),
                            getRequest.isPersist(), getRequest.isShouldCache())
                    .flatMap(new Func1<List<M>, Observable<List<M>>>() {
                        @Override
                        public Observable<List<M>> call(List<M> list) {
                            if (Utils.getInstance().isNotEmpty(list))
                                return Observable.just(list);
                            else return online;
                        }
                    })
                    .compose(ReplayingShare.instance())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public <M> Observable<M> getObjectOffLineFirst(GetRequest getRequest) {
        try {
            Observable<M> online = mDataStoreFactory.cloud()
                    .dynamicGetObject(getRequest.getUrl(), getRequest.getIdColumnName(),
                            getRequest.getItemId(), getRequest.getDataClass(), getRequest.isPersist(),
                            getRequest.isShouldCache());
            return mDataStoreFactory.disk()
                    .<M>dynamicGetObject("", getRequest.getIdColumnName(), getRequest.getItemId(),
                            getRequest.getDataClass(), getRequest.isPersist(), getRequest.isShouldCache())
                    .flatMap(object -> object != null ? Observable.just(object) : online)
                    .onErrorResumeNext(throwable -> online)
                    .compose(ReplayingShare.instance())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable uploadFile(FileIORequest fileIORequest) {
        return mDataStoreFactory.cloud()
                .dynamicUploadFile(fileIORequest.getUrl(), fileIORequest.getFile(), fileIORequest.getKey(),
                        fileIORequest.getParameters(), fileIORequest.onWifi(), fileIORequest.isWhileCharging(),
                        fileIORequest.isQueuable(), fileIORequest.getDataClass())
                .compose(applySchedulers());
    }

    @Override
    public Observable downloadFile(FileIORequest fileIORequest) {
        return mDataStoreFactory.cloud()
                .dynamicDownloadFile(fileIORequest.getUrl(), fileIORequest.getFile(), fileIORequest.onWifi(),
                        fileIORequest.isWhileCharging(), fileIORequest.isQueuable())
                .compose(applySchedulers());
    }

    /**
     * Apply the default android schedulers to a observable
     *
     * @param <T> the current observable
     * @return the transformed observable
     */
    private <T> Observable.Transformer<T, T> applySchedulers() {
        return mPostThreadExist ? observable -> observable.subscribeOn(mBackgroundThread)
                .observeOn(mPostExecutionThread)
                .unsubscribeOn(mBackgroundThread) : observable -> observable.subscribeOn(mBackgroundThread)
                .unsubscribeOn(mBackgroundThread);
    }
}
