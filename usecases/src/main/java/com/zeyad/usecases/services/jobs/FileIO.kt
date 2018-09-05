package com.zeyad.usecases.services.jobs

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.zeyad.usecases.queueFileIOCore
import com.zeyad.usecases.requests.FileIORequest
import com.zeyad.usecases.stores.CloudStore
import io.reactivex.Completable

/**
 * @author Zeyad on 6/05/16.
 */
class FileIO(private val mTrailCount: Int,
             private val mFileIORequest: FileIORequest,
             context: Context,
             private val mIsDownload: Boolean,
             private val mCloudStore: CloudStore) {
    private val mDispatcher: FirebaseJobDispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun execute(): Completable {
        val file = mFileIORequest.file
        return if (mIsDownload)
            Completable.fromObservable(mCloudStore
                    .dynamicDownloadFile(mFileIORequest.url, file!!, mFileIORequest.onWifi,
                            mFileIORequest.whileCharging, mFileIORequest.queuable)
                    .doOnSubscribe { Log.d(TAG, "Downloading " + file.name) }
                    .doOnError { t: Throwable -> this.onError(t) }
                    .toObservable())
        else
            Completable.fromObservable(mCloudStore.dynamicUploadFile(mFileIORequest.url,
                    mFileIORequest.keyFileMap!!, mFileIORequest.parameters,
                    mFileIORequest.onWifi, mFileIORequest.whileCharging,
                    mFileIORequest.queuable, mFileIORequest.getTypedResponseClass<Any>())
                    .doOnSubscribe { Log.d(TAG, "Uploading " + file!!.name) }
                    .doOnError { throwable -> onError(throwable) }
                    .toObservable())
    }

    private fun onError(throwable: Throwable) {
        queueIOFile()
        Log.e(TAG, ON_ERROR, throwable)
    }

    private fun queueIOFile() {
        if (mTrailCount < 3) {
            queueFileIOCore(mDispatcher, mIsDownload, mFileIORequest, mTrailCount + 1)
        }
    }

    companion object {
        private val TAG = FileIO::class.java.simpleName
        private const val ON_ERROR = "onError"
    }
}
