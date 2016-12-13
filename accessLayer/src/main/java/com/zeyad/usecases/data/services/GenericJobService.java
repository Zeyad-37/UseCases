package com.zeyad.usecases.data.services;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.zeyad.usecases.R;
import com.zeyad.usecases.data.services.jobs.FileIO;
import com.zeyad.usecases.data.services.jobs.Post;

import rx.subscriptions.CompositeSubscription;

public class GenericJobService extends JobService {

    public static final String DOWNLOAD_FILE = "DOWNLOAD_FILE", UPLOAD_FILE = "UPLOAD_FILE",
            JOB_TYPE = "JOB_TYPE", POST = "POST", PAYLOAD = "payload", TRIAL_COUNT = "trialCount";
    private static final String TAG = GenericJobService.class.getSimpleName();
    @Nullable
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
        mContext = this;
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
    public boolean onStartJob(@NonNull JobParameters params) {
        if (params.getExtras() != null && params.getExtras().containsKey(PAYLOAD)) {
            if (mCompositeSubscription == null || mCompositeSubscription.isUnsubscribed())
                mCompositeSubscription = new CompositeSubscription();
            switch (params.getExtras().getString(JOB_TYPE, "")) {
                case POST:
                    mCompositeSubscription.add(new Post(params.getExtras().getInt(TRIAL_COUNT),
                            params.getExtras().getString(PAYLOAD, ""), this).execute());
                    Log.d(TAG, getString(R.string.job_started, POST));
                    break;
                case DOWNLOAD_FILE:
                    mCompositeSubscription.add(new FileIO(params.getExtras().getInt(TRIAL_COUNT),
                            params.getExtras().getString(PAYLOAD, ""), this, true).execute());
                    Log.d(TAG, getString(R.string.job_started, DOWNLOAD_FILE));
                    break;
                case UPLOAD_FILE:
                    mCompositeSubscription.add(new FileIO(params.getExtras().getInt(TRIAL_COUNT),
                            params.getExtras().getString(PAYLOAD, ""), this, false).execute());
                    Log.d(TAG, getString(R.string.job_started, UPLOAD_FILE));
                    break;
                default:
                    break;
            }
        }
        return true; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters params) {
        if (mCompositeSubscription != null)
            mCompositeSubscription.unsubscribe();
        Log.i(TAG, "on stop job: " + params.getTag());
        return true; // Answers the question: "Should this job be retried?"
    }

    /**
     * This method is meant for testing purposes. To set a mocked context.
     *
     * @param context mocked context
     */
    @VisibleForTesting
    void setContext(Context context) {
        mContext = context;
    }
}
