package com.zeyad.usecases.stores

import com.zeyad.usecases.Config.gson
import com.zeyad.usecases.Mockable
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

@Mockable
class DiskStore(private val mDataBaseManager: DataBaseManager,
                private val mMemoryStore: MemoryStore?) : DataStore {

    private inline fun onNext(shouldCache: Boolean, block: () -> Unit) {
        if (withCache(shouldCache)) {
            block.invoke()
        }
    }

    override fun <M> dynamicGetList(url: String, idColumnName: String, requestType: Class<M>,
                                    persist: Boolean, shouldCache: Boolean): Flowable<List<M>> {
        return mDataBaseManager.getAll(requestType)
                .doOnNext { ms: List<M> ->
                    onNext(shouldCache) {
                        mMemoryStore?.cacheList(idColumnName, JSONArray(gson.toJson(ms)), requestType)
                    }
                }
    }

    override fun <M> dynamicGetObject(url: String, idColumnName: String, itemId: Any,
                                      requestType: Class<M>, persist: Boolean, shouldCache: Boolean): Flowable<M> {
        return mDataBaseManager.getById(idColumnName, itemId, requestType)
                .doOnNext { m: M ->
                    onNext(shouldCache) {
                        mMemoryStore?.cacheObject(idColumnName, JSONObject(gson.toJson(m)), requestType)
                    }
                }
    }

    /**
     * Patch a JSONObject which returns an [Flowable] that will emit a Object.
     */
    override fun <M> dynamicPatchObject(url: String, idColumnName: String,
                                        jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                                        persist: Boolean, cache: Boolean): Single<M> {
        return mDataBaseManager.put(jsonObject, requestType)
                .doOnSuccess {
                    onNext(cache) {
                        mMemoryStore?.cacheObject(idColumnName, JSONObject(gson.toJson(jsonObject)), requestType)
                    }
                }
                .map { it as M }
    }

    /**
     * Post a JSONObject which returns an [Single] that will emit a Object.
     */
    override fun <M> dynamicPostObject(url: String, idColumnName: String, jsonObject: JSONObject,
                                       requestType: Class<*>, responseType: Class<M>, persist: Boolean,
                                       cache: Boolean): Single<M> {
        return mDataBaseManager.put(jsonObject, requestType)
                .doOnSuccess {
                    onNext(cache) {
                        mMemoryStore?.cacheObject(idColumnName,
                                JSONObject(gson.toJson(jsonObject)), requestType)
                    }
                }
                .map { it as M }
    }

    /**
     * Post a HashMap<String></String>, Object> which returns an [Single] that will emit a list of
     * Object.
     */
    override fun <M> dynamicPostList(url: String, idColumnName: String,
                                     jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                                     persist: Boolean, cache: Boolean): Single<M> {
        return mDataBaseManager.putAll(jsonArray, requestType)
                .doOnSuccess {
                    onNext(cache) {
                        mMemoryStore?.cacheList(idColumnName, jsonArray, requestType)
                    }
                }
                .map { it as M }
    }

    /**
     * Put a HashMap<String></String>, Object> disk with a RealmQuery which returns an [Single] that
     * will emit a Object.
     */
    override fun <M> dynamicPutObject(url: String, idColumnName: String,
                                      jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                                      persist: Boolean, cache: Boolean): Single<M> {
        return mDataBaseManager.put(jsonObject, requestType)
                .doOnSuccess {
                    onNext(cache) {
                        mMemoryStore?.cacheObject(idColumnName,
                                JSONObject(gson.toJson(jsonObject)), requestType)
                    }
                }
                .map { it as M }
    }

    /**
     * Put a HashMap<String></String>, Object> disk with a RealmQuery which returns an [Single] that
     * will emit a list of Object.
     */
    override fun <M> dynamicPutList(url: String, idColumnName: String,
                                    jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                                    persist: Boolean, cache: Boolean): Single<M> {
        return mDataBaseManager.putAll(jsonArray, requestType)
                .doOnSuccess {
                    onNext(cache) {
                        mMemoryStore?.cacheList(idColumnName, jsonArray, requestType)
                    }
                }
                .map { it as M }
    }

    /**
     * Delete a HashMap<String></String>, Object> from cloud which returns an [Single] that will emit
     * a Object.
     */
    override fun <T, M> dynamicDeleteCollection(url: String,
                                                idColumnName: String,
                                                itemIdType: Class<*>,
                                                jsonArray: JSONArray,
                                                requestType: Class<T>,
                                                responseType: Class<M>,
                                                persist: Boolean,
                                                cache: Boolean): Single<M> {
        val list = mutableListOf<T>()
        for (i in 0..(jsonArray.length() - 1)) {
            list.add(gson.fromJson(jsonArray.getJSONObject(i).toString(), requestType))
        }
        return mDataBaseManager.evictCollection(list, requestType)
                .doOnSuccess {
                    onNext(cache) {
                        mMemoryStore?.deleteListById(convertToListOfId(jsonArray, String::class.java),
                                requestType)
                    }
                }
                .map { it as M }
    }

    override fun <M> dynamicDeleteCollectionById(url: String,
                                                 idColumnName: String,
                                                 itemIdType: Class<*>,
                                                 jsonArray: JSONArray,
                                                 requestType: Class<*>,
                                                 responseType: Class<M>,
                                                 persist: Boolean,
                                                 cache: Boolean): Single<M> {
        val stringIds = convertToStringListOfId(jsonArray)
        return mDataBaseManager.evictCollectionById(stringIds, requestType, idColumnName)
                .doOnSuccess {
                    onNext(cache) {
                        mMemoryStore?.deleteListById(stringIds.map { toString() }, requestType)
                    }
                }
                .map { it as M }
    }

    override fun <M> queryDisk(query: String, clazz: Class<M>): Flowable<List<M>> {
        return mDataBaseManager.getQuery(query, clazz)
    }

    override fun dynamicDeleteAll(requestType: Class<*>): Single<Boolean> {
        return mDataBaseManager.evictAll(requestType).map { it }
    }

    override fun dynamicDownloadFile(url: String, file: File): Single<File> {
        return Single.error<File>(IllegalStateException(IO_DB_ERROR))
    }

    override fun <M> dynamicUploadFile(url: String, keyFileMap: HashMap<String, File>,
                                       parameters: HashMap<String, Any>, responseType: Class<M>): Single<M> {
        return Single.error(IllegalStateException(IO_DB_ERROR))
    }

    companion object {
        private const val IO_DB_ERROR = "Can not file IO to local DB"
    }
}
