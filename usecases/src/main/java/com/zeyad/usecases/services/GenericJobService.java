package com.zeyad.usecases.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.R;
import com.zeyad.usecases.utils.Utils;

import rx.subscriptions.CompositeSubscription;

public class GenericJobService extends JobService {

    public static final String DOWNLOAD_FILE = "DOWNLOAD_FILE", UPLOAD_FILE = "UPLOAD_FILE",
            JOB_TYPE = "JOB_TYPE", POST = "POST", PAYLOAD = "payload", TRIAL_COUNT = "trialCount";
    private static final String TAG = GenericJobService.class.getSimpleName();
    private final GenericJobServiceLogic genericJobServiceLogic = new GenericJobServiceLogic();
    private CompositeSubscription mCompositeSubscription;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
    }

    @Override
    public boolean onStartJob(@NonNull JobParameters params) {
        if (mCompositeSubscription == null || mCompositeSubscription.isUnsubscribed())
            mCompositeSubscription = new CompositeSubscription();
        mCompositeSubscription.add(genericJobServiceLogic.startJob(params.getExtras().getBundle(PAYLOAD),
                Config.getCloudDataStore(),
                Utils.getInstance(), getString(R.string.job_started)).subscribe());
        return true; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters params) {
        if (mCompositeSubscription != null)
            mCompositeSubscription.unsubscribe();
        Log.i(TAG, "on stop job: " + params.getTag());
        return true; // Answers the question: "Should this job be retried?"
    }

    @Override
    public void onDestroy() {
        if (mCompositeSubscription != null)
            mCompositeSubscription.unsubscribe();
        super.onDestroy();
    }
}
