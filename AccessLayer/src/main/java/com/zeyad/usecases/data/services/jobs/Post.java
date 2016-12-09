package com.zeyad.usecases.data.services.jobs;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zeyad.usecases.data.network.RestApi;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.data.utils.Utils;

import org.json.JSONObject;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

import static com.zeyad.usecases.data.repository.stores.CloudDataStore.APPLICATION_JSON;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.PAYLOAD;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.TRIAL_COUNT;

/**
 * @author Zeyad on 6/05/16.
 */
public class Post {
    private static final String TAG = Post.class.getSimpleName();
    private static int mTrailCount;
    private final FirebaseJobDispatcher mDispatcher;
    private final PostRequest mPostRequest;
    private final Context mContext;
    private final RestApi mRestApi;
    private final Gson gson;
    @NonNull
    private Subscriber<Object> handleError = new Subscriber<Object>() {
        @Override
        public void onCompleted() {
            Log.d(TAG, "Completed");
        }

        @Override
        public void onError(Throwable e) {
            reQueue();
            e.printStackTrace();
        }

        @Override
        public void onNext(Object o) {
            Log.d(TAG, "Succeeded");
        }
    };

    public Post(@NonNull Intent intent, @NonNull Context context) {
        gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class)
                        && f.getDeclaredClass().equals(RealmModel.class)
                        && f.getDeclaringClass().equals(RealmList.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        mRestApi = new RestApiImpl();
        mContext = context;
        mTrailCount = intent.getIntExtra(TRIAL_COUNT, 0);
        mPostRequest = gson.fromJson(intent.getStringExtra(PAYLOAD), PostRequest.class);
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
    }

    Post(Context context, PostRequest postRequest, RestApi restApi, int trailCount) {
        gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class)
                        && f.getDeclaredClass().equals(RealmModel.class)
                        && f.getDeclaringClass().equals(RealmList.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        mContext = context;
        mPostRequest = postRequest;
        mRestApi = restApi;
        mTrailCount = trailCount;
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
    }

    public Subscription execute() {
        if (Utils.isNetworkAvailable(mContext)) {
            String bundle = "";
            boolean isObject = false;
            if (mPostRequest.getArrayBundle().length() == 0) {
                JSONObject jsonObject = mPostRequest.getObjectBundle();
                if (jsonObject != null) {
                    bundle = jsonObject.toString();
                    isObject = true;
                }
            } else bundle = mPostRequest.getArrayBundle().toString();
            switch (mPostRequest.getMethod()) {
                case PostRequest.POST:
                    if (isObject)
                        return mRestApi.dynamicPostObject(mPostRequest.getUrl(), RequestBody
                                .create(MediaType.parse(APPLICATION_JSON), bundle))
                                .subscribe(handleError);
                    else
                        return mRestApi.dynamicPostList(mPostRequest.getUrl(), RequestBody.create(MediaType
                                .parse(APPLICATION_JSON), mPostRequest.getArrayBundle().toString()))
                                .subscribe(handleError);
                case PostRequest.PUT:
                    if (isObject)
                        return mRestApi.dynamicPutObject(mPostRequest.getUrl(), RequestBody
                                .create(MediaType.parse(APPLICATION_JSON), bundle))
                                .subscribe(handleError);
                    else
                        return mRestApi.dynamicPutList(mPostRequest.getUrl(), RequestBody.create(MediaType
                                .parse(APPLICATION_JSON), mPostRequest.getArrayBundle().toString()))
                                .subscribe(handleError);
                case PostRequest.DELETE:
                    if (isObject)
                        return mRestApi.dynamicDeleteObject(mPostRequest.getUrl(), RequestBody
                                .create(MediaType.parse(APPLICATION_JSON), bundle))
                                .subscribe(handleError);
                    else
                        return mRestApi.dynamicDeleteList(mPostRequest.getUrl(), RequestBody.create(MediaType
                                .parse(APPLICATION_JSON), mPostRequest.getArrayBundle().toString()))
                                .subscribe(handleError);
            }
        } else
            reQueue();
        return Subscriptions.empty();
    }

    private void reQueue() {
        mTrailCount++;
        if (mTrailCount < 3) { // inject value at initRealm!
            Utils.queuePostCore(mDispatcher, mPostRequest, gson);
        }
    }

    int getTrailCount() {
        return mTrailCount;
    }
}
