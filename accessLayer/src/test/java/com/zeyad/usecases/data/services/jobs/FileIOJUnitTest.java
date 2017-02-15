package com.zeyad.usecases.data.services.jobs;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.test.rule.BuildConfig;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.requests.FileIORequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static com.zeyad.usecases.data.services.GenericJobService.DOWNLOAD_FILE;
import static com.zeyad.usecases.data.services.GenericJobService.JOB_TYPE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class FileIOJUnitTest {
    Context mockContext;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
    }

    @After
    public void tearDown() throws Exception {
        FileIOJUnitTestRobot.clearAll();
    }

    @Test
    public void testExecute_ifFileIsDownloaded_whenFileIsToBeDownloadedAndFileDoesNotExist() throws PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOJUnitTestRobot.createFileIoReq(true, true, FileIOJUnitTestRobot.createFileWhichDoesNotExist());
        final RestApiImpl restApi = FileIOJUnitTestRobot.createRestApi();
        FileIO fileIO = FileIOJUnitTestRobot.createFileIO(FileIOJUnitTestRobot.createMockedContext()
                , restApi
                , 3
                , fileIOReq
                , true);
        fileIO.execute();
        verify(restApi).dynamicDownload(eq(FileIOJUnitTestRobot.getValidUrl()));
    }

    @Test
    public void testQueueIoFile_ifTrailCountIncrements_whenFileIsToBeDownloadedAndFileDoesNotExist() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOJUnitTestRobot.createFileIoReq(true, true, FileIOJUnitTestRobot.createFileWhichDoesNotExist());
        final RestApiImpl restApi = FileIOJUnitTestRobot.createRestApi();
        FileIO fileIO = FileIOJUnitTestRobot.createFileIO(FileIOJUnitTestRobot.createMockedContext()
                , restApi
                , 3
                , fileIOReq
                , true);
        fileIO.queueIOFile();
        assertThat(fileIO.getTrailCount(), is(equalTo(4)));
    }

    @Test
    public void testQueueIoFile_ifGCMNetworkManagerIsScheduled_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOJUnitTestRobot.createFileIoReq(true, true, FileIOJUnitTestRobot.createFileWhichDoesNotExist());
        final RestApiImpl restApi = FileIOJUnitTestRobot.createRestApi();
        final FirebaseJobDispatcher gcmNetworkManager = FileIOJUnitTestRobot.getGcmNetworkManager(mockContext);
        FileIO fileIO = FileIOJUnitTestRobot.createFileIO(FileIOJUnitTestRobot.createMockedContext()
                , restApi
                , 1
                , fileIOReq
                , true);
        fileIO.queueIOFile();
        verify(gcmNetworkManager).schedule(Mockito.any(Job.class));
    }

    @Test
    public void testQueueIoFile_ifCorrectArgumentsArePassedToGCMNetworkManager_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOJUnitTestRobot.createFileIoReq(true, false, FileIOJUnitTestRobot.createFileWhichDoesNotExist());
        final RestApiImpl restApi = FileIOJUnitTestRobot.createRestApi();
        final FirebaseJobDispatcher gcmNetworkManager = FileIOJUnitTestRobot.getGcmNetworkManager(mockContext);
        FileIO fileIO = FileIOJUnitTestRobot.createFileIO(FileIOJUnitTestRobot.createMockedContext()
                , restApi
                , 1
                , fileIOReq
                , true);
        fileIO.queueIOFile();
