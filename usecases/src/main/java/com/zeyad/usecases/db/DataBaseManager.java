package com.zeyad.usecases.db;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.realm.RealmModel;

/**
 * Interface for the Database modules.
 */
public interface DataBaseManager {

    /**
     * Gets an {@link Flowable} which will emit an Object.
     *
     * @param clazz        Class type of the items to get.
     * @param idColumnName Name of the id field.
     * @param userId       The user id to retrieve data.
     */
    @NonNull
    <M> Flowable<M> getById(String idColumnName, int userId, Class clazz);

    /**
     * Gets an {@link Flowable} which will emit a List of Objects.
     *
     * @param clazz Class type of the items to get.
     */
    @NonNull
    <M> Flowable<List<M>> getAll(Class clazz);

    /**
     * Puts and element into the DB.
     *
     * @param realmModel Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    @NonNull
    <M extends RealmModel> Completable put(M realmModel, Class dataClass);

    /**
     * Puts and element into the DB.
     *
     * @param jsonObject Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    @NonNull
    Completable put(JSONObject jsonObject, String idColumnName, Class dataClass);

    /**
     * Puts and element into the DB.
     *
     * @param realmObjects Element to insert in the DB.
     * @param dataClass    Class type of the items to be put.
     */
    @NonNull
    <M extends RealmModel> Completable putAll(List<M> realmObjects, Class dataClass);

    /**
     * Puts and element into the DB.
     *
     * @param jsonArray    Element to insert in the DB.
     * @param idColumnName Name of the id field.
     * @param dataClass    Class type of the items to be put.
     */
    @NonNull
    Completable putAll(JSONArray jsonArray, String idColumnName, Class dataClass);

    /**
     * Evict all elements of the DB.
     *
     * @param clazz Class type of the items to be deleted.
     */
    @NonNull
    Completable evictAll(Class clazz);

    /**
     * Evict element by id of the DB.
     *
     * @param clazz        Class type of the items to be deleted.
     * @param idFieldName  The id used to look for inside the DB.
     * @param idFieldValue Name of the id field.
     */
    boolean evictById(Class clazz, String idFieldName, long idFieldValue);

    /**
     * Evict a collection elements of the DB.
     *
     * @param idFieldName The id used to look for inside the DB.
     * @param list        List of ids to be deleted.
     * @param dataClass   Class type of the items to be deleted.
     */
    @NonNull
    Completable evictCollection(String idFieldName, List<Long> list, Class dataClass);

    /**
     * Get list of items according to the query passed.
     *
     * @param queryFactory The query used to look for inside the DB.
     */
    @NonNull
    <M extends RealmModel> Flowable<List<M>> getQuery(RealmQueryProvider<M> queryFactory);
}