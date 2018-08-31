package com.zeyad.usecases.services.jobs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.TestRealmModel;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.requests.PostRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PostTest {
    @Nullable
    private final ResponseBody RESPONSE_BODY = mock(ResponseBody.class);
    private final InputStream INPUT_STREAM = mock(InputStream.class);
    private final TestRealmModel TEST_MODEL = new TestRealmModel(1, "123");
    private Context mockedContext;
    private ApiConnection apiConnection;

    @Before
    public void setUp() {
        mockedContext = mock(Context.class);
        apiConnection = createRestApi();
    }

    @After
    public void tearDown() {
        reset(RESPONSE_BODY, INPUT_STREAM);
    }

    @Test
    public void testPatchObject() throws JSONException {
        Post post = createPost(mockedContext,
                createPostRequestForJsonObject(PostRequest.PATCH),
                        apiConnection,
                        3);
        post.execute();
        verify(apiConnection, times(1)).dynamicPatch(anyString(), any(RequestBody.class));
    }

    @Test
    public void testPostObject() throws JSONException {
        Post post = createPost(mockedContext,
                createPostRequestForJsonObject(PostRequest.POST),
                        apiConnection,
                        3);
        post.execute();
        verify(apiConnection, times(1)).dynamicPost(anyString(), any(RequestBody.class));
    }

    @Test
    public void testPostList() throws JSONException {
        Post post = createPost(
                mockedContext,
                        createPostRequestForJsonArray(PostRequest.POST),
                        apiConnection,
                        3);
        post.execute();
        verify(apiConnection, times(1)).dynamicPost(anyString(), any(RequestBody.class));
    }

    @Test
    public void testPutObject() throws JSONException {
        Post post = createPost(mockedContext,
                createPostRequestForJsonObject(PostRequest.PUT),
                        apiConnection,
                        3);
        post.execute();
        verify(apiConnection, times(1)).dynamicPut(anyString(), any(RequestBody.class));
    }

    @Test
    public void testPutList() throws JSONException {
        Post post = createPost(mockedContext,
                        createPostRequestForJsonArray(PostRequest.PUT),
                        apiConnection,
                        3);
        post.execute();
        verify(apiConnection, times(1)).dynamicPut(anyString(), any(RequestBody.class));
    }

    @Test
    public void testDeleteObject() throws JSONException {
        Post post = createPost(mockedContext,
                createPostRequestForJsonObject(PostRequest.DELETE),
                        apiConnection,
                        3);
        post.execute();
        verify(apiConnection, times(1)).dynamicDelete(anyString());
    }

    @Test
    public void testDeleteList() throws JSONException {
        Post post = createPost(mockedContext,
                        createPostRequestForJsonArray(PostRequest.DELETE),
                        apiConnection,
                        3);
        post.execute();
        verify(apiConnection, times(1)).dynamicDelete(anyString());
    }

    @Test
    public void testReQueue() throws JSONException {
        Post post = createPost(mockedContext,
                        createPostRequestForJsonArray(PostRequest.DELETE),
                        apiConnection,
                        0);
//        Mockito.doNothing().when(utils).queuePostCore(any(), any(PostRequest.class), anyInt());
//        post.queuePost();
//        verify(utils, times(1)).queuePostCore(any(), any(PostRequest.class), anyInt());
    }

    //--------------------------------------------------------------------------------------------//

    @NonNull
    private Post createPost(
            Context context, PostRequest postRequest, ApiConnection apiConnection, int trailCount) {
        return new Post(context, postRequest, apiConnection, trailCount);
    }

    private String getValidUrl() {
        return "http://www.google.com";
    }

    private String getValidColumnName() {
        return "id";
    }

    @NonNull
    private Class getValidDataClass() {
        return TestRealmModel.class;
    }

    private ApiConnection createRestApi() {
        final Flowable<Object> OBJECT_OBSERVABLE = getObjectObservable();
        apiConnection = mock(ApiConnection.class);
        when(apiConnection.dynamicDownload(anyString())).thenReturn(getResponseBodyObservable());
        when(apiConnection.dynamicDelete(anyString())).thenReturn(OBJECT_OBSERVABLE);
        when(apiConnection.dynamicGetObject(any(), anyBoolean())).thenReturn(OBJECT_OBSERVABLE);
        when(apiConnection.dynamicGetObject(any())).thenReturn(OBJECT_OBSERVABLE);
//        when(apiConnection.dynamicGetList(any())).thenReturn(getListObservable());
//        when(apiConnection.dynamicGetList(any(), anyBoolean())).thenReturn(getListObservable());
        when(apiConnection.dynamicPost(any(), any())).thenReturn(OBJECT_OBSERVABLE);
        when(apiConnection.dynamicPut(any(), any())).thenReturn(OBJECT_OBSERVABLE);
        when(apiConnection.dynamicPatch(any(), any())).thenReturn(OBJECT_OBSERVABLE);
        when(apiConnection.dynamicUpload(any(), any(Map.class), anyListOf(MultipartBody.Part.class)))
                .thenReturn(OBJECT_OBSERVABLE);
        return apiConnection;
    }

    @NonNull
    private Flowable<List> getListObservable() {
        return Flowable.just(Collections.singletonList(createTestModel()));
    }

    @NonNull
    private Flowable<Object> getObjectObservable() {
        return Flowable.just(createTestModel());
    }

    @NonNull
    private TestRealmModel createTestModel() {
        return TEST_MODEL;
    }

    private PostRequest createPostRequestForJsonObject(String method) throws JSONException {
        return new PostRequest.Builder(getValidDataClass(), false)
                .payLoad(new JSONObject("{\"login\": \"Zeyad-37\", \"id\": 5938141, \"avatar_url\": " +
                        "\"https://avatars2.githubusercontent.com/u/5938141?v=3\"}"))
                .idColumnName(getValidColumnName(), int.class)
                .url(getValidUrl())
                .method(method)
                .build();
    }

    private PostRequest createPostRequestForJsonArray(String method) throws JSONException {
        return new PostRequest.Builder(getValidDataClass(), false)
                .payLoad(new JSONArray("[ \"Ford\", \"BMW\", \"Fiat\" ]"))
                .idColumnName(getValidColumnName(), int.class)
                .url(getValidUrl())
                .method(method)
                .build();
    }

    private Flowable<ResponseBody> getResponseBodyObservable() {
        return Flowable.fromCallable(this::getResponseBody);
    }

    @Nullable
    private ResponseBody getResponseBody() throws IOException {
        when(RESPONSE_BODY.byteStream()).thenReturn(getInputStreamReader());
        when(RESPONSE_BODY.contentLength()).thenReturn((long) (1096 * 1096));
        return RESPONSE_BODY;
    }

    private InputStream getInputStreamReader() throws IOException {
        when(INPUT_STREAM.read(any())).thenReturn(1096, 1096, 1096, -1);
        return INPUT_STREAM;
    }
}
