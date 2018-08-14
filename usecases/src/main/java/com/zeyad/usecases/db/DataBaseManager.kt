package com.zeyad.usecases.db

import io.reactivex.Flowable
import io.reactivex.Single
import io.realm.RealmModel
import org.json.JSONArray
import org.json.JSONObject

/**
 * Interface for the Database modules.
 */
interface DataBaseManager {

    /**
     * Gets an [Flowable] which will emit an Object
     * @param idColumnName name of ID variable
     * @param itemId ID value
     * @param itemIdType type of the ID
     * @param dataClass type of the data requested
     * @param <M> type of the data requested
     * @return a [Flowable] containing an object of type M.
    </M> */
    fun <M : RealmModel> getById(idColumnName: String, itemId: Any,
                                 itemIdType: Class<*>, dataClass: Class<M>): Flowable<M>

    /**
     * Gets an [Flowable] which will emit a List of Objects.
     *
     * @param clazz Class type of the items to get.
     */
    fun <M : RealmModel> getAll(clazz: Class<M>): Flowable<List<M>>

    /**
     * Get list of items according to the query passed.
     *
     * @param queryFactory The query used to look for inside the DB.
     */
    fun <M : RealmModel> getQuery(queryFactory: RealmQueryProvider<M>): Flowable<List<M>>

    /**
     * Puts and element into the DB.
     *
     * @param jsonObject Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    fun <M : RealmModel> put(jsonObject: JSONObject, idColumnName: String, itemIdType: Class<*>,
                             dataClass: Class<M>): Single<Boolean>

    /**
     * Puts and element into the DB.
     *
     * @param realmObjects Element to insert in the DB.
     * @param dataClass    Class type of the items to be put.
     */
    fun <M : RealmModel> putAll(realmObjects: List<M>, dataClass: Class<M>): Single<Boolean>

    /**
     * Puts and element into the DB.
     *
     * @param jsonArray    Element to insert in the DB.
     * @param idColumnName Name of the id field.
     * @param dataClass    Class type of the items to be put.
     */
    fun <M : RealmModel> putAll(jsonArray: JSONArray, idColumnName: String, itemIdType: Class<*>,
                                dataClass: Class<M>): Single<Boolean>

    /**
     * Evict all elements of the DB.
     *
     * @param clazz Class type of the items to be deleted.
     */
    fun <M : RealmModel> evictAll(clazz: Class<M>): Single<Boolean>

    /**
     * Evict a collection elements of the DB.
     *
     * @param idFieldName The id used to look for inside the DB.
     * @param list        List of ids to be deleted.
     * @param dataClass   Class type of the items to be deleted.
     */
    fun <M : RealmModel> evictCollection(idFieldName: String, list: List<Any>, itemIdType: Class<*>,
                                         dataClass: Class<M>): Single<Boolean>

    /**
     * Evict element by id of the DB.
     *
     * @param clazz        Class type of the items to be deleted.
     * @param idFieldName  The id used to look for inside the DB.
     * @param idFieldValue Name of the id field.
     */
    fun <M : RealmModel> evictById(clazz: Class<M>, idFieldName: String, idFieldValue: Any,
                                   itemIdType: Class<*>): Boolean
}

