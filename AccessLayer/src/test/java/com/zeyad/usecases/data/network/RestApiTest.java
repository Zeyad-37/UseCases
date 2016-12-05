package com.zeyad.usecases.data.network;

import android.support.annotation.NonNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(JUnit4.class)
public class RestApiTest {

    private final Map mMultipartBodyPart = Mockito.mock(Map.class);
    private RestApi mRestApi;
    private IApiConnection mMockedApiConnection;
    @NonNull
    private String mValidUrl = "some strong";
    @NonNull
    private RequestBody mRequestBody = MultipartBody.create(MediaType.parse("text/html"), new byte[0]);
    @NonNull
    private MultipartBody.Part mMultipart = MultipartBody.Part.create(mRequestBody);

    @Before
    public void setUp() throws Exception {
        mMockedApiConnection = RestApiTestRobot.createMockedApiConnection();
        mRestApi = getRestApiImplementation(mMockedApiConnection);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDynamicGetObjectCacheDisabled() throws Exception {
        assertThat(mRestApi.dynamicGetObject(mValidUrl), is(equalTo(mMockedApiConnection.dynamicGetObject(mValidUrl))));
    }

    @Test
    public void testDynamicGetObjectCacheEnabled_ifCacheIsTrue() throws Exception {
        assertThat(mRestApi.dynamicGetObject(mValidUrl, true), is(equalTo(mMockedApiConnection.dynamicGetObject(mValidUrl, true))));
    }

    @Test
    public void testDynamicGetObjectCacheEnabled_ifCacheIsFalse() throws Exception {
        assertThat(mRestApi.dynamicGetObject(mValidUrl, false), is(equalTo(mMockedApiConnection.dynamicGetObject(mValidUrl, false))));
    }

    @Test
    public void testDynamicGetListCacheDisabled() throws Exception {
        assertThat(mRestApi.dynamicGetList(mValidUrl), is(equalTo(mMockedApiConnection.dynamicGetList(mValidUrl))));
    }

    @Test
    public void testDynamicGetListCacheEnabled_ifCacheIsTrue() throws Exception {
        assertThat(mRestApi.dynamicGetList(mValidUrl, true), is(equalTo(mMockedApiConnection.dynamicGetList(mValidUrl, true))));
    }

    @Test
    public void testDynamicGetListCacheDisabled_ifCacheIsFalse() throws Exception {
        assertThat(mRestApi.dynamicGetList(mValidUrl, false), is(equalTo(mMockedApiConnection.dynamicGetList(mValidUrl, false))));
    }

    @Test
    public void testDynamicPostObject() throws Exception {
        assertThat(mRestApi.dynamicPostObject(mValidUrl, mRequestBody), is(equalTo(mMockedApiConnection.dynamicPostObject(mValidUrl, mRequestBody))));
    }

    @Test
    public void testDynamicPostList() throws Exception {
        assertThat(mRestApi.dynamicPostList(mValidUrl, mRequestBody), is(equalTo(mMockedApiConnection.dynamicPostList(mValidUrl, mRequestBody))));
    }

    @Test
    public void testDynamicPutObject() throws Exception {
        assertThat(mRestApi.dynamicPutObject(mValidUrl, mRequestBody), is(equalTo(mMockedApiConnection.dynamicPutObject(mValidUrl, mRequestBody))));
    }

    @Test
    public void testDynamicPutList() throws Exception {
        assertThat(mRestApi.dynamicPutList(mValidUrl, mRequestBody), is(equalTo(mMockedApiConnection.dynamicPutList(mValidUrl, mRequestBody))));
    }

    @Test
    public void testDynamicDeleteObject() throws Exception {
        assertThat(mRestApi.dynamicDeleteObject(mValidUrl, mRequestBody), is(equalTo(mMockedApiConnection.dynamicDeleteObject(mValidUrl, mRequestBody))));
    }

    @Test
    public void testDynamicDeleteList() throws Exception {
        assertThat(mRestApi.dynamicDeleteList(mValidUrl, mRequestBody), is(equalTo(mMockedApiConnection.dynamicDeleteList(mValidUrl, mRequestBody))));
    }

    @Test
    public void testDynamicDownload() throws Exception {
        assertThat(mRestApi.dynamicDownload(mValidUrl), is(equalTo(mMockedApiConnection.dynamicDownload(mValidUrl))));
    }

    @Test
    public void testUpload() throws Exception {
        assertThat(mRestApi.upload(mValidUrl, mMultipartBodyPart, mMultipart),
                is(equalTo(mMockedApiConnection.upload(mValidUrl, mMultipartBodyPart, mMultipart))));
    }

    @NonNull
    public RestApi getRestApiImplementation(IApiConnection mockedApiConnection) {
        return new RestApiImpl(mockedApiConnection);
    }
}