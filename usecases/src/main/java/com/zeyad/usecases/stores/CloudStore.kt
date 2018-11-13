package com.zeyad.usecases.stores

import android.util.Log
import com.zeyad.usecases.*
import com.zeyad.usecases.Config.gson
import com.zeyad.usecases.db.DataBaseManager
import com.zeyad.usecases.db.RealmQueryProvider
import com.zeyad.usecases.exceptions.NetworkConnectionException
import com.zeyad.usecases.mapper.DAOMapper
import com.zeyad.usecases.network.ApiConnection
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.realm.RealmModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.util.*

@Mockable
class CloudStore(private val mApiConnection: ApiConnection, //    private static final int COUNTER_START = 1, ATTEMPTS = 3;
                 private val mDataBaseManager: DataBaseManager?,
                 private val mEntityDataMapper: DAOMapper,
                 private val mMemoryStore: MemoryStore?) : DataStore {
    init {
        Config.cloudStore = this
    }

    private fun <M> getErrorFlowableNotPersisted(): Flowable<M> {
        return Flowable.error(NetworkConnectionException("Could not reach server and could not persist to queue!"))
    }

    override fun <M> dynamicGetList(url: String, idColumnName: String,
                                    requestType: Class<M>, persist: Boolean,
                                    shouldCache: Boolean): Flowable<List<M>> {
        return mApiConnection.dynamicGetList<M>(url, shouldCache)
                .map { entities: List<M> -> mEntityDataMapper.mapAllTo<List<M>>(entities, requestType) }
                .doOnNext { list: List<M> ->
                    if (withDisk(persist)) {
                        saveAllToDisk(list, requestType)
                    }
                    if (withCache(shouldCache)) {
                        saveAllToMemory(idColumnName, JSONArray(gson.toJson(list)), requestType)
                    }
                }
    }

    override fun <M> dynamicGetObject(url: String, idColumnName: String, itemId: Any, itemIdType: Class<*>,
                                      requestType: Class<M>, persist: Boolean, shouldCache: Boolean): Flowable<M> {
        return mApiConnection.dynamicGetObject<M>(url, shouldCache)
                .doOnNext { m: M ->
                    saveLocally(idColumnName, itemIdType,
                            JSONObject(gson.toJson(m)), requestType, persist, shouldCache)
                }
                .map { entity: M -> mEntityDataMapper.mapTo<M>(entity, requestType) }
    }

    override fun <M : RealmModel> queryDisk(queryFactory: RealmQueryProvider<M>): Flowable<List<M>> {
        return Flowable.error(IllegalAccessException("Can not search disk in cloud data store!"))
    }

    override fun <M> dynamicPatchObject(url: String, idColumnName: String, itemIdType: Class<*>,
                                        jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                                        persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return Flowable.defer {
            saveLocally(idColumnName, itemIdType, jsonObject, requestType, persist, cache)
            if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<M>()
            } else
                mApiConnection.dynamicPatch<M>(url,
                        RequestBody.create(MediaType.parse(APPLICATION_JSON), jsonObject.toString()))
                        .map { `object`: M -> daoMapHelper(responseType, `object`) }
        }
    }

    override fun <M> dynamicPostObject(url: String, idColumnName: String, itemIdType: Class<*>,
                                       jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                                       persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return Flowable.defer {
            saveLocally(idColumnName, itemIdType, jsonObject, requestType, persist, cache)
            if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<M>()
            } else
                mApiConnection.dynamicPost<M>(url,
                        RequestBody.create(MediaType.parse(APPLICATION_JSON), jsonObject.toString()))
                        .map { `object`: M -> daoMapHelper(responseType, `object`) }
        }
    }

    override fun <M> dynamicPostList(url: String, idColumnName: String, itemIdType: Class<*>,
                                     jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                                     persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return Flowable.defer {
            saveAllLocally(idColumnName, itemIdType, jsonArray, requestType, persist, cache)
            if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<M>()
            } else
                mApiConnection.dynamicPost<M>(url,
                        RequestBody.create(MediaType.parse(APPLICATION_JSON), jsonArray.toString()))
                        .map { `object`: M -> daoMapHelper(responseType, `object`) }
        }
    }

    override fun <M> dynamicPutObject(url: String, idColumnName: String, itemIdType: Class<*>,
                                      jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                                      persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return Flowable.defer {
            saveLocally(idColumnName, itemIdType, jsonObject, requestType, persist, cache)
            if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<M>()
            } else
                mApiConnection.dynamicPut<M>(url,
                        RequestBody.create(MediaType.parse(APPLICATION_JSON), jsonObject.toString()))
                        .map { `object` -> daoMapHelper(responseType, `object`) }
        }
    }

    override fun <M> dynamicPutList(url: String, idColumnName: String, itemIdType: Class<*>,
                                    jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                                    persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return Flowable.defer {
            saveAllLocally(idColumnName, itemIdType, jsonArray, requestType, persist, cache)
            if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<M>()
            } else
                mApiConnection.dynamicPut<M>(url,
                        RequestBody.create(MediaType.parse(APPLICATION_JSON), jsonArray.toString()))
                        .map { `object` -> daoMapHelper(responseType, `object`) }
        }
    }

    override fun <M> dynamicDeleteCollection(url: String, idColumnName: String, itemIdType: Class<*>,
                                             jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                                             persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return Flowable.defer {
            deleteLocally(convertToListOfId(jsonArray, itemIdType), idColumnName, itemIdType,
                    requestType, persist, cache)
            if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<M>()
            } else
                mApiConnection.dynamicDelete<M>(url)
                        .map { `object`: M -> daoMapHelper(responseType, `object`) }
        }
    }

    override fun dynamicDeleteAll(requestType: Class<*>): Single<Boolean> {
        return Single.error(IllegalStateException("Can not delete all from cloud data store!"))
    }

    override fun dynamicDownloadFile(url: String, file: File, onWifi: Boolean,
                                     whileCharging: Boolean, queuable: Boolean): Flowable<File> {
        return Flowable.defer {
            if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<File>()
            } else mApiConnection.dynamicDownload(url)
                    .map { responseBody ->
                        try {
                            var inputStream: InputStream? = null
                            var outputStream: OutputStream? = null
                            try {
                                val fileReader = ByteArray(4096)
                                val fileSize = responseBody.contentLength()
                                var fileSizeDownloaded: Long = 0
                                inputStream = responseBody.byteStream()
                                outputStream = FileOutputStream(file)
                                while (true) {
                                    val read = inputStream!!.read(fileReader)
                                    if (read == -1) {
                                        break
                                    }
                                    outputStream.write(fileReader, 0, read)
                                    fileSizeDownloaded += read.toLong()
                                    Log.d(TAG, "file download: $fileSizeDownloaded of $fileSize")
                                }
                                outputStream.flush()
                            } catch (e: IOException) {
                                Log.e(TAG, "", e)
                            } finally {
                                inputStream?.close()
                                outputStream?.close()
                            }
                        } catch (e: IOException) {
                            Log.e(TAG, "", e)
                        }
                        file
                    }
        }
    }

    override fun <M> dynamicUploadFile(url: String, keyFileMap: HashMap<String, File>,
                                       parameters: HashMap<String, Any>, onWifi: Boolean, whileCharging: Boolean,
                                       queuable: Boolean, responseType: Class<M>): Flowable<M> {
        return Flowable.defer {
            val multiPartBodyParts = ArrayList<MultipartBody.Part>()
            keyFileMap.toMap().forEach { entry ->
                multiPartBodyParts.add(MultipartBody.Part.createFormData(entry.key,
                        entry.value.name, RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA),
                        entry.value)))
            }
            val map = mutableMapOf<String, RequestBody>()
            for ((key, value) in parameters) {
                map[key] = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA),
                        value.toString())
            }
            if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<M>()
            } else mApiConnection.dynamicUpload<M>(url, map, multiPartBodyParts)
                    .map { `object` -> daoMapHelper(responseType, `object`) }
        }
    }

    private fun <M> daoMapHelper(requestType: Class<*>, `object`: M): M? {
        return if (`object` is List<*>)
            mEntityDataMapper.mapAllTo(`object` as List<*>, requestType)
        else
            mEntityDataMapper.mapTo(`object`, requestType)
    }

    private fun saveAllToDisk(collection: List<*>, requestType: Class<*>) {
        mDataBaseManager?.putAll(collection as List<RealmModel>, requestType)
                ?.subscribeOn(Config.backgroundThread)
                ?.subscribe(SimpleSubscriber(requestType))
    }

    private fun saveAllToMemory(idColumnName: String, jsonArray: JSONArray, requestType: Class<*>) {
        mMemoryStore?.cacheList(idColumnName, jsonArray, requestType)
    }

    private fun saveLocally(idColumnName: String, itemIdType: Class<*>, jsonObject: JSONObject,
                            requestType: Class<*>, persist: Boolean, cache: Boolean) {
        if (withDisk(persist)) {
            mDataBaseManager?.put(jsonObject, idColumnName, itemIdType, requestType)
                    ?.subscribeOn(Config.backgroundThread)
                    ?.subscribe(SimpleSubscriber(requestType))
        }
        if (withCache(cache)) {
            mMemoryStore?.cacheObject(idColumnName, jsonObject, requestType)
        }
    }

    private fun saveAllLocally(idColumnName: String, itemIdType: Class<*>, jsonArray: JSONArray,
                               requestType: Class<*>, persist: Boolean, cache: Boolean) {
        if (withDisk(persist)) {
            mDataBaseManager?.putAll(jsonArray, idColumnName, itemIdType, requestType)
                    ?.subscribeOn(Config.backgroundThread)
                    ?.subscribe(SimpleSubscriber(requestType))
        }
        if (withCache(cache)) {
            mMemoryStore?.cacheList(idColumnName, jsonArray, requestType)
        }
    }

    private fun deleteLocally(ids: List<Any>, idColumnName: String, itemIdType: Class<*>, requestType: Class<*>,
                              persist: Boolean, cache: Boolean) {
        if (withDisk(persist)) {
            val collectionSize = ids.size
            for (i in 0 until collectionSize) {
                mDataBaseManager?.evictById(requestType, idColumnName, ids[i], itemIdType)
            }
        }
        if (withCache(cache)) {
            val stringIds = Flowable.fromIterable(ids)
                    .map { it.toString() }
                    .toList(ids.size)
                    .blockingGet()
            mMemoryStore?.deleteList(stringIds, requestType)
        }
    }

    private class SimpleSubscriber internal constructor(private val mClass: Class<*>) : SingleObserver<Any> {
        override fun onSuccess(t: Any) {
            subscription!!.dispose()
            Log.d(TAG, mClass.simpleName + " persisted!")
        }

        private var subscription: Disposable? = null

        override fun onSubscribe(d: Disposable) {
            subscription = d
        }

        override fun onError(e: Throwable) {
            Log.e(TAG, "", e)
            subscription!!.dispose()
        }
    }

    companion object {

        const val APPLICATION_JSON = "application/json"
        const val MULTIPART_FORM_DATA = "multipart/form-data"
        private val TAG = CloudStore::class.java.simpleName
    }
}