package com.zeyad.usecases.domain.interactors.files;

import android.content.Context;

import com.zeyad.usecases.data.executor.JobExecutor;
import com.zeyad.usecases.data.repository.FilesRepository;
import com.zeyad.usecases.data.requests.FileIORequest;
import com.zeyad.usecases.domain.executors.PostExecutionThread;
import com.zeyad.usecases.domain.executors.ThreadExecutor;
import com.zeyad.usecases.domain.executors.UIThread;
import com.zeyad.usecases.domain.repositories.Files;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @author zeyad on 11/11/16.
 */
class FileUseCase implements IFileUseCase {

    private static FileUseCase sFilesUseCase;
    private final Files mFiles;
    private final ThreadExecutor mThreadExecutor;
    private final PostExecutionThread mPostExecutionThread;

    private FileUseCase(Context context, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        mThreadExecutor = threadExecutor;
        mPostExecutionThread = postExecutionThread;
        FilesRepository.init(context);
        mFiles = FilesRepository.getInstance();
    }

    public static void init(Context context) {
        sFilesUseCase = new FileUseCase(context, new JobExecutor(), new UIThread());
    }

    public static void init(Context context, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        sFilesUseCase = new FileUseCase(context, threadExecutor, postExecutionThread);
    }

    protected static FileUseCase getInstance() {
        if (sFilesUseCase == null)
            throw new NullPointerException("FileUseCase#initRealm must be called before calling getInstance()");
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
    public Observable uploadFile(FileIORequest fileIORequest) {
        return mFiles.uploadFileDynamically(fileIORequest.getUrl(), fileIORequest.getFile(),
                fileIORequest.getKey(), fileIORequest.getParameters(), fileIORequest.onWifi(),
                fileIORequest.isWhileCharging(), fileIORequest.isQueuable(),
                fileIORequest.getPresentationClass(), fileIORequest.getDataClass())
                .compose(applySchedulers());
    }

    @Override
    public Observable downloadFile(FileIORequest fileIORequest) {
        return mFiles.downloadFileDynamically(fileIORequest.getUrl(), fileIORequest.getFile(),
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
        return observable -> observable.subscribeOn(Schedulers.from(mThreadExecutor))
                .observeOn(mPostExecutionThread.getScheduler());
    }
}
