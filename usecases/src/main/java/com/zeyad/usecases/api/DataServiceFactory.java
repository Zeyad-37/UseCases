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

import static st.lowlevel.storo.StoroBuilder.Storage.INTERNAL;

public final class DataServiceFactory {
    @Nullable
    private static IDataService sDataUseCase;

    private DataServiceFactory() {
    }

    /**
     * @return IDataService the implementation instance of IDataService, throws NullPointerException
     * if null.
     */
    @Nullable
    public static IDataService getInstance() {
        if (sDataUseCase == null) {
            throw new IllegalAccessError(
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
                        .setCacheDirectory(config.getContext(), INTERNAL)
                        .setDefaultCacheDirectory(config.getContext())
                        .setGsonInstance(Config.getGson())
                        .initialize();
        }
        HandlerThread handlerThread = config.getHandlerThread();
        if (config.isWithRealm()) {
            handlerThread.start();
            Config.setBackgroundThread(AndroidSchedulers.from(handlerThread.getLooper()));
        }
        ApiConnection apiConnection = new ApiConnection(ApiConnection.init(config.getOkHttpBuilder()),
                ApiConnection.initWithCache(config.getOkHttpBuilder(), config.getOkHttpCache()));
        dataBaseManagerUtil = config.isWithRealm() || isSQLite ? isSQLite ? dataBaseManagerUtil :
                dataClass -> new RealmManager() : dataClass -> null;
        sDataUseCase = new DataService(new DataStoreFactory(dataBaseManagerUtil, apiConnection,
                config.getEntityMapper()), config.getPostExecutionThread(), Config.getBackgroundThread());
        Config.setApiConnection(apiConnection);
    }

    /**
     * Destroys the singleton instance of DataUseCase.
     */
    public static void destoryInstance() {
        sDataUseCase = null;
    }

    private static boolean doesContextBelongsToApplication(Context mContext) {
        return !(mContext instanceof Activity || mContext instanceof Service);
    }
}
