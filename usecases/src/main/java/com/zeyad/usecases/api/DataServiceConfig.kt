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
data class DataServiceConfig(val context: Context,
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
                             val entityMapper: DAOMapper = DAOMapper())