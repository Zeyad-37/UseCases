package com.zeyad.usecases.data.services.jobs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.zeyad.usecases.data.network.ApiConnection;
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
    private final ApiConnection mRestApi;
    private final Utils mUtils;
    @NonNull
    private Subscriber<Object> handleError = new Subscriber<Object>() {
        @Override
        public void onCompleted() {
            Log.d(TAG, "Completed");
        }

        @Override
        public void onError(Throwable e) {
            queuePost();
            e.printStackTrace();
        }

        @Override
        public void onNext(Object o) {
            Log.d(TAG, "Succeeded");
        }
    };

    public Post(Context context, PostRequest postRequest, ApiConnection restApi, int trailCount, Utils utils) {
        mPostRequest = postRequest;
        mRestApi = restApi;
        mTrailCount = trailCount;
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        mUtils = utils;
    }

    public Subscription execute() {
        String bundle = "";
        boolean isObject = false;
        if (mPostRequest.getArrayBundle().length() == 0) {
            JSONObject jsonObject = mPostRequest.getObjectBundle();
            if (jsonObject != null) {
                bundle = jsonObject.toString();
                isObject = true;
            }
        } else bundle = mPostRequest.getArrayBundle().toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse(APPLICATION_JSON), bundle);
        RequestBody listRequestBody = RequestBody.create(MediaType
                .parse(APPLICATION_JSON), mPostRequest.getArrayBundle().toString());
        switch (mPostRequest.getMethod()) {
            case PostRequest.PATCH:
                return mRestApi.dynamicPatch(mPostRequest.getUrl(), requestBody)
                        .doOnSubscribe(() -> Log.d(TAG, "Patching " + mPostRequest.getDataClass()
                                .getSimpleName()))
                        .subscribe(handleError);
            case PostRequest.POST:
                if (isObject)
                    return mRestApi.dynamicPost(mPostRequest.getUrl(), requestBody)
                            .doOnSubscribe(() -> Log.d(TAG, "Posting " + mPostRequest.getDataClass()
                                    .getSimpleName()))
                            .subscribe(handleError);
                else
                    return mRestApi.dynamicPost(mPostRequest.getUrl(), listRequestBody)
                            .doOnSubscribe(() -> Log.d(TAG, "Posting List of " + mPostRequest
                                    .getDataClass().getSimpleName()))
                            .subscribe(handleError);
            case PostRequest.PUT:
                if (isObject)
                    return mRestApi.dynamicPut(mPostRequest.getUrl(), requestBody)
                            .doOnSubscribe(() -> Log.d(TAG, "Putting " + mPostRequest.getDataClass()
                                    .getSimpleName()))
                            .subscribe(handleError);
                else
                    return mRestApi.dynamicPut(mPostRequest.getUrl(), listRequestBody)
                            .doOnSubscribe(() -> Log.d(TAG, "Putting List of " + mPostRequest.getDataClass()
                                    .getSimpleName()))
                            .subscribe(handleError);
            case PostRequest.DELETE:
                if (isObject)
                    return mRestApi.dynamicDelete(mPostRequest.getUrl(), requestBody)
                            .doOnSubscribe(() -> Log.d(TAG, "Deleting " + mPostRequest.getDataClass()
                                    .getSimpleName()))
                            .subscribe(handleError);
                else
                    return mRestApi.dynamicDelete(mPostRequest.getUrl(), listRequestBody)
                            .doOnSubscribe(() -> Log.d(TAG, "Deleting List of " + mPostRequest
                                    .getDataClass().getSimpleName()))
                            .subscribe(handleError);
        }
        return Subscriptions.empty();
    }

    void queuePost() {
        mTrailCount++;
        if (mTrailCount < 3) { // inject value at initRealm!
            mUtils.queuePostCore(mDispatcher, mPostRequest);
        }
    }
}
