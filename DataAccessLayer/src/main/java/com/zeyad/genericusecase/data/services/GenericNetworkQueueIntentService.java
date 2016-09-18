package com.zeyad.genericusecase.data.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.genericusecase.data.services.jobs.FileIO;
import com.zeyad.genericusecase.data.services.jobs.Post;
import com.zeyad.genericusecase.data.utils.Utils;

import rx.subscriptions.CompositeSubscription;

public class GenericNetworkQueueIntentService extends IntentService {

    public static final String TAG = GenericNetworkQueueIntentService.class.getSimpleName(),
            DOWNLOAD_FILE = "DOWNLOAD_FILE", UPLOAD_FILE = "UPLOAD_FILE", JOB_TYPE = "JOB_TYPE",
            POST = "POST", LIST = "LIST", PAYLOAD = "payload", TRIAL_COUNT = "trialCount";
    @Nullable
    private CompositeSubscription mCompositeSubscription;

    public GenericNetworkQueueIntentService() {
        super(GenericNetworkQueueIntentService.class.getName());
        mCompositeSubscription = Utils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
    }

    @Override
    protected void onHandleIntent(@NonNull Intent intent) {
        switch (intent.getStringExtra(JOB_TYPE)) {
            case POST:
                mCompositeSubscription.add(new Post(intent, getApplicationContext()).execute());
                break;
            case DOWNLOAD_FILE:
                mCompositeSubscription.add(new FileIO(intent, getApplicationContext(), true).execute());
                break;
            case UPLOAD_FILE:
                mCompositeSubscription.add(new FileIO(intent, getApplicationContext(), false).execute());
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        Utils.unsubscribeIfNotNull(mCompositeSubscription);
        super.onDestroy();
    }
}
