package com.zeyad.usecases.network;

import android.support.annotation.NonNull;

import com.zeyad.usecases.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

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
    private final MultipartBody.Part mMultipartBodyPart =
            MultipartBody.Part.create(mMockedRequestBody);
    private ApiConnection mApiConnection;
    private RestApi mRestApiWithCache;
    private RestApi mRestApiWithoutCache;

    @Before
    public void setUp() throws Exception {
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
    public void testProvideHttpLoggingInterceptor() throws Exception {
        HttpLoggingInterceptor httpLoggingInterceptor =
                ApiConnection.provideHttpLoggingInterceptor();
        assertThat(
                httpLoggingInterceptor.getLevel(),
                is(
                        equalTo(
                                BuildConfig.DEBUG
                                        ? HttpLoggingInterceptor.Level.BODY
                                        : HttpLoggingInterceptor.Level.NONE)));
    }

    @Test
    public void testDynamicDownload() throws Exception {
        mApiConnection.dynamicDownload(mValidUrl);
        Mockito.verify(mRestApiWithoutCache).dynamicDownload(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetObject_ifCorrectMethodOfCacheRestApiIsCalled_whenToCacheIsTrue()
            throws Exception {
        mApiConnection.dynamicGetObject(mValidUrl, true);
        Mockito.verify(mRestApiWithoutCache).dynamicGetObject(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetObject_ifCorrectMethodOfCacheRestApiIsCalled_whenToCacheIsFalse()
            throws Exception {
        mApiConnection.dynamicGetObject(mValidUrl, false);
        Mockito.verify(mRestApiWithoutCache).dynamicGetObject(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetObjectWithoutCacheSupport() throws Exception {
        mApiConnection.dynamicGetObject(mValidUrl);
        Mockito.verify(mRestApiWithoutCache).dynamicGetObject(mValidUrl);
    }

    @Test
    public void testDynamicGetListWithoutCacheSupport() throws Exception {
        mApiConnection.dynamicGetList(mValidUrl);
        Mockito.verify(mRestApiWithoutCache).dynamicGetList(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetListCache_ifCorrectMethodOfCacheRestApiIsCalled_whenToCacheIsTrue()
            throws Exception {
        mApiConnection.dynamicGetList(mValidUrl, true);
        Mockito.verify(mRestApiWithoutCache).dynamicGetList(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetListCache_ifCorrectMethodOfCacheRestApiIsCalled_whenToCacheIsFalse()
            throws Exception {
        mApiConnection.dynamicGetList(mValidUrl, false);
        Mockito.verify(mRestApiWithoutCache).dynamicGetList(eq(mValidUrl));
    }

    @Test
    public void testDynamicPostObject() throws Exception {
        mApiConnection.dynamicPost(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicPost(eq(mValidUrl), eq(mMockedRequestBody));
    }

    @Test
    public void testDynamicPostList() throws Exception {
        mApiConnection.dynamicPost(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicPost(eq(mValidUrl), eq(mMockedRequestBody));
    }

    @Test
    public void testDynamicPutObject() throws Exception {
        mApiConnection.dynamicPut(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicPut(eq(mValidUrl), eq(mMockedRequestBody));
    }

    @Test
    public void testDynamicPutList() throws Exception {
        mApiConnection.dynamicPut(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicPut(eq(mValidUrl), eq(mMockedRequestBody));
    }

    @Test
    public void testUploadPartAndRequestBody() throws Exception {
        mApiConnection.dynamicUpload(mValidUrl, mPartMap, mMultipartBodyPart);
        Mockito.verify(mRestApiWithoutCache)
                .dynamicUpload(eq(mValidUrl), eq(mPartMap), eq(mMultipartBodyPart));
    }

    @Test
    public void testDynamicDeleteList() throws Exception {
        mApiConnection.dynamicDelete(mValidUrl);
        Mockito.verify(mRestApiWithoutCache).dynamicDelete(eq(mValidUrl));
    }

    @Test
    public void testDynamicDeleteObject() throws Exception {
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

