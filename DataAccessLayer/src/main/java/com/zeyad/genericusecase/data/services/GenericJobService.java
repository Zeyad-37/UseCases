package com.zeyad.genericusecase.data.services;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.LinkedList;

import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.JOB_TYPE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.PAYLOAD;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.POST;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.TRIAL_COUNT;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.UPLOAD_FILE;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class GenericJobService extends JobService { // runs on the ui thread

    private static final String TAG = GenericJobService.class.getName();
    private final LinkedList<JobParameters> jobParamsMap = new LinkedList<>();

    /**
     * Send job to the JobScheduler.
     */
    public void scheduleJob(JobInfo t) {
        Log.d(TAG, "Scheduling job");
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(t);
    }

    /**
     * Not currently used, but as an exercise you can hook this
     * up to a button in the UI to finish a job that has landed
     * in onStartJob().
     */
    public boolean callJobFinished() {
        JobParameters params = jobParamsMap.poll();
        if (params == null) {
            return false;
        } else {
            jobFinished(params, false);
            return true;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }

    /**
     * When the app's MainActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCalback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(@NonNull JobParameters params) { // return true if u r doing background thread work, else return false
        switch (params.getExtras().getString(JOB_TYPE, "")) {
            case GenericNetworkQueueIntentService.DOWNLOAD_FILE:
                startService(new Intent(getApplicationContext(), GenericNetworkQueueIntentService.class)
                        .putExtra(JOB_TYPE, UPLOAD_FILE)
                        .putExtra(TRIAL_COUNT, 0)
                        .putExtra(PAYLOAD, params.getExtras().getString(PAYLOAD)));
                Log.d(TAG, "FileIO Job started!");
                break;
            case UPLOAD_FILE:
                startService(new Intent(getApplicationContext(), GenericNetworkQueueIntentService.class)
                        .putExtra(JOB_TYPE, UPLOAD_FILE)
                        .putExtra(TRIAL_COUNT, 0)
                        .putExtra(PAYLOAD, params.getExtras().getString(PAYLOAD)));
                Log.d(TAG, "FileIO Job started!");
                break;
            case POST:
                startService(new Intent(this, GenericNetworkQueueIntentService.class)
                        .putExtra(JOB_TYPE, POST)
                        .putExtra(TRIAL_COUNT, 0)
                        .putExtra(PAYLOAD, params.getExtras().getString(PAYLOAD)));
                Log.d(TAG, "Post Object Job Started!");
                break;
            default:
                break;
        }
        jobFinished(params, false);// true to reschedule, false to drop
        return true;
    }

    // called if the preset conditions changed during the job is running.
    @Override
    public boolean onStopJob(@NonNull JobParameters params) { // return true if u want to reschedule, false to drop
        // Stop tracking these job parameters, as we've 'finished' executing.
        jobParamsMap.remove(params);
        Log.i(TAG, "on stop job: " + params.getJobId());
        return true;
    }
}
