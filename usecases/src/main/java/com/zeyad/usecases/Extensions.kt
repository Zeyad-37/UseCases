package com.zeyad.usecases

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.firebase.jobdispatcher.Constraint
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.RetryStrategy
import com.zeyad.usecases.requests.FileIORequest
import com.zeyad.usecases.requests.PostRequest
import com.zeyad.usecases.services.GenericJobService
import org.json.JSONArray
import org.json.JSONException
import java.util.*

/**
 * @author ZIaDo on 8/29/18.
 */

fun withDisk(shouldPersist: Boolean): Boolean {
    return Config.withSQLite && shouldPersist
}

fun withCache(shouldCache: Boolean): Boolean {
    return Config.withCache && shouldCache
}

fun <T> convertToListOfId(jsonArray: JSONArray?, idType: Class<T>): List<T> {
    var idList: MutableList<T> = ArrayList()
    if (jsonArray != null && jsonArray.length() > 0) {
        val length = jsonArray.length()
        idList = ArrayList(length)
        for (i in 0 until length) {
            try {
                idList.add(idType.cast(jsonArray.get(i)))
            } catch (e: JSONException) {
                Log.e("Utils", "convertToListOfId", e)
            }

        }
    }
    return idList
}

fun convertToStringListOfId(jsonArray: JSONArray?): List<String> {
    var idList: MutableList<String> = ArrayList()
    if (jsonArray != null && jsonArray.length() > 0) {
        idList = ArrayList(jsonArray.length())
        val length = jsonArray.length()
        for (i in 0 until length) {
            try {
                idList.add(jsonArray.get(i).toString())
            } catch (e: JSONException) {
                Log.e("Utils", "convertToListOfId", e)
            }

        }
    }
    return idList
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val networks = connectivityManager.allNetworks
        for (network in networks) {
            if (connectivityManager.getNetworkInfo(network).state == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
    } else {
        val info = connectivityManager.allNetworkInfo
        if (info != null) {
            for (anInfo in info) {
                if (anInfo.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        }
    }
    return false
}

fun queuePostCore(dispatcher: FirebaseJobDispatcher, postRequest: PostRequest, trailCount: Int) {
    val extras = Bundle(3)
    extras.putString(GenericJobService.JOB_TYPE, GenericJobService.POST)
    extras.putParcelable(GenericJobService.PAYLOAD, postRequest)
    extras.putInt(GenericJobService.TRIAL_COUNT, trailCount)
    queueCore(dispatcher, extras, postRequest.method, postRequest.onWifi, postRequest.whileCharging)
}

fun queueFileIOCore(dispatcher: FirebaseJobDispatcher, isDownload: Boolean,
                    fileIORequest: FileIORequest, trailCount: Int) {
    val extras = Bundle(3)
    extras.putString(GenericJobService.JOB_TYPE, if (isDownload)
        GenericJobService.DOWNLOAD_FILE
    else
        GenericJobService.UPLOAD_FILE)
    extras.putParcelable(GenericJobService.PAYLOAD, fileIORequest)
    extras.putInt(GenericJobService.TRIAL_COUNT, trailCount)
    queueCore(dispatcher, extras, (if (isDownload) "Download" else "Upload") + " file",
            fileIORequest.onWifi, fileIORequest.whileCharging)
}

private fun queueCore(dispatcher: FirebaseJobDispatcher, bundle: Bundle, message: String,
                      isOnWifi: Boolean, whileCharging: Boolean) {
    val network = if (isOnWifi) Constraint.ON_UNMETERED_NETWORK else Constraint.ON_ANY_NETWORK
    val power = if (whileCharging) Constraint.DEVICE_CHARGING else Constraint.DEVICE_IDLE
    try {
        dispatcher.mustSchedule(dispatcher.newJobBuilder()
                .setService(GenericJobService::class.java)
                .setTag(bundle.getString("JOB_TYPE") + System.currentTimeMillis().toString())
                .setRecurring(false)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                //                                          .setTrigger(Trigger.executionWindow(0, 10))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setExtras(bundle)
                .setConstraints(network, power)
                .build())
    } catch (ignored: Exception) {

    }

    Log.d("FBJD", "$message request is queued successfully!")
}