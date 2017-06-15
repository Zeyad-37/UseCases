package com.zeyad.usecases.api;

import android.content.Context;
import android.support.test.rule.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author by ZIaDo on 5/14/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DataServiceFactoryTest {
    private final String URL = "https://api.github.com/";
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
        cache =
                new Cache(
                        new File("/data/data/com/zeyad/usecases/cache/", "http-cache"),
                        10 * 1024 * 1024);
        mDataServiceConfig =
                new DataServiceConfig.Builder(mockContext)
                        .baseUrl(URL)
                        .cacheSize(cacheSize)
                        .okHttpBuilder(builder)
                        .okhttpCache(cache)
                        .postExecutionThread(null)
                        .withRealm()
                        .build();
    }

    @Test
    public void init() throws Exception {
        DataServiceFactory.init(mDataServiceConfig);
        assertNotNull(com.zeyad.usecases.Config.getApiConnection());
        assertNotNull(com.zeyad.usecases.Config.getBackgroundThread());
        assertNotNull(com.zeyad.usecases.Config.getGson());
        assertNotNull(com.zeyad.usecases.Config.getInstance());
        assertNotNull(DataServiceFactory.getInstance());
        assertEquals(com.zeyad.usecases.Config.getBaseURL(), URL);
    }
}
