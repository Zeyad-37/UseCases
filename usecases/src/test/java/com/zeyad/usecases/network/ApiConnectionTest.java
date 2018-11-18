package com.zeyad.usecases.network;

import android.support.annotation.NonNull;

import com.zeyad.usecases.BuildConfig;
import com.zeyad.usecases.TestModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ApiConnectionTest {

    private final String mValidUrl = getValidUrl();
    private final RequestBody mMockedRequestBody = mock(RequestBody.class);
    private final Map<String, RequestBody> mPartMap = Mockito.mock(Map.class);
    private final List<MultipartBody.Part> mMultipartBodyParts = Collections.singletonList(MultipartBody.Part.create(mMockedRequestBody));
    private ApiConnection mApiConnection;
    private RestApi mRestApiWithCache;
    private RestApi mRestApiWithoutCache;

    @Before
    public void setUp() {
        mRestApiWithCache = mock(RestApi.class);
        mRestApiWithoutCache = mock(RestApi.class);

        when(mRestApiWithoutCache.dynamicGetList(mValidUrl))
                .thenReturn(Flowable.just(Collections.singletonList(new TestModel())));

        when(mRestApiWithoutCache.dynamicGetList(mValidUrl, true))
                .thenReturn(Flowable.just(Collections.singletonList(new TestModel())));
        when(mRestApiWithoutCache.dynamicGetList(mValidUrl, false))
                .thenReturn(Flowable.just(Collections.singletonList(new TestModel())));

        when(mRestApiWithoutCache.dynamicGetObject(mValidUrl))
                .thenReturn(Flowable.just(Collections.singletonList(new TestModel())));

        when(mRestApiWithoutCache.dynamicGetObject(mValidUrl, true))
                .thenReturn(Flowable.just(Collections.singletonList(new TestModel())));
        when(mRestApiWithoutCache.dynamicGetObject(mValidUrl, false))
                .thenReturn(Flowable.just(Collections.singletonList(new TestModel())));

        when(mRestApiWithoutCache.dynamicPost(mValidUrl, mMockedRequestBody))
                .thenReturn(Single.just(Collections.singletonList(new TestModel())));
        when(mRestApiWithoutCache.dynamicPut(mValidUrl, mMockedRequestBody))
                .thenReturn(Single.just(Collections.singletonList(new TestModel())));
        when(mRestApiWithoutCache.dynamicDelete(mValidUrl, mMockedRequestBody))
                .thenReturn(Single.just(Collections.singletonList(new TestModel())));
        when(mRestApiWithoutCache.dynamicUpload(mValidUrl, mPartMap, mMultipartBodyParts))
                .thenReturn(Single.just(Collections.singletonList(new TestModel())));

        mApiConnection = getApiImplementation(mRestApiWithoutCache, mRestApiWithCache);
    }

    @NonNull
    private String getValidUrl() {
        return "http://www.google.com";
    }

    @Test
    public void testSetupDoneSuccessfully() {
        assertThat(getCurrentSetRestApiWithCache(mApiConnection), is(notNullValue()));
        assertThat(getCurrentSetRestApiWithoutCache(mApiConnection), is(notNullValue()));
        assertThat(mRestApiWithCache, is(notNullValue()));
        assertThat(mRestApiWithoutCache, is(notNullValue()));
        assertThat(getCurrentSetRestApiWithCache(mApiConnection), is(equalTo(mRestApiWithCache)));
        assertThat(getCurrentSetRestApiWithoutCache(mApiConnection), is(equalTo(mRestApiWithoutCache)));
    }

    @Test
    public void testProvideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = ApiConnection.Companion.provideHttpLoggingInterceptor();
        assertThat(httpLoggingInterceptor.getLevel(),
                is(equalTo(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
                        : HttpLoggingInterceptor.Level.NONE)));
    }

    @Test
    public void testDynamicDownload() {
        mApiConnection.dynamicDownload(mValidUrl);
        Mockito.verify(mRestApiWithoutCache).dynamicDownload(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetObject_ifCorrectMethodOfCacheRestApiIsCalled_whenToCacheIsTrue() {
        mApiConnection.dynamicGetObject(mValidUrl, true);
        Mockito.verify(mRestApiWithoutCache).dynamicGetObject(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetObject_ifCorrectMethodOfCacheRestApiIsCalled_whenToCacheIsFalse() {
        mApiConnection.dynamicGetObject(mValidUrl, false);
        Mockito.verify(mRestApiWithoutCache).dynamicGetObject(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetObjectWithoutCacheSupport() {
        mApiConnection.dynamicGetObject(mValidUrl);
        Mockito.verify(mRestApiWithoutCache).dynamicGetObject(mValidUrl);
    }

    @Test
    public void testDynamicGetListWithoutCacheSupport() {
        mApiConnection.dynamicGetList(mValidUrl);
        Mockito.verify(mRestApiWithoutCache).dynamicGetList(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetListCache_ifCorrectMethodOfCacheRestApiIsCalled_whenToCacheIsTrue() {
        mApiConnection.dynamicGetList(mValidUrl, true);
        Mockito.verify(mRestApiWithoutCache).dynamicGetList(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetListCache_ifCorrectMethodOfCacheRestApiIsCalled_whenToCacheIsFalse() {
        mApiConnection.dynamicGetList(mValidUrl, false);
        Mockito.verify(mRestApiWithoutCache).dynamicGetList(eq(mValidUrl));
    }

    @Test
    public void testDynamicPostObject() {
        mApiConnection.dynamicPost(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicPost(eq(mValidUrl), eq(mMockedRequestBody));
    }

    @Test
    public void testDynamicPutObject() {
        mApiConnection.dynamicPut(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicPut(eq(mValidUrl), eq(mMockedRequestBody));
    }

    @Test
    public void testUploadPartAndRequestBody() {
        mApiConnection.dynamicUpload(mValidUrl, mPartMap, mMultipartBodyParts);
        Mockito.verify(mRestApiWithoutCache)
                .dynamicUpload(eq(mValidUrl), eq(mPartMap), eq(mMultipartBodyParts));
    }

    @Test
    public void testDynamicDeleteObject() {
        mApiConnection.dynamicDelete(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicDelete(eq(mValidUrl), eq(mMockedRequestBody));
    }

    private RestApi getCurrentSetRestApiWithoutCache(@NonNull ApiConnection apiConnection) {
        return apiConnection.getRestApiWithoutCache();
    }

    private RestApi getCurrentSetRestApiWithCache(@NonNull ApiConnection apiConnection) {
        return apiConnection.getRestApiWithCache();
    }

    @NonNull
    private ApiConnection getApiImplementation(RestApi restApiWithoutCache, RestApi restApiWithCache) {
        return new ApiConnection(restApiWithoutCache, restApiWithCache);
    }
}
