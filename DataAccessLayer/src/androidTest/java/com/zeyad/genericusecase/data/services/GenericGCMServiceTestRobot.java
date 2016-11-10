package com.zeyad.genericusecase.data.services;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.gcm.TaskParams;

import org.mockito.Mockito;

import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.DOWNLOAD_FILE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.JOB_TYPE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.PAYLOAD;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.POST;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.UPLOAD_FILE;

class GenericGCMServiceTestRobot {

    private static final String TASK_PARAM_PAYLOAD = "some_payload";
    private static final Context MOCKED_CONTEXT = Mockito.mock(Context.class);

    private static TaskParams createTaskParam(String tag, String jobType) {
        TaskParams taskParam = Mockito.mock(TaskParams.class);
        final Bundle extraBundle = getTaskParamExtraBundle(jobType);
        Mockito.when(taskParam.getExtras()).thenReturn(extraBundle);
        Mockito.when(taskParam.getTag()).thenReturn(tag);
        return taskParam;
    }

    @NonNull
    private static Bundle getTaskParamExtraBundle(String jobType) {
        final Bundle bundle = new Bundle();
        bundle.putString(PAYLOAD, TASK_PARAM_PAYLOAD);
        bundle.putString(JOB_TYPE, jobType);
        return bundle;
    }

    @NonNull
    static String getTaskParamPayload() {
        return TASK_PARAM_PAYLOAD;
    }

    static Context getMockedContext() {
        return MOCKED_CONTEXT;
    }

    static int runForDownloadFile() {
        final GenericGCMService genericGCMService = new GenericGCMService();
        genericGCMService.setContext(GenericGCMServiceTestRobot.getMockedContext());
        return genericGCMService.onRunTask(GenericGCMServiceTestRobot
                .createTaskParam(GenericGCMService.TAG_TASK_ONE_OFF_LOG, DOWNLOAD_FILE));
    }

    static int runForUploadFile() {
        final GenericGCMService genericGCMService = new GenericGCMService();
        genericGCMService.setContext(GenericGCMServiceTestRobot.getMockedContext());
        return genericGCMService.onRunTask(GenericGCMServiceTestRobot
                .createTaskParam(GenericGCMService.TAG_TASK_ONE_OFF_LOG, UPLOAD_FILE));
    }

    static int runForPost() {
        final GenericGCMService genericGCMService = new GenericGCMService();
        genericGCMService.setContext(GenericGCMServiceTestRobot.getMockedContext());
        return genericGCMService.onRunTask(GenericGCMServiceTestRobot
                .createTaskParam(GenericGCMService.TAG_TASK_ONE_OFF_LOG, POST));
    }

    static int runForPeriodicLog() {
        final GenericGCMService genericGCMService = new GenericGCMService();
        genericGCMService.setContext(GenericGCMServiceTestRobot.getMockedContext());
        return genericGCMService.onRunTask(GenericGCMServiceTestRobot
                .createTaskParam(GenericGCMService.TAG_TASK_PERIODIC_LOG, null));
    }

    static int runForDefaultTag() {
        final GenericGCMService genericGCMService = new GenericGCMService();
        genericGCMService.setContext(GenericGCMServiceTestRobot.getMockedContext());
        return genericGCMService.onRunTask(GenericGCMServiceTestRobot
                .createTaskParam("-1", null));
    }

    static void clearAll() {
        Mockito.reset(MOCKED_CONTEXT);
    }
}
