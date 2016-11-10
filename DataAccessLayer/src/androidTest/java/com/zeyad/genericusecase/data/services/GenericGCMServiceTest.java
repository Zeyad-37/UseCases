package com.zeyad.genericusecase.data.services;

import android.content.Intent;

import com.google.android.gms.gcm.GcmNetworkManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.DOWNLOAD_FILE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.JOB_TYPE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.PAYLOAD;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.POST;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.TRIAL_COUNT;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.UPLOAD_FILE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class)
public class GenericGCMServiceTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        GenericGCMServiceTestRobot.clearAll();
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsDownloadFile() throws Exception {
        GenericGCMServiceTestRobot.runForDownloadFile();
        Mockito.verify(GenericGCMServiceTestRobot.getMockedContext()).startService(Mockito.any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        GenericGCMServiceTestRobot.runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(DOWNLOAD_FILE)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        GenericGCMServiceTestRobot.runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        GenericGCMServiceTestRobot.runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(GenericGCMServiceTestRobot.getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsDownloadFile() throws Exception {
        int returnedValue = GenericGCMServiceTestRobot.runForDownloadFile();
        assertThat(returnedValue, is(equalTo(GcmNetworkManager.RESULT_SUCCESS)));
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsUploadFile() throws Exception {
        GenericGCMServiceTestRobot.runForUploadFile();
        Mockito.verify(GenericGCMServiceTestRobot.getMockedContext()).startService(Mockito.any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsUploadFile() throws Exception {
        GenericGCMServiceTestRobot.runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(UPLOAD_FILE)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsUploadFile() throws Exception {
        GenericGCMServiceTestRobot.runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsUploadFile() throws Exception {
        GenericGCMServiceTestRobot.runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(GenericGCMServiceTestRobot.getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsUploadFile() throws Exception {
        int returnedValue = GenericGCMServiceTestRobot.runForUploadFile();
        assertThat(returnedValue, is(equalTo(GcmNetworkManager.RESULT_SUCCESS)));
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsPost() throws Exception {
        GenericGCMServiceTestRobot.runForPost();
        Mockito.verify(GenericGCMServiceTestRobot.getMockedContext()).startService(Mockito.any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsPost() throws Exception {
        GenericGCMServiceTestRobot.runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(POST)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsPost() throws Exception {
        GenericGCMServiceTestRobot.runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsPost() throws Exception {
        GenericGCMServiceTestRobot.runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(GenericGCMServiceTestRobot.getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericNetworkQueueIntentService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(GenericGCMServiceTestRobot.getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsPost() throws Exception {
        int returnedValue = GenericGCMServiceTestRobot.runForPost();
        assertThat(returnedValue, is(equalTo(GcmNetworkManager.RESULT_SUCCESS)));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenTagIsPeriodicTag() throws Exception {
        int returnedValue = GenericGCMServiceTestRobot.runForPeriodicLog();
        assertThat(returnedValue, is(equalTo(GcmNetworkManager.RESULT_SUCCESS)));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenTagIsNeitherPeriodicNorOneOff() throws Exception {
        int returnedValue = GenericGCMServiceTestRobot.runForDefaultTag();
        assertThat(returnedValue, is(equalTo(GcmNetworkManager.RESULT_FAILURE)));
    }
}