package com.zeyad.genericusecase.data.services.jobs;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.zeyad.genericusecase.data.network.RestApi;
import com.zeyad.genericusecase.data.network.RestApiImpl;
import com.zeyad.genericusecase.data.services.realm_test_models.TestModel;
import com.zeyad.genericusecase.data.services.realm_test_models.TestViewModel;
import com.zeyad.genericusecase.data.utils.Utils;
import com.zeyad.genericusecase.domain.interactors.PostRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class PostTestRobot {

    @Nullable
    private static final JobScheduler JOB_SCHEDULER;
    private static final ResponseBody RESPONSE_BODY = mock(ResponseBody.class);
    private static final InputStream INPUT_STREAM = mock(InputStream.class);
    private static final TestModel TEST_MODEL = new TestModel(1, "123");

    static {
        JOB_SCHEDULER = Utils.hasLollipop() ? mock(JobScheduler.class) : null;
    }

    @NonNull
    static Post createPost(Context context, PostRequest postRequest, RestApi restApi, int trailCount,
                           boolean hasLollipop, boolean isPlayServicesAvailable, boolean networkAvailable,
                           GcmNetworkManager gcmNetworkManager) {
        return new Post(context, postRequest, restApi
                , trailCount, isPlayServicesAvailable, networkAvailable, gcmNetworkManager);
    }

    static String getValidUrl() {
        return "http://www.google.com";
    }

    @NonNull
    static Class getValidDomainClass() {
        return TestViewModel.class;
    }

    static int getValidColumnId() {
        return 12;
    }

    @NonNull
    static HashMap<String, Object> getValidHashmap() {
        return new HashMap<>();
    }

    @NonNull
    static Class getPresentationClass() {
        return TestModel.class;
    }

    static String getValidColumnName() {
        return "id";
    }

    @NonNull
    static Class getValidDataClass() {
        return TestModel.class;
    }

    static Context createMockedContext() throws PackageManager.NameNotFoundException {
        final Context context = mock(Context.class);
        final Resources resources = mock(Resources.class);
        final PackageManager packageManager = mock(PackageManager.class);
        when(context.getApplicationContext())
                .thenReturn(mock(Context.class));
        when(context.getResources()).thenReturn(resources);
        when(context.getPackageManager()).thenReturn(packageManager);
        when(context.getSystemService(Context.STORAGE_SERVICE)).thenReturn(getMockedJobScheduler());
        return context;
    }

    @Nullable
    static JobScheduler getMockedJobScheduler() {
        return JOB_SCHEDULER;
    }

    static RestApiImpl createRestApi() {
        final Observable<List> LIST_OBSERVABLE = getListObservable();
        final Observable<Object> OBJECT_OBSERVABLE = getObjectObservable();
        final RestApiImpl mock = mock(RestApiImpl.class);
        when(mock.dynamicDownload(anyString())).thenReturn(getResponseBodyObservable());
        when(mock.dynamicDeleteList(anyString(), any())).thenReturn(LIST_OBSERVABLE);
        when(mock.dynamicGetObject(any(), anyBoolean())).thenReturn(OBJECT_OBSERVABLE);
        when(mock.dynamicGetObject(any())).thenReturn(OBJECT_OBSERVABLE);
        when(mock.dynamicGetList(any())).thenReturn(getListObservable());
        when(mock.dynamicGetList(any(), anyBoolean())).thenReturn(getListObservable());
        when(mock.dynamicPostObject(any(), any())).thenReturn(OBJECT_OBSERVABLE);
        when(mock.dynamicPostList(any(), any())).thenReturn(LIST_OBSERVABLE);
        when(mock.dynamicPutObject(any(), any())).thenReturn(OBJECT_OBSERVABLE);
        when(mock.dynamicPutList(any(), any())).thenReturn(LIST_OBSERVABLE);
        when(mock.dynamicDeleteObject(any(), any())).thenReturn(OBJECT_OBSERVABLE);
        when(mock.dynamicDeleteList(any(), any())).thenReturn(LIST_OBSERVABLE);
        when(mock.upload(any(), any(RequestBody.class))).thenReturn(OBJECT_OBSERVABLE);
        when(mock.upload(any(), any(MultipartBody.Part.class))).thenReturn(getResponseBodyObservable());
        when(mock.upload(any(), any(MultipartBody.Part.class))).thenReturn(getResponseBodyObservable());
        return mock;
    }

    @NonNull
    private static Observable<List> getListObservable() {
        return Observable.create(
                new Observable.OnSubscribe<List>() {
                    @Override
                    public void call(Subscriber<? super List> subscriber) {
                        subscriber.onNext(Collections.singletonList(createTestModel()));
                    }
                });
    }

    @NonNull
    private static Observable<Object> getObjectObservable() {
        return Observable.create(
                subscriber -> {
                    subscriber.onNext(createTestModel());
                });
    }

    public static TestModel createTestModel() {
        return TEST_MODEL;
    }


    public static PostRequest createPostRequestForHashmap(Subscriber subscriber, String method) {
        return new PostRequest.PostRequestBuilder(getValidDataClass(), false)
                .hashMap(new HashMap<>())
                .idColumnName(getValidColumnName())
                .presentationClass(getPresentationClass())
                .subscriber(subscriber)
                .url(getValidUrl())
                .method(method)
                .build();
    }

    public static PostRequest createPostRequestForJsonObject(Subscriber subscriber, String method) {
        return new PostRequest.PostRequestBuilder(getValidDataClass(), false)
                .jsonObject(new JSONObject())
                .idColumnName(getValidColumnName())
                .presentationClass(getPresentationClass())
                .subscriber(subscriber)
                .url(getValidUrl())
                .method(method)
                .build();
    }

    public static PostRequest createPostRequestForJsonArray(Subscriber subscriber, String method) {
        return new PostRequest.PostRequestBuilder(getValidDataClass(), false)
                .jsonArray(new JSONArray())
                .idColumnName(getValidColumnName())
                .presentationClass(getPresentationClass())
                .subscriber(subscriber)
                .url(getValidUrl())
                .method(method)
                .build();
    }

    static Observable<ResponseBody> getResponseBodyObservable() {
        return Observable.create(new Observable.OnSubscribe<ResponseBody>() {
            @Override
            public void call(@NonNull Subscriber<? super ResponseBody> subscriber) {
                try {
                    subscriber.onNext(getResponseBody());
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    static ResponseBody getResponseBody() throws IOException {
        when(RESPONSE_BODY.byteStream()).thenReturn(getInputSreamReader());
        when(RESPONSE_BODY.contentLength()).thenReturn((long) (1096 * 1096));
        return RESPONSE_BODY;
    }

    static InputStream getInputSreamReader() throws IOException {
        when(INPUT_STREAM.read(any())).thenReturn(1096, 1096, 1096, -1);
        return INPUT_STREAM;
    }

    static void clearAll() {
        reset(RESPONSE_BODY, INPUT_STREAM, JOB_SCHEDULER);
    }

    static GcmNetworkManager getGcmNetworkManager() {
        return mock(GcmNetworkManager.class);
    }

}
