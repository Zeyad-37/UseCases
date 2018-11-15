package com.zeyad.usecases.api

import android.arch.persistence.room.RoomDatabase
import android.content.Context
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
data class DataServiceConfig internal constructor(val context: Context,
                                                  val okHttpBuilder: OkHttpClient.Builder?,
                                                  val okHttpCache: Cache? = null,
                                                  val baseUrl: String = "",
                                                  val isWithCache: Boolean = false,
                                                  val cacheSize: Int = 8192,
                                                  val cacheDuration: Long = 3,
                                                  val timeUnit: TimeUnit = TimeUnit.SECONDS,
                                                  val withSQL: Boolean = false,
                                                  val postExecutionThread: Scheduler = Schedulers.io(),
                                                  val dataBaseManagerUtil: DataBaseManagerUtil? = null,
                                                  val db: RoomDatabase? = null,
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
            dataUseCaseConfigBuilder.withSQLite,
            dataUseCaseConfigBuilder.postExecutionThread,
            dataUseCaseConfigBuilder.dataBaseManagerUtil,
            dataUseCaseConfigBuilder.db
    )

    class Builder(internal val context: Context) {
        internal var okHttpBuilder: OkHttpClient.Builder? = null
        internal var okHttpCache: Cache? = null
        internal var baseUrl: String = ""
        internal var withCache: Boolean = false
        internal var withSQLite: Boolean = false
        internal var cacheSize: Int = 0
        internal var cacheDuration: Long = 0
        internal var timeUnit: TimeUnit = TimeUnit.SECONDS
        internal var postExecutionThread: Scheduler = Schedulers.io()
        internal var dataBaseManagerUtil: DataBaseManagerUtil? = null
        internal var db: RoomDatabase? = null

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

        fun okHttpCache(cache: Cache): Builder {
            this.okHttpCache = cache
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
            this.withSQLite = true
            return this
        }

        fun build(): DataServiceConfig {
            return DataServiceConfig(this)
        }
    }
}