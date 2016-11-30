package com.zeyad.genericusecase.data.services;

import android.app.job.JobScheduler;
import android.content.Intent;
import android.support.test.rule.BuildConfig;

import com.google.android.gms.gcm.GcmNetworkManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.DOWNLOAD_FILE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.JOB_TYPE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.PAYLOAD;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.POST;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.TRIAL_COUNT;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.UPLOAD_FILE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({JobScheduler.class})
public class GenericGCMServiceJUnitTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        GenericGCMServiceJUnitTestRobot.clearAll();
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsDownloadFile() throws Exception {
        GenericGCMServiceJUnitTestRobot.runForDownloadFile();
        Mockito.verify(GenericGCMServiceJUnitTestRobot.getMockedContext()).startService(Mockito.any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        GenericGCMServiceJUnitTestRobot.runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(DOWNLOAD_FILE)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        GenericGCMServiceJUnitTestRobot.runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        GenericGCMServiceJUnitTestRobot.runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(GenericGCMServiceJUnitTestRobot.getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsDownloadFile() throws Exception {
        int returnedValue = GenericGCMServiceJUnitTestRobot.runForDownloadFile();
        assertThat(returnedValue, is(equalTo(GcmNetworkManager.RESULT_SUCCESS)));
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsUploadFile() throws Exception {
        GenericGCMServiceJUnitTestRobot.runForUploadFile();
        Mockito.verify(GenericGCMServiceJUnitTestRobot.getMockedContext()).startService(Mockito.any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsUploadFile() throws Exception {
        GenericGCMServiceJUnitTestRobot.runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(UPLOAD_FILE)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsUploadFile() throws Exception {
        GenericGCMServiceJUnitTestRobot.runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsUploadFile() throws Exception {
        GenericGCMServiceJUnitTestRobot.runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(GenericGCMServiceJUnitTestRobot.getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsUploadFile() throws Exception {
        int returnedValue = GenericGCMServiceJUnitTestRobot.runForUploadFile();
        assertThat(returnedValue, is(equalTo(GcmNetworkManager.RESULT_SUCCESS)));
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsPost() throws Exception {
        GenericGCMServiceJUnitTestRobot.runForPost();
        Mockito.verify(GenericGCMServiceJUnitTestRobot.getMockedContext()).startService(Mockito.any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsPost() throws Exception {
        GenericGCMServiceJUnitTestRobot.runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(POST)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsPost() throws Exception {
        GenericGCMServiceJUnitTestRobot.runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsPost() throws Exception {
        GenericGCMServiceJUnitTestRobot.runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceJUnitTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(GenericGCMServiceJUnitTestRobot.getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsPost() throws Exception {
        int returnedValue = GenericGCMServiceJUnitTestRobot.runForPost();
        assertThat(returnedValue, is(equalTo(GcmNetworkManager.RESULT_SUCCESS)));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenTagIsPeriodicTag() throws Exception {
        int returnedValue = GenericGCMServiceJUnitTestRobot.runForPeriodicLog();
        assertThat(returnedValue, is(equalTo(GcmNetworkManager.RESULT_SUCCESS)));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenTagIsNeitherPeriodicNorOneOff() throws Exception {
        int returnedValue = GenericGCMServiceJUnitTestRobot.runForDefaultTag();
        assertThat(returnedValue, is(equalTo(GcmNetworkManager.RESULT_FAILURE)));
    }
}