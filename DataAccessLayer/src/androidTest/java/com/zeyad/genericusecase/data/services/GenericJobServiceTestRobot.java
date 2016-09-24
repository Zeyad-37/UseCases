package com.zeyad.genericusecase.data.services;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;

import org.mockito.Mockito;

import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.DOWNLOAD_FILE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.JOB_TYPE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.PAYLOAD;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.POST;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.UPLOAD_FILE;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class GenericJobServiceTestRobot {


    private static final String TASK_PARAM_PAYLOAD = "some_payload";
    private static final Context MOCKED_CONTEXT = Mockito.mock(Context.class);
    private static final JobScheduler JOB_SCHEDULER = Mockito.mock(JobScheduler.class);

    private static JobParameters createJobParam(String tag, String jobType) {
        JobParameters jobParameters = Mockito.mock(JobParameters.class);
        final PersistableBundle extraBundle = getJobParamsExtraBundle(jobType);
        Mockito.when(jobParameters.getExtras()).thenReturn(extraBundle);
//        Mockito.when(jobParameters.getTag()).thenReturn(tag);
        return jobParameters;
    }

    @NonNull
    private static PersistableBundle getJobParamsExtraBundle(String jobType) {
        final PersistableBundle bundle = new PersistableBundle();
        bundle.putString(PAYLOAD, TASK_PARAM_PAYLOAD);
        bundle.putString(JOB_TYPE, jobType);
        return bundle;
    }

    static String getTaskParamPayload() {
        return TASK_PARAM_PAYLOAD;
    }

    static Context getMockedContext() {
//        Mockito.when(MOCKED_CONTEXT.getMainLooper()).thenReturn(Mockito.mock(Looper.class));
        Mockito.when(MOCKED_CONTEXT.getSystemService(Context.JOB_SCHEDULER_SERVICE)).thenReturn(JOB_SCHEDULER);
        return MOCKED_CONTEXT;
    }

    static boolean runForDownloadFile() {
        final GenericJobService service = new GenericJobService();
        service.setContext(getMockedContext());
        service.setApplicationContext(getMockedContext());
        return service.onStartJob(createJobParam(GenericGCMService.TAG_TASK_ONE_OFF_LOG, DOWNLOAD_FILE));
    }

    static boolean runForUploadFile() {
        final GenericJobService service = new GenericJobService();
        service.setContext(getMockedContext());
        service.setApplicationContext(getMockedContext());
        return service.onStartJob(createJobParam(GenericGCMService.TAG_TASK_ONE_OFF_LOG, UPLOAD_FILE));
    }

    static boolean runForPost() {
        final GenericJobService service = new GenericJobService();
        service.setContext(getMockedContext());
        service.setApplicationContext(getMockedContext());
        return service.onStartJob(createJobParam(GenericGCMService.TAG_TASK_ONE_OFF_LOG, POST));
    }

    static void clearAll() {
        Mockito.reset(MOCKED_CONTEXT);
    }

    static JobInfo scheduleJob() {
        final JobInfo mockedJobInfo = Mockito.mock(JobInfo.class);
        final GenericJobService genericJobService = new GenericJobService();
        genericJobService.setContext(GenericJobServiceTestRobot.getMockedContext());
        genericJobService.setApplicationContext(GenericJobServiceTestRobot.getMockedContext());
        genericJobService.scheduleJob(mockedJobInfo);
        return mockedJobInfo;
    }

    public static JobScheduler getMockedJobScheduler() {
        return JOB_SCHEDULER;
    }
}
