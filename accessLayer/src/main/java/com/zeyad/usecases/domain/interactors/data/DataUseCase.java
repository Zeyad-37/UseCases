package com.zeyad.usecases.domain.interactors.data;

import android.os.HandlerThread;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.db.DatabaseManagerFactory;
import com.zeyad.usecases.data.executor.JobExecutor;
import com.zeyad.usecases.data.mappers.IDAOMapperFactory;
import com.zeyad.usecases.data.repository.DataRepository;
import com.zeyad.usecases.data.repository.stores.DataStoreFactory;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.domain.executors.PostExecutionThread;
import com.zeyad.usecases.domain.executors.ThreadExecutor;
import com.zeyad.usecases.domain.executors.UIThread;
import com.zeyad.usecases.domain.repositories.Data;

import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * This class is a general implementation that represents a use case for retrieving data.
 */
public class DataUseCase implements IDataUseCase {

    public static HandlerThread handlerThread = new HandlerThread("");
    private static DataUseCase sDataUseCase;
    private final Data mData;
    private final ThreadExecutor mThreadExecutor;
    private final PostExecutionThread mPostExecutionThread;

    private DataUseCase(Data data, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
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
    static void initWithoutDB(IDAOMapperFactory entityMapper, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        sDataUseCase = new DataUseCase(new DataRepository(new DataStoreFactory(Config.getInstance()
                .getContext()), entityMapper), threadExecutor, postExecutionThread);
    }

    /**
     * This function should be called at-least once before calling getInstance() method
     * This function should not be called multiple times, but only when required.
     * Ideally this function should be called once when application  is started or created.
     * This function may be called n number of times if required, during mocking and testing.
     */
    static void initWithRealm(IDAOMapperFactory entityMapper, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
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
                .isPersist(), genericUseCaseRequest.isShouldCache()).compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     *
     * @param getRequest The guy who will be listen to the observable build with .
     */
    @Override
    @SuppressWarnings("unchecked")
    public Observable getObject(GetRequest getRequest) {
        return mData.getObjectDynamicallyById(getRequest.getUrl(), getRequest
                        .getIdColumnName(), getRequest.getItemId(), getRequest.getPresentationClass(),
                getRequest.getDataClass(), getRequest.isPersist(), getRequest.isShouldCache())
                .compose(applySchedulers());
    }

    @Override
    public Observable postObject(PostRequest postRequest) {
        return mData.postObjectDynamically(postRequest.getUrl(), postRequest.getIdColumnName(),
                postRequest.getObjectBundle(), postRequest.getPresentationClass(), postRequest
                        .getDataClass(), postRequest.isPersist(), postRequest.isQueuable()).compose(applySchedulers());
    }

    @Override
    public Observable postList(PostRequest postRequest) {
        return mData.postListDynamically(postRequest.getUrl(), postRequest.getIdColumnName(),
                postRequest.getArrayBundle(), postRequest.getPresentationClass(), postRequest.getDataClass(),
                postRequest.isPersist(), postRequest.isQueuable()).compose(applySchedulers());
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
    public Observable searchDisk(String query, String column, Class presentationClass,
                                 Class dataClass) {
        return mData.searchDisk(query, column, presentationClass, dataClass)
                .compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Observable searchDisk(RealmQuery realmQuery, Class presentationClass) {
        return mData.searchDisk(realmQuery, presentationClass)
                .compose(applySchedulers());
    }

    /**
     * Apply the default android schedulers to a observable
     *
     * @param <T> the current observable
     * @return the transformed observable
     */
    private <T> Observable.Transformer<T, T> applySchedulers() {
        if (handlerThread == null)
            handlerThread = new HandlerThread("backgroundThread");
        if (!handlerThread.isAlive()) {
            handlerThread.start();
        }
        return observable -> observable.subscribeOn(AndroidSchedulers.from(handlerThread.getLooper()))
//        return observable -> observable.subscribeOn(Schedulers.from(mThreadExecutor))
                .observeOn(mPostExecutionThread.getScheduler());
    }
}
