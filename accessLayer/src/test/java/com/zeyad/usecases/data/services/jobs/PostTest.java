package com.zeyad.usecases.data.services.jobs;

import android.content.Context;

import com.zeyad.usecases.data.network.RestApi;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.utils.TestRealmModel;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author by ZIaDo on 2/16/17.
 */
public class PostTest {
    private Post post;
    private RestApi restApi;
    private Context context;
    private Observable<Object> objectObservable;

    static RestApiImpl createRestApi() throws IOException {
        Observable<Object> objectObservable = Observable.just(new TestRealmModel());
        Observable<List> listObservable = Observable.just(Collections.singletonList(new TestRealmModel()));
        InputStream inputStream = mock(InputStream.class);
        when(inputStream.read(any())).thenReturn(1096, 1096, 1096, -1);
        final ResponseBody responseBody = mock(ResponseBody.class);
//        when(responseBody.byteStream()).thenReturn(inputStream);
//        when(responseBody.contentLength()).thenReturn((long) (1096 * 1096));
        final RestApiImpl mock = mock(RestApiImpl.class);
        when(mock.dynamicDownload(anyString())).thenReturn(Observable.just(responseBody));
        when(mock.dynamicDelete(anyString(), any())).thenReturn(objectObservable);
        when(mock.dynamicGetObject(any(), anyBoolean())).thenReturn(objectObservable);
        when(mock.dynamicGetObject(any())).thenReturn(objectObservable);
        when(mock.dynamicGetList(any())).thenReturn(listObservable);
        when(mock.dynamicGetList(any(), anyBoolean())).thenReturn(listObservable);
        when(mock.dynamicPost(any(), any())).thenReturn(objectObservable);
        when(mock.dynamicPut(any(), any())).thenReturn(objectObservable);
        when(mock.dynamicUpload(any(), any(Map.class), any(MultipartBody.Part.class))).thenReturn(objectObservable);
        return mock;
    }

    @Before
    public void setUp() throws Exception {
        context = mock(Context.class);
        restApi = createRestApi();
        objectObservable = Observable.just(true);
    }

    @Test
    public void execute() throws Exception {
        when(restApi.dynamicPost(anyString(), any(RequestBody.class))).thenReturn(objectObservable);
        post = new Post(context, new PostRequest.PostRequestBuilder(TestRealmModel.class, false)
                .payLoad(new JSONObject("{}"))
                .idColumnName("")
                .url("")
                .method(PostRequest.POST)
                .build(), restApi, 0);
        post.execute();
        verify(restApi, times(1)).dynamicPost(anyString(), any(RequestBody.class));
    }
}
