package com.zeyad.genericusecase.domain.interactors.generic;

import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.UIThread;
import com.zeyad.genericusecase.data.db.DatabaseManagerFactory;
import com.zeyad.genericusecase.data.executor.JobExecutor;
import com.zeyad.genericusecase.data.repository.DataRepository;
import com.zeyad.genericusecase.data.repository.stores.DataStoreFactory;
import com.zeyad.genericusecase.data.requests.FileIORequest;
import com.zeyad.genericusecase.data.requests.GetRequest;
import com.zeyad.genericusecase.data.requests.PostRequest;
import com.zeyad.genericusecase.data.utils.IEntityMapperUtil;
import com.zeyad.genericusecase.domain.executors.PostExecutionThread;
import com.zeyad.genericusecase.domain.executors.ThreadExecutor;
import com.zeyad.genericusecase.domain.repository.Repository;

import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * This class is a general implementation that represents a use case for retrieving data.
 */
public class GenericUseCase implements IGenericUseCase {

    private static GenericUseCase sGenericUseCase;
    private final Repository mRepository;
    private final ThreadExecutor mThreadExecutor;
    private final PostExecutionThread mPostExecutionThread;

    private GenericUseCase(Repository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        mThreadExecutor = threadExecutor;
        mPostExecutionThread = postExecutionThread;
        mRepository = repository;
    }

    /**
     * This function should be called at-least once before calling getInstance() method
     * This function should not be called multiple times, but only when required.
     * Ideally this function should be called once when application  is started or created.
     * This function may be called n number of times if required, during mocking and testing.
     */
    static void initWithoutDB(IEntityMapperUtil entityMapper) {
        sGenericUseCase = new GenericUseCase(new DataRepository(new DataStoreFactory(Config.getInstance()
                .getContext()), entityMapper), new JobExecutor(), new UIThread());
    }

    /**
     * This function should be called at-least once before calling getInstance() method
     * This function should not be called multiple times, but only when required.
     * Ideally this function should be called once when application  is started or created.
     * This function may be called n number of times if required, during mocking and testing.
     */
    static void initWithRealm(IEntityMapperUtil entityMapper) {
        DatabaseManagerFactory.initRealm();
        sGenericUseCase = new GenericUseCase(new DataRepository(new DataStoreFactory(DatabaseManagerFactory
                .getInstance(), Config.getInstance().getContext()), entityMapper), new JobExecutor(), new UIThread());
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
        sGenericUseCase = new GenericUseCase(dataRepository, jobExecutor, uiThread);
    }

    public static GenericUseCase getInstance() {
        if (sGenericUseCase == null)
            throw new NullPointerException("GenericUseCase#initRealm must be called before calling getInstance()");
        return sGenericUseCase;
    }

    /**
     * Executes the current use case.
     *
     * @param genericUseCaseRequest The guy who will be listen to the observable build with .
     */
    @Override
    @SuppressWarnings("unchecked")
    public Observable<List> getList(GetRequest genericUseCaseRequest) {
        return mRepository.getListDynamically(genericUseCaseRequest.getUrl(), genericUseCaseRequest
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
        return mRepository.getObjectDynamicallyById(getRequest.getUrl(), getRequest
                        .getIdColumnName(), getRequest.getItemId(), getRequest.getPresentationClass(),
                getRequest.getDataClass(), getRequest.isPersist(), getRequest.isShouldCache())
                .compose(applySchedulers());
    }

    @Override
    public Observable postObject(PostRequest postRequest) {
        return mRepository.postObjectDynamically(postRequest.getUrl(), postRequest.getIdColumnName(),
                postRequest.getObjectBundle(), postRequest.getPresentationClass(), postRequest
                        .getDataClass(), postRequest.isPersist(), postRequest.isQueuable()).compose(applySchedulers());
    }

    @Override
    public Observable postList(PostRequest postRequest) {
        return mRepository.postListDynamically(postRequest.getUrl(), postRequest.getIdColumnName(),
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
        return mRepository.putObjectDynamically(postRequest.getUrl(), postRequest.getIdColumnName(),
                postRequest.getObjectBundle(), postRequest.getPresentationClass(),
                postRequest.getDataClass(), postRequest.isPersist(), postRequest.isQueuable())
                .compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     */
    @Override
    public Observable putList(PostRequest postRequest) {
        return mRepository.putListDynamically(postRequest.getUrl(), postRequest.getIdColumnName(),
                postRequest.getArrayBundle(), postRequest.getPresentationClass(), postRequest.getDataClass(),
                postRequest.isPersist(), postRequest.isQueuable()).compose(applySchedulers());
    }

    @Override
    public Observable deleteCollection(PostRequest deleteRequest) {
        return mRepository.deleteListDynamically(deleteRequest.getUrl(), deleteRequest.getArrayBundle(),
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
        return mRepository.deleteAllDynamically(postRequest.getUrl(), postRequest.getDataClass(),
                postRequest.isPersist()).compose(applySchedulers());
    }

    @Override
    public Observable uploadFile(FileIORequest fileIORequest) {
        return mRepository.uploadFileDynamically(fileIORequest.getUrl(), fileIORequest.getFile(),
                fileIORequest.getKey(), fileIORequest.getParameters(), fileIORequest.onWifi(),
                fileIORequest.isWhileCharging(), fileIORequest.isQueuable(),
                fileIORequest.getPresentationClass(), fileIORequest.getDataClass())
                .compose(applySchedulers());
    }

    @Override
    public Observable downloadFile(FileIORequest fileIORequest) {
        return mRepository.downloadFileDynamically(fileIORequest.getUrl(), fileIORequest.getFile(),
                fileIORequest.onWifi(), fileIORequest.isWhileCharging(), fileIORequest.isQueuable(),
                fileIORequest.getPresentationClass(), fileIORequest.getDataClass()).compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Observable searchDisk(String query, String column, Class presentationClass,
                                 Class dataClass) {
        return mRepository.searchDisk(query, column, presentationClass, dataClass)
                .compose(applySchedulers());
    }

    /**
     * Executes the current use case.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Observable searchDisk(RealmQuery realmQuery, Class presentationClass) {
        return mRepository.searchDisk(realmQuery, presentationClass)
                .compose(applySchedulers());
    }

    /**
     * Apply the default android schedulers to a observable
     *
     * @param <T> the current observable
     * @return the transformed observable
     */
    private <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.from(mThreadExecutor))
                .observeOn(mPostExecutionThread.getScheduler())
                .unsubscribeOn(Schedulers.from(mThreadExecutor));
    }
}
