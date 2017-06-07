package com.zeyad.usecases.db;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.realm.RealmModel;

/**
 * Interface for the Database modules.
 */
public interface DataBaseManager {

    /**
     * Gets an {@link Flowable} which will emit an Object
     * @param idColumnName name of ID variable
     * @param itemId ID value
     * @param itemIdType type of the ID
     * @param dataClass type of the data requested
     * @param <M> type of the data requested
     * @return a {@link Flowable} containing an object of type M.
     */
    @NonNull
    <M> Flowable<M> getById(@NonNull final String idColumnName, final Object itemId,
                            final Class itemIdType, Class dataClass);

    /**
     * Gets an {@link Flowable} which will emit a List of Objects.
     *
     * @param clazz Class type of the items to get.
     */
    @NonNull
    <M> Flowable<List<M>> getAll(Class clazz);

    /**
     * Get list of items according to the query passed.
     *
     * @param queryFactory The query used to look for inside the DB.
     */
    @NonNull
    <M extends RealmModel> Flowable<List<M>> getQuery(RealmQueryProvider<M> queryFactory);

    /**
     * Puts and element into the DB.
     *
     * @param realmModel Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    @NonNull
    <M extends RealmModel> Single<Boolean> put(M realmModel, Class dataClass);

    /**
     * Puts and element into the DB.
     *
     * @param jsonObject Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    @NonNull
    Single<Boolean> put(JSONObject jsonObject, String idColumnName, Class dataClass);

    /**
     * Puts and element into the DB.
     *
     * @param realmObjects Element to insert in the DB.
     * @param dataClass    Class type of the items to be put.
     */
    @NonNull
    <M extends RealmModel> Single<Boolean> putAll(List<M> realmObjects, Class dataClass);

    /**
     * Puts and element into the DB.
     *
     * @param jsonArray    Element to insert in the DB.
     * @param idColumnName Name of the id field.
     * @param dataClass    Class type of the items to be put.
     */
    @NonNull
    Single<Boolean> putAll(JSONArray jsonArray, String idColumnName, Class dataClass);

    /**
     * Evict all elements of the DB.
     *
     * @param clazz Class type of the items to be deleted.
     */
    @NonNull
    Single<Boolean> evictAll(Class clazz);

    /**
     * Evict a collection elements of the DB.
     *
     * @param idFieldName The id used to look for inside the DB.
     * @param list        List of ids to be deleted.
     * @param dataClass   Class type of the items to be deleted.
     */
    @NonNull
    Flowable<Boolean> evictCollection(String idFieldName, List<Long> list, Class dataClass);

    /**
     * Evict element by id of the DB.
     *
     * @param clazz        Class type of the items to be deleted.
     * @param idFieldName  The id used to look for inside the DB.
     * @param idFieldValue Name of the id field.
     */
    boolean evictById(Class clazz, String idFieldName, long idFieldValue);
}

