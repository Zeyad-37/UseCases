package com.zeyad.usecases.domain.interactors.data;

import android.support.annotation.VisibleForTesting;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.network.ApiConnectionFactory;
import com.zeyad.usecases.data.utils.Utils;

import st.lowlevel.storo.StoroBuilder;

public class DataUseCaseFactory {
    public static final int NONE = 0, REALM = 1;
    public static int CACHE_SIZE;
    private static boolean withCache;
    private static int mDBType;
    private static IDataUseCase sDataUseCase;

    /**
     * @return IDataUseCase the implementation instance of IDataUseCase, throws NullPointerException if null.
     */
    public static IDataUseCase getInstance() {
        if (sDataUseCase == null)
            throw new NullPointerException("DataUseCaseFactory#init must be called before calling getInstance()");
        return sDataUseCase;
    }

    /**
     * Initialization method, that takes a DataUseCaseConfig object to setup DataUseCase Singleton instance.
     *
     * @param config configuration object to DataUseCase.
     */
    public static void init(DataUseCaseConfig config) {
        if (!Utils.doesContextBelongsToApplication(config.getContext()))
            throw new IllegalArgumentException("Context should be application context only.");
        Config.init(config.getContext());
        Config.setBaseURL(config.getBaseUrl());
        if (config.getOkHttpBuilder() == null) {
            ApiConnectionFactory.init();
        } else {
            ApiConnectionFactory.init(config.getOkHttpBuilder(), config.getOkHttpCache());
        }
        if (config.isWithRealm()) {
            mDBType = REALM;
            DataUseCase.initWithRealm(config.getEntityMapper(), config.getThreadExecutor(), config.getPostExecutionThread());
        } else {
            mDBType = NONE;
            DataUseCase.initWithoutDB(config.getEntityMapper(), config.getThreadExecutor(), config.getPostExecutionThread());
        }
        if (config.isWithCache())
            StoroBuilder.configure(CACHE_SIZE)
                    .setDefaultCacheDirectory(config.getContext())
                    .initialize();
        sDataUseCase = DataUseCase.getInstance();
        CACHE_SIZE = config.getCacheSize();
        withCache = config.isWithCache();
    }

    /**
     * Destroys the singleton instance of DataUseCase.
     */
    public static void destoryInstance() {
        sDataUseCase = null;
    }

    /**
     * @return withCache, whether DataUseCase is using caching or not.
     */
    public static boolean isWithCache() {
        return withCache;
    }

    /**
     * @return returns database type, whether realm or none.
     */
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
