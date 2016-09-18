package com.zeyad.genericusecase.data.services.jobs;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;
import com.zeyad.genericusecase.data.network.RestApiImpl;
import com.zeyad.genericusecase.data.services.GenericGCMService;
import com.zeyad.genericusecase.data.services.GenericJobService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;

import static android.app.job.JobInfo.NETWORK_TYPE_ANY;
import static com.google.android.gms.gcm.Task.NETWORK_STATE_CONNECTED;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.JOB_TYPE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.POST;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

@RunWith(JUnit4.class)
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
                , PostTestRobot.createPostRequestForHashmap(null)
                , PostTestRobot.createRestApi()
                , 3
                , true
                , true
                , false
                , PostTestRobot.getGcmNetworkManager());
        post.execute();
        assertThat(post.getTrailCount(), is(equalTo(4)));
    }

    @Test
    public void testExecute_ifGCMNetworkManagerIsScheduled_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final GcmNetworkManager gcmNetworkManager = PostTestRobot.getGcmNetworkManager();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(null)
                , PostTestRobot.createRestApi()
                , 1
                , true
                , true
                , false
                , gcmNetworkManager);
        post.execute();
        Mockito.verify(gcmNetworkManager).schedule(Mockito.any(Task.class));
    }

    @Test
    public void testExecute_ifCorrectArgumentsArePassedToGCMNetworkManager_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final GcmNetworkManager gcmNetworkManager = PostTestRobot.getGcmNetworkManager();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(null)
                , PostTestRobot.createRestApi()
                , 1
                , true
                , true
                , false
                , gcmNetworkManager);
        post.execute();
        ArgumentCaptor<OneoffTask> peopleCaptor = ArgumentCaptor.forClass(OneoffTask.class);
        Mockito.verify(gcmNetworkManager).schedule(peopleCaptor.capture());
        assertThat(peopleCaptor.getValue().getWindowEnd(), is(30L));
        assertThat(peopleCaptor.getValue().getWindowStart(), is(0L));
        assertThat(peopleCaptor.getValue().getRequiresCharging(), is(false));
        assertThat(peopleCaptor.getValue().getExtras(), is(notNullValue()));
        assertThat(peopleCaptor.getValue().getExtras().getString(JOB_TYPE), is(POST));
        assertThat(peopleCaptor.getValue().getServiceName(), is(GenericGCMService.class.getName()));
        assertThat(peopleCaptor.getValue().getRequiredNetwork(), is(NETWORK_STATE_CONNECTED));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testExecute_ifJobSchedulerIsInvoked_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final GcmNetworkManager gcmNetworkManager = PostTestRobot.getGcmNetworkManager();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(null)
                , PostTestRobot.createRestApi()
                , 1
                , true
                , false
                , false
                , gcmNetworkManager);
        post.execute();
        Mockito.verify(PostTestRobot.getMockedJobScheduler()).schedule(Mockito.any(JobInfo.class));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testExecute_ifCorrectArgumentsArePassedToJobScheduler_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final GcmNetworkManager gcmNetworkManager = PostTestRobot.getGcmNetworkManager();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(null)
                , PostTestRobot.createRestApi()
                , 1
                , true
                , false
                , false
                , gcmNetworkManager);
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
    public void testExecute_ifGCMNetworkManagerIsNotScheduled_whenGooglePlayServicesAreNotAvailable() throws IOException, PackageManager.NameNotFoundException {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final GcmNetworkManager gcmNetworkManager = PostTestRobot.getGcmNetworkManager();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(null)
                , PostTestRobot.createRestApi()
                , 1
                , true
                , false
                , false
                , gcmNetworkManager);
        post.execute();
        Mockito.verify(gcmNetworkManager, times(0)).schedule(Mockito.any(Task.class));
    }

    @Test
    public void testExecute_ifFileIsNotDownloaded_whenFileIsToBeDownloadedAndFileDoesExist() throws IOException, PackageManager.NameNotFoundException {
        final Context mockedContext = PostTestRobot.createMockedContext();
        final GcmNetworkManager gcmNetworkManager = PostTestRobot.getGcmNetworkManager();
        final RestApiImpl restApi = PostTestRobot.createRestApi();
        Post post = PostTestRobot.createPost(mockedContext
                , PostTestRobot.createPostRequestForHashmap(null)
                , restApi
                , 1
                , true
                , true
                , false
                , gcmNetworkManager);
        post.execute();
        Mockito.verify(restApi, times(0)).dynamicDownload(eq(PostTestRobot.getValidUrl()));
    }
}
