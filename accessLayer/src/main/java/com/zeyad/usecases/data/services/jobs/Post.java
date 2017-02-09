package com.zeyad.usecases.data.services.jobs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.gson.Gson;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.network.RestApi;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.data.utils.Utils;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

import static com.zeyad.usecases.data.repository.stores.CloudDataStore.APPLICATION_JSON;

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

    public Post(int trailCount, @NonNull String payLoad, @NonNull Context context) {
        gson = Config.getGson();
        mRestApi = RestApiImpl.getInstance();
        mContext = context;
        mTrailCount = trailCount;
        mPostRequest = gson.fromJson(payLoad, PostRequest.class);
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
    }

    Post(Context context, PostRequest postRequest, RestApi restApi, int trailCount) {
        gson = Config.getGson();
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
                case PostRequest.PATCH:
                    return mRestApi.dynamicPatch(mPostRequest.getUrl(), RequestBody
                            .create(MediaType.parse(APPLICATION_JSON), bundle))
                            .doOnSubscribe(() -> Log.d(TAG, "Posting " + mPostRequest.getDataClass()
                                    .getSimpleName()))
                            .subscribe(handleError);
                case PostRequest.POST:
                    if (isObject)
                        return mRestApi.dynamicPost(mPostRequest.getUrl(), RequestBody
                                .create(MediaType.parse(APPLICATION_JSON), bundle))
                                .doOnSubscribe(() -> Log.d(TAG, "Posting " + mPostRequest.getDataClass()
                                        .getSimpleName()))
                                .subscribe(handleError);
                    else
                        return mRestApi.dynamicPost(mPostRequest.getUrl(), RequestBody.create(MediaType
                                .parse(APPLICATION_JSON), mPostRequest.getArrayBundle().toString()))
                                .doOnSubscribe(() -> Log.d(TAG, "Posting List of " + mPostRequest
                                        .getDataClass().getSimpleName()))
                                .subscribe(handleError);
                case PostRequest.PUT:
                    if (isObject)
                        return mRestApi.dynamicPut(mPostRequest.getUrl(), RequestBody
                                .create(MediaType.parse(APPLICATION_JSON), bundle))
                                .doOnSubscribe(() -> Log.d(TAG, "Puting " + mPostRequest.getDataClass()
                                        .getSimpleName()))
                                .subscribe(handleError);
                    else
                        return mRestApi.dynamicPut(mPostRequest.getUrl(), RequestBody.create(MediaType
                                .parse(APPLICATION_JSON), mPostRequest.getArrayBundle().toString()))
                                .doOnSubscribe(() -> Log.d(TAG, "Puting " + mPostRequest.getDataClass()
                                        .getSimpleName()))
                                .subscribe(handleError);
                case PostRequest.DELETE:
                    if (isObject)
                        return mRestApi.dynamicDelete(mPostRequest.getUrl(), RequestBody
                                .create(MediaType.parse(APPLICATION_JSON), bundle))
                                .doOnSubscribe(() -> Log.d(TAG, "Deleting " + mPostRequest.getDataClass()
                                        .getSimpleName()))
                                .subscribe(handleError);
                    else
                        return mRestApi.dynamicDelete(mPostRequest.getUrl(), RequestBody.create(MediaType
                                .parse(APPLICATION_JSON), mPostRequest.getArrayBundle().toString()))
                                .doOnSubscribe(() -> Log.d(TAG, "Deleting List of " + mPostRequest
                                        .getPresentationClass().getSimpleName()))
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
