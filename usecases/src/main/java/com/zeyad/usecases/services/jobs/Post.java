package com.zeyad.usecases.services.jobs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.requests.PostRequest;
import com.zeyad.usecases.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Completable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.zeyad.usecases.stores.CloudStore.APPLICATION_JSON;

/**
 * @author Zeyad on 6/05/16.
 */
public class Post {
    private static final String TAG = Post.class.getSimpleName(), ON_ERROR = "onError", COMPLETED = "Completed";
    @NonNull
    private final FirebaseJobDispatcher mDispatcher;
    private final PostRequest mPostRequest;
    private final ApiConnection mRestApi;
    private final Utils mUtils;
    private final int mTrailCount;
    private boolean isObject = false;

    public Post(Context context, PostRequest postRequest, ApiConnection restApi, int trailCount, Utils utils) {
        mPostRequest = postRequest;
        mRestApi = restApi;
        mTrailCount = trailCount;
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        mUtils = utils;
    }

    public Completable execute() {
        String bundle = "";
        try {
            if (mPostRequest.getArrayBundle().length() == 0) {
                JSONObject jsonObject = mPostRequest.getObjectBundle();
                if (jsonObject != null) {
                    bundle = jsonObject.toString();
                    isObject = true;
                }
            } else {
                bundle = mPostRequest.getArrayBundle().toString();
            }
        } catch (JSONException e) {
            return Completable.error(e);
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse(APPLICATION_JSON), bundle);
        switch (mPostRequest.getMethod()) {
            case PostRequest.PATCH:
                return Completable.fromObservable(mRestApi.dynamicPatch(mPostRequest.getUrl(), requestBody)
                        .doOnSubscribe(subscription -> Log.d(TAG, "Patching " + mPostRequest.getRequestType()
                                .getSimpleName()))
                        .doOnError(this::onError)
                        .doOnComplete(() -> Log.d(TAG, COMPLETED))
                        .toObservable());
            case PostRequest.POST:
                return Completable.fromObservable(mRestApi.dynamicPost(mPostRequest.getUrl(), requestBody)
                                                          .doOnSubscribe(subscription -> Log.d(TAG,
                                                                  "Posting " + (isObject ? "List of " : "") + mPostRequest.getRequestType()
                                                                                                                          .getSimpleName()))
                                                          .doOnError(this::onError)
                                                          .doOnComplete(() -> Log.d(TAG, COMPLETED))
                                                          .toObservable());
            case PostRequest.PUT:
                return Completable.fromObservable(mRestApi.dynamicPut(mPostRequest.getUrl(), requestBody)
                                                          .doOnSubscribe(subscription -> Log.d(TAG,
                                                                  "Putting " + (isObject ? "List of " : "") + mPostRequest.getRequestType()
                                                                                                                          .getSimpleName()))
                                                          .doOnError(this::onError)
                                                          .doOnComplete(() -> Log.d(TAG, COMPLETED))
                                                          .toObservable());
            case PostRequest.DELETE:
                return Completable.fromObservable(mRestApi.dynamicDelete(mPostRequest.getUrl())
                                                          .doOnSubscribe(subscription -> Log.d(TAG,
                                                                  "Deleting " + (isObject ? "List of " : "") + mPostRequest.getRequestType()
                                                                                                                           .getSimpleName()))
                                                          .doOnError(this::onError)
                                                          .doOnComplete(() -> Log.d(TAG, COMPLETED))
                                                          .toObservable());
            default:
                return Completable.error(new IllegalArgumentException("Method does not exist!"));
        }
    }

    private void onError(Throwable throwable) {
        queuePost();
        Log.e(TAG, ON_ERROR, throwable);
    }

    void queuePost() {
        if (mTrailCount < 3) { // inject value at init!
            mUtils.queuePostCore(mDispatcher, mPostRequest, mTrailCount + 1);
        }
    }
}
