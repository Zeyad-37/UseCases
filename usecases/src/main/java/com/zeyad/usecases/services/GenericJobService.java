package com.zeyad.usecases.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.utils.Utils;

import io.reactivex.disposables.CompositeDisposable;

public class GenericJobService extends JobService {

    public static final String DOWNLOAD_FILE = "DOWNLOAD_FILE",
            UPLOAD_FILE = "UPLOAD_FILE",
            JOB_TYPE = "JOB_TYPE",
            POST = "POST",
            PAYLOAD = "payload",
            TRIAL_COUNT = "trialCount",
            TAG = GenericJobService.class.getSimpleName();
    private static final CompositeDisposable disposable = new CompositeDisposable();
    private final GenericJobServiceLogic genericJobServiceLogic = new GenericJobServiceLogic();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();
    }

    @Override
    public boolean onStartJob(@NonNull JobParameters params) {
        disposable.add(genericJobServiceLogic.startJob(params.getExtras().getBundle(PAYLOAD),
                Config.getCloudStore(), Utils.getInstance(), "Job Started").subscribe());
        return true; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters params) {
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
        Log.i(TAG, "on stop job: " + params.getTag());
        return true; // Answers the question: "Should this job be retried?"
    }
}
