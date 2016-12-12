package com.zeyad.usecases.domain.interactors.files;

import android.content.Context;

import com.zeyad.usecases.data.utils.Utils;
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
            throw new NullPointerException("FileUseCaseFactory#init must be called before calling getInstance()");
        return sFilesUseCase;
    }

    public static void init(Context context) {
        if (!Utils.doesContextBelongsToApplication(context))
            throw new IllegalArgumentException("Context should be application context only.");
        FileUseCase.init(context);
        sFilesUseCase = FileUseCase.getInstance();
    }

    public static void init(Context context, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        if (!Utils.doesContextBelongsToApplication(context))
            throw new IllegalArgumentException("Context should be application context only.");
        FileUseCase.init(context, threadExecutor, postExecutionThread);
        sFilesUseCase = FileUseCase.getInstance();
    }

    /**
     * Destroys the singleton instance of FileUseCase.
     */
    public static void destoryInstance() {
        sFilesUseCase = null;
    }
}
