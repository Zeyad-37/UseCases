package com.zeyad.usecases.api

import android.content.Context
import android.os.HandlerThread
import com.zeyad.usecases.mapper.DAOMapper
import com.zeyad.usecases.utils.DataBaseManagerUtil
import io.reactivex.Scheduler
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * @author by ZIaDo on 12/9/16.
 */
class DataServiceConfig private constructor(dataUseCaseConfigBuilder: Builder) {

    val context: Context
    internal val okHttpBuilder: OkHttpClient.Builder?
    internal val okHttpCache: Cache?
    private val baseUrl: String?
    internal val isWithCache: Boolean
    internal val isWithRealm: Boolean
    private val cacheSize: Int
    internal val cacheAmount: Int
    internal val timeUnit: TimeUnit?
    internal val postExecutionThread: Scheduler?
    internal val dataBaseManagerUtil: DataBaseManagerUtil?

    internal val entityMapper: DAOMapper = DAOMapper()

    internal val handlerThread: HandlerThread
        get() = HandlerThread("backgroundThread")

    init {
        context = dataUseCaseConfigBuilder.context
        okHttpBuilder = dataUseCaseConfigBuilder.okHttpBuilder
        okHttpCache = dataUseCaseConfigBuilder.okHttpCache
        baseUrl = dataUseCaseConfigBuilder.baseUrl
        isWithCache = dataUseCaseConfigBuilder.withCache
        isWithRealm = dataUseCaseConfigBuilder.withRealm
        cacheSize = dataUseCaseConfigBuilder.cacheSize
        cacheAmount = dataUseCaseConfigBuilder.cacheAmount
        timeUnit = dataUseCaseConfigBuilder.timeUnit
        postExecutionThread = dataUseCaseConfigBuilder.postExecutionThread
        dataBaseManagerUtil = dataUseCaseConfigBuilder.dataBaseManagerUtil
    }

    internal fun getBaseUrl(): String {
        return baseUrl ?: ""
    }

    internal fun getCacheSize(): Int {
        return if (cacheSize == 0 || cacheSize > 8192) 8192 else cacheSize
    }

    class Builder(internal val context: Context) {
        internal var okHttpBuilder: OkHttpClient.Builder? = null
        internal var okHttpCache: Cache? = null
        internal var baseUrl: String? = null
        internal var withCache: Boolean = false
        internal var withRealm: Boolean = false
        internal var cacheSize: Int = 0
        internal var cacheAmount: Int = 0
        internal var timeUnit: TimeUnit? = null
        internal var postExecutionThread: Scheduler? = null
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

        fun withRealm(): Builder {
            this.withRealm = true
            this.dataBaseManagerUtil = null
            return this
        }

        fun withCache(expiryAmount: Int, timeUnit: TimeUnit): Builder {
            this.withCache = true
            this.cacheAmount = expiryAmount
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
