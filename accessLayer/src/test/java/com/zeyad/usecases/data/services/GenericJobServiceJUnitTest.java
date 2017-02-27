package com.zeyad.usecases.data.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.zeyad.usecases.BuildConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({FirebaseJobDispatcher.class})
public class GenericJobServiceJUnitTest {

    // --- //
    private final String TASK_PARAM_PAYLOAD = "some_payload";
    private Context MOCKED_CONTEXT = mock(Context.class); // InstrumentationRegistry.getContext()
    private FirebaseJobDispatcher JOB_SCHEDULER = new FirebaseJobDispatcher(new GooglePlayDriver(MOCKED_CONTEXT));

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        clearAll();
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsDownloadFile() throws Exception {
        runForDownloadFile();
        verify(getMockedContext()).startService(any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        verify(getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(DOWNLOAD_FILE)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        verify(getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsDownloadFile() throws Exception {
        runForDownloadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        verify(getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsDownloadFile() throws Exception {
        boolean returnedValue = runForDownloadFile();
        assertThat(returnedValue, is(equalTo(true)));
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsUploadFile() throws Exception {
        runForUploadFile();
        verify(getMockedContext()).startService(any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsUploadFile() throws Exception {
        runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        verify(getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(UPLOAD_FILE)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsUploadFile() throws Exception {
        runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        verify(getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsUploadFile() throws Exception {
        runForUploadFile();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        verify(getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsUploadFile() throws Exception {
        boolean returnedValue = runForUploadFile();
        assertThat(returnedValue, is(equalTo(true)));
    }

    @Test
    public void testRunTask_ifServiceIsStarted_whenJobTypeIsPost() throws Exception {
        runForPost();
        verify(getMockedContext()).startService(any(Intent.class));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectJobTypeValuesSet_whenJobTypeIsPost() throws Exception {
        runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        verify(getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(JOB_TYPE), is(equalTo(POST)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectTrailCountValuesSet_whenJobTypeIsPost() throws Exception {
        runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        verify(getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getIntExtra(TRIAL_COUNT, -1), is(equalTo(0)));
    }

    @Test
    public void testRunTask_ifIntentHasCorrectPayloadValuesSet_whenJobTypeIsPost() throws Exception {
        runForPost();
        ArgumentCaptor<Intent> argCapture = ArgumentCaptor.forClass(Intent.class);
        verify(getMockedContext()).startService(argCapture.capture());
        assertThat(argCapture.getValue().getComponent().getClassName(), is(equalTo(GenericJobService.class.getName())));
        assertThat(argCapture.getValue().getStringExtra(PAYLOAD), is(equalTo(getTaskParamPayload())));
    }

    @Test
    public void testRunTask_ifReturnedValueIsCorrect_whenJobTypeIsPost() throws Exception {
        boolean returnedValue = runForPost();
        assertThat(returnedValue, is(equalTo(true)));
    }

    @Test
    public void testJobSchedule_ifJobServiceIsScheduled_whenMethodIsCalled() {
        final Job mockedJobInfo = scheduleJob();
        verify(getMockedJobScheduler()).schedule(mockedJobInfo);
    }

    private Job createJobParam(String jobType) {
        Job jobParameters = JOB_SCHEDULER.newJobBuilder()
//                .setService(GenericJobService.class) // the JobService that will be called
                .setTag("my-unique-tag")        // uniquely identifies the job
                .build();
        final Bundle extraBundle = getJobParamsExtraBundle(jobType);
        when(jobParameters.getExtras()).thenReturn(extraBundle);
//        Mockito.when(jobParameters.getTag()).thenReturn(tag);
        return jobParameters;
    }

    @NonNull
    private Bundle getJobParamsExtraBundle(String jobType) {
        final Bundle bundle = new Bundle();
        bundle.putString(PAYLOAD, TASK_PARAM_PAYLOAD);
        bundle.putString(JOB_TYPE, jobType);
        return bundle;
    }

    @NonNull
    String getTaskParamPayload() {
        return TASK_PARAM_PAYLOAD;
    }

    Context getMockedContext() {
//        when(MOCKED_CONTEXT.getMainLooper()).thenReturn(Mockito.mock(Looper.class));
        when(MOCKED_CONTEXT.getSystemService(Context.JOB_SCHEDULER_SERVICE)).thenReturn(JOB_SCHEDULER);
        return MOCKED_CONTEXT;
    }

    boolean runForDownloadFile() {
        final GenericJobService service = new GenericJobService();
        service.setContext(getMockedContext());
        return service.onStartJob(createJobParam(DOWNLOAD_FILE));
    }

    boolean runForUploadFile() {
        final GenericJobService service = new GenericJobService();
        service.setContext(getMockedContext());
        return service.onStartJob(createJobParam(UPLOAD_FILE));
    }

    boolean runForPost() {
        final GenericJobService service = new GenericJobService();
        service.setContext(getMockedContext());
        return service.onStartJob(createJobParam(POST));
    }

    void clearAll() {
        reset(MOCKED_CONTEXT);
    }

    Job scheduleJob() {
        final Job mockedJobInfo = mock(Job.class);
        final GenericJobService genericJobService = new GenericJobService();
        genericJobService.setContext(getMockedContext());
        genericJobService.scheduleJob(mockedJobInfo);
        return mockedJobInfo;
    }

    public FirebaseJobDispatcher getMockedJobScheduler() {
        return JOB_SCHEDULER;
    }
}