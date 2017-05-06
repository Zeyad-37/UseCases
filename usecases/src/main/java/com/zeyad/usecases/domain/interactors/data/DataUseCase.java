package com.zeyad.usecases.domain.interactors.data;

import android.os.HandlerThread;

import com.zeyad.usecases.data.db.DatabaseManagerFactory;
import com.zeyad.usecases.data.mappers.IDAOMapperFactory;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.repository.DataRepository;
import com.zeyad.usecases.data.repository.stores.DataStoreFactory;
import com.zeyad.usecases.data.requests.FileIORequest;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.data.utils.Utils;
import com.zeyad.usecases.domain.executors.PostExecutionThread;
import com.zeyad.usecases.domain.executors.UIThread;
import com.zeyad.usecases.domain.repository.Data;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

/**
 * This class is a general implementation that represents a use case for retrieving data.
 */
public class DataUseCase implements IDataUseCase {

    private final static BehaviorSubject ObjectOffLineFirst = BehaviorSubject.create();
    private final static BehaviorSubject<List> listOffLineFirst = BehaviorSubject.create();
    private static boolean hasRealm;
    private static HandlerThread handlerThread;
    private static DataUseCase sDataUseCase;
    private final Data mData;
    private final PostExecutionThread mPostExecutionThread;

    private DataUseCase(Data data, PostExecutionThread postExecutionThread, HandlerThread thread) {
        mPostExecutionThread = postExecutionThread;
        mData = data;
    }

    /**
     * This function should be called at-least once before calling getInstance() method
     * This function should not be called multiple times, but only when required.
     * Ideally this function should be called once when application  is started or created.
     * This function may be called n number of times if required, during mocking and testing.
     */
    static void initWithoutDB(IDAOMapperFactory entityMapper, PostExecutionThread postExecutionThread,
                              HandlerThread thread) {
        hasRealm = false;
        handlerThread = thread;
        if (!handlerThread.isAlive())
            handlerThread.start();
        sDataUseCase = new DataUseCase(new DataRepository(new DataStoreFactory(RestApiImpl.getInstance()),
                entityMapper), postExecutionThread, thread);
    }

    /**
     * This function should be called at-least once before calling getInstance() method
     * This function should not be called multiple times, but only when required.
     * Ideally this function should be called once when application  is started or created.
     * This function may be called n number of times if required, during mocking and testing.
     */
    static void initWithRealm(IDAOMapperFactory entityMapper, PostExecutionThread postExecutionThread,
                              HandlerThread thread) {
        hasRealm = true;
        handlerThread = thread;
        if (!handlerThread.isAlive())
            handlerThread.start();
        DatabaseManagerFactory.initRealm(handlerThread.getLooper());
        sDataUseCase = new DataUseCase(new DataRepository(new DataStoreFactory(DatabaseManagerFactory
                .getInstance(), RestApiImpl.getInstance()), entityMapper), postExecutionThread, thread);
    }

    /**
     * Testing only!
     * This function should be called at-least once before calling getInstance() method
     * This function should not be called multiple times, but only when required.
     * Ideally this function should be called once when application  is started or created.
     * This function may be called n number of times if required, during mocking and testing.
     *
     * @param dataRepository data repository
     * @param uiThread       ui thread implementation
     * @param handlerThread  background thread
     */
    public static void init(DataRepository dataRepository, UIThread uiThread, HandlerThread handlerThread) {
        sDataUseCase = new DataUseCase(dataRepository, uiThread, handlerThread);
    }

    public static DataUseCase getInstance() {
        if (sDataUseCase == null)
            throw new NullPointerException("DataUseCase#initRealm must be called before calling getInstance()");
        return sDataUseCase;
    }

    public static HandlerThread getHandlerThread() {
        if (handlerThread == null)
            handlerThread = new HandlerThread("backgroundThread");
        return handlerThread;
    }

    /**
     * @return returns database type, whether realm or none.
     */
    public static boolean hasRealm() {
        return hasRealm;
    }

    public static void setHasRealm(boolean value) {
        hasRealm = value;
    }

    /**
     * Executes the current use case.
     *
     * @param genericUseCaseRequest The guy who will be listen to the observable build with .
     */
    @Override
    @SuppressWarnings("unchecked")
    public Observable<List> getList(GetRequest genericUseCaseRequest) {
        return mData.getListDynamically(genericUseCaseRequest.getUrl(), genericUseCaseRequest
                .getPresentationClass(), genericUseCaseRequest.getDataClass(), genericUseCaseRequest
                .isPersist(), genericUseCaseRequest.isShouldCache())
                .compose(applySchedulers())
                .flatMap(Observable::just);
    }

    /**
     * Executes the current use case.
     *
     * @param getRequest The guy who will be listen to the observable build with .
     */
    @Override
    @SuppressWarnings("unchecked")
    public Observable getObject(GetRequest getRequest) {
        return mData.getObjectDynamicallyById(getRequest.getUrl(), getRequest.getIdColumnName(),
                getRequest.getItemId(), getRequest.getPresentationClass(), getRequest.getDataClass(),
                getRequest.isPersist(), getRequest.isShouldCache())
                .compose(applySchedulers())
                .flatMap(Observable::just);
    }

    @Override
    public Observable patchObject(PostRequest postRequest) {
        return mData.dynamicPatchObject(postRequest.getUrl(), postRequest.getIdColumnName(),
                postRequest.getObjectBundle(), postRequest.getPresentationClass(), postRequest
                        .getDataClass(), postRequest.isPersist(), postRequest.isQueuable())
                .compose(applySchedulers());
    }

