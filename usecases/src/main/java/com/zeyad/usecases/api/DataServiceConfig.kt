package com.zeyad.usecases.api

import android.content.Context
import android.os.HandlerThread
import com.zeyad.usecases.mapper.DAOMapper
import com.zeyad.usecases.utils.DataBaseManagerUtil
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


/**
 * @author by ZIaDo on 12/9/16.
 */
data class DataServiceConfig private constructor(val context: Context,
                                                 val okHttpBuilder: OkHttpClient.Builder?,
                                                 val okHttpCache: Cache? = null,
                                                 val baseUrl: String = "",
                                                 val isWithCache: Boolean = false,
                                                 val cacheSize: Int = 8192,
                                                 val cacheDuration: Long = 3,
                                                 val timeUnit: TimeUnit = TimeUnit.SECONDS,
                                                 val withSQL: Boolean = false,
                                                 val withRealm: Boolean = false,
                                                 val postExecutionThread: Scheduler = Schedulers.io(),
                                                 val handlerThread: HandlerThread = HandlerThread("backgroundHandler"),
                                                 val dataBaseManagerUtil: DataBaseManagerUtil? = null,
                                                 val entityMapper: DAOMapper = DAOMapper()) {

    constructor(dataUseCaseConfigBuilder: Builder) : this(
            dataUseCaseConfigBuilder.context,
            dataUseCaseConfigBuilder.okHttpBuilder,
            dataUseCaseConfigBuilder.okHttpCache,
            dataUseCaseConfigBuilder.baseUrl,
            dataUseCaseConfigBuilder.withCache,
            dataUseCaseConfigBuilder.cacheSize,
            dataUseCaseConfigBuilder.cacheDuration,
            dataUseCaseConfigBuilder.timeUnit,
            dataUseCaseConfigBuilder.withSQL,
            dataUseCaseConfigBuilder.withRealm,
            dataUseCaseConfigBuilder.postExecutionThread,
            dataUseCaseConfigBuilder.handlerThread,
            dataUseCaseConfigBuilder.dataBaseManagerUtil
    )

    class Builder(internal val context: Context) {
        internal var okHttpBuilder: OkHttpClient.Builder? = null
        internal var okHttpCache: Cache? = null
        internal var baseUrl: String = ""
        internal var withCache: Boolean = false
        internal var withRealm: Boolean = false
        internal var withSQL: Boolean = false
        internal var cacheSize: Int = 0
        internal var cacheDuration: Long = 0
        internal var timeUnit: TimeUnit = TimeUnit.SECONDS
        internal var handlerThread: HandlerThread = HandlerThread("backgroundHandler")
        internal var postExecutionThread: Scheduler = Schedulers.io()
        internal var dataBaseManagerUtil: DataBaseManagerUtil? = null

        fun postExecutionThread(postExecutionThread: Scheduler): Builder {
            this.postExecutionThread = postExecutionThread
            return this
        }

        fun baseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        fun okHttpBuilder(okHttpBuilder: OkHttpClient.Builder): Builder {
            this.okHttpBuilder = okHttpBuilder
            return this
        }

        fun okhttpCache(cache: Cache): Builder {
            this.okHttpCache = cache
            return this
        }

        fun withRealm(handlerThread: HandlerThread): Builder {
            this.withRealm = true
            this.dataBaseManagerUtil = null
            this.handlerThread = handlerThread
            return this
        }

        fun withRealm(): Builder {
            this.withRealm = true
            this.dataBaseManagerUtil = null
            return this
        }

        fun withCache(expiryAmount: Long, timeUnit: TimeUnit): Builder {
            this.withCache = true
            this.cacheDuration = expiryAmount
            this.timeUnit = timeUnit
            return this
        }

        fun cacheSize(cacheSize: Int): Builder {
            this.cacheSize = cacheSize
            return this
        }

        fun withSQLite(dataBaseManagerUtil: DataBaseManagerUtil): Builder {
            this.dataBaseManagerUtil = dataBaseManagerUtil
            this.withRealm = false
            return this
        }

        fun build(): DataServiceConfig {
            return DataServiceConfig(this)
        }
    }
}