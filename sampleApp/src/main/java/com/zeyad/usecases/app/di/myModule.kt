package com.zeyad.usecases.app.di

import android.content.Context
import android.os.HandlerThread
import android.util.Log
import com.zeyad.usecases.api.DataServiceConfig
import com.zeyad.usecases.api.DataServiceFactory
import com.zeyad.usecases.api.IDataService
import com.zeyad.usecases.app.BuildConfig
import com.zeyad.usecases.app.screens.user.detail.UserDetailVM
import com.zeyad.usecases.app.screens.user.list.UserListVM
import com.zeyad.usecases.app.utils.Constants.URLS.API_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import java.util.concurrent.TimeUnit

val myModule: Module = applicationContext {
    viewModel { UserListVM(get()) }
    viewModel { UserDetailVM(get()) }

    bean { createDataService(get()) }
}

fun createDataService(context: Context): IDataService {
    return DataServiceFactory(DataServiceConfig.Builder(context)
            .baseUrl(API_BASE_URL)
            .okHttpBuilder(getOkHttpBuilder())
            .withRealm(HandlerThread("BackgroundHandlerThread"))
            .build()).instance!!
}

fun getOkHttpBuilder(): OkHttpClient.Builder {
    return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor { Log.d("NetworkInfo", it) }
                    .setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE))
            .connectTimeout(15L, TimeUnit.SECONDS)
            .writeTimeout(15L, TimeUnit.SECONDS)
            .readTimeout(15L, TimeUnit.SECONDS)
}
