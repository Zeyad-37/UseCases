package com.zeyad.usecases.domain.interactors.prefs;

import android.content.Context;

import com.zeyad.usecases.data.utils.Utils;
import com.zeyad.usecases.domain.executors.PostExecutionThread;
import com.zeyad.usecases.domain.executors.ThreadExecutor;

/**
 * @author zeyad on 11/11/16.
 */

public class PrefsUseCaseFactory {

    private static IPrefsUseCase sPrefsUseCase;

    /**
     * @return IPrefsUseCase the implementation instance of IDataUseCase, throws NullPointerException if null.
     */
    public static IPrefsUseCase getInstance() {
        if (sPrefsUseCase == null)
            throw new NullPointerException("PrefsUseCaseFactory#init must be called before calling getInstance()");
        return sPrefsUseCase;
    }

    public static void init(Context context, String prefsFileName) {
        if (!Utils.getInstance().doesContextBelongsToApplication(context))
            throw new IllegalArgumentException("Context should be application context only.");
        PrefsUseCase.init(context, prefsFileName);
        sPrefsUseCase = PrefsUseCase.getInstance();
    }

    public static void init(Context context, String prefsFileName, ThreadExecutor threadExecutor,
                            PostExecutionThread postExecutionThread) {
        if (!Utils.getInstance().doesContextBelongsToApplication(context))
            throw new IllegalArgumentException("Context should be application context only.");
        PrefsUseCase.init(context, prefsFileName, threadExecutor, postExecutionThread);
        sPrefsUseCase = PrefsUseCase.getInstance();
    }

    /**
     * Destroys the singleton instance of PrefsUseCase.
     */
    public static void destoryInstance() {
        sPrefsUseCase = null;
    }
}
