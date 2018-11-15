package com.zeyad.usecases.stores

import android.util.Log
import com.google.gson.Gson
import com.zeyad.usecases.Config
import com.zeyad.usecases.Mockable
import io.reactivex.Observable
import io.reactivex.Single
import org.json.JSONArray
import org.json.JSONObject
import st.lowlevel.storo.Storo
import java.util.*

/**
 * @author by ZIaDo on 6/5/17.
 */
@Mockable
class MemoryStore(private val gson: Gson,
                  private val mapOfIds: MutableMap<Class<*>, MutableSet<String>> = mutableMapOf()) {

    fun <M> getItem(itemId: String, dataClass: Class<*>): Single<M> {
        return Single.defer<M> {
            val key = dataClass.simpleName + itemId
            if (isValid(key)) {
                Storo.get<M>(key, dataClass).async().firstElement().toSingle()
            } else {
                Single.error<M>(IllegalAccessException("Cache Miss!"))
            }
        }
    }

    fun <M> getAllItems(dataClass: Class<M>): Single<List<M>> {
        return Single.defer {
            val missed = BooleanArray(1)
            val stringSet = mapOfIds[dataClass]
                    ?: return@defer Single.error<List<M>>(IllegalAccessException("Cache Miss!"))
            val result = Observable.fromIterable(stringSet)
                    .filter { key ->
                        if (isValid(key)) {
                            true
                        } else {
                            missed[0] = true
                            false
                        }
                    }
                    .filter { !missed[0] }
                    .map<M> { Storo.get(it, dataClass).execute() }
                    .toList(if (missed[0]) 0 else stringSet.size)
                    .blockingGet()
                    .toList<M>()
            when {
                missed[0] -> Single.error<List<M>>(IllegalAccessException("Cache Miss!"))
                else -> Single.just<List<M>>(result)
            }
        }
    }

    fun cacheObject(idColumnName: String, jsonObject: JSONObject, dataClass: Class<*>) {
        val className = dataClass.simpleName
        val key = className + jsonObject.optString(idColumnName)
        Storo.put(key, gson.fromJson(jsonObject.toString(), dataClass))
                .setExpiry(Config.cacheDuration, Config.cacheTimeUnit)
                .execute()
        addKey(dataClass, key)
        Log.d(TAG, "$className cached!, id = $key")
    }

    fun cacheList(idColumnName: String?, jsonArray: JSONArray, dataClass: Class<*>) {
        if (idColumnName == null || idColumnName.isEmpty()) {
            Log.e(TAG, "cacheList",
                    IllegalArgumentException("idColumnName is not available to cache list"))
            return
        }
        val size = jsonArray.length()
        for (i in 0 until size) {
            cacheObject(idColumnName, jsonArray.optJSONObject(i), dataClass)
        }
    }

    fun deleteListById(ids: List<String>, dataClass: Class<*>) {
        if (ids.isEmpty()) {
            return
        }
        val className = dataClass.simpleName
        Observable.fromIterable(ids)
                .map { id -> className + id }
                .filter { key ->
                    if (isValid(key)) {
                        true
                    } else {
                        removeKey(dataClass, key)
                        false
                    }
                }
                .doOnEach { stringNotification ->
                    val key = stringNotification.value!!
                    removeKey(dataClass, key)
                    Log.d(TAG, String.format("%s %s deleted!, id = %s", className,
                            if (Storo.delete(key)) "" else "not ", key))
                }
                .blockingSubscribe()
    }

    private fun addKey(dataType: Class<*>, key: String) {
        if (!mapOfIds.containsKey(dataType)) {
            mapOfIds[dataType] = HashSet(setOf(key))
        } else {
            mapOfIds[dataType]?.add(key)
        }
    }

    private fun removeKey(dataType: Class<*>, key: String) {
        if (mapOfIds.containsKey(dataType)) {
            mapOfIds[dataType]?.remove(key)
        }
    }

    private fun isValid(key: String): Boolean {
        return Storo.contains(key) && !Storo.hasExpired(key).execute()
    }

    companion object {
        private const val TAG = "MemoryStore"
    }
}
