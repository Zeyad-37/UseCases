package com.zeyad.usecases.domain.interactors.prefs;

import android.content.Context;

import com.zeyad.usecases.data.utils.Utils;

/**
 * @author zeyad on 11/11/16.
 */

public class PrefsUseCaseFactory {

    private static IPrefsUseCase sPrefsUseCase;

    public static IPrefsUseCase getInstance() {
        return sPrefsUseCase;
    }

    public static void init(Context context, String prefsFileName) {
        if (!Utils.doesContextBelongsToApplication(context))
            throw new IllegalArgumentException("Context should be application context only.");
        PrefsUseCase.init(context, prefsFileName);
        sPrefsUseCase = PrefsUseCase.getInstance();
    }

    public static void destoryInstance() {
        sPrefsUseCase = null;
    }
}
