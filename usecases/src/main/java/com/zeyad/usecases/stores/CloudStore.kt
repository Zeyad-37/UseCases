package com.zeyad.usecases.stores

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.util.Log
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.zeyad.usecases.*
import com.zeyad.usecases.Config.gson
import com.zeyad.usecases.db.DataBaseManager
import com.zeyad.usecases.exceptions.NetworkConnectionException
import com.zeyad.usecases.mapper.DAOMapper
import com.zeyad.usecases.network.ApiConnection
import com.zeyad.usecases.requests.FileIORequest
import com.zeyad.usecases.requests.PostRequest
import com.zeyad.usecases.requests.PostRequest.*
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
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.*

class CloudStore(private val mApiConnection: ApiConnection, //    private static final int COUNTER_START = 1, ATTEMPTS = 3;
                 private val mDataBaseManager: DataBaseManager,
                 private val mEntityDataMapper: DAOMapper,
                 private val mMemoryStore: MemoryStore?) : DataStore {

    private val mDispatcher: FirebaseJobDispatcher = FirebaseJobDispatcher(GooglePlayDriver(Config.context))

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

//    fun <M> queryDisk(queryFactory: RealmQueryProvider): Flowable<List<M>> {
//        return Flowable.error(IllegalAccessException("Can not search disk in cloud data store!"))
//    }

    override fun <M> dynamicPatchObject(url: String, idColumnName: String, itemIdType: Class<*>,
                                        jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                                        persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return Flowable.defer {
            saveLocally(idColumnName, itemIdType, jsonObject, requestType, persist, cache)
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(PATCH, url, idColumnName, itemIdType, jsonObject, persist)
                Flowable.just<M>(responseType.newInstance() as M)
            } else if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<M>()
            } else
                mApiConnection.dynamicPatch<M>(url,
                        RequestBody.create(MediaType.parse(APPLICATION_JSON), jsonObject.toString()))
                        .map { `object`: M -> daoMapHelper(responseType, `object`) }
                        .onErrorResumeNext { throwable: Throwable ->
                            if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                                queuePost(PATCH, url, idColumnName, itemIdType, jsonObject, persist)
                                return@onErrorResumeNext Flowable.empty<M>()
                            } else Flowable.error(throwable)
                        }
        }
    }

    override fun <M> dynamicPostObject(url: String, idColumnName: String, itemIdType: Class<*>,
                                       jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                                       persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return Flowable.defer {
            saveLocally(idColumnName, itemIdType, jsonObject, requestType, persist, cache)
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(POST, url, idColumnName, itemIdType, jsonObject, persist)
                Flowable.just<M>(responseType.newInstance() as M)
            } else if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<M>()
            } else
                mApiConnection.dynamicPost<M>(url,
                        RequestBody.create(MediaType.parse(APPLICATION_JSON), jsonObject.toString()))
                        .map { `object`: M -> daoMapHelper(responseType, `object`) }
                        .onErrorResumeNext { throwable: Throwable ->
                            if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                                queuePost(POST, url, idColumnName, itemIdType, jsonObject, persist)
                                return@onErrorResumeNext Flowable.empty<M>()
                            } else Flowable.error(throwable)
                        }
        }
    }

    override fun <M> dynamicPostList(url: String, idColumnName: String, itemIdType: Class<*>,
                                     jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                                     persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return Flowable.defer {
            saveAllLocally(idColumnName, itemIdType, jsonArray, requestType, persist, cache)
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(POST, url, idColumnName, itemIdType, jsonArray, persist)
                Flowable.just<M>(responseType.newInstance() as M)
            } else if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<M>()
            } else
                mApiConnection.dynamicPost<M>(url,
                        RequestBody.create(MediaType.parse(APPLICATION_JSON), jsonArray.toString()))
                        .map { `object`: M -> daoMapHelper(responseType, `object`) }
                        .onErrorResumeNext { throwable: Throwable ->
                            if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                                queuePost(POST, url, idColumnName, itemIdType, jsonArray, persist)
                                return@onErrorResumeNext Flowable.empty<M>()
                            } else Flowable.error(throwable)
                        }
        }
    }

    override fun <M> dynamicPutObject(url: String, idColumnName: String, itemIdType: Class<*>,
                                      jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                                      persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return Flowable.defer {
            saveLocally(idColumnName, itemIdType, jsonObject, requestType, persist, cache)
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(PUT, url, idColumnName, itemIdType, jsonObject, persist)
                Flowable.just<M>(responseType.newInstance() as M)
            } else if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<M>()
            } else
                mApiConnection.dynamicPut<M>(url,
                        RequestBody.create(MediaType.parse(APPLICATION_JSON), jsonObject.toString()))
                        .map { `object` -> daoMapHelper(responseType, `object`) }
                        .onErrorResumeNext { throwable: Throwable ->
                            if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                                queuePost(PUT, url, idColumnName, itemIdType, jsonObject, persist)
                                return@onErrorResumeNext Flowable.empty<M>()
                            } else Flowable.error(throwable)
                        }
        }
    }

    override fun <M> dynamicPutList(url: String, idColumnName: String, itemIdType: Class<*>,
                                    jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                                    persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return Flowable.defer {
            saveAllLocally(idColumnName, itemIdType, jsonArray, requestType, persist, cache)
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(PUT, url, idColumnName, itemIdType, jsonArray, persist)
                Flowable.just<M>(responseType.newInstance() as M)
            } else if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<M>()
            } else
                mApiConnection.dynamicPut<M>(url,
                        RequestBody.create(MediaType.parse(APPLICATION_JSON), jsonArray.toString()))
                        .map { `object` -> daoMapHelper(responseType, `object`) }
                        .onErrorResumeNext { throwable: Throwable ->
                            if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                                queuePost(PUT, url, idColumnName, itemIdType, jsonArray, persist)
                                return@onErrorResumeNext Flowable.empty<M>()
                            } else Flowable.error(throwable)
                        }
        }
    }

    override fun <M> dynamicDeleteCollection(url: String, idColumnName: String, itemIdType: Class<*>,
                                             jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                                             persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M> {
        return Flowable.defer {
            deleteLocally(convertToListOfId(jsonArray, itemIdType), idColumnName, itemIdType,
                    requestType, persist, cache)
            if (isQueuableIfOutOfNetwork(queuable)) {
                queuePost(DELETE, url, idColumnName, itemIdType, jsonArray, persist)
                Flowable.just<M>(responseType.newInstance() as M)
            } else if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<M>()
            } else
                mApiConnection.dynamicDelete<M>(url)
                        .map { `object`: M -> daoMapHelper(responseType, `object`) }
                        .onErrorResumeNext { throwable: Throwable ->
                            if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                                queuePost(DELETE, url, idColumnName, itemIdType, jsonArray, persist)
                                return@onErrorResumeNext Flowable.empty<M>()
                            } else Flowable.error<M>(throwable)
                        }
        }
    }

    override fun dynamicDeleteAll(requestType: Class<*>): Single<Boolean> {
        return Single.error(IllegalStateException("Can not delete all from cloud data store!"))
    }

    override fun dynamicDownloadFile(url: String, file: File, onWifi: Boolean,
                                     whileCharging: Boolean, queuable: Boolean): Flowable<File> {
        return Flowable.defer {
            if (isQueuableIfOutOfNetwork(queuable) && isOnWifi(Config.context) == onWifi
                    && isChargingReqCompatible(isCharging(Config.context), whileCharging)) {
                queueIOFile(url, null, file, onWifi, whileCharging, true)
                Flowable.just<File>(File(""))
            } else if (!isNetworkAvailable(Config.context)) {
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
                    .onErrorResumeNext { throwable: Throwable ->
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                            queueIOFile(url, null, file, true, whileCharging, false)
                            return@onErrorResumeNext Flowable.just(File(""))
                        } else Flowable.error(throwable)
                    }
        }
    }

    override fun <M> dynamicUploadFile(url: String, keyFileMap: HashMap<String, File>,
                                       parameters: HashMap<String, Any>?, onWifi: Boolean, whileCharging: Boolean, queuable: Boolean,
                                       responseType: Class<*>): Flowable<M> {
        return Flowable.defer {
            val multiPartBodyParts = ArrayList<MultipartBody.Part>()
            keyFileMap.forEach { key, file ->
                multiPartBodyParts.add(MultipartBody.Part.createFormData(key,
                        file.name, RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file)))
            }
            val map = HashMap<String, RequestBody>()
            if (parameters != null && !parameters.isEmpty()) {
                for ((key, value) in parameters) {
                    map[key] = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA),
                            value.toString())
                }
            }
            if (isQueuableIfOutOfNetwork(queuable) && isOnWifi(Config.context) == onWifi
                    && isChargingReqCompatible(isCharging(Config.context), whileCharging)) {
                queueIOFile(url, keyFileMap, null, true, whileCharging, false)
                Flowable.just<M>(responseType.newInstance() as M)
            } else if (!isNetworkAvailable(Config.context)) {
                getErrorFlowableNotPersisted<M>()
            } else mApiConnection.dynamicUpload<M>(url, map, multiPartBodyParts)
                    .map { `object` -> daoMapHelper(responseType, `object`) }
                    .onErrorResumeNext { throwable: Throwable ->
                        if (isQueuableIfOutOfNetwork(queuable) && isNetworkFailure(throwable)) {
                            queueIOFile(url, keyFileMap, null, true, whileCharging, false)
                            return@onErrorResumeNext Flowable.empty<M>()
                        }
                        Flowable.error(throwable)
                    }
        }
    }

    //    private <M> FlowableTransformer<M, M> applyExponentialBackoff() {
    //        return observable -> observable.retryWhen(attempts -> {
    //            return attempts.zipWith(
    //                    Flowable.range(COUNTER_START, ATTEMPTS), (n, i) -> i)
    //                    .flatMap(i -> {
    //                        Log.d(TAG, "delay retry by " + i + " second(s)");
    //                        return Flowable.timer(5 * i, TimeUnit.SECONDS);
    //                    });
    //        });
    //    }

    private fun <M> daoMapHelper(requestType: Class<*>, `object`: M): M? {
        return if (`object` is List<*>)
            mEntityDataMapper.mapAllTo(`object` as List<*>, requestType)
        else
            mEntityDataMapper.mapTo(`object`, requestType)
    }

    private fun isNetworkFailure(throwable: Throwable): Boolean {
        return (throwable is UnknownHostException
                || throwable is ConnectException
                || throwable is IOException)
    }

    private fun isQueuableIfOutOfNetwork(queuable: Boolean): Boolean {
        return queuable && !isNetworkAvailable(Config.context)
    }

    private fun isChargingReqCompatible(isChargingCurrently: Boolean, doWhileCharging: Boolean): Boolean {
        return !doWhileCharging || isChargingCurrently
    }

    private fun isCharging(context: Context): Boolean {
        var charging = false
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        if (batteryIntent != null) {
            val status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val batteryCharge = status == BatteryManager.BATTERY_STATUS_CHARGING
            val chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
            val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
            if (batteryCharge) {
                charging = true
            }
            if (usbCharge) {
                charging = true
            }
            if (acCharge) {
                charging = true
            }
        }
        return charging
        //        Intent intent = Config.context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        //        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    private fun isOnWifi(context: Context): Boolean {
        return (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
    }

    private fun queueIOFile(url: String, keyFileMap: HashMap<String, File>?, file: File?, onWifi: Boolean,
                            whileCharging: Boolean, isDownload: Boolean) {
        queueFileIOCore(mDispatcher, isDownload, FileIORequest.Builder(url)
                .keyFileMapToUpload(keyFileMap)
                .file(file)
//                .queuable(onWifi, whileCharging)
                .build(), 0)
    }

    private fun queuePost(method: String, url: String, idColumnName: String, idType: Class<*>,
                          jsonArray: JSONArray, persist: Boolean) {
        queuePostCore(PostRequest.Builder(null, persist)
                .idColumnName(idColumnName, idType)
                .payLoad(jsonArray)
                .url(url)
                .method(method)
                .build())
    }

    private fun queuePost(method: String, url: String, idColumnName: String, idType: Class<*>,
                          jsonObject: JSONObject, persist: Boolean) {
        queuePostCore(PostRequest.Builder(null, persist)
                .idColumnName(idColumnName, idType)
                .payLoad(jsonObject)
                .url(url)
                .method(method)
                .build())
    }

    private fun queuePostCore(postRequest: PostRequest) {
        queuePostCore(mDispatcher, postRequest, 0)
    }

    private fun saveAllToDisk(collection: List<*>, requestType: Class<*>) {
        mDataBaseManager.putAll(collection as List<RealmModel>, requestType)
                .subscribeOn(Config.backgroundThread)
                .subscribe(SimpleSubscriber(requestType))
    }

    private fun saveAllToMemory(idColumnName: String, jsonArray: JSONArray, requestType: Class<*>) {
        mMemoryStore?.cacheList(idColumnName, jsonArray, requestType)
    }

    private fun saveLocally(idColumnName: String, itemIdType: Class<*>, jsonObject: JSONObject,
                            requestType: Class<*>, persist: Boolean, cache: Boolean) {
        if (withDisk(persist)) {
            mDataBaseManager.put(jsonObject, idColumnName, itemIdType, requestType)
                    .subscribeOn(Config.backgroundThread)
                    .subscribe(SimpleSubscriber(requestType))
        }
        if (withCache(cache)) {
            mMemoryStore?.cacheObject(idColumnName, jsonObject, requestType)
        }
    }

    private fun saveAllLocally(idColumnName: String, itemIdType: Class<*>, jsonArray: JSONArray,
                               requestType: Class<*>, persist: Boolean, cache: Boolean) {
        if (withDisk(persist)) {
            mDataBaseManager.putAll(jsonArray, idColumnName, itemIdType, requestType)
                    .subscribeOn(Config.backgroundThread)
                    .subscribe(SimpleSubscriber(requestType))
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
                mDataBaseManager.evictById(requestType, idColumnName, ids[i], itemIdType)
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