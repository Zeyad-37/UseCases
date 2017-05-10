package com.zeyad.usecases.api;

import android.os.HandlerThread;

import com.zeyad.usecases.ReplayingShare;
import com.zeyad.usecases.db.RealmManager;
import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.executors.PostExecutionThread;
import com.zeyad.usecases.mapper.DAOMapper;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.requests.FileIORequest;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;
import com.zeyad.usecases.stores.DataStoreFactory;
import com.zeyad.usecases.utils.Utils;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * @author by ZIaDo on 5/9/17.
 */
class DataService implements IDataService {

    private static final String DEFAULT_ID_KEY = "id";
    private final DataStoreFactory mDataStoreFactory;
    private final PostExecutionThread mPostExecutionThread;
    private static HandlerThread handlerThread;
    private static DataService sDataService;

    DataService(ApiConnection apiConnection, DAOMapper daoMapper, PostExecutionThread postExecutionThread,
                HandlerThread thread, boolean withRealm) {
        handlerThread = thread;
        if (!handlerThread.isAlive())
            handlerThread.start();
        if (withRealm)
            mDataStoreFactory = new DataStoreFactory(new RealmManager(handlerThread.getLooper()),
                    apiConnection, daoMapper);
        else mDataStoreFactory = new DataStoreFactory(apiConnection, daoMapper);
        mPostExecutionThread = postExecutionThread;
        sDataService = this;
    }

    DataService(DataStoreFactory dataStoreFactory, PostExecutionThread postExecutionThread, HandlerThread thread) {
        handlerThread = thread;
        if (!handlerThread.isAlive())
            handlerThread.start();
        mDataStoreFactory = dataStoreFactory;
        mPostExecutionThread = postExecutionThread;
        sDataService = this;
    }

    public static DataService getInstance() {
        if (sDataService == null)
            throw new NullPointerException("DataUseCase#initRealm must be called before calling getInstance()");
        return sDataService;
    }

    @Override
    public Observable<List> getList(GetRequest getListRequest) {
        try {
            return mDataStoreFactory.dynamically(getListRequest.getUrl())
                    .dynamicGetList(getListRequest.getUrl(), getListRequest.getDataClass(),
                            getListRequest.isPersist(), getListRequest.isShouldCache())
                    .compose(ReplayingShare.instance())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable getObject(GetRequest getRequest) {
        try {
            return mDataStoreFactory.dynamically(getRequest.getUrl())
                    .dynamicGetObject(getRequest.getUrl(), getRequest.getIdColumnName(),
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
    public Observable deleteCollection(PostRequest deleteRequest) {
        try {
            return mDataStoreFactory.dynamically(deleteRequest.getUrl())
                    .dynamicDeleteCollection(deleteRequest.getUrl(), DEFAULT_ID_KEY,
                            deleteRequest.getJsonArray(), deleteRequest.getDataClass(),
                            deleteRequest.isPersist(), deleteRequest.isQueuable())
                    .compose(applySchedulers());
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable<Boolean> deleteAll(PostRequest deleteRequest) {
        try {
            return mDataStoreFactory.disk().dynamicDeleteAll(deleteRequest.getDataClass())
                    .compose(applySchedulers());
        } catch (IllegalAccessException e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable<List> queryDisk(RealmQueryProvider realmQueryProvider) {
        try {
            return mDataStoreFactory.disk().queryDisk(realmQueryProvider)
                    .compose(ReplayingShare.instance())
                    .compose(applySchedulers());
        } catch (IllegalAccessException e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable<List> getListOffLineFirst(GetRequest getRequest) {
        try {
            Observable<List> online = mDataStoreFactory.dynamically(getRequest.getUrl())
                    .dynamicGetList(getRequest.getUrl(), getRequest.getDataClass(),
                            getRequest.isPersist(), getRequest.isShouldCache());
            return mDataStoreFactory.disk()
                    .dynamicGetList("", getRequest.getDataClass(),
                            getRequest.isPersist(), getRequest.isShouldCache())
                    .flatMap(new Func1<List, Observable<List>>() {
                        @Override
                        public Observable<List> call(List list) {
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
    public Observable getObjectOffLineFirst(GetRequest getRequest) {
        try {
            Observable online = mDataStoreFactory.dynamically(getRequest.getUrl())
                    .dynamicGetObject(getRequest.getUrl(), getRequest.getIdColumnName(),
                            getRequest.getItemId(), getRequest.getDataClass(), getRequest.isPersist(),
                            getRequest.isShouldCache());
            return mDataStoreFactory.disk()
                    .dynamicGetObject("", getRequest.getIdColumnName(), getRequest.getItemId(),
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
        if (!handlerThread.isAlive())
            handlerThread.start();
        Scheduler backgroundThread = AndroidSchedulers.from(handlerThread.getLooper());
        return mPostExecutionThread != null ? observable -> observable.subscribeOn(backgroundThread)
                .observeOn(mPostExecutionThread.getScheduler())
                .unsubscribeOn(backgroundThread) : observable -> observable.subscribeOn(backgroundThread)
                .unsubscribeOn(backgroundThread);
    }
}
