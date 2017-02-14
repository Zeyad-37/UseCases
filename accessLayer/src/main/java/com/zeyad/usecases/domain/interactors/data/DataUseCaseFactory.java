package com.zeyad.usecases.domain.interactors.data;

import android.support.annotation.VisibleForTesting;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.network.ApiConnectionFactory;
import com.zeyad.usecases.data.utils.Utils;

import st.lowlevel.storo.StoroBuilder;

public class DataUseCaseFactory {
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
        if (!Utils.getInstance().doesContextBelongsToApplication(config.getContext()))
            throw new IllegalArgumentException("Context should be application context only.");
        Config.init(config.getContext());
        Config.setBaseURL(config.getBaseUrl());
        Config.setWithCache(config.isWithCache());
        Config.setCacheExpiry(config.getCacheAmount(), config.getTimeUnit());
        ApiConnectionFactory.init(config.getOkHttpBuilder(), config.getOkHttpCache());
        if (config.isWithRealm()) {
            DataUseCase.initWithRealm(config.getEntityMapper(), config.getThreadExecutor(), config
                    .getPostExecutionThread());
        } else
            DataUseCase.initWithoutDB(config.getEntityMapper(), config.getThreadExecutor(), config
                    .getPostExecutionThread());
        if (config.isWithCache())
            StoroBuilder.configure(config.getCacheSize())
                    .setDefaultCacheDirectory(config.getContext().getApplicationContext())
                    .setGsonInstance(Config.getGson())
                    .initialize();
        sDataUseCase = DataUseCase.getInstance();
    }

    /**
     * Destroys the singleton instance of DataUseCase.
     */
    public static void destoryInstance() {
        sDataUseCase = null;
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
