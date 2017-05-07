package com.zeyad.usecases.data.services.jobs;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.rule.BuildConfig;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.zeyad.usecases.TestRealmModel;
import com.zeyad.usecases.data.network.RestApi;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.data.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;

import static com.zeyad.usecases.data.services.GenericJobService.JOB_TYPE;
import static com.zeyad.usecases.data.services.GenericJobService.POST;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PostJUnitTest {
    @Nullable
    private final JobScheduler JOB_SCHEDULER = Utils.getInstance().hasLollipop() ? mock(JobScheduler.class) : null;
    private final ResponseBody RESPONSE_BODY = mock(ResponseBody.class);
    private final InputStream INPUT_STREAM = mock(InputStream.class);
    private final TestRealmModel TEST_MODEL = new TestRealmModel(1, "123");
    private Context mockedContext;

    @Before
    public void setUp() {
        mockedContext = createMockedContext();
    }

    @After
    public void tearDown() {
        clearAll();
    }

    @Test
    public void testExecute_ifTrailCountIncrements_whenNetworkNotAvailable() {
        Post post = createPost(mockedContext
                , createPostRequestForHashmap(PostRequest.POST)
                , createRestApi()
                , 3);
        post.execute();
        assertThat(post.getTrailCount(), is(equalTo(3)));
    }

    @Test
    public void testExecute_ifGCMNetworkManagerIsScheduled_whenNetworkNotAvailableAndGooglePlayServicesAreAvailable() {
        final FirebaseJobDispatcher gcmNetworkManager = getGcmNetworkManager();
        Post post = createPost(mockedContext
                , createPostRequestForHashmap(PostRequest.POST)
                , createRestApi()
                , 1);
        post.execute();
        verify(gcmNetworkManager).schedule(any(Job.class));
    }

    @Test
    public void testExecute_ifCorrectArgumentsArePassedToGCMNetworkManager_whenNetworkNotAvailableAndGooglePlayServicesAreAvailable() {
        final FirebaseJobDispatcher gcmNetworkManager = getGcmNetworkManager();
        Post post = createPost(mockedContext
                , createPostRequestForHashmap(PostRequest.POST)
                , createRestApi()
                , 1);
        post.execute();
        ArgumentCaptor<Job> peopleCaptor = ArgumentCaptor.forClass(Job.class);
        verify(gcmNetworkManager).schedule(peopleCaptor.capture());
//        assertThat(peopleCaptor.getValue().getWindowEnd(), is(30L));
//        assertThat(peopleCaptor.getValue().getWindowStart(), is(0L));
//        assertThat(peopleCaptor.getValue().getRequiresCharging(), is(false));
        assertThat(peopleCaptor.getValue().getExtras(), is(notNullValue()));
        assertThat(peopleCaptor.getValue().getExtras().getString(JOB_TYPE), is(POST));
//        assertThat(peopleCaptor.getValue().getServiceName(), is(GenericJobService.class.getName()));
//        assertThat(peopleCaptor.getValue().getRequiredNetwork(), is(NETWORK_STATE_CONNECTED));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testExecute_ifJobSchedulerIsInvoked_whenNetworkNotAvailableAndGooglePlayServicesAreAvailable() {
        Post post = createPost(mockedContext
                , createPostRequestForHashmap(PostRequest.POST)
                , createRestApi()
                , 1);
        post.execute();
        verify(getMockedJobScheduler()).schedule(any(JobInfo.class));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testExecute_ifCorrectArgumentsArePassedToJobScheduler_whenNetworkNotAvailableAndGooglePlayServicesAreAvailable() {
        Post post = createPost(mockedContext
                , createPostRequestForHashmap(PostRequest.POST)
                , createRestApi()
                , 1);
        post.execute();
        ArgumentCaptor<Job> argumentCaptor = ArgumentCaptor.forClass(Job.class);
        verify(getGcmNetworkManager()).schedule(argumentCaptor.capture());
//        assertThat(argumentCaptor.getValue().getService().getClassName(), is(equalTo(GenericJobService.class.getName())));
//        assertThat(argumentCaptor.getValue().isRequireCharging(), is(false));
//        assertThat(argumentCaptor.getValue().isPersisted(), is(true));
//        assertThat(argumentCaptor.getValue().getNetworkType(), is(NETWORK_TYPE_ANY));
        assertThat(argumentCaptor.getValue().getExtras(), is(notNullValue()));
        assertThat(argumentCaptor.getValue().getExtras().getString(JOB_TYPE), is(POST));
    }

    @Test
    public void testExecute_ifGCMNetworkManagerIsNotScheduled_whenNetworkNotAvailableAndGooglePlayServicesAreNotAvailable() {
        final FirebaseJobDispatcher gcmNetworkManager = getGcmNetworkManager();
        Post post = createPost(mockedContext
                , createPostRequestForHashmap(PostRequest.POST)
                , createRestApi()
                , 1);
        post.execute();
        verify(gcmNetworkManager, times(0)).schedule(any(Job.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicPostObjectIsCalled_whenNetworkIsAvailableAndHashmapAndPostMethodIsPassed() {
        final RestApiImpl restApi = createRestApi();
        Post post = createPost(mockedContext
                , createPostRequestForHashmap(PostRequest.POST)
                , restApi
                , 1);
        post.execute();
        verify(restApi, times(1)).dynamicPost(anyString(), any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicPostObjectIsCalled_whenNetworkIsAvailableAndJsonObjectAndPostMethodIsPassed() {
        final RestApiImpl restApi = createRestApi();
        Post post = createPost(mockedContext
                , createPostRequestForJsonObject(PostRequest.POST)
                , restApi
                , 1);
        post.execute();
        verify(restApi, times(1)).dynamicPost(anyString(), any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicPostListIsCalled_whenNetworkIsAvailableAndJsonArrayAndPostMethodIsPassed() {
        final RestApiImpl restApi = createRestApi();
        Post post = createPost(mockedContext
                , createPostRequestForJsonArray(PostRequest.POST)
                , restApi
                , 1);
        post.execute();
        verify(restApi, times(1)).dynamicPost(anyString(), any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicPutObjectIsCalled_whenNetworkIsAvailableAndHashmapAndPutMethodIsPassed() {
        final RestApiImpl restApi = createRestApi();
        Post post = createPost(mockedContext
                , createPostRequestForHashmap(PostRequest.PUT)
                , restApi
                , 1);
        post.execute();
        verify(restApi, times(1)).dynamicPut(anyString(), any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicPutObjectIsCalled_whenNetworkIsAvailableAndJsonObjectAndPutMethodIsPassed() {
        final RestApiImpl restApi = createRestApi();
        Post post = createPost(mockedContext
                , createPostRequestForJsonObject(PostRequest.PUT)
                , restApi
                , 1);
        post.execute();
        verify(restApi, times(1)).dynamicPut(anyString(), any(RequestBody.class));
    }


    // --- //

    @Test
    public void testExecute_ifRestApiDynamicPutListIsCalled_whenNetworkIsAvailableAndJsonArrayAndPutMethodIsPassed() {
        final RestApiImpl restApi = createRestApi();
        Post post = createPost(mockedContext
                , createPostRequestForJsonArray(PostRequest.PUT)
                , restApi
                , 1);
        post.execute();
        verify(restApi, times(1)).dynamicPut(anyString(), any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicDeleteObjectIsCalled_whenNetworkIsAvailableAndHashmapAndDeleteMethodIsPassed() {
        final RestApiImpl restApi = createRestApi();
        Post post = createPost(mockedContext
                , createPostRequestForHashmap(PostRequest.DELETE)
                , restApi
                , 1);
        post.execute();
        verify(restApi, times(1)).dynamicDelete(anyString(), any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicDeleteObjectIsCalled_whenNetworkIsAvailableAndJsonObjectAndDeleteMethodIsPassed() {
        final RestApiImpl restApi = createRestApi();
        Post post = createPost(mockedContext
                , createPostRequestForJsonObject(PostRequest.DELETE)
                , restApi
                , 1);
        post.execute();
        verify(restApi, times(1)).dynamicDelete(anyString(), any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicDeleteListIsCalled_whenNetworkIsAvailableAndJsonArrayAndDeleteMethodIsPassed() {
        final RestApiImpl restApi = createRestApi();
        Post post = createPost(mockedContext
                , createPostRequestForJsonArray(PostRequest.DELETE)
                , restApi
                , 1);
        post.execute();
        verify(restApi, times(1)).dynamicDelete(anyString(), any(RequestBody.class));
    }

    @NonNull
    Post createPost(Context context, PostRequest postRequest, RestApi restApi, int trailCount) {
        return new Post(context, postRequest, restApi, trailCount, null);
    }

    String getValidUrl() {
        return "http://www.google.com";
    }

    @NonNull
    Class getPresentationClass() {
        return Object.class;
    }

    String getValidColumnName() {
        return "id";
    }

    @NonNull
    Class getValidDataClass() {
        return TestRealmModel.class;
    }

    Context createMockedContext() {
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
    JobScheduler getMockedJobScheduler() {
        return JOB_SCHEDULER;
    }

    RestApiImpl createRestApi() {
        final Observable<Object> OBJECT_OBSERVABLE = getObjectObservable();
        final RestApiImpl mock = mock(RestApiImpl.class);
        when(mock.dynamicDownload(anyString())).thenReturn(getResponseBodyObservable());
        when(mock.dynamicDelete(anyString(), any())).thenReturn(OBJECT_OBSERVABLE);
        when(mock.dynamicGetObject(any(), anyBoolean())).thenReturn(OBJECT_OBSERVABLE);
        when(mock.dynamicGetObject(any())).thenReturn(OBJECT_OBSERVABLE);
        when(mock.dynamicGetList(any())).thenReturn(getListObservable());
        when(mock.dynamicGetList(any(), anyBoolean())).thenReturn(getListObservable());
        when(mock.dynamicPost(any(), any())).thenReturn(OBJECT_OBSERVABLE);
        when(mock.dynamicPut(any(), any())).thenReturn(OBJECT_OBSERVABLE);
        when(mock.dynamicUpload(any(), any(Map.class), any(MultipartBody.Part.class))).thenReturn(OBJECT_OBSERVABLE);
        return mock;
    }

    @NonNull
    private Observable<List> getListObservable() {
        return Observable.create(
                new Observable.OnSubscribe<List>() {
                    @Override
                    public void call(@NonNull Subscriber<? super List> subscriber) {
                        subscriber.onNext(Collections.singletonList(createTestModel()));
                    }
                });
    }

    @NonNull
    private Observable<Object> getObjectObservable() {
        return Observable.create(
                subscriber -> {
                    subscriber.onNext(createTestModel());
                });
    }

    @NonNull
    public TestRealmModel createTestModel() {
        return TEST_MODEL;
    }


    public PostRequest createPostRequestForHashmap(String method) {
        return new PostRequest.PostRequestBuilder(getValidDataClass(), false)
                .payLoad(new HashMap<>())
                .idColumnName(getValidColumnName())
                .presentationClass(getPresentationClass())
                .url(getValidUrl())
                .method(method)
                .build();
    }

    public PostRequest createPostRequestForJsonObject(String method) {
        return new PostRequest.PostRequestBuilder(getValidDataClass(), false)
                .payLoad(new JSONObject())
                .idColumnName(getValidColumnName())
                .presentationClass(getPresentationClass())
                .url(getValidUrl())
                .method(method)
                .build();
    }

    public PostRequest createPostRequestForJsonArray(String method) {
        return new PostRequest.PostRequestBuilder(getValidDataClass(), false)
                .payLoad(new JSONArray())
                .idColumnName(getValidColumnName())
                .presentationClass(getPresentationClass())
                .url(getValidUrl())
                .method(method)
                .build();
    }

    Observable<ResponseBody> getResponseBodyObservable() {
        return Observable.fromCallable(this::getResponseBody);
    }

    ResponseBody getResponseBody() throws IOException {
        when(RESPONSE_BODY.byteStream()).thenReturn(getInputSreamReader());
        when(RESPONSE_BODY.contentLength()).thenReturn((long) (1096 * 1096));
        return RESPONSE_BODY;
    }

    InputStream getInputSreamReader() throws IOException {
        when(INPUT_STREAM.read(any())).thenReturn(1096, 1096, 1096, -1);
        return INPUT_STREAM;
    }

    void clearAll() {
        reset(RESPONSE_BODY, INPUT_STREAM, JOB_SCHEDULER);
    }

    FirebaseJobDispatcher getGcmNetworkManager() {
        return new FirebaseJobDispatcher(new GooglePlayDriver(mock(Context.class)));
    }
}
