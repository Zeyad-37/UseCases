package com.zeyad.usecases.api

import android.util.Log
import com.jakewharton.rx.ReplayingShare
import com.zeyad.usecases.requests.FileIORequest
import com.zeyad.usecases.requests.GetRequest
import com.zeyad.usecases.requests.PostRequest
import com.zeyad.usecases.stores.DataStoreFactory
import com.zeyad.usecases.withCache
import com.zeyad.usecases.withDisk
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.Scheduler
import io.reactivex.Single
import org.json.JSONException
import java.io.File

/**
 * @author by ZIaDo on 5/9/17.
 */
internal class DataService(private val mDataStoreFactory: DataStoreFactory,
                           private val mPostExecutionThread: Scheduler?,
                           private val mBackgroundThread: Scheduler) : IDataService {
    private val mPostThreadExist: Boolean = mPostExecutionThread != null

    override fun <M> getList(getListRequest: GetRequest): Flowable<List<M>> {
        var result: Flowable<List<M>>
        try {
            val dataClass = getListRequest.getTypedDataClass<M>()
            val url = getListRequest.getCorrectUrl()
            val simpleName = dataClass.simpleName
            val shouldCache = getListRequest.cache
            val dynamicGetList = mDataStoreFactory.dynamically(url, dataClass)
                    .dynamicGetList(url, getListRequest.idColumnName, dataClass,
                            getListRequest.persist, shouldCache)
            result = if (withCache(shouldCache)) {
                mDataStoreFactory.memory()!!.getAllItems(dataClass)
                        .doOnSuccess { Log.d("getList", CACHE_HIT + simpleName) }
                        .doOnError { Log.d("getList", CACHE_MISS + simpleName) }
                        .toFlowable()
                        .onErrorResumeNext { _: Throwable -> dynamicGetList }
            } else {
                dynamicGetList
            }
        } catch (e: IllegalAccessException) {
            result = Flowable.error(e)
        }

        return result.compose(applySchedulers())
    }

    override fun <M> getObject(getRequest: GetRequest): Flowable<M> {
        var result: Flowable<M>
        try {
            val itemId = getRequest.itemId
            val dataClass = getRequest.getTypedDataClass<M>()
            val shouldCache = getRequest.cache
            val url = getRequest.getCorrectUrl()
            val simpleName = dataClass.simpleName
            val dynamicGetObject = mDataStoreFactory.dynamically(url, dataClass)
                    .dynamicGetObject(url, getRequest.idColumnName, itemId, getRequest.idType,
                            dataClass, getRequest.persist, shouldCache)
            result = if (withCache(shouldCache)) {
                mDataStoreFactory.memory()!!
                        .getItem<M>(itemId.toString(), dataClass)
                        .doOnSuccess { Log.d("getObject", CACHE_HIT + simpleName) }
                        .doOnError { Log.d("getObject", CACHE_MISS + simpleName) }
                        .toFlowable()
                        .onErrorResumeNext { _: Throwable -> dynamicGetObject }
            } else {
                dynamicGetObject
            }
        } catch (e: IllegalAccessException) {
            result = Flowable.error(e)
        }

        return result.compose(applySchedulers())
    }

    override fun <M> patchObject(postRequest: PostRequest): Flowable<M> {
        val result: Flowable<M> = try {
            mDataStoreFactory.dynamically(postRequest.getCorrectUrl(), postRequest.requestType)
                    .dynamicPatchObject(postRequest.getCorrectUrl(), postRequest.idColumnName,
                            postRequest.idType, postRequest.getObjectBundle(),
                            postRequest.requestType, postRequest.getTypedResponseClass(),
                            postRequest.persist, postRequest.cache, postRequest.queuable)
        } catch (e: IllegalAccessException) {
            Flowable.error(e)
        } catch (e: JSONException) {
            Flowable.error(e)
        }

        return result.compose(applySchedulers())
    }

    override fun <M> postObject(postRequest: PostRequest): Flowable<M> {
        val result: Flowable<M> = try {
            mDataStoreFactory.dynamically(postRequest.getCorrectUrl(), postRequest.requestType)
                    .dynamicPostObject(postRequest.getCorrectUrl(), postRequest.idColumnName,
                            postRequest.idType, postRequest.getObjectBundle(),
                            postRequest.requestType, postRequest.getTypedResponseClass(),
                            postRequest.persist, postRequest.cache, postRequest.queuable)
        } catch (e: IllegalAccessException) {
            Flowable.error(e)
        } catch (e: JSONException) {
            Flowable.error(e)
        }

        return result.compose(applySchedulers())
    }

    override fun <M> postList(postRequest: PostRequest): Flowable<M> {
        val result: Flowable<M> = try {
            mDataStoreFactory.dynamically(postRequest.getCorrectUrl(), postRequest.requestType)
                    .dynamicPostList(postRequest.getCorrectUrl(), postRequest.idColumnName,
                            postRequest.idType, postRequest.getArrayBundle(),
                            postRequest.requestType, postRequest.getTypedResponseClass(),
                            postRequest.persist, postRequest.cache, postRequest.queuable)
        } catch (e: Exception) {
            Flowable.error(e)
        } catch (e: JSONException) {
            Flowable.error(e)
        }
        return result.compose(applySchedulers())
    }

    override fun <M> putObject(postRequest: PostRequest): Flowable<M> {
        val result: Flowable<M> = try {
            mDataStoreFactory.dynamically(postRequest.getCorrectUrl(), postRequest.requestType)
                    .dynamicPutObject(postRequest.getCorrectUrl(), postRequest.idColumnName,
                            postRequest.idType, postRequest.getObjectBundle(),
                            postRequest.requestType, postRequest.getTypedResponseClass(),
                            postRequest.persist, postRequest.cache, postRequest.queuable)
        } catch (e: IllegalAccessException) {
            Flowable.error(e)
        } catch (e: JSONException) {
            Flowable.error(e)
        }

        return result.compose(applySchedulers())
    }

    override fun <M> putList(postRequest: PostRequest): Flowable<M> {
        val result: Flowable<M> = try {
            mDataStoreFactory.dynamically(postRequest.getCorrectUrl(), postRequest.requestType)
                    .dynamicPutList(postRequest.getCorrectUrl(), postRequest.idColumnName,
                            postRequest.idType, postRequest.getArrayBundle(),
                            postRequest.requestType, postRequest.getTypedResponseClass(),
                            postRequest.persist, postRequest.cache, postRequest.queuable)
        } catch (e: IllegalAccessException) {
            Flowable.error(e)
        } catch (e: JSONException) {
            Flowable.error(e)
        }
        return result.compose(applySchedulers())
    }

    override fun <M> deleteItemById(request: PostRequest): Flowable<M> {
        return deleteCollectionByIds(request)
    }

    override fun <M> deleteCollectionByIds(deleteRequest: PostRequest): Flowable<M> {
        val result: Flowable<M> = try {
            mDataStoreFactory.dynamically(deleteRequest.getCorrectUrl(), deleteRequest.requestType)
                    .dynamicDeleteCollection(deleteRequest.getCorrectUrl(), deleteRequest.idColumnName,
                            deleteRequest.idType, deleteRequest.getArrayBundle(),
                            deleteRequest.requestType, deleteRequest.getTypedResponseClass<M>(),
                            deleteRequest.persist, deleteRequest.cache, deleteRequest.queuable)
                    .compose(applySchedulers())
        } catch (e: IllegalAccessException) {
            Flowable.error(e)
        } catch (e: JSONException) {
            Flowable.error(e)
        }
        return result.compose(applySchedulers())
    }

    override fun deleteAll(deleteRequest: PostRequest): Single<Boolean> {
        val result: Single<Boolean> = try {
            mDataStoreFactory.disk(deleteRequest.requestType)
                    .dynamicDeleteAll(deleteRequest.requestType)
        } catch (e: IllegalAccessException) {
            Single.error(e)
        }
        return result.compose {
            if (mPostThreadExist)
                it.subscribeOn(mBackgroundThread)
                        .observeOn(mPostExecutionThread!!).unsubscribeOn(mBackgroundThread)
            else
                it.subscribeOn(mBackgroundThread).unsubscribeOn(mBackgroundThread)
        }
    }

//    override fun <M : RealmModel> queryDisk(realmQueryProvider: RealmQueryProvider<M>): Flowable<List<M>> {
//        val result: Flowable<List<M>> = try {
//            mDataStoreFactory.disk(Any::class.java).queryDisk<M>(realmQueryProvider)
//                    .compose(ReplayingShare.instance())
//        } catch (e: IllegalAccessException) {
//            Flowable.error(e)
//        }
//        return result.compose(applySchedulers())
//    }

    override fun <M> getListOffLineFirst(getRequest: GetRequest): Flowable<List<M>> {
        var result: Flowable<List<M>>
        try {
            val dataClass = getRequest.getTypedDataClass<M>()
            val simpleName = dataClass.simpleName
            val idColumnName = getRequest.idColumnName
            val persist = getRequest.persist
            val shouldCache = getRequest.cache
            val withDisk = withDisk(persist)
            val withCache = withCache(shouldCache)
            val cloud = mDataStoreFactory.cloud(dataClass)
                    .dynamicGetList(getRequest.getCorrectUrl(), idColumnName, dataClass, persist, shouldCache)
            val disk = mDataStoreFactory.disk(dataClass)
                    .dynamicGetList("", idColumnName, dataClass, persist, shouldCache)
                    .doOnNext { Log.d(GET_LIST_OFFLINE_FIRST, "Disk Hit $simpleName") }
                    .doOnError { throwable ->
                        Log.e(GET_LIST_OFFLINE_FIRST, "Disk Miss $simpleName",
                                throwable)
                    }
                    .flatMap { m -> if (m.isEmpty()) cloud else Flowable.just(m) }
                    .onErrorResumeNext { _: Throwable -> cloud }
            result = when {
                withCache -> mDataStoreFactory.memory()!!.getAllItems(dataClass)
                        .doOnSuccess { Log.d(GET_LIST_OFFLINE_FIRST, CACHE_HIT + simpleName) }
                        .doOnError { Log.d(GET_LIST_OFFLINE_FIRST, CACHE_MISS + simpleName) }
                        .toFlowable()
                        .onErrorResumeNext { _: Throwable -> if (withDisk) disk else cloud }
                withDisk -> disk
                else -> cloud
            }
        } catch (e: IllegalAccessException) {
            result = Flowable.error(e)
        }

        return result.compose(ReplayingShare.instance()).compose(applySchedulers())
    }

    override fun <M> getObjectOffLineFirst(getRequest: GetRequest): Flowable<M> {
        var result: Flowable<M>
        try {
            val itemId = getRequest.itemId
            val dataClass = getRequest.getTypedDataClass<M>()
            val idType = getRequest.idType
            val idColumnName = getRequest.idColumnName
            val simpleName = dataClass.simpleName
            val persist = getRequest.persist
            val shouldCache = getRequest.cache
            val withDisk = withDisk(persist)
            val withCache = withCache(shouldCache)
            val cloud = mDataStoreFactory.cloud(dataClass)
                    .dynamicGetObject(getRequest.getCorrectUrl(), idColumnName, itemId, idType, dataClass,
                            persist, shouldCache)
                    .doOnNext { Log.d(GET_OBJECT_OFFLINE_FIRST, "Cloud Hit $simpleName") }
            val disk = mDataStoreFactory.disk(dataClass)
                    .dynamicGetObject("", idColumnName, itemId, idType, dataClass, persist, shouldCache)
                    .doOnNext { Log.d(GET_OBJECT_OFFLINE_FIRST, "Disk Hit $simpleName") }
                    .doOnError { throwable ->
                        Log.e(GET_OBJECT_OFFLINE_FIRST, "Disk Miss $simpleName",
                                throwable)
                    }
                    .onErrorResumeNext { _: Throwable -> cloud }
            result = when {
                withCache -> mDataStoreFactory.memory()!!
                        .getItem<M>(itemId.toString(), dataClass)
                        .doOnSuccess { Log.d(GET_OBJECT_OFFLINE_FIRST, CACHE_HIT + simpleName) }
                        .doOnError { Log.d(GET_OBJECT_OFFLINE_FIRST, CACHE_MISS + simpleName) }
                        .toFlowable()
                        .onErrorResumeNext { _: Throwable -> if (withDisk) disk else cloud }
                withDisk -> disk
                else -> cloud
            }
        } catch (e: IllegalAccessException) {
            result = Flowable.error(e)
        }

        return result.compose(ReplayingShare.instance()).compose(applySchedulers())
    }

    override fun <M> uploadFile(fileIORequest: FileIORequest): Flowable<M> {
        return fileIORequest.dataClass?.let {
            fileIORequest.keyFileMap?.let { it1 ->
                mDataStoreFactory.cloud(it)
                        .dynamicUploadFile<M>(fileIORequest.url, it1,
                                fileIORequest.parameters, fileIORequest.onWifi,
                                fileIORequest.whileCharging, fileIORequest.queuable,
                                fileIORequest.getTypedResponseClass<M>())
                        .compose(applySchedulers())
            }
        }!!
    }

    override fun downloadFile(fileIORequest: FileIORequest): Flowable<File> {
        return fileIORequest.dataClass?.let {
            fileIORequest.file?.let { it1 ->
                mDataStoreFactory.cloud(it)
                        .dynamicDownloadFile(fileIORequest.url, it1,
                                fileIORequest.onWifi, fileIORequest.whileCharging,
                                fileIORequest.queuable).compose(applySchedulers())
            }
        }!!
    }

    /**
     * Apply the default android schedulers to a observable
     *
     * @param <M> the current observable
     * @return the transformed observable
    </M> */
    private fun <M> applySchedulers(): FlowableTransformer<M, M> {
        return if (mPostThreadExist)
            FlowableTransformer {
                it.subscribeOn(mBackgroundThread).observeOn(mPostExecutionThread!!).unsubscribeOn(mBackgroundThread)
            }
        else
            FlowableTransformer {
                it.subscribeOn(mBackgroundThread).unsubscribeOn(mBackgroundThread)
            }
    }

    companion object {

        private const val CACHE_HIT = "cache Hit "
        private const val CACHE_MISS = "cache Miss "
        private const val GET_LIST_OFFLINE_FIRST = "getListOffLineFirst"
        private const val GET_OBJECT_OFFLINE_FIRST = "getObjectOffLineFirst"
    }
}
