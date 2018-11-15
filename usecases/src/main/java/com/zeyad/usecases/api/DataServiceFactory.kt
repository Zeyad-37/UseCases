package com.zeyad.usecases.api

import android.app.Application
import android.content.Context
import com.zeyad.usecases.Config
import com.zeyad.usecases.db.DataBaseManager
import com.zeyad.usecases.network.ApiConnection
import com.zeyad.usecases.stores.DataStoreFactory
import com.zeyad.usecases.utils.DataBaseManagerUtil
import st.lowlevel.storo.StoroBuilder

class DataServiceFactory(val config: DataServiceConfig) {

    init {
        if (!doesContextBelongsToApplication(config.context)) {
            throw IllegalArgumentException("Context should be application context only.")
        }
        var dataBaseManagerUtil: DataBaseManagerUtil? = config.dataBaseManagerUtil
        Config.context = config.context
        Config.baseURL = config.baseUrl
        Config.withCache = config.isWithCache
        Config.cacheDuration = config.cacheDuration
        Config.cacheTimeUnit = config.timeUnit
        Config.withSQLite = config.withSQL

        if (config.isWithCache) {
            StoroBuilder.configure(config.cacheSize.toLong())
                    .setCacheDirectory(config.context, StoroBuilder.Storage.INTERNAL)
                    .setDefaultCacheDirectory(config.context)
                    .setGsonInstance(Config.gson)
                    .initialize()
        }

        val apiConnection = ApiConnection(ApiConnection.init(config.okHttpBuilder),
                ApiConnection.initWithCache(config.okHttpBuilder, config.okHttpCache))

        dataBaseManagerUtil =
                if (config.withSQL)
                    dataBaseManagerUtil
                else
                    object : DataBaseManagerUtil {
                        override fun getDataBaseManager(dataClass: Class<*>): DataBaseManager? {
                            return null
                        }
                    }

        dataService = DataService(DataStoreFactory(dataBaseManagerUtil, apiConnection,
                config.entityMapper), config.postExecutionThread, Config.backgroundThread)
        Config.apiConnection = apiConnection
    }

    fun destoryInstance() {
        dataService = null
    }

    fun getInstance() = dataService!!

    companion object {
        var dataService: IDataService? = null
    }

    private fun doesContextBelongsToApplication(context: Context): Boolean = context is Application
}
