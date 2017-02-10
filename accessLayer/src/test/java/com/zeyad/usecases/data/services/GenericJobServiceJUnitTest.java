package com.zeyad.usecases.data.services;

import android.annotation.TargetApi;
import android.app.job.JobScheduler;
import android.content.Intent;
import android.os.Build;

import com.firebase.jobdispatcher.Job;
import com.zeyad.usecases.BuildConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.zeyad.usecases.data.services.GenericJobService.DOWNLOAD_FILE;
import static com.zeyad.usecases.data.services.GenericJobService.JOB_TYPE;
import static com.zeyad.usecases.data.services.GenericJobService.PAYLOAD;
import static com.zeyad.usecases.data.services.GenericJobService.POST;
import static com.zeyad.usecases.data.services.GenericJobService.TRIAL_COUNT;
import static com.zeyad.usecases.data.services.GenericJobService.UPLOAD_FILE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({JobScheduler.class})
public class GenericJobServiceJUnitTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        GenericJobServiceJUnitTestRobot.clearAll();
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsDownloadFile() throws Exception {
        GenericJobServiceJUnitTestRobot.runForDownloadFile();
        Mockito.verify(GenericJobServiceJUnitTestRobot.getMockedContext()).startService(Mockito.any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        GenericJobServiceJUnitTestRobot.runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(DOWNLOAD_FILE)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        GenericJobServiceJUnitTestRobot.runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        GenericJobServiceJUnitTestRobot.runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(GenericJobServiceJUnitTestRobot.getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsDownloadFile() throws Exception {
        boolean returnedValue = GenericJobServiceJUnitTestRobot.runForDownloadFile();
        assertThat(returnedValue, is(equalTo(true)));
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsUploadFile() throws Exception {
        GenericJobServiceJUnitTestRobot.runForUploadFile();
        Mockito.verify(GenericJobServiceJUnitTestRobot.getMockedContext()).startService(Mockito.any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsUploadFile() throws Exception {
        GenericJobServiceJUnitTestRobot.runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(UPLOAD_FILE)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsUploadFile() throws Exception {
        GenericJobServiceJUnitTestRobot.runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsUploadFile() throws Exception {
        GenericJobServiceJUnitTestRobot.runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(GenericJobServiceJUnitTestRobot.getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsUploadFile() throws Exception {
        boolean returnedValue = GenericJobServiceJUnitTestRobot.runForUploadFile();
        assertThat(returnedValue, is(equalTo(true)));
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsPost() throws Exception {
        GenericJobServiceJUnitTestRobot.runForPost();
        Mockito.verify(GenericJobServiceJUnitTestRobot.getMockedContext()).startService(Mockito.any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsPost() throws Exception {
        GenericJobServiceJUnitTestRobot.runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(POST)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsPost() throws Exception {
        GenericJobServiceJUnitTestRobot.runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsPost() throws Exception {
        GenericJobServiceJUnitTestRobot.runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(GenericJobServiceJUnitTestRobot.getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsPost() throws Exception {
        boolean returnedValue = GenericJobServiceJUnitTestRobot.runForPost();
        assertThat(returnedValue, is(equalTo(true)));
    }

    @Test
    public void testJobSchedule_ifJobServiceIsScheduled_whenMethodIsCalled() {
        final Job mockedJobInfo = GenericJobServiceJUnitTestRobot.scheduleJob();
        Mockito.verify(GenericJobServiceJUnitTestRobot.getMockedJobScheduler()).schedule(mockedJobInfo);
    }
}