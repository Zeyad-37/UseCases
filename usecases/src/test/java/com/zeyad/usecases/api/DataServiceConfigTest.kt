package com.zeyad.usecases.api

import android.content.Context
import android.support.test.rule.BuildConfig
import com.zeyad.usecases.db.DataBaseManager
import com.zeyad.usecases.mapper.DAOMapper
import com.zeyad.usecases.utils.DataBaseManagerUtil
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author by ZIaDo on 5/14/17.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [25])
class DataServiceConfigTest {
    private val URL = "www.google.com"
    private val cacheSize = 8192
    private lateinit var mDataServiceConfig: DataServiceConfig
    private lateinit var mockContext: Context
    private lateinit var builder: OkHttpClient.Builder
    private lateinit var cache: Cache

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mockContext = RuntimeEnvironment.application // mock(Context::class.java)
        builder = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
        cache = Cache(File("", "http-cache"), (10 * 1024 * 1024).toLong())
        mDataServiceConfig = DataServiceConfig.Builder(mockContext)
                .baseUrl(URL)
                .okHttpBuilder(builder)
                .okHttpCache(cache)
                .withCache(3, TimeUnit.MINUTES, cacheSize)
                .withRoom(object : DataBaseManagerUtil {
                    override fun getDataBaseManager(dataClass: Class<*>): DataBaseManager? {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
                .build()
    }

    @Test
    @Throws(Exception::class)
    fun getContext() {
        assertThat(mDataServiceConfig.context, `is`(equalTo<Context>(mockContext)))
    }

    @Test
    @Throws(Exception::class)
    fun getEntityMapper() {
        assertThat<Class<out DAOMapper>>(mDataServiceConfig.entityMapper.javaClass, `is`(equalTo<Class<out DAOMapper>>(DAOMapper::class.java)))
    }

    @Test
    @Throws(Exception::class)
    fun getPostExecutionThread() {
        assertThat<Scheduler>(mDataServiceConfig.postExecutionThread,
                `is`<Scheduler>(equalTo<Scheduler>(Schedulers.io())))
    }

    @Test
    @Throws(Exception::class)
    fun getOkHttpBuilder() {
        assertThat(mDataServiceConfig.okHttpBuilder, `is`(equalTo(builder)))
    }

    @Test
    @Throws(Exception::class)
    fun getOkHttpCache() {
        assertThat(mDataServiceConfig.okHttpCache, `is`(equalTo(cache)))
    }

    @Test
    @Throws(Exception::class)
    fun getBaseUrl() {
        assertThat(mDataServiceConfig.baseUrl, `is`(equalTo(URL)))
    }

    @Test
    @Throws(Exception::class)
    fun isWithRealm() {
        assertThat(mDataServiceConfig.isWithCache, `is`(equalTo(true)))
    }

    @Test
    @Throws(Exception::class)
    fun isWithCache() {
        assertThat(mDataServiceConfig.isWithCache, `is`(equalTo(true)))
    }

    @Test
    @Throws(Exception::class)
    fun getCacheSize() {
        assertThat(mDataServiceConfig.cacheSize, `is`(equalTo(cacheSize)))
    }

    @Test
    @Throws(Exception::class)
    fun getCacheAmount() {
        assertThat(mDataServiceConfig.cacheDuration, `is`(equalTo(3L)))
    }

    @Test
    @Throws(Exception::class)
    fun getTimeUnit() {
        assertThat(mDataServiceConfig.timeUnit, `is`(equalTo(TimeUnit.MINUTES)))
    }
}
