package com.zeyad.usecases.services.jobs

import android.content.Context
import android.util.Log
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.zeyad.usecases.network.ApiConnection
import com.zeyad.usecases.queuePostCore
import com.zeyad.usecases.requests.PostRequest
import com.zeyad.usecases.stores.CloudStore.Companion.APPLICATION_JSON
import io.reactivex.Completable
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 * @author Zeyad on 6/05/16.
 */
class Post(context: Context,
           private val mPostRequest: PostRequest,
           private val mRestApi: ApiConnection,
           private val mTrailCount: Int) {
    private val mDispatcher: FirebaseJobDispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))
    private var isObject = false

    fun execute(): Completable {
        val bundle: String
        if (mPostRequest.getObjectBundle().length() > 0) {
            val jsonObject = mPostRequest.getObjectBundle()
            bundle = jsonObject.toString()
            isObject = true
        } else {
            bundle = mPostRequest.getArrayBundle().toString()
        }
        val requestBody = RequestBody.create(MediaType.parse(APPLICATION_JSON), bundle)
        when (mPostRequest.method) {
            PostRequest.PATCH -> return Completable.fromObservable(mRestApi.dynamicPatch<Any>(mPostRequest.getCorrectUrl(), requestBody)
                    .doOnSubscribe {
                        Log.d(TAG, "Patching " + mPostRequest.requestType.simpleName)
                    }
                    .doOnError { t: Throwable -> this.onError(t) }
                    .doOnComplete { Log.d(TAG, COMPLETED) }
                    .toObservable())
            PostRequest.POST -> return Completable.fromObservable(mRestApi.dynamicPost<Any>(mPostRequest.getCorrectUrl(), requestBody)
                    .doOnSubscribe {
                        Log.d(TAG, "Posting " + (if (isObject) "List of " else "") + mPostRequest.requestType.simpleName)
                    }
                    .doOnError { t: Throwable -> this.onError(t) }
                    .doOnComplete { Log.d(TAG, COMPLETED) }
                    .toObservable())
            PostRequest.PUT -> return Completable.fromObservable(mRestApi.dynamicPut<Any>(mPostRequest.getCorrectUrl(), requestBody)
                    .doOnSubscribe {
                        Log.d(TAG, "Putting " + (if (isObject) "List of " else "") + mPostRequest.requestType.simpleName)
                    }
                    .doOnError { t: Throwable -> this.onError(t) }
                    .doOnComplete { Log.d(TAG, COMPLETED) }
                    .toObservable())
            PostRequest.DELETE -> return Completable.fromObservable(mRestApi.dynamicDelete<Any>(mPostRequest.getCorrectUrl())
                    .doOnSubscribe {
                        Log.d(TAG, "Deleting " + (if (isObject) "List of " else "") + mPostRequest.requestType.simpleName)
                    }
                    .doOnError { t: Throwable -> this.onError(t) }
                    .doOnComplete { Log.d(TAG, COMPLETED) }
                    .toObservable())
            else -> return Completable.error(IllegalArgumentException("Method does not exist!"))
        }
    }

    private fun onError(throwable: Throwable) {
        queuePost()
        Log.e(TAG, ON_ERROR, throwable)
    }

    private fun queuePost() {
        if (mTrailCount < 3) { // inject value at init!
            queuePostCore(mDispatcher, mPostRequest, mTrailCount + 1)
        }
    }

    companion object {
        private val TAG = Post::class.java.simpleName
        private const val ON_ERROR = "onError"
        private const val COMPLETED = "Completed"
    }
}
