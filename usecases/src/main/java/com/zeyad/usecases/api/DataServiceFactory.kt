package com.zeyad.usecases.api

import android.app.Activity
import android.app.Service
import android.content.Context
import com.zeyad.usecases.Config
import com.zeyad.usecases.db.RealmManager
import com.zeyad.usecases.network.ApiConnection
import com.zeyad.usecases.stores.DataStoreFactory
import com.zeyad.usecases.utils.DataBaseManagerUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import st.lowlevel.storo.StoroBuilder
import st.lowlevel.storo.StoroBuilder.Storage.INTERNAL

object DataServiceFactory {
    private var sDataUseCase: IDataService? = null

    /**
     * @return IDataService the implementation instance of IDataService, throws NullPointerException
     * if null.
     */
    val instance: IDataService
        get() {
            if (sDataUseCase == null) {
                throw IllegalAccessError(
                        "DataServiceFactory#init must be called before calling getInstance()")
            }
            return sDataUseCase as IDataService
        }

    /**
     * Initialization method, that takes a DataServiceConfig object to setup DataUseCase Singleton
     * instance.
     *
     * @param config configuration object to DataUseCase.
     */
    fun init(config: DataServiceConfig) {
        if (!doesContextBelongsToApplication(config.context)) {
            throw IllegalArgumentException("Context should be application context only.")
        }
        var dataBaseManagerUtil: DataBaseManagerUtil? = config.dataBaseManagerUtil
        val isSQLite = dataBaseManagerUtil != null
        Config.init(config.context)
        Config.setBaseURL(config.getBaseUrl())
        Config.setWithCache(config.isWithCache)
        Config.setCacheExpiry(config.cacheAmount, config.timeUnit)
        Config.setWithSQLite(isSQLite)
        Config.setHasRealm(config.isWithRealm)
        if (config.isWithCache) {
            StoroBuilder.configure(config.getCacheSize().toLong())
                    .setCacheDirectory(config.context, INTERNAL)
                    .setDefaultCacheDirectory(config.context)
                    .setGsonInstance(Config.getGson())
                    .initialize()
        }
        val handlerThread = config.handlerThread
        if (config.isWithRealm) {
            handlerThread.start()
            Config.setBackgroundThread(AndroidSchedulers.from(handlerThread.looper))
        }
        val apiConnection = ApiConnection(ApiConnection.init(config.okHttpBuilder),
                ApiConnection.initWithCache(config.okHttpBuilder, config.okHttpCache))
        if (!isSQLite) {
            dataBaseManagerUtil = if (config.isWithRealm)
                DataBaseManagerUtil { RealmManager() }
            else
                DataBaseManagerUtil { null }
        }
        sDataUseCase = DataService(DataStoreFactory(dataBaseManagerUtil, apiConnection,
                config.entityMapper), config.postExecutionThread, Config.getBackgroundThread())
        Config.setApiConnection(apiConnection)
    }

    /**
     * Destroys the singleton instance of DataUseCase.
     */
    fun destoryInstance() {
        sDataUseCase = null
    }

    private fun doesContextBelongsToApplication(mContext: Context): Boolean {
        return !(mContext is Activity || mContext is Service)
    }
}
