package com.zeyad.usecases.domain.interactors.prefs;

import android.content.Context;

/**
 * @author zeyad on 11/11/16.
 */

public class PrefsUseCaseFactory {

    private static IPrefsUseCase sPrefsUseCase;

    public static IPrefsUseCase getInstance() {
        return sPrefsUseCase;
    }

    public static void init(Context context) {
        PrefsUseCase.init(context);
        sPrefsUseCase = PrefsUseCase.getInstance();
    }

    public static void destoryInstance() {
        sPrefsUseCase = null;
    }
}
