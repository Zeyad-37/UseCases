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
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;

public class PostTestRobot {

    @Nullable
    private static final JobScheduler JOB_SCHEDULER;
    private static final ResponseBody RESPONSE_BODY = Mockito.mock(ResponseBody.class);
    private static final InputStream INPUT_STREAM = Mockito.mock(InputStream.class);

    static {
        JOB_SCHEDULER = Utils.hasLollipop() ? Mockito.mock(JobScheduler.class) : null;
    }

    @NonNull
    static Post createPost(Context context, PostRequest postRequest, RestApi restApi
            , int trailCount, boolean hasLollipop
            , boolean isPlayServicesAvailable, boolean networkAvailable, GcmNetworkManager gcmNetworkManager) {
        return new Post(context, postRequest, restApi
                , trailCount, hasLollipop, isPlayServicesAvailable, networkAvailable, gcmNetworkManager);
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
        final Context context = Mockito.mock(Context.class);
        final Resources resources = Mockito.mock(Resources.class);
        final PackageManager packageManager = Mockito.mock(PackageManager.class);
        Mockito.when(context.getApplicationContext())
                .thenReturn(Mockito.mock(Context.class));
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(context.getPackageManager()).thenReturn(packageManager);
        Mockito.when(context.getSystemService(Mockito.anyString())).thenReturn(getMockedJobScheduler());
        return context;
    }

    @Nullable
    static JobScheduler getMockedJobScheduler() {
        return JOB_SCHEDULER;
    }

    static RestApiImpl createRestApi() {
        final RestApiImpl restApi = Mockito.mock(RestApiImpl.class);
        Mockito.when(restApi.dynamicDownload(Mockito.anyString())).thenReturn(getResponseBodyObservable());
        return restApi;
    }

    public static PostRequest createPostRequestForHashmap(Subscriber subscriber) {
        return new PostRequest.PostRequestBuilder(getValidDataClass(), false)
                .domainClass(getValidDomainClass())
                .hashMap(new HashMap<>())
                .idColumnName(getValidColumnName())
                .presentationClass(getPresentationClass())
                .subscriber(subscriber)
                .url(getValidUrl())
                .build();
    }

    public static PostRequest createPostRequestForJsonObject(Subscriber subscriber) {
        return new PostRequest.PostRequestBuilder(getValidDataClass(), false)
                .domainClass(getValidDomainClass())
                .jsonObject(new JSONObject())
                .idColumnName(getValidColumnName())
                .presentationClass(getPresentationClass())
                .subscriber(subscriber)
                .url(getValidUrl())
                .build();
    }

    public static PostRequest createPostRequestForJsonArray(Subscriber subscriber) {
        return new PostRequest.PostRequestBuilder(getValidDataClass(), false)
                .domainClass(getValidDomainClass())
                .jsonArray(new JSONArray())
                .idColumnName(getValidColumnName())
                .presentationClass(getPresentationClass())
                .subscriber(subscriber)
                .url(getValidUrl())
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
        Mockito.when(RESPONSE_BODY.byteStream()).thenReturn(getInputSreamReader());
        Mockito.when(RESPONSE_BODY.contentLength()).thenReturn((long) (1096 * 1096));
        return RESPONSE_BODY;
    }

    static InputStream getInputSreamReader() throws IOException {
        Mockito.when(INPUT_STREAM.read(Mockito.any())).thenReturn(1096, 1096, 1096, -1);
        return INPUT_STREAM;
    }

    static void clearAll() {
        Mockito.reset(RESPONSE_BODY, INPUT_STREAM, JOB_SCHEDULER);
    }

    static GcmNetworkManager getGcmNetworkManager() {
        return Mockito.mock(GcmNetworkManager.class);
    }

}
