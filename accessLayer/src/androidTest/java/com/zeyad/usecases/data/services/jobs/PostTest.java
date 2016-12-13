package com.zeyad.usecases.data.services.jobs;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.test.runner.AndroidJUnit4;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.data.services.GenericJobService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;

import okhttp3.RequestBody;
import rx.observers.TestSubscriber;

import static android.app.job.JobInfo.NETWORK_TYPE_ANY;
import static com.zeyad.usecases.data.services.GenericJobService.JOB_TYPE;
import static com.zeyad.usecases.data.services.GenericJobService.POST;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

@RunWith(AndroidJUnit4.class)
//@Config(constants = BuildConfig.class)
public class PostTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        PostTestRobot.clearAll();
    }

    @Test
    public void testExecute_ifTrailCountIncrements_whenNetworkNotAvailable() throws IOException, PackageManager.NameNotFoundException {
        final Context mockedContext = PostTestRobot.createMockedContext();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(null, PostRequest.POST)
                , PostTestRobot.createRestApi()
                , 3);
        post.execute();
        assertThat(post.getTrailCount(), is(equalTo(4)));
    }

    @Test
    public void testExecute_ifGCMNetworkManagerIsScheduled_whenNetworkNotAvailableAndGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final FirebaseJobDispatcher gcmNetworkManager = PostTestRobot.getGcmNetworkManager();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(null, PostRequest.POST)
                , PostTestRobot.createRestApi()
                , 1);
        post.execute();
        Mockito.verify(gcmNetworkManager).schedule(Mockito.any(Job.class));
    }

    @Test
    public void testExecute_ifCorrectArgumentsArePassedToGCMNetworkManager_whenNetworkNotAvailableAndGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final FirebaseJobDispatcher gcmNetworkManager = PostTestRobot.getGcmNetworkManager();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(null, PostRequest.POST)
                , PostTestRobot.createRestApi()
                , 1);
        post.execute();
