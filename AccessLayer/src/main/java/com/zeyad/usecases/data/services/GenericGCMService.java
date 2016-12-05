package com.zeyad.usecases.data.services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.zeyad.usecases.R;

import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.DOWNLOAD_FILE;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.JOB_TYPE;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.PAYLOAD;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.POST;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.TRIAL_COUNT;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.UPLOAD_FILE;

public class GenericGCMService extends GcmTaskService {

    public static final String TAG = GenericGCMService.class.getSimpleName(),
            TAG_TASK_ONE_OFF_LOG = "one_off_task", TAG_TASK_PERIODIC_LOG = "periodic_task";
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    public void onInitializeTasks() {
        super.onInitializeTasks();
        // Reschedule removed tasks here
    }

    @Override
    public int onRunTask(@NonNull TaskParams taskParams) {
        switch (taskParams.getTag()) {
            case TAG_TASK_ONE_OFF_LOG:
                Log.i(TAG, TAG_TASK_ONE_OFF_LOG);
                if (taskParams.getExtras().containsKey(JOB_TYPE))
                    switch (taskParams.getExtras().getString(JOB_TYPE)) {
                        case DOWNLOAD_FILE:
                            mContext.startService(new Intent(mContext, GenericNetworkQueueIntentService.class)
                                    .putExtra(JOB_TYPE, DOWNLOAD_FILE)
                                    .putExtra(TRIAL_COUNT, 0)
                                    .putExtra(PAYLOAD, taskParams.getExtras().getString(PAYLOAD)));
                            Log.d(TAG, getString(R.string.job_started, DOWNLOAD_FILE));
                            break;
                        case UPLOAD_FILE:
                            mContext.startService(new Intent(mContext, GenericNetworkQueueIntentService.class)
                                    .putExtra(JOB_TYPE, UPLOAD_FILE)
                                    .putExtra(TRIAL_COUNT, 0)
                                    .putExtra(PAYLOAD, taskParams.getExtras().getString(PAYLOAD)));
                            Log.d(TAG, getString(R.string.job_started, UPLOAD_FILE));
                            break;
                        case POST:
                            mContext.startService(new Intent(mContext, GenericNetworkQueueIntentService.class)
                                    .putExtra(JOB_TYPE, POST)
                                    .putExtra(TRIAL_COUNT, 0)
                                    .putExtra(PAYLOAD, taskParams.getExtras().getString(PAYLOAD)));
                            Log.d(TAG, getString(R.string.job_started, POST));
                            break;
                        default:
                            break;
                    }
                return GcmNetworkManager.RESULT_SUCCESS;
            case TAG_TASK_PERIODIC_LOG:
                Log.i(TAG, TAG_TASK_PERIODIC_LOG);
                // This is where useful work would go
                return GcmNetworkManager.RESULT_SUCCESS;
            default:
                return GcmNetworkManager.RESULT_FAILURE;
        }
    }

    /**
     * This method is meant for testing purposes. To set a mocked context.
     *
     * @param context mocked context
     */
    void setContext(Context context) {
        mContext = context;
    }
}