//        ArgumentCaptor<OneoffTask> peopleCaptor = ArgumentCaptor.forClass(OneoffTask.class);
//        Mockito.verify(gcmNetworkManager).schedule(peopleCaptor.capture());
//        assertThat(peopleCaptor.getValue().getWindowEnd(), is(30L));
//        assertThat(peopleCaptor.getValue().getWindowStart(), is(0L));
//        assertThat(peopleCaptor.getValue().getRequiresCharging(), is(false));
//        assertThat(peopleCaptor.getValue().getExtras(), is(notNullValue()));
//        assertThat(peopleCaptor.getValue().getExtras().getString(JOB_TYPE), is(DOWNLOAD_FILE));
//        assertThat(peopleCaptor.getValue().getServiceName(), is(GenericGCMService.class.getName()));
//        assertThat(peopleCaptor.getValue().getRequiredNetwork(), is(fileIOReq.onWifi() ? NETWORK_STATE_UNMETERED : NETWORK_STATE_CONNECTED));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testQueueIoFile_ifJobSchedulerIsInvoked_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOJUnitTestRobot.createFileIoReq(true, true, FileIOJUnitTestRobot.createFileWhichDoesNotExist());
        final RestApiImpl restApi = FileIOJUnitTestRobot.createRestApi();
        FileIO fileIO = FileIOJUnitTestRobot.createFileIO(FileIOJUnitTestRobot.createMockedContext()
                , restApi
                , 1
                , fileIOReq
                , true);
        fileIO.queueIOFile();
        verify(FileIOJUnitTestRobot.getMockedJobScheduler()).schedule(Mockito.any(Job.class));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testQueueIoFile_ifCorrectArgumentsArePassedToJobScheduler_whenGooglePlayServicesAreAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOJUnitTestRobot.createFileIoReq(true, true, FileIOJUnitTestRobot.createFileWhichDoesNotExist());
        final RestApiImpl restApi = FileIOJUnitTestRobot.createRestApi();
        FileIO fileIO = FileIOJUnitTestRobot.createFileIO(FileIOJUnitTestRobot.createMockedContext()
                , restApi
                , 1
                , fileIOReq
                , true);
        fileIO.queueIOFile();
        ArgumentCaptor<Job> argumentCaptor = ArgumentCaptor.forClass(Job.class);
        verify(FileIOJUnitTestRobot.getMockedJobScheduler()).schedule(argumentCaptor.capture());
//        assertThat(argumentCaptor.getValue().getService().getClassName(), is(equalTo(GenericJobService.class.getName())));
//        assertThat(argumentCaptor.getValue().isRequireCharging(), is(fileIOReq.isWhileCharging()));
//        assertThat(argumentCaptor.getValue().isPersisted(), is(true));
//        assertThat(argumentCaptor.getValue().getNetworkType(), is(fileIOReq.onWifi() ? NETWORK_TYPE_UNMETERED : NETWORK_TYPE_ANY));
        assertThat(argumentCaptor.getValue().getExtras(), is(notNullValue()));
        assertThat(argumentCaptor.getValue().getExtras().getString(JOB_TYPE), is(DOWNLOAD_FILE));
    }

    @Test
    public void testQueueIoFile_ifGCMNetworkManagerIsNotScheduled_whenGooglePlayServicesAreNotAvailable() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOJUnitTestRobot.createFileIoReq(true, true, FileIOJUnitTestRobot.createFileWhichDoesNotExist());
        final RestApiImpl restApi = FileIOJUnitTestRobot.createRestApi();
        final FirebaseJobDispatcher gcmNetworkManager = FileIOJUnitTestRobot.getGcmNetworkManager(mockContext);
        FileIO fileIO = FileIOJUnitTestRobot.createFileIO(FileIOJUnitTestRobot.createMockedContext()
                , restApi
                , 2
                , fileIOReq
                , true);
        fileIO.queueIOFile();
        verify(gcmNetworkManager, times(0)).schedule(Mockito.any(Job.class));
    }

    @Test
    public void testExecute_ifFileIsNotDownloaded_whenFileIsToBeDownloadedAndFileDoesExist() throws IOException, PackageManager.NameNotFoundException {
        FileIORequest fileIOReq =
                FileIOJUnitTestRobot.createFileIoReq(true, true, FileIOJUnitTestRobot.createFileWhichDoesExist());
        final RestApiImpl restApi = FileIOJUnitTestRobot.createRestApi();
        FileIO fileIO = FileIOJUnitTestRobot.createFileIO(FileIOJUnitTestRobot.createMockedContext()
                , restApi
                , 3
                , fileIOReq
                , true);
        fileIO.execute();
        verify(restApi, times(0)).dynamicDownload(anyString());
    }
}
