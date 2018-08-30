package com.zeyad.usecases.services

import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.zeyad.usecases.Config
import io.reactivex.disposables.CompositeDisposable

class GenericJobService : JobService() {
    private val genericJobServiceLogic = GenericJobServiceLogic()

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service created")
    }

    override fun onDestroy() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartJob(params: JobParameters): Boolean {
        disposable.add(genericJobServiceLogic.startJob(params.extras!!.getBundle(PAYLOAD)!!,
                Config.cloudStore!!, "Job Started")
                .subscribe({ }, { t: Throwable -> t.printStackTrace() }))
        return true // Answers the question: "Is there still work going on?"
    }

    override fun onStopJob(params: JobParameters): Boolean {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
        Log.i(TAG, "on stop job: " + params.tag)
        return true // Answers the question: "Should this job be retried?"
    }

    companion object {

        val DOWNLOAD_FILE = "DOWNLOAD_FILE"
        val UPLOAD_FILE = "UPLOAD_FILE"
        val JOB_TYPE = "JOB_TYPE"
        val POST = "POST"
        val PAYLOAD = "payload"
        val TRIAL_COUNT = "trialCount"
        val TAG = GenericJobService::class.java.simpleName
        private val disposable = CompositeDisposable()
    }
}
