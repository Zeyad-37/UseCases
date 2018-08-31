package com.zeyad.usecases.network;

import android.support.annotation.NonNull;

import com.zeyad.usecases.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

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
        assertThat(
                getCurrentSetRestApiWithoutCache(mApiConnection),
                is(equalTo(mRestApiWithoutCache)));
    }

    @Test
    public void testProvideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor =
                ApiConnection.Companion.provideHttpLoggingInterceptor();
        assertThat(
                httpLoggingInterceptor.getLevel(),
                is(
                        equalTo(
                                BuildConfig.DEBUG
                                        ? HttpLoggingInterceptor.Level.BODY
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
    public void testDynamicPostList() {
        mApiConnection.dynamicPost(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicPost(eq(mValidUrl), eq(mMockedRequestBody));
    }

    @Test
    public void testDynamicPutObject() {
        mApiConnection.dynamicPut(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicPut(eq(mValidUrl), eq(mMockedRequestBody));
    }

    @Test
    public void testDynamicPutList() {
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
    public void testDynamicDeleteList() {
        mApiConnection.dynamicDelete(mValidUrl);
        Mockito.verify(mRestApiWithoutCache).dynamicDelete(eq(mValidUrl));
    }

    @Test
    public void testDynamicDeleteObject() {
        mApiConnection.dynamicDelete(mValidUrl);
        Mockito.verify(mRestApiWithoutCache).dynamicDelete(eq(mValidUrl));
    }

    private RestApi getCurrentSetRestApiWithoutCache(@NonNull ApiConnection apiConnection) {
        return apiConnection.getRestApiWithoutCache();
    }

    private RestApi getCurrentSetRestApiWithCache(@NonNull ApiConnection apiConnection) {
        return apiConnection.getRestApiWithCache();
    }

    @NonNull
    private ApiConnection getApiImplementation(
            RestApi restApiWithoutCache, RestApi restApiWithCache) {
        return new ApiConnection(restApiWithoutCache, restApiWithCache);
    }
}

