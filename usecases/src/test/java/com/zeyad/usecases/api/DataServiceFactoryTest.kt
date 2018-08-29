package com.zeyad.usecases.api

import android.content.Context
import android.support.test.rule.BuildConfig
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author by ZIaDo on 5/14/17.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
class DataServiceFactoryTest {
    private val URL = "https://api.github.com/"
    private val cacheSize = 8192
    private var mDataServiceConfig: DataServiceConfig? = null
    private var mockContext: Context? = null
    private var builder: OkHttpClient.Builder? = null
    private var cache: Cache? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mockContext = mock(Context::class.java)
        builder = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
        cache = Cache(File("/data/data/com/zeyad/usecases/cache/", "http-cache"),
                (10 * 1024 * 1024).toLong())
        mDataServiceConfig = DataServiceConfig.Builder(mockContext!!)
                .baseUrl(URL)
                .cacheSize(cacheSize)
                .okHttpBuilder(builder!!)
                .okhttpCache(cache!!)
                .withRealm()
                .build()
    }

    @Test
    @Throws(Exception::class)
    fun init() {
        DataServiceFactory.init(mDataServiceConfig!!)
        assertNotNull(com.zeyad.usecases.Config.apiConnection)
        assertNotNull(com.zeyad.usecases.Config.backgroundThread)
        assertNotNull(com.zeyad.usecases.Config.gson)
        assertNotNull(com.zeyad.usecases.Config.instance)
        assertNotNull(DataServiceFactory.instance)
        assertEquals(com.zeyad.usecases.Config.baseURL, URL)
    }
}
