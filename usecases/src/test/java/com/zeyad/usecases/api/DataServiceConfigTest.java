package com.zeyad.usecases.api;

import android.content.Context;
import android.os.HandlerThread;
import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.mapper.DAOMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

/**
 * @author by ZIaDo on 5/14/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DataServiceConfigTest {
    private final String URL = "www.google.com";
    private final int cacheSize = 8192;
    private DataServiceConfig mDataServiceConfig;
    private Context mockContext;
    private OkHttpClient.Builder builder;
    private Cache cache;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        builder =
                new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS);
        cache = new Cache(new File("", "http-cache"), 10 * 1024 * 1024);
        mDataServiceConfig =
                new DataServiceConfig.Builder(mockContext)
                        .baseUrl(URL)
                        .cacheSize(cacheSize)
                        .okHttpBuilder(builder)
                        .okhttpCache(cache)
                        .postExecutionThread(null)
                        .withCache(3, TimeUnit.MINUTES)
                        .withRealm()
                        .build();
    }

    @Test
    public void getContext() throws Exception {
        assertThat(mDataServiceConfig.getContext(), is(equalTo(mockContext)));
    }

    @Test
    public void getEntityMapper() throws Exception {
        assertThat(mDataServiceConfig.getEntityMapper().getClass(), is(equalTo(DAOMapper.class)));
    }

    @Test
    public void getPostExecutionThread() throws Exception {
        assertThat(mDataServiceConfig.getPostExecutionThread(), is(equalTo(null)));
    }

    @Test
    public void getOkHttpBuilder() throws Exception {
        assertThat(mDataServiceConfig.getOkHttpBuilder(), is(equalTo(builder)));
    }

    @Test
    public void getOkHttpCache() throws Exception {
        assertThat(mDataServiceConfig.getOkHttpCache(), is(equalTo(cache)));
    }

    @Test
    public void getBaseUrl() throws Exception {
        assertThat(mDataServiceConfig.getBaseUrl(), is(equalTo(URL)));
    }

    @Test
    public void isWithRealm() throws Exception {
        assertThat(mDataServiceConfig.isWithRealm(), is(equalTo(true)));
    }

    @Test
    public void isWithCache() throws Exception {
        assertThat(mDataServiceConfig.isWithCache(), is(equalTo(true)));
    }

    @Test
    public void getCacheSize() throws Exception {
        assertThat(mDataServiceConfig.getCacheSize(), is(equalTo(cacheSize)));
    }

    @Test
    public void getCacheAmount() throws Exception {
        assertThat(mDataServiceConfig.getCacheAmount(), is(equalTo(3)));
    }

    @Test
    public void getTimeUnit() throws Exception {
        assertThat(mDataServiceConfig.getTimeUnit(), is(equalTo(TimeUnit.MINUTES)));
    }

    @Test
    public void getHandlerThread() throws Exception {
        assertThat(
                mDataServiceConfig.getHandlerThread().getClass(), is(equalTo(HandlerThread.class)));
    }
}
