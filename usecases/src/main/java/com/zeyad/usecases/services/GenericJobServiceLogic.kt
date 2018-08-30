package com.zeyad.usecases.services

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import com.zeyad.usecases.Config
import com.zeyad.usecases.services.jobs.FileIO
import com.zeyad.usecases.services.jobs.Post
import com.zeyad.usecases.stores.CloudStore
import io.reactivex.Completable

class GenericJobServiceLogic {

    @RequiresApi(Build.VERSION_CODES.N)
    fun startJob(extras: Bundle, cloudStore: CloudStore, log: String): Completable {
        if (extras.containsKey(GenericJobService.PAYLOAD)) {
            val trailCount = extras.getInt(GenericJobService.TRIAL_COUNT)
            when (extras.getString(GenericJobService.JOB_TYPE, "")) {
                GenericJobService.POST -> {
                    Log.d(GenericJobServiceLogic::class.java.simpleName,
                            String.format(log, GenericJobService.POST))
                    return Post(Config.context, extras.getParcelable(GenericJobService.PAYLOAD),
                            Config.apiConnection!!, trailCount)
                            .execute()
                }
                GenericJobService.DOWNLOAD_FILE -> {
                    Log.d(GenericJobServiceLogic::class.java.simpleName,
                            String.format(log, GenericJobService.DOWNLOAD_FILE))
                    return FileIO(trailCount, extras.getParcelable(GenericJobService.PAYLOAD),
                            Config.context, true, cloudStore)
                            .execute()
                }
                GenericJobService.UPLOAD_FILE -> {
                    Log.d(GenericJobServiceLogic::class.java.simpleName,
                            String.format(log, GenericJobService.UPLOAD_FILE))
                    return FileIO(trailCount, extras.getParcelable(GenericJobService.PAYLOAD),
                            Config.context, false, cloudStore)
                            .execute()
                }
                else -> return Completable.complete()
            }
        }
        return Completable.complete()
    }
}

