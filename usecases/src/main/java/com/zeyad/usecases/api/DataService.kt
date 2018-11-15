package com.zeyad.usecases.api

import android.util.Log
import com.jakewharton.rx.ReplayingShare
import com.zeyad.usecases.requests.FileIORequest
import com.zeyad.usecases.requests.GetRequest
import com.zeyad.usecases.requests.PostRequest
import com.zeyad.usecases.stores.DataStoreFactory
import com.zeyad.usecases.withCache
import com.zeyad.usecases.withDisk
import io.reactivex.*
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
            val url = getListRequest.fullUrl
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
            val url = getRequest.fullUrl
            val simpleName = dataClass.simpleName
            val dynamicGetObject = mDataStoreFactory.dynamically(url, dataClass)
                    .dynamicGetObject(url, getRequest.idColumnName, itemId, dataClass,
                            getRequest.persist, shouldCache)
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

    override fun <M> patchObject(postRequest: PostRequest): Single<M> {
        val result: Single<M> = try {
            mDataStoreFactory.dynamically(postRequest.fullUrl, postRequest.requestType)
                    .dynamicPatchObject(postRequest.fullUrl, postRequest.idColumnName,
                            postRequest.getObjectBundle(), postRequest.requestType,
                            postRequest.getTypedResponseClass(), postRequest.persist,
                            postRequest.cache)
        } catch (e: IllegalAccessException) {
            Single.error(e)
        } catch (e: JSONException) {
            Single.error(e)
        }

        return result.compose(applySingleSchedulers())
    }

    override fun <M> postObject(postRequest: PostRequest): Single<M> {
        val result: Single<M> = try {
            mDataStoreFactory.dynamically(postRequest.fullUrl, postRequest.requestType)
                    .dynamicPostObject(postRequest.fullUrl, postRequest.idColumnName,
                            postRequest.getObjectBundle(), postRequest.requestType,
                            postRequest.getTypedResponseClass(), postRequest.persist, postRequest.cache)
        } catch (e: IllegalAccessException) {
            Single.error(e)
        } catch (e: JSONException) {
            Single.error(e)
        }

        return result.compose(applySingleSchedulers())
    }

    override fun <M> postList(postRequest: PostRequest): Single<M> {
        val result: Single<M> = try {
            mDataStoreFactory.dynamically(postRequest.fullUrl, postRequest.requestType)
                    .dynamicPostList(postRequest.fullUrl, postRequest.idColumnName,
                            postRequest.getArrayBundle(), postRequest.requestType,
                            postRequest.getTypedResponseClass(), postRequest.persist, postRequest.cache)
        } catch (e: Exception) {
            Single.error(e)
        } catch (e: JSONException) {
            Single.error(e)
        }
        return result.compose(applySingleSchedulers())
    }

    override fun <M> putObject(postRequest: PostRequest): Single<M> {
        val result: Single<M> = try {
            mDataStoreFactory.dynamically(postRequest.fullUrl, postRequest.requestType)
                    .dynamicPutObject(postRequest.fullUrl, postRequest.idColumnName,
                            postRequest.getObjectBundle(), postRequest.requestType,
                            postRequest.getTypedResponseClass(), postRequest.persist, postRequest.cache)
        } catch (e: IllegalAccessException) {
            Single.error(e)
        } catch (e: JSONException) {
            Single.error(e)
        }

        return result.compose(applySingleSchedulers())
    }

    override fun <M> putList(postRequest: PostRequest): Single<M> {
        val result: Single<M> = try {
            mDataStoreFactory.dynamically(postRequest.fullUrl, postRequest.requestType)
                    .dynamicPutList(postRequest.fullUrl, postRequest.idColumnName,
                            postRequest.getArrayBundle(), postRequest.requestType,
                            postRequest.getTypedResponseClass(), postRequest.persist, postRequest.cache)
        } catch (e: IllegalAccessException) {
            Single.error(e)
        } catch (e: JSONException) {
            Single.error(e)
        }
        return result.compose(applySingleSchedulers())
    }

    override fun <M> deleteItemById(request: PostRequest): Single<M> {
        return deleteCollectionByIds(request)
    }

    override fun <M> deleteCollectionByIds(deleteRequest: PostRequest): Single<M> {
        val result: Single<M> = try {
            mDataStoreFactory.dynamically(deleteRequest.fullUrl, deleteRequest.requestType)
                    .dynamicDeleteCollection(deleteRequest.fullUrl, deleteRequest.idColumnName,
                            deleteRequest.idType, deleteRequest.getArrayBundle(),
                            deleteRequest.requestType, deleteRequest.getTypedResponseClass<M>(),
                            deleteRequest.persist, deleteRequest.cache)
                    .compose(applySingleSchedulers())
        } catch (e: IllegalAccessException) {
            Single.error(e)
        } catch (e: JSONException) {
            Single.error(e)
        }
        return result.compose(applySingleSchedulers())
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

    override fun <M> queryDisk(query: String, clazz: Class<M>): Single<M> {
        val result: Single<M> = try {
            mDataStoreFactory.disk(Any::class.java).queryDisk(query, clazz)
                    .compose(ReplayingShare.instance()).singleOrError()
        } catch (e: IllegalAccessException) {
            Single.error(e)
        }
        return result.compose(applySingleSchedulers())
    }

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
                    .dynamicGetList(getRequest.fullUrl, idColumnName, dataClass, persist, shouldCache)
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
            val idColumnName = getRequest.idColumnName
            val simpleName = dataClass.simpleName
            val persist = getRequest.persist
            val shouldCache = getRequest.cache
            val withDisk = withDisk(persist)
            val withCache = withCache(shouldCache)
            val cloud = mDataStoreFactory.cloud(dataClass)
                    .dynamicGetObject(getRequest.fullUrl, idColumnName, itemId, dataClass,
                            persist, shouldCache)
                    .doOnNext { Log.d(GET_OBJECT_OFFLINE_FIRST, "Cloud Hit $simpleName") }
            val disk = mDataStoreFactory.disk(dataClass)
                    .dynamicGetObject("", idColumnName, itemId, dataClass, persist, shouldCache)
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

    override fun <M> uploadFile(fileIORequest: FileIORequest): Single<M> {
        return fileIORequest.dataClass?.let {
            fileIORequest.keyFileMap?.let { it1 ->
                mDataStoreFactory.cloud(it)
                        .dynamicUploadFile(fileIORequest.url, it1, fileIORequest.parameters,
                                fileIORequest.getTypedResponseClass<M>())
                        .compose(applySingleSchedulers())
            }
        }!!
    }

    override fun downloadFile(fileIORequest: FileIORequest): Single<File> {
        return fileIORequest.dataClass?.let {
            fileIORequest.file?.let { it1 ->
                mDataStoreFactory.cloud(it)
                        .dynamicDownloadFile(fileIORequest.url, it1).compose(applySingleSchedulers())
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

    private fun <M> applySingleSchedulers(): SingleTransformer<M, M> {
        return if (mPostThreadExist)
            SingleTransformer {
                it.subscribeOn(mBackgroundThread).observeOn(mPostExecutionThread!!).unsubscribeOn(mBackgroundThread)
            }
        else
            SingleTransformer {
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
