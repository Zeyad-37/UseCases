package com.zeyad.usecases.stores

import com.zeyad.usecases.Config.gson
import com.zeyad.usecases.convertToListOfId
import com.zeyad.usecases.convertToStringListOfId
import com.zeyad.usecases.db.DataBaseManager
import com.zeyad.usecases.withCache
import io.reactivex.Flowable
import io.reactivex.Single
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

class DiskStore(private val mDataBaseManager: DataBaseManager,
                private val mMemoryStore: MemoryStore?) : DataStore {

    override fun <M> dynamicGetList(url: String, idColumnName: String, requestType: Class<M>,
                                    persist: Boolean, shouldCache: Boolean): Flowable<List<M>> {
        return mDataBaseManager.getAll<M>(requestType)
                .doOnNext { ms: List<M> ->
                    if (withCache(shouldCache)) {
                        mMemoryStore?.cacheList(idColumnName, JSONArray(gson.toJson(ms)), requestType)
                    }
                }
    }

    override fun <M> dynamicGetObject(url: String, idColumnName: String, itemId: Any, itemIdType: Class<*>,
                                      requestType: Class<M>, persist: Boolean, shouldCache: Boolean): Flowable<M> {
        return mDataBaseManager.getById<M>(idColumnName, itemId, itemIdType, requestType)
                .doOnNext { m: M ->
                    if (withCache(shouldCache)) {
                        mMemoryStore?.cacheObject(idColumnName, JSONObject(gson.toJson(m)), requestType)
                    }
                }
    }

    /**
     * Patch a JSONObject which returns an [Flowable] that will emit a Object.
     */
    override fun <M> dynamicPatchObject(url: String, idColumnName: String, itemIdType: Class<*>,
                                        jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                                        persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return mDataBaseManager.put(jsonObject, idColumnName, itemIdType, requestType)
                .doOnSuccess {
                    if (withCache(cache)) {
                        mMemoryStore?.cacheObject(idColumnName, JSONObject(gson.toJson(jsonObject)), requestType)
                    }
                }
                .map { it as M }
                .toFlowable()
    }

    /**
     * Post a JSONObject which returns an [Flowable] that will emit a Object.
     */
    override fun <M> dynamicPostObject(url: String, idColumnName: String, itemIdType: Class<*>,
                                       jsonObject: JSONObject, requestType: Class<*>,
                                       responseType: Class<M>, persist: Boolean, cache: Boolean,
                                       queuable: Boolean): Flowable<M> {
        return mDataBaseManager.put(jsonObject, idColumnName, itemIdType, requestType)
                .doOnSuccess {
                    if (withCache(cache)) {
                        mMemoryStore?.cacheObject(idColumnName,
                                JSONObject(gson.toJson(jsonObject)), requestType)
                    }
                }
                .map { it as M }
                .toFlowable()
    }

    /**
     * Post a HashMap<String></String>, Object> which returns an [Flowable] that will emit a list of
     * Object.
     */
    override fun <M> dynamicPostList(url: String, idColumnName: String, itemIdType: Class<*>,
                                     jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                                     persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return mDataBaseManager.putAll(jsonArray, idColumnName, itemIdType, requestType)
                .doOnSuccess {
                    if (withCache(cache)) {
                        mMemoryStore?.cacheList(idColumnName, jsonArray, requestType)
                    }
                }
                .map { it as M }
                .toFlowable()
    }

    /**
     * Put a HashMap<String></String>, Object> disk with a RealmQuery which returns an [Flowable] that
     * will emit a Object.
     */
    override fun <M> dynamicPutObject(url: String, idColumnName: String, itemIdType: Class<*>,
                                      jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                                      persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return mDataBaseManager.put(jsonObject, idColumnName, itemIdType, requestType)
                .doOnSuccess {
                    if (withCache(cache)) {
                        mMemoryStore?.cacheObject(idColumnName,
                                JSONObject(gson.toJson(jsonObject)), requestType)
                    }
                }
                .map { it as M }
                .toFlowable()
    }

    /**
     * Put a HashMap<String></String>, Object> disk with a RealmQuery which returns an [Flowable] that
     * will emit a list of Object.
     */
    override fun <M> dynamicPutList(url: String, idColumnName: String, itemIdType: Class<*>,
                                    jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                                    persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return mDataBaseManager.putAll(jsonArray, idColumnName, itemIdType, requestType)
                .doOnSuccess {
                    if (withCache(cache)) {
                        mMemoryStore?.cacheList(idColumnName, jsonArray, requestType)
                    }
                }
                .map { it as M }
                .toFlowable()
    }

    /**
     * Delete a HashMap<String></String>, Object> from cloud which returns an [Flowable] that will emit
     * a Object.
     */
    override fun <M> dynamicDeleteCollection(url: String, idColumnName: String, itemIdType: Class<*>,
                                             jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                                             persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        val stringIds = convertToStringListOfId(jsonArray)
        return mDataBaseManager.evictCollection(idColumnName, convertToListOfId(jsonArray, itemIdType),
                itemIdType, requestType)
                .doOnSuccess {
                    if (withCache(cache)) {
                        mMemoryStore?.deleteList(stringIds, requestType)
                    }
                }
                .map { it as M }
                .toFlowable()
    }


//    fun <M> queryDisk(queryFactory: RealmQueryProvider): Flowable<List<M>> {
//        return mDataBaseManager.getQuery(queryFactory)
//    }

    override fun dynamicDeleteAll(requestType: Class<*>): Single<Boolean> {
        return mDataBaseManager.evictAll(requestType)
    }

    override fun dynamicDownloadFile(url: String, file: File, onWifi: Boolean, whileCharging: Boolean,
                                     queuable: Boolean): Flowable<File> {
        return Flowable.error<File>(IllegalStateException(IO_DB_ERROR))
    }

    override fun <M> dynamicUploadFile(url: String, keyFileMap: HashMap<String, File>,
                                       parameters: HashMap<String, Any>?, onWifi: Boolean,
                                       whileCharging: Boolean, queuable: Boolean, responseType: Class<*>): Flowable<M> {
        return Flowable.error(IllegalStateException(IO_DB_ERROR))
    }

    companion object {
        private const val IO_DB_ERROR = "Can not file IO to local DB"
    }
}