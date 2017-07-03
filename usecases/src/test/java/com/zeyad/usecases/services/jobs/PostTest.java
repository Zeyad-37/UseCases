package com.zeyad.usecases.services.jobs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.TestRealmModel;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.requests.PostRequest;
import com.zeyad.usecases.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PostTest {
    @Nullable private final ResponseBody RESPONSE_BODY = mock(ResponseBody.class);
    private final InputStream INPUT_STREAM = mock(InputStream.class);
    private final TestRealmModel TEST_MODEL = new TestRealmModel(1, "123");
    private Context mockedContext;
    private ApiConnection apiConnection;
    private Utils utils;

    @Before
    public void setUp() {
        mockedContext = mock(Context.class);
        apiConnection = createRestApi();
        utils = mock(Utils.class);
    }

    @After
    public void tearDown() {
        reset(RESPONSE_BODY, INPUT_STREAM);
    }

    @Test
    public void testPatchObject() {
        Post post =
                createPost(
                        mockedContext,
                        createPostRequestForHashmap(PostRequest.PATCH),
                        apiConnection,
                        3);
        post.execute();
        verify(apiConnection).dynamicPatch(anyString(), any(RequestBody.class));
    }

    @Test
    public void testPostObject() {
        Post post =
                createPost(
                        mockedContext,
                        createPostRequestForHashmap(PostRequest.POST),
                        apiConnection,
                        3);
        post.execute();
        verify(apiConnection).dynamicPost(anyString(), any(RequestBody.class));
    }

    @Test
    public void testPostList() throws JSONException {
        Post post =
                createPost(
                        mockedContext,
                        createPostRequestForJsonArray(PostRequest.POST),
                        apiConnection,
                        3);
        post.execute();
        verify(apiConnection).dynamicPost(anyString(), any(RequestBody.class));
    }

    @Test
    public void testPutObject() {
        Post post =
                createPost(
                        mockedContext,
                        createPostRequestForHashmap(PostRequest.PUT),
                        apiConnection,
                        3);
        post.execute();
        verify(apiConnection).dynamicPut(anyString(), any(RequestBody.class));
    }

    @Test
    public void testPutList() throws JSONException {
        Post post =
                createPost(
                        mockedContext,
                        createPostRequestForJsonArray(PostRequest.PUT),
                        apiConnection,
                        3);
        post.execute();
        verify(apiConnection).dynamicPut(anyString(), any(RequestBody.class));
    }

    @Test
    public void testDeleteObject() {
        Post post =
                createPost(
                        mockedContext,
                        createPostRequestForHashmap(PostRequest.DELETE),
                        apiConnection,
                        3);
        post.execute();
        verify(apiConnection).dynamicDelete(anyString(), any(RequestBody.class));
    }

    @Test
    public void testDeleteList() throws JSONException {
        Post post =
                createPost(
                        mockedContext,
                        createPostRequestForJsonArray(PostRequest.DELETE),
                        apiConnection,
                        3);
        post.execute();
        verify(apiConnection).dynamicDelete(anyString(), any(RequestBody.class));
    }

    @Test
    public void testReQueue() throws JSONException {
        Post post =
                createPost(
                        mockedContext,
                        createPostRequestForJsonArray(PostRequest.DELETE),
                        apiConnection,
                        0);
        Mockito.doNothing().when(utils).queuePostCore(any(), any(PostRequest.class), anyInt());
        post.queuePost();
        verify(utils, times(1)).queuePostCore(any(), any(PostRequest.class), anyInt());
    }

    //--------------------------------------------------------------------------------------------//

    @NonNull
    private Post createPost(
            Context context, PostRequest postRequest, ApiConnection apiConnection, int trailCount) {
        return new Post(context, postRequest, apiConnection, trailCount, utils);
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
        when(apiConnection.dynamicDelete(anyString(), any())).thenReturn(OBJECT_OBSERVABLE);
        when(apiConnection.dynamicGetObject(any(), anyBoolean())).thenReturn(OBJECT_OBSERVABLE);
        when(apiConnection.dynamicGetObject(any())).thenReturn(OBJECT_OBSERVABLE);
        when(apiConnection.dynamicGetList(any())).thenReturn(getListObservable());
        when(apiConnection.dynamicGetList(any(), anyBoolean())).thenReturn(getListObservable());
        when(apiConnection.dynamicPost(any(), any())).thenReturn(OBJECT_OBSERVABLE);
        when(apiConnection.dynamicPut(any(), any())).thenReturn(OBJECT_OBSERVABLE);
        when(apiConnection.dynamicPatch(any(), any())).thenReturn(OBJECT_OBSERVABLE);
        when(apiConnection.dynamicUpload(any(), any(Map.class), any(MultipartBody.Part.class)))
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

    private PostRequest createPostRequestForHashmap(String method) {
        return new PostRequest.Builder(getValidDataClass(), false)
                .payLoad(new HashMap<>())
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
