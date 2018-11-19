package com.zeyad.usecases.stores

import android.util.Log
import com.zeyad.usecases.*
import com.zeyad.usecases.Config.gson
import com.zeyad.usecases.db.DataBaseManager
import com.zeyad.usecases.exceptions.NetworkConnectionException
import com.zeyad.usecases.mapper.DAOMapper
import com.zeyad.usecases.network.ApiConnection
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.create
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.util.*

@Mockable
class CloudStore(private val mApiConnection: ApiConnection,
                 private val mDataBaseManager: DataBaseManager?,
                 private val mEntityDataMapper: DAOMapper,
                 private val mMemoryStore: MemoryStore?) : DataStore {
    init {
        Config.cloudStore = this
    }

    private fun <M> getErrorSingleNotPersisted(): Single<M> {
        return Single.error(NetworkConnectionException("Could not reach server!"))
    }

    private inline fun <M> persistErrorExecute(disk: () -> Unit, network: () -> Single<M>): Single<M> {
        disk.invoke()
        return if (isNetworkNotAvailable(Config.context)) {
            getErrorSingleNotPersisted()
        } else network.invoke()
    }

    override fun <M> dynamicGetList(url: String, idColumnName: String,
                                    requestType: Class<M>, persist: Boolean,
                                    shouldCache: Boolean): Flowable<List<M>> {
        return mApiConnection.dynamicGetList<M>(url, shouldCache)
                .map { mEntityDataMapper.mapAllTo<List<M>>(it, requestType) }
                .doOnNext { saveAllLocally(idColumnName, it, requestType, persist, shouldCache) }
    }

    override fun <M> dynamicGetObject(url: String, idColumnName: String, itemId: Any,
                                      requestType: Class<M>, persist: Boolean, shouldCache: Boolean): Flowable<M> {
        return mApiConnection.dynamicGetObject<M>(url, shouldCache)
                .doOnNext {
                    saveLocally(idColumnName, JSONObject(gson.toJson(it)), requestType, persist, shouldCache)
                }
                .map { mEntityDataMapper.mapTo<M>(it, requestType) }
    }

    override fun <M> queryDisk(query: String, clazz: Class<M>): Flowable<List<M>> {
        return Flowable.error(IllegalAccessException("Can not search disk in cloud data store!"))
    }

    override fun <M> dynamicPatchObject(url: String, idColumnName: String,
                                        jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                                        persist: Boolean, cache: Boolean): Single<M> {
        return Single.defer {
            persistErrorExecute({
                saveLocally(idColumnName, jsonObject, requestType, persist, cache)
            }, {
                mApiConnection.dynamicPatch<M>(url,
                        create(MediaType.parse(APPLICATION_JSON), jsonObject.toString()))
                        .map { daoMapHelper(responseType, it) }
            })
        }
    }

    override fun <M> dynamicPostObject(url: String, idColumnName: String,
                                       jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                                       persist: Boolean, cache: Boolean): Single<M> {
        return Single.defer {
            persistErrorExecute({
                saveLocally(idColumnName, jsonObject, requestType, persist, cache)
            }, {
                mApiConnection.dynamicPost<M>(url,
                        create(MediaType.parse(APPLICATION_JSON), jsonObject.toString()))
                        .map { daoMapHelper(responseType, it) }
            })
        }
    }

    override fun <M> dynamicPostList(url: String, idColumnName: String,
                                     jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                                     persist: Boolean, cache: Boolean): Single<M> {
        return Single.defer {
            persistErrorExecute({
                saveAllLocally(idColumnName, jsonArray, requestType, persist, cache)
            }, {
                mApiConnection.dynamicPost<M>(url,
                        create(MediaType.parse(APPLICATION_JSON), jsonArray.toString()))
                        .map { daoMapHelper(responseType, it) }
            })
        }
    }

    override fun <M> dynamicPutObject(url: String, idColumnName: String,
                                      jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                                      persist: Boolean, cache: Boolean): Single<M> {
        return Single.defer {
            persistErrorExecute({
                saveLocally(idColumnName, jsonObject, requestType, persist, cache)
            }, {
                mApiConnection.dynamicPut<M>(url,
                        create(MediaType.parse(APPLICATION_JSON), jsonObject.toString()))
                        .map { daoMapHelper(responseType, it) }
            })
        }
    }

    override fun <M> dynamicPutList(url: String, idColumnName: String,
                                    jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                                    persist: Boolean, cache: Boolean): Single<M> {
        return Single.defer {
            persistErrorExecute({
                saveAllLocally(idColumnName, jsonArray, requestType, persist, cache)
            }, {
                mApiConnection.dynamicPut<M>(url,
                        create(MediaType.parse(APPLICATION_JSON), jsonArray.toString()))
                        .map { daoMapHelper(responseType, it) }
            })
        }
    }

    override fun <T, M> dynamicDeleteCollection(url: String, idColumnName: String, itemIdType: Class<*>,
                                                jsonArray: JSONArray, requestType: Class<T>,
                                                responseType: Class<M>, persist: Boolean, cache: Boolean): Single<M> {
        return Single.defer {
            persistErrorExecute({
                val list = mutableListOf<T>()
                for (i in 0..(jsonArray.length() - 1)) {
                    list.add(gson.fromJson(jsonArray.getJSONObject(i).toString(), requestType))
                }
                deleteLocally(list, idColumnName, requestType, persist, cache)
            }, {
                mApiConnection.dynamicDelete<M>(url,
                        create(MediaType.parse(APPLICATION_JSON), jsonArray.toString()))
                        .map { daoMapHelper(responseType, it) }
            })
        }
    }

    override fun <M> dynamicDeleteCollectionById(url: String, idColumnName: String, itemIdType: Class<*>,
                                                 jsonArray: JSONArray, requestType: Class<*>,
                                                 responseType: Class<M>, persist: Boolean, cache: Boolean): Single<M> {
        return Single.defer {
            persistErrorExecute({
                deleteLocallyById(convertToListOfId(jsonArray, itemIdType), idColumnName, requestType,
                        persist, cache)
            }, {
                mApiConnection.dynamicDelete<M>(url, create(MediaType.parse(APPLICATION_JSON), jsonArray.toString()))
                        .map { daoMapHelper(responseType, it) }
            })
        }
    }


    override fun dynamicDeleteAll(requestType: Class<*>): Single<Boolean> {
        return Single.error(IllegalStateException("Can not delete all from cloud data store!"))
    }

    override fun dynamicDownloadFile(url: String, file: File): Single<File> {
        return Single.defer {
            if (isNetworkNotAvailable(Config.context)) {
                getErrorSingleNotPersisted<File>()
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
                                       parameters: HashMap<String, Any>, responseType: Class<M>): Single<M> {
        return Single.defer {
            val multiPartBodyParts = ArrayList<MultipartBody.Part>()
            keyFileMap.toMap().forEach { entry ->
                multiPartBodyParts.add(MultipartBody.Part.createFormData(entry.key,
                        entry.value.name, create(MediaType.parse(MULTIPART_FORM_DATA),
                        entry.value)))
            }
            val map = mutableMapOf<String, RequestBody>()
            for ((key, value) in parameters) {
                map[key] = create(MediaType.parse(MULTIPART_FORM_DATA),
                        value.toString())
            }
            if (isNetworkNotAvailable(Config.context)) {
                getErrorSingleNotPersisted<M>()
            } else mApiConnection.dynamicUpload<M>(url, map, multiPartBodyParts)
                    .map { daoMapHelper(responseType, it) }
        }
    }

    private fun <M> daoMapHelper(requestType: Class<*>, model: M): M? {
        return if (model is List<*>)
            mEntityDataMapper.mapAllTo(model as List<*>, requestType)
        else
            mEntityDataMapper.mapTo(model, requestType)
    }

    private fun <M> saveAllToDisk(collection: List<M>, requestType: Class<M>) {
        mDataBaseManager?.putAll(collection, requestType)
                ?.subscribeOn(Config.backgroundThread)
                ?.subscribe(SimpleSingleObserver())
    }

    private fun saveAllToMemory(idColumnName: String, jsonArray: JSONArray, requestType: Class<*>) {
        mMemoryStore?.cacheList(idColumnName, jsonArray, requestType)
    }

    private fun <M> saveAllLocally(idColumnName: String, collection: List<M>,
                                   requestType: Class<M>, persist: Boolean, cache: Boolean) {
        if (withDisk(persist)) {
            saveAllToDisk(collection, requestType)
        }
        if (withCache(cache)) {
            saveAllToMemory(idColumnName, JSONArray(gson.toJson(collection)), requestType)
        }
    }

    private fun saveLocally(idColumnName: String, jsonObject: JSONObject,
                            requestType: Class<*>, persist: Boolean, cache: Boolean) {
        if (withDisk(persist)) {
            mDataBaseManager?.put(jsonObject, requestType)
                    ?.subscribeOn(Config.backgroundThread)
                    ?.subscribe(SimpleSingleObserver())
        }
        if (withCache(cache)) {
            mMemoryStore?.cacheObject(idColumnName, jsonObject, requestType)
        }
    }

    private fun saveAllLocally(idColumnName: String, jsonArray: JSONArray, requestType: Class<*>,
                               persist: Boolean, cache: Boolean) {
        if (withDisk(persist)) {
            mDataBaseManager?.putAll(jsonArray, requestType)
                    ?.subscribeOn(Config.backgroundThread)
                    ?.subscribe(SimpleSingleObserver())
        }
        if (withCache(cache)) {
            mMemoryStore?.cacheList(idColumnName, jsonArray, requestType)
        }
    }

    private fun <T> deleteLocally(list: List<T>, idColumnName: String, requestType: Class<T>,
                                  persist: Boolean, cache: Boolean) {
        if (withDisk(persist)) {
            val collectionSize = list.size
            for (i in 0 until collectionSize) {
                mDataBaseManager?.evictCollection(list, requestType)
            }
        }
        if (withCache(cache)) {
            val stringIds = Flowable.fromIterable(list)
                    .map { JSONObject(gson.toJson(it)).opt(idColumnName) }
                    .map { it.toString() }
                    .toList(list.size)
                    .blockingGet()
            mMemoryStore?.deleteListById(stringIds, requestType)
        }
    }

    private fun deleteLocallyById(ids: List<Any>, idColumnName: String, requestType: Class<*>,
                                  persist: Boolean, cache: Boolean) {
        if (withDisk(persist)) {
            val collectionSize = ids.size
            for (i in 0 until collectionSize) {
                mDataBaseManager?.evictById(requestType, idColumnName, ids[i])?.blockingGet()
            }
        }
        if (withCache(cache)) {
            val stringIds = Flowable.fromIterable(ids)
                    .map { it.toString() }
                    .toList(ids.size)
                    .blockingGet()
            mMemoryStore?.deleteListById(stringIds, requestType)
        }
    }

    private class SimpleSingleObserver : SingleObserver<Any> {
        private var subscription: Disposable? = null

        override fun onSuccess(t: Any) {
            subscription!!.dispose()
        }

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