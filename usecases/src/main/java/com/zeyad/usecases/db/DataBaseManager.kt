package com.zeyad.usecases.db

import io.reactivex.Flowable
import io.reactivex.Single
import org.json.JSONArray
import org.json.JSONObject

/**
 * Interface for the ServiceDatabase modules.
 */
interface DataBaseManager {

    /**
     * Gets an [Flowable] which will emit an Object
     * @param idColumnName name of ID variable
     * @param itemId ID value
     * @param clazz type of the data requested
     * @param <E> type of the data requested
     * @return a [Flowable] containing an object of type E.
    </E> */
    fun <E> getById(idColumnName: String, itemId: Any, clazz: Class<E>): Flowable<E>

    /**
     * Gets an [Flowable] which will emit a List of Objects.
     *
     * @param clazz Class type of the items to get.
     */
    fun <E> getAll(clazz: Class<E>): Flowable<List<E>>

    /**
     * Get list of items according to the query passed.
     *
     * @param query The query used to look for inside the DB.
     */
    fun <E> getQuery(query: String, clazz: Class<E>): Flowable<List<E>>

    /**
     * Puts and element into the DB.
     *
     * @param entity Element to insert in the DB.
     */
    fun <E> put(entity: E, clazz: Class<E>): Single<Any>

    /**
     * Puts and element into the DB.
     *
     * @param jsonObject Element to insert in the DB.
     * @param clazz  Class type of the items to be put.
     */
    fun <E> put(jsonObject: JSONObject, clazz: Class<E>): Single<Any>

    /**
     * Puts and element into the DB.
     *
     * @param entities Element to insert in the DB.
     * @param clazz    Class type of the items to be put.
     */
    fun <E> putAll(entities: List<E>, clazz: Class<E>): Single<Any>

    /**
     * Puts and element into the DB.
     *
     * @param jsonArray    Element to insert in the DB.
     * @param clazz    Class type of the items to be put.
     */
    fun <E> putAll(jsonArray: JSONArray, clazz: Class<E>): Single<Any>

    /**
     * Evict all elements of the DB.
     *
     * @param clazz Class type of the items to be deleted.
     */
    fun <E> evictAll(clazz: Class<E>): Single<Boolean>

    /**
     * Evict a collection elements of the DB.
     *
     * @param list        List to be deleted.
     * @param clazz   Class type of the items to be deleted.
     */
    fun <E> evictCollection(list: List<E>, clazz: Class<E>): Single<Boolean>

    /**
     * Evict a collection elements of the DB.
     *
     * @param list        List of ids to be deleted.
     * @param clazz   Class type of the items to be deleted.
     */
    fun <E> evictCollectionById(list: List<Any>, clazz: Class<E>, idFieldName: String): Single<Boolean>

    /**
     * Evict element by id of the DB.
     *
     * @param clazz        Class type of the items to be deleted.
     * @param idFieldName  The id used to look for inside the DB.
     * @param idFieldValue Name of the id field.
     */
    fun <E> evictById(clazz: Class<E>, idFieldName: String, idFieldValue: Any): Single<Boolean>
}

