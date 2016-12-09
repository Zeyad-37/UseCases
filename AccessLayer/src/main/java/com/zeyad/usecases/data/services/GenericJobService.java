package com.zeyad.usecases.data.services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.zeyad.usecases.R;

import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.DOWNLOAD_FILE;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.JOB_TYPE;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.PAYLOAD;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.POST;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.TRIAL_COUNT;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.UPLOAD_FILE;

public class GenericJobService extends JobService {

    private static final String TAG = GenericJobService.class.getName();
    private Context mContext;
    private Context mApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
        mContext = this;
        mApplicationContext = getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }

    /**
     * Send job to the JobScheduler.
     */
    public void scheduleJob(Job params) {
        Log.d(TAG, "Scheduling job");
        FirebaseJobDispatcher tm = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
        tm.schedule(params);
    }

    @Override
    public boolean onStartJob(@NonNull JobParameters params) { // return true if u r doing background thread work, else return false
        if (params.getExtras() != null)
            switch (params.getExtras().getString(JOB_TYPE, "")) {
                case DOWNLOAD_FILE:
                    mContext.startService(new Intent(mApplicationContext, GenericNetworkQueueIntentService.class)
                            .putExtra(JOB_TYPE, DOWNLOAD_FILE)
                            .putExtra(TRIAL_COUNT, 0)
                            .putExtra(PAYLOAD, params.getExtras().getString(PAYLOAD)));
                    Log.d(TAG, getString(R.string.job_started, DOWNLOAD_FILE));
                    break;
                case UPLOAD_FILE:
                    mContext.startService(new Intent(mApplicationContext, GenericNetworkQueueIntentService.class)
                            .putExtra(JOB_TYPE, UPLOAD_FILE)
                            .putExtra(TRIAL_COUNT, 0)
                            .putExtra(PAYLOAD, params.getExtras().getString(PAYLOAD)));
                    Log.d(TAG, getString(R.string.job_started, UPLOAD_FILE));
                    break;
                case POST:
                    mContext.startService(new Intent(mApplicationContext, GenericNetworkQueueIntentService.class)
                            .putExtra(JOB_TYPE, POST)
                            .putExtra(TRIAL_COUNT, 0)
                            .putExtra(PAYLOAD, params.getExtras().getString(PAYLOAD)));
                    Log.d(TAG, getString(R.string.job_started, POST));
                    break;
                default:
                    break;
            }
        return true; // Answers the question: "Is there still work going on?"
    }

    // called if the preset conditions changed during the job is running.
    @Override
    public boolean onStopJob(@NonNull JobParameters params) {
        // Stop tracking these job parameters, as we've 'finished' executing.
        Log.i(TAG, "on stop job: " + params.getTag());
        return true; // Answers the question: "Should this job be retried?"
    }

    /**
     * This method is meant for testing purposes. To set a mocked context.
     *
     * @param context mocked context
     */
    void setContext(Context context) {
        mContext = context;
    }

    /**
     * This method is meant for testing purposes. To set a mocked context.
     *
     * @param applicationContext mocked context
     */
    void setApplicationContext(Context applicationContext) {
        mApplicationContext = applicationContext;
    }
}
