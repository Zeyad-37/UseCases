package com.zeyad.usecases.api;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.db.RealmManager;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.stores.DataStoreFactory;
import com.zeyad.usecases.utils.DataBaseManagerUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import st.lowlevel.storo.StoroBuilder;

public class DataServiceFactory {
    @Nullable private static IDataService sDataUseCase;

    /**
     * @return IDataService the implementation instance of IDataService, throws NullPointerException
     *     if null.
     */
    @Nullable
    public static IDataService getInstance() {
        if (sDataUseCase == null) {
            throw new NullPointerException(
                    "DataServiceFactory#init must be called before calling getInstance()");
        }
        return sDataUseCase;
    }

    /**
     * Initialization method, that takes a DataServiceConfig object to setup DataUseCase Singleton
     * instance.
     *
     * @param config configuration object to DataUseCase.
     */
    public static void init(@NonNull DataServiceConfig config) {
        if (!doesContextBelongsToApplication(config.getContext())) {
            throw new IllegalArgumentException("Context should be application context only.");
        }
        DataBaseManagerUtil dataBaseManagerUtil = config.getDataBaseManagerUtil();
        boolean isSQLite = dataBaseManagerUtil != null;
        Config.init(config.getContext());
        Config.setBaseURL(config.getBaseUrl());
        Config.setWithCache(config.isWithCache());
        Config.setCacheExpiry(config.getCacheAmount(), config.getTimeUnit());
        Config.setWithSQLite(isSQLite);
        Config.setHasRealm(config.isWithRealm());
        if (config.isWithCache()) {
            StoroBuilder.configure(config.getCacheSize())
                    .setDefaultCacheDirectory(config.getContext())
                    .setGsonInstance(Config.getGson())
                    .initialize();
        }
        HandlerThread backgroundThread = config.getHandlerThread();
        if (config.isWithRealm()) {
            backgroundThread.start();
            Config.setBackgroundThread(AndroidSchedulers.from(backgroundThread.getLooper()));
        }
        ApiConnection apiConnection =
                new ApiConnection(
                        ApiConnection.init(config.getOkHttpBuilder()),
                        ApiConnection.initWithCache(
                                config.getOkHttpBuilder(), config.getOkHttpCache()));
        sDataUseCase =
                new DataService(
                        config.isWithRealm() || isSQLite
                                ? new DataStoreFactory(
                                        isSQLite
                                                ? dataBaseManagerUtil
                                                : dataClass ->
                                                        new RealmManager(
                                                                backgroundThread.getLooper()),
                                        apiConnection,
                                        config.getEntityMapper())
                                : new DataStoreFactory(apiConnection, config.getEntityMapper()),
                        config.getPostExecutionThread(),
                        Config.getBackgroundThread());
        Config.setApiConnection(apiConnection);
    }

    /** Destroys the singleton instance of DataUseCase. */
    public static void destoryInstance() {
        sDataUseCase = null;
    }

    private static boolean doesContextBelongsToApplication(Context mContext) {
        return !(mContext instanceof Activity || mContext instanceof Service);
    }
}
