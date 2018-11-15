package com.zeyad.usecases

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import java.util.*

fun withDisk(shouldPersist: Boolean): Boolean {
    return Config.withSQLite && shouldPersist
}

fun withCache(shouldCache: Boolean): Boolean {
    return Config.withCache && shouldCache
}

fun <T> convertToListOfId(jsonArray: JSONArray, idType: Class<T>): List<T> {
    var idList: MutableList<T> = ArrayList()
    if (jsonArray.length() > 0) {
        val length = jsonArray.length()
        idList = ArrayList(length)
        for (i in 0 until length) {
            try {
                idList.add(idType.cast(jsonArray.get(i))!!)
            } catch (e: JSONException) {
                Log.e("Utils", "convertToListOfId", e)
            }
        }
    }
    return idList
}

fun convertToStringListOfId(jsonArray: JSONArray?): List<Long> {
    var idList: MutableList<Long> = ArrayList()
    if (jsonArray != null && jsonArray.length() > 0) {
        idList = ArrayList(jsonArray.length())
        val length = jsonArray.length()
        for (i in 0 until length) {
            try {
                idList.add(jsonArray.get(i).toString().toLong())
            } catch (e: JSONException) {
                Log.e("Utils", "convertToListOfId", e)
            }

        }
    }
    return idList
}

fun isNetworkNotAvailable(context: Context): Boolean = !isNetworkAvailable(context)

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