    @Override
    public Observable postObject(PostRequest postRequest) {
        return mData.postObjectDynamically(postRequest.getUrl(), postRequest.getIdColumnName(),
                postRequest.getObjectBundle(), postRequest.getPresentationClass(), postRequest
                        .getDataClass(), postRequest.isPersist(), postRequest.isQueuable())
                .compose(applySchedulers());
    }

    @Override
    public Observable postList(PostRequest postRequest) {
        return mData.postListDynamically(postRequest.getUrl(), postRequest.getIdColumnName(),
                postRequest.getArrayBundle(), postRequest.getPresentationClass(), postRequest.getDataClass(),
                postRequest.isPersist(), postRequest.isQueuable())
                .compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     *
     * @param postRequest The guy who will be listen to the observable build with .
     */
    @Override
    public Observable putObject(PostRequest postRequest) {
        return mData.putObjectDynamically(postRequest.getUrl(), postRequest.getIdColumnName(),
                postRequest.getObjectBundle(), postRequest.getPresentationClass(),
                postRequest.getDataClass(), postRequest.isPersist(), postRequest.isQueuable())
                .compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     */
    @Override
    public Observable putList(PostRequest postRequest) {
        return mData.putListDynamically(postRequest.getUrl(), postRequest.getIdColumnName(),
                postRequest.getArrayBundle(), postRequest.getPresentationClass(), postRequest.getDataClass(),
                postRequest.isPersist(), postRequest.isQueuable()).compose(applySchedulers());
    }

    @Override
    public Observable deleteCollection(PostRequest deleteRequest) {
        return mData.deleteListDynamically(deleteRequest.getUrl(), deleteRequest.getArrayBundle(),
                deleteRequest.getPresentationClass(), deleteRequest.getDataClass(), deleteRequest.isPersist(),
                deleteRequest.isQueuable())
                .compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     *
     * @param postRequest The guy who will be listen to the observable build with .
     */
    @Override
    public Observable<Boolean> deleteAll(PostRequest postRequest) {
        return mData.deleteAllDynamically(postRequest.getUrl(), postRequest.getDataClass(),
                postRequest.isPersist()).compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Observable<List> queryDisk(GetRequest getRequest) {
        return mData.queryDisk(getRequest.getQueryFactory(), getRequest.getPresentationClass())
                .flatMap(Observable::just)
                .compose(applySchedulers());
    }

    @Override
    public Observable<List> getListOffLineFirst(GetRequest getRequest) {
        Observable<List> online = mData.getListDynamically(getRequest.getUrl(), getRequest.getPresentationClass(),
                getRequest.getDataClass(), getRequest.isPersist(), getRequest.isShouldCache());
        mData.getListDynamically("", getRequest.getPresentationClass(), getRequest.getDataClass(),
                getRequest.isPersist(), getRequest.isShouldCache())
                .flatMap(new Func1<List, Observable<List>>() {
                    @Override
                    public Observable<List> call(List list) {
                        if (Utils.getInstance().isNotEmpty(list))
                            return Observable.just(list);
                        else return online;
                    }
                })
                .onErrorResumeNext(throwable -> online)
                .doOnNext(listOffLineFirst::onNext)
                .doOnError(listOffLineFirst::onError)
                .compose(applySchedulers())
                .subscribe(o -> {
                }, OnErrorNotImplementedException::new);
        return listOffLineFirst.compose(applySchedulers());
    }

    @Override
    public Observable<?> getObjectOffLineFirst(GetRequest getRequest) {
        Observable<?> online = mData.getObjectDynamicallyById(getRequest.getUrl(), getRequest
                        .getIdColumnName(), getRequest.getItemId(), getRequest.getPresentationClass(),
                getRequest.getDataClass(), getRequest.isPersist(), getRequest.isShouldCache());
        mData.getObjectDynamicallyById("", getRequest.getIdColumnName(), getRequest.getItemId(),
                getRequest.getPresentationClass(), getRequest.getDataClass(), getRequest.isPersist(),
                getRequest.isShouldCache())
                .flatMap(object -> object != null ? Observable.just(object) : online)
                .onErrorResumeNext(throwable -> online)
                .doOnNext(ObjectOffLineFirst::onNext)
                .doOnError(ObjectOffLineFirst::onError)
                .compose(applySchedulers())
                .subscribe(o -> {
                }, OnErrorNotImplementedException::new);
        return ObjectOffLineFirst.compose(applySchedulers());
    }

    @Override
    public Observable uploadFile(FileIORequest fileIORequest) {
        return mData.uploadFileDynamically(fileIORequest.getUrl(), fileIORequest.getFile(),
                fileIORequest.getKey(), fileIORequest.getParameters(), fileIORequest.onWifi(),
                fileIORequest.isWhileCharging(), fileIORequest.isQueuable(),
                fileIORequest.getPresentationClass(), fileIORequest.getDataClass())
                .compose(applySchedulers());
    }

    @Override
    public Observable downloadFile(FileIORequest fileIORequest) {
        return mData.downloadFileDynamically(fileIORequest.getUrl(), fileIORequest.getFile(),
                fileIORequest.onWifi(), fileIORequest.isWhileCharging(), fileIORequest.isQueuable(),
                fileIORequest.getPresentationClass(), fileIORequest.getDataClass()).compose(applySchedulers());
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