//        ArgumentCaptor<OneoffTask> peopleCaptor = ArgumentCaptor.forClass(OneoffTask.class);
//        Mockito.verify(gcmNetworkManager).schedule(peopleCaptor.capture());
//        assertThat(peopleCaptor.getValue().getWindowEnd(), is(30L));
//        assertThat(peopleCaptor.getValue().getWindowStart(), is(0L));
//        assertThat(peopleCaptor.getValue().getRequiresCharging(), is(false));
//        assertThat(peopleCaptor.getValue().getExtras(), is(notNullValue()));
//        assertThat(peopleCaptor.getValue().getExtras().getString(JOB_TYPE), is(POST));
//        assertThat(peopleCaptor.getValue().getServiceName(), is(GenericGCMService.class.getName()));
//        assertThat(peopleCaptor.getValue().getRequiredNetwork(), is(NETWORK_STATE_CONNECTED));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testExecute_ifJobSchedulerIsInvoked_whenNetworkNotAvailableAndGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        final Context mockedContext = PostTestRobot.createMockedContext();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(null, PostRequest.POST)
                , PostTestRobot.createRestApi()
                , 1);
        post.execute();
        Mockito.verify(PostTestRobot.getMockedJobScheduler()).schedule(Mockito.any(JobInfo.class));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testExecute_ifCorrectArgumentsArePassedToJobScheduler_whenNetworkNotAvailableAndGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        final Context mockedContext = PostTestRobot.createMockedContext();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(null, PostRequest.POST)
                , PostTestRobot.createRestApi()
                , 1);
        post.execute();
        ArgumentCaptor<JobInfo> argumentCaptor = ArgumentCaptor.forClass(JobInfo.class);
        Mockito.verify(PostTestRobot.getMockedJobScheduler()).schedule(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getService().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argumentCaptor.getValue().isRequireCharging(), is(false));
        assertThat(argumentCaptor.getValue().isPersisted(), is(true));
        assertThat(argumentCaptor.getValue().getNetworkType(), is(NETWORK_TYPE_ANY));
        assertThat(argumentCaptor.getValue().getExtras(), is(notNullValue()));
        assertThat(argumentCaptor.getValue().getExtras().getString(JOB_TYPE), is(POST));
    }

    @Test
    public void testExecute_ifGCMNetworkManagerIsNotScheduled_whenNetworkNotAvailableAndGooglePlayServicesAreNotAvailable() throws IOException, PackageManager.NameNotFoundException {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final FirebaseJobDispatcher gcmNetworkManager = PostTestRobot.getGcmNetworkManager();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(null, PostRequest.POST)
                , PostTestRobot.createRestApi()
                , 1);
        post.execute();
        Mockito.verify(gcmNetworkManager, times(0)).schedule(Mockito.any(Job.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicPostObjectIsCalled_whenNetworkIsAvailableAndHashmapAndPostMethodIsPassed() throws Exception {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final RestApiImpl restApi = PostTestRobot.createRestApi();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(new TestSubscriber<>(), PostRequest.POST)
                , restApi
                , 1);
        post.execute();
        Mockito.verify(restApi, times(1)).dynamicPost(eq(PostTestRobot.getValidUrl()), Mockito.any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicPostObjectIsCalled_whenNetworkIsAvailableAndJsonObjectAndPostMethodIsPassed() throws Exception {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final RestApiImpl restApi = PostTestRobot.createRestApi();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForJsonObject(new TestSubscriber<>(), PostRequest.POST)
                , restApi
                , 1);
        post.execute();
        Mockito.verify(restApi, times(1)).dynamicPost(eq(PostTestRobot.getValidUrl()), Mockito.any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicPostListIsCalled_whenNetworkIsAvailableAndJsonArrayAndPostMethodIsPassed() throws Exception {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final RestApiImpl restApi = PostTestRobot.createRestApi();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForJsonArray(new TestSubscriber<>(), PostRequest.POST)
                , restApi
                , 1);
        post.execute();
        Mockito.verify(restApi, times(1)).dynamicPost(eq(PostTestRobot.getValidUrl()), Mockito.any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicPutObjectIsCalled_whenNetworkIsAvailableAndHashmapAndPutMethodIsPassed() throws Exception {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final RestApiImpl restApi = PostTestRobot.createRestApi();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(new TestSubscriber<>(), PostRequest.PUT)
                , restApi
                , 1);
        post.execute();
        Mockito.verify(restApi, times(1)).dynamicPut(eq(PostTestRobot.getValidUrl()), Mockito.any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicPutObjectIsCalled_whenNetworkIsAvailableAndJsonObjectAndPutMethodIsPassed() throws Exception {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final RestApiImpl restApi = PostTestRobot.createRestApi();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForJsonObject(new TestSubscriber<>(), PostRequest.PUT)
                , restApi
                , 1);
        post.execute();
        Mockito.verify(restApi, times(1)).dynamicPut(eq(PostTestRobot.getValidUrl()), Mockito.any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicPutListIsCalled_whenNetworkIsAvailableAndJsonArrayAndPutMethodIsPassed() throws Exception {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final RestApiImpl restApi = PostTestRobot.createRestApi();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForJsonArray(new TestSubscriber<>(), PostRequest.PUT)
                , restApi
                , 1);
        post.execute();
        Mockito.verify(restApi, times(1)).dynamicPut(eq(PostTestRobot.getValidUrl()), Mockito.any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicDeleteObjectIsCalled_whenNetworkIsAvailableAndHashmapAndDeleteMethodIsPassed() throws Exception {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final RestApiImpl restApi = PostTestRobot.createRestApi();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(new TestSubscriber<>(), PostRequest.DELETE)
                , restApi
                , 1);
        post.execute();
        Mockito.verify(restApi, times(1)).dynamicDelete(eq(PostTestRobot.getValidUrl()), Mockito.any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicDeleteObjectIsCalled_whenNetworkIsAvailableAndJsonObjectAndDeleteMethodIsPassed() throws Exception {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final RestApiImpl restApi = PostTestRobot.createRestApi();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForJsonObject(new TestSubscriber<>(), PostRequest.DELETE)
                , restApi
                , 1);
        post.execute();
        Mockito.verify(restApi, times(1)).dynamicDelete(eq(PostTestRobot.getValidUrl()), Mockito.any(RequestBody.class));
    }

    @Test
    public void testExecute_ifRestApiDynamicDeleteListIsCalled_whenNetworkIsAvailableAndJsonArrayAndDeleteMethodIsPassed() throws Exception {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final RestApiImpl restApi = PostTestRobot.createRestApi();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForJsonArray(new TestSubscriber<>(), PostRequest.DELETE)
                , restApi
                , 1);
        post.execute();
        Mockito.verify(restApi, times(1)).dynamicDelete(eq(PostTestRobot.getValidUrl()), Mockito.any(RequestBody.class));
    }
}
