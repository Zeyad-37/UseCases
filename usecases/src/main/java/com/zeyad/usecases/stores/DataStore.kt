package com.zeyad.usecases.stores

import com.zeyad.usecases.db.RealmQueryProvider
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.realm.RealmModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

/**
 * Interface that represents a data store from where data is retrieved.
 */
interface DataStore {

    fun <M> dynamicGetList(url: String, idColumnName: String, requestType: Class<M>,
                           persist: Boolean, shouldCache: Boolean): Flowable<List<M>>

    /**
     * Get an [Flowable] which will emit a Object by its id.
     */
    fun <M> dynamicGetObject(url: String, idColumnName: String, itemId: Any, itemIdType: Class<*>,
                             requestType: Class<M>, persist: Boolean, shouldCache: Boolean): Flowable<M>

    /**
     * Search disk with a RealmQuery which returns an [Flowable] that will emit a list of
     * Object.
     */
    fun <M : RealmModel> queryDisk(queryFactory: RealmQueryProvider<M>): Flowable<List<M>>

    /**
     * Patch a JSONObject which returns an [Flowable] that will emit a Object.
     */
    fun <M> dynamicPatchObject(url: String, idColumnName: String, itemIdType: Class<*>,
                               jsonObject: JSONObject, requestType: Class<*>,
                               responseType: Class<M>, persist: Boolean, cache: Boolean,
                               queuable: Boolean): Flowable<M>

    /**
     * Post a JSONObject which returns an [Flowable] that will emit a Object.
     */
    fun <M> dynamicPostObject(url: String, idColumnName: String, itemIdType: Class<*>,
                              jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                              persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M>

    /**
     * Post a HashMap<String></String>, Object> which returns an [Flowable] that will emit a list of
     * Object.
     */
    fun <M> dynamicPostList(url: String, idColumnName: String, itemIdType: Class<*>,
                            jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                            persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M>

    /**
     * Put a HashMap<String></String>, Object> disk with a RealmQuery which returns an [Flowable] that
     * will emit a Object.
     */
    fun <M> dynamicPutObject(url: String, idColumnName: String, itemIdType: Class<*>,
                             jsonObject: JSONObject, requestType: Class<*>, responseType: Class<M>,
                             persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M>

    /**
     * Put a HashMap<String></String>, Object> disk with a RealmQuery which returns an [Flowable] that
     * will emit a list of Object.
     */
    fun <M> dynamicPutList(url: String, idColumnName: String, itemIdType: Class<*>,
                           jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                           persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M>

    /**
     * Delete a HashMap<String></String>, Object> from cloud which returns an [Flowable] that will emit
     * a Object.
     */
    fun <M> dynamicDeleteCollection(url: String, idColumnName: String, itemIdType: Class<*>,
                                    jsonArray: JSONArray, requestType: Class<*>, responseType: Class<M>,
                                    persist: Boolean, cache: Boolean, queuable: Boolean): Flowable<M>

    /**
     * Delete all items of the same type from cloud or disk which returns an [Completable]
     * that will emit a list of Object.
     */
    fun dynamicDeleteAll(requestType: Class<*>): Single<Boolean>

    fun dynamicDownloadFile(url: String, file: File, onWifi: Boolean, whileCharging: Boolean,
                            queuable: Boolean): Flowable<File>

    fun <M> dynamicUploadFile(url: String, keyFileMap: HashMap<String, File>, parameters: HashMap<String, Any>,
                              onWifi: Boolean, whileCharging: Boolean, queuable: Boolean,
                              responseType: Class<M>): Flowable<M>
}
