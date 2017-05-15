package com.zeyad.usecases.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.R;
import com.zeyad.usecases.services.jobs.FileIO;
import com.zeyad.usecases.services.jobs.Post;
import com.zeyad.usecases.stores.CloudDataStore;
import com.zeyad.usecases.utils.Utils;

import rx.subscriptions.CompositeSubscription;

public class GenericJobService extends JobService {

    public static final String DOWNLOAD_FILE = "DOWNLOAD_FILE", UPLOAD_FILE = "UPLOAD_FILE",
            JOB_TYPE = "JOB_TYPE", POST = "POST", PAYLOAD = "payload", TRIAL_COUNT = "trialCount";
    private static final String TAG = GenericJobService.class.getSimpleName();
    @Nullable
    private CompositeSubscription mCompositeSubscription;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
    }

    @Override
    public boolean onStartJob(@NonNull JobParameters params) {
        if (params.getExtras() != null && params.getExtras().containsKey(PAYLOAD)) {
            if (mCompositeSubscription == null || mCompositeSubscription.isUnsubscribed())
                mCompositeSubscription = new CompositeSubscription();
            CloudDataStore instance = Config.getCloudDataStore();
            int trailCount = params.getExtras().getInt(TRIAL_COUNT);
            Utils utils = Utils.getInstance();
            switch (params.getExtras().getString(JOB_TYPE, "")) {
                case POST:
                    mCompositeSubscription.add(new Post(this, params.getExtras().getParcelable(PAYLOAD),
                            Config.getApiConnection(), trailCount, utils).execute());
                    Log.d(TAG, getString(R.string.job_started, POST));
                    break;
                case DOWNLOAD_FILE:
                    mCompositeSubscription.add(new FileIO(trailCount, params.getExtras().getParcelable(PAYLOAD),
                            this, true, instance, utils).execute());
                    Log.d(TAG, getString(R.string.job_started, DOWNLOAD_FILE));
                    break;
                case UPLOAD_FILE:
                    mCompositeSubscription.add(new FileIO(trailCount, params.getExtras().getParcelable(PAYLOAD),
                            this, false, instance, utils).execute());
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

    @Override
    public void onDestroy() {
        if (mCompositeSubscription != null)
            mCompositeSubscription.unsubscribe();
        super.onDestroy();
    }
}
