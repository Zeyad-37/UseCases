package com.zeyad.usecases.data.services.jobs;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.requests.FileIORequest;
import com.zeyad.usecases.data.services.GenericGCMService;
import com.zeyad.usecases.data.services.GenericJobService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;

import static android.app.job.JobInfo.NETWORK_TYPE_ANY;
import static android.app.job.JobInfo.NETWORK_TYPE_UNMETERED;
import static com.google.android.gms.gcm.Task.NETWORK_STATE_CONNECTED;
import static com.google.android.gms.gcm.Task.NETWORK_STATE_UNMETERED;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.DOWNLOAD_FILE;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.JOB_TYPE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

@RunWith(AndroidJUnit4.class)
//@Config(constants = BuildConfig.class)
public class FileIOTest {
    // TODO: 9/25/16 Mock Job Scheduler
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        FileIOTestRobot.clearAll();
    }


    @Test
    public void testExecute_ifFileIsDownloaded_whenFileIsToBeDownloadedAndFileDoesNotExist() throws PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOTestRobot.createFileIoReq(true, true, FileIOTestRobot.createFileWhichDoesNotExist());
        final RestApiImpl restApi = FileIOTestRobot.createRestApi();
        FileIO fileIO = FileIOTestRobot.createFileIO(FileIOTestRobot.createMockedContext()
                , restApi
                , 3
                , fileIOReq
                , true
                , FileIOTestRobot.getGcmNetworkManager()
                , true
                , false);
        fileIO.execute();
        Mockito.verify(restApi).dynamicDownload(eq(FileIOTestRobot.getValidUrl()));
    }

    @Test
    public void testQueueIoFile_ifTrailCountIncrements_whenFileIsToBeDownloadedAndFileDoesNotExist() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOTestRobot.createFileIoReq(true, true, FileIOTestRobot.createFileWhichDoesNotExist());
        final RestApiImpl restApi = FileIOTestRobot.createRestApi();
        FileIO fileIO = FileIOTestRobot.createFileIO(FileIOTestRobot.createMockedContext()
                , restApi
                , 3
                , fileIOReq
                , true
                , FileIOTestRobot.getGcmNetworkManager()
                , true
                , false);
        fileIO.queueIOFile();
        assertThat(fileIO.getTrailCount(), is(equalTo(4)));
    }

    @Test
    public void testQueueIoFile_ifGCMNetworkManagerIsScheduled_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOTestRobot.createFileIoReq(true, true, FileIOTestRobot.createFileWhichDoesNotExist());
        final RestApiImpl restApi = FileIOTestRobot.createRestApi();
        final GcmNetworkManager gcmNetworkManager = FileIOTestRobot.getGcmNetworkManager();
        FileIO fileIO = FileIOTestRobot.createFileIO(FileIOTestRobot.createMockedContext()
                , restApi
                , 1
                , fileIOReq
                , true
                , gcmNetworkManager
                , true
                , false);
        fileIO.queueIOFile();
        Mockito.verify(gcmNetworkManager).schedule(Mockito.any(Task.class));
    }

    @Test
    public void testQueueIoFile_ifCorrectArgumentsArePassedToGCMNetworkManager_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOTestRobot.createFileIoReq(true, false, FileIOTestRobot.createFileWhichDoesNotExist());
        final RestApiImpl restApi = FileIOTestRobot.createRestApi();
        final GcmNetworkManager gcmNetworkManager = FileIOTestRobot.getGcmNetworkManager();
        FileIO fileIO = FileIOTestRobot.createFileIO(FileIOTestRobot.createMockedContext()
                , restApi
                , 1
                , fileIOReq
                , true
                , gcmNetworkManager
                , true
                , false);
        fileIO.queueIOFile();
        ArgumentCaptor<OneoffTask> peopleCaptor = ArgumentCaptor.forClass(OneoffTask.class);
        Mockito.verify(gcmNetworkManager).schedule(peopleCaptor.capture());
        assertThat(peopleCaptor.getValue().getWindowEnd(), is(30L));
        assertThat(peopleCaptor.getValue().getWindowStart(), is(0L));
        assertThat(peopleCaptor.getValue().getRequiresCharging(), is(false));
        assertThat(peopleCaptor.getValue().getExtras(), is(notNullValue()));
        assertThat(peopleCaptor.getValue().getExtras().getString(JOB_TYPE), is(DOWNLOAD_FILE));
        assertThat(peopleCaptor.getValue().getServiceName(), is(GenericGCMService.class.getName()));
        assertThat(peopleCaptor.getValue().getRequiredNetwork(), is(fileIOReq.onWifi() ? NETWORK_STATE_UNMETERED : NETWORK_STATE_CONNECTED));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testQueueIoFile_ifJobSchedulerIsInvoked_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOTestRobot.createFileIoReq(true, true, FileIOTestRobot.createFileWhichDoesNotExist());
        final RestApiImpl restApi = FileIOTestRobot.createRestApi();
        final GcmNetworkManager gcmNetworkManager = FileIOTestRobot.getGcmNetworkManager();
        FileIO fileIO = FileIOTestRobot.createFileIO(FileIOTestRobot.createMockedContext()
                , restApi
                , 1
                , fileIOReq
                , true
                , gcmNetworkManager
                , false
                , true);
        fileIO.queueIOFile();
        Mockito.verify(FileIOTestRobot.getMockedJobScheduler()).schedule(Mockito.any(JobInfo.class));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testQueueIoFile_ifCorrectArgumentsArePassedToJobScheduler_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOTestRobot.createFileIoReq(true, true, FileIOTestRobot.createFileWhichDoesNotExist());
        final RestApiImpl restApi = FileIOTestRobot.createRestApi();
        final GcmNetworkManager gcmNetworkManager = FileIOTestRobot.getGcmNetworkManager();
        FileIO fileIO = FileIOTestRobot.createFileIO(FileIOTestRobot.createMockedContext()
                , restApi
                , 1
                , fileIOReq
                , true
                , gcmNetworkManager
                , false
                , true);
        fileIO.queueIOFile();
        ArgumentCaptor<JobInfo> argumentCaptor = ArgumentCaptor.forClass(JobInfo.class);
        Mockito.verify(FileIOTestRobot.getMockedJobScheduler()).schedule(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getService().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argumentCaptor.getValue().isRequireCharging(), is(fileIOReq.isWhileCharging()));
        assertThat(argumentCaptor.getValue().isPersisted(), is(true));
        assertThat(argumentCaptor.getValue().getNetworkType(), is(fileIOReq.onWifi() ? NETWORK_TYPE_UNMETERED : NETWORK_TYPE_ANY));
        assertThat(argumentCaptor.getValue().getExtras(), is(notNullValue()));
        assertThat(argumentCaptor.getValue().getExtras().getString(JOB_TYPE), is(DOWNLOAD_FILE));
    }

    @Test
    public void testQueueIoFile_ifGCMNetworkManagerIsNotScheduled_whenGooglePlayServicesAreNotAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOTestRobot.createFileIoReq(true, true, FileIOTestRobot.createFileWhichDoesNotExist());
        final RestApiImpl restApi = FileIOTestRobot.createRestApi();
        final GcmNetworkManager gcmNetworkManager = FileIOTestRobot.getGcmNetworkManager();
        FileIO fileIO = FileIOTestRobot.createFileIO(FileIOTestRobot.createMockedContext()
                , restApi
                , 2
                , fileIOReq
                , true
                , gcmNetworkManager
                , false
                , false);
        fileIO.queueIOFile();
        Mockito.verify(gcmNetworkManager, times(0)).schedule(Mockito.any(Task.class));
    }

    @Test
    public void testExecute_ifFileIsNotDownloaded_whenFileIsToBeDownloadedAndFileDoesExist() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOTestRobot.createFileIoReq(true, true, FileIOTestRobot.createFileWhichDoesExist());
        final RestApiImpl restApi = FileIOTestRobot.createRestApi();
        FileIO fileIO = FileIOTestRobot.createFileIO(FileIOTestRobot.createMockedContext()
                , restApi
                , 3
                , fileIOReq
                , true
                , FileIOTestRobot.getGcmNetworkManager()
                , true
                , false);
        fileIO.execute();
        Mockito.verify(restApi, times(0)).dynamicDownload(eq(FileIOTestRobot.getValidUrl()));
    }
}