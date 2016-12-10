package com.zeyad.usecases.domain.interactors.data;

import android.support.annotation.VisibleForTesting;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.network.ApiConnectionFactory;
import com.zeyad.usecases.data.utils.Utils;

public class DataUseCaseFactory {
    public static final int NONE = 0, REALM = 1;
    public static int CACHE_SIZE;
    private static boolean withCache;
    private static int mDBType;
    private static IDataUseCase sDataUseCase;

    public static IDataUseCase getInstance() {
        return sDataUseCase;
    }

    public static void init(DataUseCaseConfig config) {
        if (!Utils.doesContextBelongsToApplication(config.getContext()))
            throw new IllegalArgumentException("Context should be application context only.");
        Config.init(config.getContext());
        if (config.getOkHttpBuilder() == null) {
            ApiConnectionFactory.init();
        } else {
            ApiConnectionFactory.init(config.getOkHttpBuilder(), config.getCache());
        }
        if (config.isWithRealm()) {
            mDBType = REALM;
            DataUseCase.initWithRealm(config.getEntityMapper());
        } else {
            mDBType = NONE;
            DataUseCase.initWithoutDB(config.getEntityMapper());
        }
        sDataUseCase = DataUseCase.getInstance();
        CACHE_SIZE = config.getCacheSize();
        withCache = config.isWithCache();
        Config.setBaseURL(config.getBaseUrl());
    }

    public static void destoryInstance() {
        sDataUseCase = null;
    }

    public static boolean isWithCache() {
        return withCache;
    }

    public static int getDBType() {
        return mDBType;
    }

    /**
     * This method is meant for test purposes only. Use other versions of initRealm for production code.
     *
     * @param dataUseCase mocked generic use(expected) or any IDataUseCase implementation
     */
    @VisibleForTesting
    static void init(IDataUseCase dataUseCase) {
        sDataUseCase = dataUseCase;
    }
}
