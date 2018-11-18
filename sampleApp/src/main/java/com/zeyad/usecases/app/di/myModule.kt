package com.zeyad.usecases.app.di

import android.content.Context
import android.util.Log
import com.zeyad.usecases.api.DataServiceConfig
import com.zeyad.usecases.api.DataServiceFactory
import com.zeyad.usecases.api.IDataService
import com.zeyad.usecases.app.AppDatabase
import com.zeyad.usecases.app.BuildConfig
import com.zeyad.usecases.app.screens.user.User
import com.zeyad.usecases.app.screens.user.detail.Repository
import com.zeyad.usecases.app.screens.user.detail.UserDetailVM
import com.zeyad.usecases.app.screens.user.list.UserListVM
import com.zeyad.usecases.app.utils.Constants.URLS.API_BASE_URL
import com.zeyad.usecases.db.BaseDao
import com.zeyad.usecases.db.DaoResolver
import com.zeyad.usecases.db.DataBaseManager
import com.zeyad.usecases.db.RoomManager
import com.zeyad.usecases.utils.DataBaseManagerUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import java.util.concurrent.TimeUnit

val myModule: Module = applicationContext {
    viewModel { UserListVM(get()) }
    viewModel { UserDetailVM(get()) }

    bean { createDataService(get(), get()) }

    bean { createDataBase(get()) }
}

fun createDataBase(context: Context): AppDatabase = AppDatabase.getInstance(context)

fun createDataService(context: Context, db: AppDatabase): IDataService {
    return DataServiceFactory(DataServiceConfig.Builder(context)
            .baseUrl(API_BASE_URL)
            .okHttpBuilder(getOkHttpBuilder())
            .withRoom(object : DataBaseManagerUtil {
                override fun getDataBaseManager(dataClass: Class<*>): DataBaseManager? {
                    return RoomManager(db, object : DaoResolver {
                        override fun <E> getDao(dataClass: Class<E>): BaseDao<E> {
                            return when (dataClass) {
                                User::class.java -> db.userDao() as BaseDao<E>
                                Repository::class.java -> db.repoDao() as BaseDao<E>
                                else -> throw IllegalArgumentException("")
                            }
                        }
                    })
                }
            })
            .build())
            .getInstance()
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
