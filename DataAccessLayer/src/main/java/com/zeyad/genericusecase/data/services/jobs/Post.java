package com.zeyad.genericusecase.data.services.jobs;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zeyad.genericusecase.data.network.RestApi;
import com.zeyad.genericusecase.data.network.RestApiImpl;
import com.zeyad.genericusecase.data.services.GenericGCMService;
import com.zeyad.genericusecase.data.services.GenericJobService;
import com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService;
import com.zeyad.genericusecase.data.utils.Constants;
import com.zeyad.genericusecase.data.utils.Utils;
import com.zeyad.genericusecase.domain.interactors.PostRequest;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.functions.Action1;

import static android.app.job.JobInfo.NETWORK_TYPE_ANY;
import static com.google.android.gms.gcm.Task.NETWORK_STATE_CONNECTED;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.JOB_TYPE;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.PAYLOAD;
import static com.zeyad.genericusecase.data.services.GenericNetworkQueueIntentService.TRIAL_COUNT;

/**
 * @author Zeyad on 6/05/16.
 */
public class Post {
    public static final String TAG = Post.class.getSimpleName();
    private final Context mContext;
    private final PostRequest mPostRequest;
    private final RestApi mRestApi;
    private int mTrailCount;
    private boolean mHasLollipop;
    private boolean mGooglePlayServicesAvailable;
    private boolean mNetworkAvailable;
    private GcmNetworkManager mGcmNetworkManager;

    public Post(@NonNull Intent intent, Context context) {
        mRestApi = new RestApiImpl();
        mContext = context;
        mTrailCount = intent.getIntExtra(TRIAL_COUNT, 0);
        mPostRequest = new Gson().fromJson(intent.getStringExtra(PAYLOAD), PostRequest.class);
        mHasLollipop = Utils.hasLollipop();
        mGooglePlayServicesAvailable = Utils.isGooglePlayServicesAvailable(mContext);
        mNetworkAvailable = Utils.isNetworkAvailable(mContext);
        mGcmNetworkManager = GcmNetworkManager.getInstance(mContext);
    }

    Post(Context context, PostRequest postRequest, RestApi restApi, int trailCount, boolean hasLollipop,
         boolean googlePlayServicesAvailable, boolean networkAvailable, GcmNetworkManager gcmNetworkManager) {
        mContext = context;
        mPostRequest = postRequest;
        mRestApi = restApi;
        mTrailCount = trailCount;
        mHasLollipop = hasLollipop;
        mGooglePlayServicesAvailable = googlePlayServicesAvailable;
        mNetworkAvailable = networkAvailable;
        mGcmNetworkManager = gcmNetworkManager;

    }

    @Nullable
    public Subscription execute() {
        if (mNetworkAvailable) {
            String bundle = "";
            boolean isObject = false;
            if (mPostRequest.getJsonArray() == null) {
                JSONObject jsonObject = mPostRequest.getObjectBundle();
                if (jsonObject != null) {
                    bundle = jsonObject.toString();
                    isObject = true;
                }
            } else bundle = mPostRequest.getJsonArray().toString();
            switch (mPostRequest.getMethod()) {
                case PostRequest.POST:
                    if (isObject)
                        return mRestApi.dynamicPostObject(mPostRequest.getUrl(), RequestBody
                                .create(MediaType.parse(Constants.APPLICATION_JSON), bundle))
                                .subscribe(handleError);
                    else
                        return mRestApi.dynamicPostList(mPostRequest.getUrl(), RequestBody.create(MediaType
                                .parse(Constants.APPLICATION_JSON), mPostRequest.getJsonArray().toString()))
                                .subscribe(handleError);
                case PostRequest.PUT:
                    if (isObject)
                        return mRestApi.dynamicPutObject(mPostRequest.getUrl(), RequestBody
                                .create(MediaType.parse(Constants.APPLICATION_JSON), bundle))
                                .subscribe(handleError);
                    else
                        return mRestApi.dynamicPutList(mPostRequest.getUrl(), RequestBody.create(MediaType
                                .parse(Constants.APPLICATION_JSON), mPostRequest.getJsonArray().toString()))
                                .subscribe(handleError);
                case PostRequest.DELETE:
                    if (isObject)
                        return mRestApi.dynamicDeleteObject(mPostRequest.getUrl(), RequestBody
                                .create(MediaType.parse(Constants.APPLICATION_JSON), bundle))
                                .subscribe(handleError);
                    else
                        return mRestApi.dynamicDeleteList(mPostRequest.getUrl(), RequestBody.create(MediaType
                                .parse(Constants.APPLICATION_JSON), mPostRequest.getJsonArray().toString()))
                                .subscribe(handleError);
            }
        } else
            reQueue();
        return null;
    }

    @NonNull
    private Action1<Object> handleError = object -> {
        if (object instanceof IOException) {
            reQueue();
            ((Exception) object).printStackTrace();
        }
    };

    private void reQueue() {
        mTrailCount++;
        if (mTrailCount < 3) { // inject value at init!
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Class.class, new ClassTypeAdapter())
                    .create();
            if (mGooglePlayServicesAvailable) {
                Bundle extras = new Bundle();
                extras.putString(JOB_TYPE, GenericNetworkQueueIntentService.POST);
                extras.putString(PAYLOAD, gson.toJson(mPostRequest));
                extras.putInt(TRIAL_COUNT, mTrailCount);
                mGcmNetworkManager.schedule(new OneoffTask.Builder()
                        .setService(GenericGCMService.class)
                        .setRequiredNetwork(NETWORK_STATE_CONNECTED)
                        .setRequiresCharging(false)
                        .setUpdateCurrent(false)
                        .setPersisted(true)
                        .setExtras(extras)
                        .setTag(Constants.POST_TAG)
                        .setExecutionWindow(0, 30)
                        .build());
                Log.d(TAG, "Request reQueued through GcmNetworkManager: " + true);
            } else {
                if (mHasLollipop) {
                    PersistableBundle persistableBundle = new PersistableBundle();
                    persistableBundle.putString(JOB_TYPE, GenericNetworkQueueIntentService.POST);
                    persistableBundle.putString(PAYLOAD, gson.toJson(mPostRequest));
                    persistableBundle.putInt(TRIAL_COUNT, mTrailCount);
                    boolean isScheduled = Utils.scheduleJob(mContext, new JobInfo.Builder(1,
                            new ComponentName(mContext, GenericJobService.class))
                            .setRequiredNetworkType(NETWORK_TYPE_ANY)
                            .setRequiresCharging(false)
                            .setPersisted(true)
                            .setExtras(persistableBundle)
                            .build());
                    Log.d(TAG, "Request reQueued through JobScheduler: " + isScheduled);
                }
            }
        }
    }

    int getTrailCount() {
        return mTrailCount;
    }
}
