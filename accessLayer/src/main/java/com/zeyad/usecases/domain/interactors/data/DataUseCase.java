package com.zeyad.usecases.domain.interactors.data;

import android.os.HandlerThread;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.db.DatabaseManagerFactory;
import com.zeyad.usecases.data.db.RealmManager;
import com.zeyad.usecases.data.executor.JobExecutor;
import com.zeyad.usecases.data.mappers.IDAOMapperFactory;
import com.zeyad.usecases.data.repository.DataRepository;
import com.zeyad.usecases.data.repository.stores.DataStoreFactory;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.data.utils.Utils;
import com.zeyad.usecases.domain.executors.PostExecutionThread;
import com.zeyad.usecases.domain.executors.ThreadExecutor;
import com.zeyad.usecases.domain.executors.UIThread;
import com.zeyad.usecases.domain.repositories.Data;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

/**
 * This class is a general implementation that represents a use case for retrieving data.
 */
public class DataUseCase implements IDataUseCase {

    private final static BehaviorSubject ObjectOffLineFirst = BehaviorSubject.create();
    private final static BehaviorSubject<List> listOffLineFirst = BehaviorSubject.create();
    private final static BehaviorSubject lastObject = BehaviorSubject.create();
    private final static BehaviorSubject<List> lastList = BehaviorSubject.create();
    private static boolean hasRealm;
    private static HandlerThread handlerThread;
    private static DataUseCase sDataUseCase;
    private final Data mData;
    private final ThreadExecutor mThreadExecutor;
    private final PostExecutionThread mPostExecutionThread;

    private DataUseCase(Data data, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        handlerThread = getHandlerThread();
        mThreadExecutor = threadExecutor;
        mPostExecutionThread = postExecutionThread;
        mData = data;
    }

    /**
     * This function should be called at-least once before calling getInstance() method
     * This function should not be called multiple times, but only when required.
     * Ideally this function should be called once when application  is started or created.
     * This function may be called n number of times if required, during mocking and testing.
     */
    static void initWithoutDB(IDAOMapperFactory entityMapper, ThreadExecutor threadExecutor,
                              PostExecutionThread postExecutionThread) {
        hasRealm = false;
        sDataUseCase = new DataUseCase(new DataRepository(new DataStoreFactory(Config.getInstance()
                .getContext()), entityMapper), threadExecutor, postExecutionThread);
    }

    /**
     * This function should be called at-least once before calling getInstance() method
     * This function should not be called multiple times, but only when required.
     * Ideally this function should be called once when application  is started or created.
     * This function may be called n number of times if required, during mocking and testing.
     */
    static void initWithRealm(IDAOMapperFactory entityMapper, ThreadExecutor threadExecutor,
                              PostExecutionThread postExecutionThread) {
        hasRealm = true;
        DatabaseManagerFactory.initRealm();
        sDataUseCase = new DataUseCase(new DataRepository(new DataStoreFactory(DatabaseManagerFactory
                .getInstance(), Config.getInstance().getContext()), entityMapper), threadExecutor,
                postExecutionThread);
    }

    /**
     * Testing only!
     * This function should be called at-least once before calling getInstance() method
     * This function should not be called multiple times, but only when required.
     * Ideally this function should be called once when application  is started or created.
     * This function may be called n number of times if required, during mocking and testing.
     *
     * @param dataRepository data repository
     * @param jobExecutor    job executor
     * @param uiThread       ui thread implementation
     */
    public static void init(DataRepository dataRepository, JobExecutor jobExecutor, UIThread uiThread) {
        sDataUseCase = new DataUseCase(dataRepository, jobExecutor, uiThread);
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
                .flatMap(list -> {
                    lastList.onNext(list);
                    return Observable.just(list);
                });
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
                .flatMap(object -> {
                    lastObject.onNext(object);
                    return Observable.just(object);
                });
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
    public Observable<List> queryDisk(RealmManager.RealmQueryProvider queryFactory, Class presentationClass) {
        return mData.queryDisk(queryFactory, presentationClass)
                .flatMap(list -> {
                    lastObject.onNext(list);
                    return Observable.just(list);
                })
                .compose(applySchedulers());
    }

    @Override
    public BehaviorSubject getLastObject() {
        return lastObject;
    }

    @Override
    public BehaviorSubject<List> getLastList() {
        return lastList;
    }

    @Override
    public Observable<List> getListOffLineFirst(GetRequest getRequest) {
        Observable<List> online = mData.getListDynamically(getRequest.getUrl(), getRequest.getPresentationClass(),
                getRequest.getDataClass(), getRequest.isPersist(), getRequest.isShouldCache());
        List lastList = listOffLineFirst.getValue();
        if (Utils.isNotEmpty(lastList) && lastList.get(0).getClass() == getRequest.getPresentationClass())
            listOffLineFirst.onNext(lastList);
        else
            mData.getListDynamically("", getRequest.getPresentationClass(), getRequest.getDataClass(),
                    getRequest.isPersist(), getRequest.isShouldCache())
                    .flatMap(new Func1<List, Observable<?>>() {
                        @Override
                        public Observable<List> call(List list) {
                            if (Utils.isNotEmpty(list))
                                return Observable.just(list);
                            else return online;
                        }
                    })
                    .onErrorResumeNext(throwable -> online)
                    .doOnNext(list -> listOffLineFirst.onNext((List) list))
                    .doOnError(listOffLineFirst::onError)
                    .compose(applySchedulers())
                    .subscribe();
        return listOffLineFirst.subscribeOn(AndroidSchedulers.from(handlerThread.getLooper()))
                .observeOn(mPostExecutionThread.getScheduler());
    }

    @Override
    public BehaviorSubject getObjectOffLineFirst(GetRequest getRequest) {
        Observable<?> online = mData.getObjectDynamicallyById(getRequest.getUrl(), getRequest
                        .getIdColumnName(), getRequest.getItemId(), getRequest.getPresentationClass(),
                getRequest.getDataClass(), getRequest.isPersist(), getRequest.isShouldCache());
        mData.getObjectDynamicallyById("", getRequest.getIdColumnName(), getRequest.getItemId(),
                getRequest.getPresentationClass(), getRequest.getDataClass(), getRequest.isPersist(),
                getRequest.isShouldCache())
                .flatMap(object -> object == null ? Observable.just(object) : online)
                .onErrorResumeNext(throwable -> online)
                .doOnNext(ObjectOffLineFirst::onNext)
                .doOnError(ObjectOffLineFirst::onError)
                .compose(applySchedulers())
                .subscribe(o -> {
                }, throwable -> {
                });
        return ObjectOffLineFirst;
    }

    /**
     * Apply the default android schedulers to a observable
     *
     * @param <T> the current observable
     * @return the transformed observable
     */
    private <T> Observable.Transformer<T, T> applySchedulers() {
        handlerThread = getHandlerThread();
        if (!handlerThread.isAlive())
            handlerThread.start();
//        return observable -> observable.subscribeOn(AndroidSchedulers.from(mThreadExecutor.getLooper()))
        return observable -> observable.subscribeOn(AndroidSchedulers.from(handlerThread.getLooper()))
                .observeOn(mPostExecutionThread.getScheduler());
    }
}
