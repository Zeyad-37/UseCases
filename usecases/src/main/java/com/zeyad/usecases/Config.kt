package com.zeyad.usecases

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.zeyad.usecases.network.ApiConnection
import com.zeyad.usecases.stores.CloudStore
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object Config {
    val gson: Gson = Gson()
    var baseURL: String = ""
    var cacheTimeUnit: TimeUnit = TimeUnit.SECONDS
    var backgroundThread: Scheduler = Schedulers.io()
    var apiConnection: ApiConnection? = null
    var cloudStore: CloudStore? = null
    var withCache: Boolean = false
    var withSQLite: Boolean = false
    var useApiWithCache: Boolean = false
    var cacheDuration: Long = 0
    lateinit var context: Context
}