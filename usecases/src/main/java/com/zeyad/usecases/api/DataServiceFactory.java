package com.zeyad.usecases.api;

import android.os.HandlerThread;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.db.RealmManager;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.stores.DataStoreFactory;
import com.zeyad.usecases.utils.Utils;

import rx.android.schedulers.AndroidSchedulers;
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
        if (config.isWithCache())
            StoroBuilder.configure(config.getCacheSize())
                    .setDefaultCacheDirectory(config.getContext())
                    .setGsonInstance(Config.getGson())
                    .initialize();
        HandlerThread backgroundThread = config.getHandlerThread();
        if (config.isWithRealm()) {
            backgroundThread.start();
            Config.setBackgroundThread(AndroidSchedulers.from(backgroundThread.getLooper()));
        }
        ApiConnection apiConnection = new ApiConnection(ApiConnection.init(config.getOkHttpBuilder()),
                ApiConnection.initWithCache(config.getOkHttpBuilder(), config.getOkHttpCache()));
        sDataUseCase = new DataService(config.isWithCache() ?
                new DataStoreFactory(new RealmManager(backgroundThread.getLooper()),
                        apiConnection, config.getEntityMapper()) :
                new DataStoreFactory(apiConnection, config.getEntityMapper()), config.getPostExecutionThread(),
                Config.getBackgroundThread());
        Config.setApiConnection(apiConnection);
    }

    /**
     * Destroys the singleton instance of DataUseCase.
     */
    public static void destoryInstance() {
        sDataUseCase = null;
    }
}
