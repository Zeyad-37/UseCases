package com.zeyad.usecases.api

import android.util.Log
import com.zeyad.usecases.db.RealmQueryProvider
import com.zeyad.usecases.requests.FileIORequest
import com.zeyad.usecases.requests.GetRequest
import com.zeyad.usecases.requests.PostRequest
import com.zeyad.usecases.stores.DataStoreFactory
import com.zeyad.usecases.utils.ReplayingShare
import com.zeyad.usecases.utils.Utils
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.Scheduler
import io.reactivex.Single
import io.realm.RealmModel
import org.json.JSONException
import java.io.File

/**
 * @author by ZIaDo on 5/9/17.
 */
internal class DataService(private val mDataStoreFactory: DataStoreFactory, private val mPostExecutionThread: Scheduler?, private val mBackgroundThread: Scheduler) : IDataService {
    private val mPostThreadExist: Boolean = mPostExecutionThread != null

    override fun <M> getList(getListRequest: GetRequest<M>): Flowable<List<M>> {
        var result: Flowable<List<M>>
        try {
            val dataClass = getListRequest.dataClass
            val url = getListRequest.url
            val simpleName = dataClass.simpleName
            val shouldCache = getListRequest.isShouldCache
            val dynamicGetList = mDataStoreFactory.dynamically(url, dataClass)
                    .dynamicGetList<M>(url, getListRequest.idColumnName, dataClass,
                            getListRequest.isPersist, shouldCache)
            result = if (Utils.getInstance().withCache(shouldCache)) {
                mDataStoreFactory.memory()!!.getAllItems<M>(dataClass)
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

    override fun <M> getObject(getRequest: GetRequest<M>): Flowable<M> {
        var result: Flowable<M>
        try {
            val itemId = getRequest.itemId
            val dataClass = getRequest.dataClass
            val shouldCache = getRequest.isShouldCache
            val url = getRequest.url
            val simpleName = dataClass.simpleName
            val dynamicGetObject = mDataStoreFactory.dynamically(url, dataClass)
                    .dynamicGetObject<M>(url, getRequest.idColumnName, itemId, getRequest.idType,
                            dataClass, getRequest.isPersist, shouldCache)
            result = if (Utils.getInstance().withCache(shouldCache)) {
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
            mDataStoreFactory.dynamically(postRequest.url, postRequest.requestType)
                    .dynamicPatchObject(postRequest.url, postRequest.idColumnName,
                            postRequest.idType, postRequest.objectBundle,
                            postRequest.requestType, postRequest.responseType,
                            postRequest.isPersist, postRequest.isCache, postRequest.isQueuable)
        } catch (e: IllegalAccessException) {
            Flowable.error(e)
        } catch (e: JSONException) {
            Flowable.error(e)
        }

        return result.compose(applySchedulers())
    }

    override fun <M> postObject(postRequest: PostRequest): Flowable<M> {
        val result: Flowable<M> = try {
            mDataStoreFactory.dynamically(postRequest.url, postRequest.requestType)
                    .dynamicPostObject(postRequest.url, postRequest.idColumnName,
                            postRequest.idType, postRequest.objectBundle,
                            postRequest.requestType, postRequest.responseType,
                            postRequest.isPersist, postRequest.isCache, postRequest.isQueuable)
        } catch (e: IllegalAccessException) {
            Flowable.error(e)
        } catch (e: JSONException) {
            Flowable.error(e)
        }

        return result.compose(applySchedulers())
    }

    override fun <M> postList(postRequest: PostRequest): Flowable<M> {
        val result: Flowable<M> = try {
            mDataStoreFactory.dynamically(postRequest.url, postRequest.requestType)
                    .dynamicPostList(postRequest.url, postRequest.idColumnName,
                            postRequest.idType, postRequest.arrayBundle,
                            postRequest.requestType, postRequest.responseType,
                            postRequest.isPersist, postRequest.isCache, postRequest.isQueuable)
        } catch (e: IllegalAccessException) {
            Flowable.error(e)
        } catch (e: JSONException) {
            Flowable.error(e)
        }
        return result.compose(applySchedulers())
    }

    override fun <M> putObject(postRequest: PostRequest): Flowable<M> {
        val result: Flowable<M> = try {
            mDataStoreFactory.dynamically(postRequest.url, postRequest.requestType)
                    .dynamicPutObject(postRequest.url, postRequest.idColumnName,
                            postRequest.idType, postRequest.objectBundle,
                            postRequest.requestType, postRequest.responseType,
                            postRequest.isPersist, postRequest.isCache, postRequest.isQueuable)
        } catch (e: IllegalAccessException) {
            Flowable.error(e)
        } catch (e: JSONException) {
            Flowable.error(e)
        }

        return result.compose(applySchedulers())
    }

    override fun <M> putList(postRequest: PostRequest): Flowable<M> {
        val result: Flowable<M> = try {
            mDataStoreFactory.dynamically(postRequest.url, postRequest.requestType)
                    .dynamicPutList(postRequest.url, postRequest.idColumnName,
                            postRequest.idType, postRequest.arrayBundle,
                            postRequest.requestType, postRequest.responseType,
                            postRequest.isPersist, postRequest.isCache, postRequest.isQueuable)
        } catch (e: IllegalAccessException) {
            Flowable.error(e)
        } catch (e: JSONException) {
            Flowable.error(e)
        }
        return result.compose(applySchedulers())
    }

    override fun <M> deleteItemById(request: PostRequest): Flowable<M> {
        val builder = PostRequest.Builder(request.requestType, request.isPersist)
                .payLoad(listOf(request.getObject()))
                .idColumnName(request.idColumnName, request.idType)
                .responseType(request.responseType)
                .fullUrl(request.url)
        //        if (request.isQueuable()) {
        //            builder.queuable(request.isOnWifi(), request.isWhileCharging());
        //        }
        return deleteCollectionByIds(builder.build())
    }

    override fun <M> deleteCollectionByIds(deleteRequest: PostRequest): Flowable<M> {
        val result: Flowable<M> = try {
            mDataStoreFactory.dynamically(deleteRequest.url, deleteRequest.requestType)
                    .dynamicDeleteCollection<M>(deleteRequest.url, deleteRequest.idColumnName,
                            deleteRequest.idType, deleteRequest.arrayBundle,
                            deleteRequest.requestType, deleteRequest.responseType,
                            deleteRequest.isPersist, deleteRequest.isCache, deleteRequest.isQueuable)
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

    override fun <M : RealmModel> queryDisk(realmQueryProvider: RealmQueryProvider<M>): Flowable<List<M>> {
        val result: Flowable<List<M>> = try {
            mDataStoreFactory.disk(Any::class.java).queryDisk<M>(realmQueryProvider)
                    .compose(ReplayingShare.instance())
        } catch (e: IllegalAccessException) {
            Flowable.error(e)
        }
        return result.compose(applySchedulers())
    }

    override fun <M> getListOffLineFirst(getRequest: GetRequest<M>): Flowable<List<M>> {
        var result: Flowable<List<M>>
        try {
            val utils = Utils.getInstance()
            val dataClass = getRequest.dataClass
            val simpleName = dataClass.simpleName
            val idColumnName = getRequest.idColumnName
            val persist = getRequest.isPersist
            val shouldCache = getRequest.isShouldCache
            val withDisk = utils.withDisk(persist)
            val withCache = utils.withCache(shouldCache)
            val cloud = mDataStoreFactory.cloud(dataClass)
                    .dynamicGetList<M>(getRequest.url, idColumnName, dataClass, persist, shouldCache)
            val disk = mDataStoreFactory.disk(dataClass)
                    .dynamicGetList<M>("", idColumnName, dataClass, persist, shouldCache)
                    .doOnNext { Log.d(GET_LIST_OFFLINE_FIRST, "Disk Hit $simpleName") }
                    .doOnError { throwable ->
                        Log.e(GET_LIST_OFFLINE_FIRST, "Disk Miss $simpleName",
                                throwable)
                    }
                    .flatMap { m -> if (m.isEmpty()) cloud else Flowable.just(m) }
                    .onErrorResumeNext { _: Throwable -> cloud }
            result = when {
                withCache -> mDataStoreFactory.memory()!!.getAllItems<M>(dataClass)
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

    override fun <M> getObjectOffLineFirst(getRequest: GetRequest<M>): Flowable<M> {
        var result: Flowable<M>
        try {
            val utils = Utils.getInstance()
            val itemId = getRequest.itemId
            val dataClass = getRequest.dataClass
            val idType = getRequest.idType
            val idColumnName = getRequest.idColumnName
            val simpleName = dataClass.simpleName
            val persist = getRequest.isPersist
            val shouldCache = getRequest.isShouldCache
            val withDisk = utils.withDisk(persist)
            val withCache = utils.withCache(shouldCache)
            val cloud = mDataStoreFactory.cloud(dataClass)
                    .dynamicGetObject<M>(getRequest.url, idColumnName, itemId, idType, dataClass,
                            persist, shouldCache)
                    .doOnNext { Log.d(GET_OBJECT_OFFLINE_FIRST, "Cloud Hit $simpleName") }
            val disk = mDataStoreFactory.disk(dataClass)
                    .dynamicGetObject<M>("", idColumnName, itemId, idType, dataClass, persist, shouldCache)
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
        return mDataStoreFactory.cloud(fileIORequest.dataClass)
                .dynamicUploadFile<M>(fileIORequest.url, fileIORequest.keyFileMap,
                        fileIORequest.parameters, fileIORequest.isOnWifi,
                        fileIORequest.isWhileCharging, fileIORequest.isQueuable,
                        fileIORequest.dataClass)
                .compose(applySchedulers())
    }

    override fun downloadFile(fileIORequest: FileIORequest): Flowable<File> {
        return mDataStoreFactory.cloud(fileIORequest.dataClass)
                .dynamicDownloadFile(fileIORequest.url, fileIORequest.file,
                        fileIORequest.isOnWifi, fileIORequest.isWhileCharging,
                        fileIORequest.isQueuable).compose(applySchedulers())
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
