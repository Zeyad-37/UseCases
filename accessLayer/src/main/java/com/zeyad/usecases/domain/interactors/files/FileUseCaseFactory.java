package com.zeyad.usecases.domain.interactors.files;

import com.zeyad.usecases.domain.executors.PostExecutionThread;
import com.zeyad.usecases.domain.executors.ThreadExecutor;

/**
 * @author zeyad on 11/11/16.
 */

public class FileUseCaseFactory {

    private static IFileUseCase sFilesUseCase;

    /**
     * @return IFileUseCase the implementation instance of IDataUseCase, throws NullPointerException if null.
     */
    public static IFileUseCase getInstance() {
        if (sFilesUseCase == null)
            sFilesUseCase = FileUseCase.getInstance();
        return sFilesUseCase;
    }

    public static void init() {
        FileUseCase.init();
        sFilesUseCase = FileUseCase.getInstance();
    }

    public static void init(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        FileUseCase.init(threadExecutor, postExecutionThread);
        sFilesUseCase = FileUseCase.getInstance();
    }

    /**
     * Destroys the singleton instance of FileUseCase.
     */
    public static void destoryInstance() {
        sFilesUseCase = null;
    }
}
