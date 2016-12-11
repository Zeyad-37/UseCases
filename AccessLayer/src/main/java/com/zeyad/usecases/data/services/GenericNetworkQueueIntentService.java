package com.zeyad.usecases.data.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zeyad.usecases.R;
import com.zeyad.usecases.data.services.jobs.FileIO;
import com.zeyad.usecases.data.services.jobs.Post;

import rx.subscriptions.CompositeSubscription;

public class GenericNetworkQueueIntentService extends IntentService {

    public static final String TAG = GenericNetworkQueueIntentService.class.getSimpleName(),
            DOWNLOAD_FILE = "DOWNLOAD_FILE", UPLOAD_FILE = "UPLOAD_FILE", JOB_TYPE = "JOB_TYPE",
            POST = "POST", PAYLOAD = "payload", TRIAL_COUNT = "trialCount";
    @Nullable
    private CompositeSubscription mCompositeSubscription;

    public GenericNetworkQueueIntentService() {
        super(GenericNetworkQueueIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (mCompositeSubscription == null || mCompositeSubscription.isUnsubscribed())
            mCompositeSubscription = new CompositeSubscription();
        switch (intent.getStringExtra(JOB_TYPE)) {
            case POST:
                mCompositeSubscription.add(new Post(intent, getApplicationContext()).execute());
                Log.d(TAG, getString(R.string.job_started, POST));
                break;
            case DOWNLOAD_FILE:
                mCompositeSubscription.add(new FileIO(intent, getApplicationContext(), true).execute());
                Log.d(TAG, getString(R.string.job_started, DOWNLOAD_FILE));
                break;
            case UPLOAD_FILE:
                mCompositeSubscription.add(new FileIO(intent, getApplicationContext(), false).execute());
                Log.d(TAG, getString(R.string.job_started, UPLOAD_FILE));
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (mCompositeSubscription != null)
            mCompositeSubscription.unsubscribe();
        super.onDestroy();
    }
}
