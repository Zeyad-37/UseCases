package com.zeyad.usecases.data.services;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.test.runner.AndroidJUnit4;

import com.firebase.jobdispatcher.Job;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

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
@RunWith(AndroidJUnit4.class)
//@Config(constants = BuildConfig.class)
public class GenericJobServiceTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        GenericJobServiceTestRobot.clearAll();
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsDownloadFile() throws Exception {
        GenericJobServiceTestRobot.runForDownloadFile();
        Mockito.verify(GenericJobServiceTestRobot.getMockedContext()).startService(Mockito.any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        GenericJobServiceTestRobot.runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(DOWNLOAD_FILE)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        GenericJobServiceTestRobot.runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        GenericJobServiceTestRobot.runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(GenericJobServiceTestRobot.getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsDownloadFile() throws Exception {
        boolean returnedValue = GenericJobServiceTestRobot.runForDownloadFile();
        assertThat(returnedValue, is(equalTo(true)));
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsUploadFile() throws Exception {
        GenericJobServiceTestRobot.runForUploadFile();
        Mockito.verify(GenericJobServiceTestRobot.getMockedContext()).startService(Mockito.any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsUploadFile() throws Exception {
        GenericJobServiceTestRobot.runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(UPLOAD_FILE)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsUploadFile() throws Exception {
        GenericJobServiceTestRobot.runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsUploadFile() throws Exception {
        GenericJobServiceTestRobot.runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(GenericJobServiceTestRobot.getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsUploadFile() throws Exception {
        boolean returnedValue = GenericJobServiceTestRobot.runForUploadFile();
        assertThat(returnedValue, is(equalTo(true)));
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsPost() throws Exception {
        GenericJobServiceTestRobot.runForPost();
        Mockito.verify(GenericJobServiceTestRobot.getMockedContext()).startService(Mockito.any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsPost() throws Exception {
        GenericJobServiceTestRobot.runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(POST)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsPost() throws Exception {
        GenericJobServiceTestRobot.runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsPost() throws Exception {
        GenericJobServiceTestRobot.runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericJobServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(GenericJobServiceTestRobot.getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsPost() throws Exception {
        boolean returnedValue = GenericJobServiceTestRobot.runForPost();
        assertThat(returnedValue, is(equalTo(true)));
    }

    @Test
    public void testJobSchedule_ifJobServiceIsScheduled_whenMethodIsCalled() {
        final Job mockedJobInfo = GenericJobServiceTestRobot.scheduleJob();
        Mockito.verify(GenericJobServiceTestRobot.getMockedJobScheduler()).schedule(mockedJobInfo);
    }
}