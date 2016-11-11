package com.zeyad.genericusecase.domain.interactors.prefs;

/**
 * @author zeyad on 11/11/16.
 */

public class PrefsUseCaseFactory {

    private static IPrefsUseCase sPrefsUseCase;

    public static IPrefsUseCase getInstance() {
        return sPrefsUseCase;
    }

    public static void init() {
        PrefsUseCases.init();
        sPrefsUseCase = PrefsUseCases.getInstance();
    }
}
