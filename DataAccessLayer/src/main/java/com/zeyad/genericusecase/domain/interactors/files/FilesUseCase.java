package com.zeyad.genericusecase.domain.interactors.files;

import com.zeyad.genericusecase.UIThread;
import com.zeyad.genericusecase.data.executor.JobExecutor;
import com.zeyad.genericusecase.data.repository.FilesRepository;
import com.zeyad.genericusecase.domain.executors.PostExecutionThread;
import com.zeyad.genericusecase.domain.executors.ThreadExecutor;
import com.zeyad.genericusecase.domain.repository.Files;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @author zeyad on 11/11/16.
 */

public class FilesUseCase implements IFilesUseCase {

    private static FilesUseCase sFilesUseCase;
    private final Files mFiles;
    private final ThreadExecutor mThreadExecutor;
    private final PostExecutionThread mPostExecutionThread;

    private FilesUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        mThreadExecutor = threadExecutor;
        mPostExecutionThread = postExecutionThread;
        mFiles = FilesRepository.getInstance();
    }

    public static void init() {
        sFilesUseCase = new FilesUseCase(new JobExecutor(), new UIThread());
    }

    public static FilesUseCase getInstance() {
        if (sFilesUseCase == null)
            sFilesUseCase = new FilesUseCase(new JobExecutor(), new UIThread());
        return sFilesUseCase;
    }

    @Override
    public Observable<String> readFromResource(String filePath) {
        return mFiles.readFromResource(filePath).compose(applySchedulers());
    }


    @Override
    public Observable<String> readFromFile(String fullFilePath) {
        return mFiles.readFromFile(fullFilePath).compose(applySchedulers());
    }

    @Override
    public Observable<Boolean> saveToFile(String fullFilePath, String data) {
        return mFiles.saveToFile(fullFilePath, data).compose(applySchedulers());
    }

    @Override
    public Observable<Boolean> saveToFile(String fullFilePath, byte[] data) {
        return mFiles.saveToFile(fullFilePath, data).compose(applySchedulers());
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
