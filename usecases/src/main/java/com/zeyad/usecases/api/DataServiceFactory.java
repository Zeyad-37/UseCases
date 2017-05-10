package com.zeyad.usecases.api;

import android.support.annotation.VisibleForTesting;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.utils.Utils;

import st.lowlevel.storo.StoroBuilder;

public class DataServiceFactory {
    private static IDataService sDataUseCase;

    /**
     * @return IDataService the implementation instance of IDataService, throws NullPointerException if null.
     */
    public static IDataService getInstance() {
        if (sDataUseCase == null)
            throw new NullPointerException("DataServiceFactory#init must be called before calling getInstance()");
        return sDataUseCase;
    }

    /**
     * Initialization method, that takes a DataServiceConfig object to setup DataUseCase Singleton instance.
     *
     * @param config configuration object to DataUseCase.
     */
    public static void init(DataServiceConfig config) {
        if (!Utils.getInstance().doesContextBelongsToApplication(config.getContext()))
            throw new IllegalArgumentException("Context should be application context only.");
        Config.init(config.getContext());
        Config.setBaseURL(config.getBaseUrl());
        Config.setWithCache(config.isWithCache());
        Config.setCacheExpiry(config.getCacheAmount(), config.getTimeUnit());
        sDataUseCase = new DataService(new ApiConnection(ApiConnection.init(config.getOkHttpBuilder()),
                ApiConnection.initWithCache(config.getOkHttpBuilder(), config.getOkHttpCache())),
                config.getEntityMapper(), config.getPostExecutionThread(),
                config.getHandlerThread(), config.isWithRealm());
        if (config.isWithCache())
            StoroBuilder.configure(config.getCacheSize())
                    .setDefaultCacheDirectory(config.getContext())
                    .setGsonInstance(Config.getGson())
                    .initialize();
        sDataUseCase = DataService.getInstance();
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
     * @param dataUseCase mocked generic use(expected) or any IDataService implementation
     */
    @VisibleForTesting
    static void init(IDataService dataUseCase) {
        sDataUseCase = dataUseCase;
    }
}
