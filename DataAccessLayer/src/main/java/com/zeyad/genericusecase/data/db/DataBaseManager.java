package com.zeyad.genericusecase.data.db;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import rx.Observable;

/**
 * Interface for the Database modules.
 */
public interface DataBaseManager {

    String COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE = "collection_last_cache_update",
            DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE = "detail_last_cache_update";

    /**
     * Gets an {@link Observable} which will emit an Object.
     *
     * @param clazz        Class type of the items to get.
     * @param idColumnName Name of the id field.
     * @param userId       The user id to retrieve data.
     */
    @NonNull
    Observable<?> getById(final String idColumnName, final int userId, Class clazz);

    /**
     * Gets an {@link Observable} which will emit a List of Objects.
     *
     * @param clazz Class type of the items to get.
     */
    @NonNull
    Observable<List> getAll(Class clazz);

    /**
     * Puts and element into the DB.
     *
     * @param realmObject Element to insert in the DB.
     * @param dataClass   Class type of the items to be put.
     */
    @NonNull
    Observable<?> put(RealmObject realmObject, Class dataClass);

    /**
     * Puts and element into the DB.
     *
     * @param realmModel Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    @NonNull
    Observable<?> put(RealmModel realmModel, Class dataClass);

    /**
     * Puts and element into the DB.
     *
     * @param jsonObject Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    @NonNull
    Observable<?> put(JSONObject jsonObject, String idColumnName, Class dataClass);

    /**
     * Puts and element into the DB.
     *
     * @param realmObjects Element to insert in the DB.
     * @param dataClass    Class type of the items to be put.
     */
    void putAll(List<RealmObject> realmObjects, Class dataClass);

    /**
     * Puts and element into the DB.
     *
     * @param jsonArray    Element to insert in the DB.
     * @param idColumnName Name of the id field.
     * @param dataClass    Class type of the items to be put.
     */
    @NonNull
    Observable<?> putAll(JSONArray jsonArray, String idColumnName, Class dataClass);

    /**
     * Checks if an element (User) exists in the DB.
     *
     * @param itemId   The id used to look for inside the DB.
     * @param columnId Name of the id field.
     * @param clazz    Class type of the items to check.
     * @return true if the element is cached, otherwise false.
     */
    boolean isCached(final int itemId, String columnId, Class clazz);

    /**
     * Checks if the DB is expired.
     *
     * @param itemId   The id used to look for inside the DB.
     * @param columnId Name of the id field.
     * @param clazz    Class type of the items to check.
     * @return true, the DB is expired, otherwise false.
     */
    boolean isItemValid(final int itemId, String columnId, Class clazz);

    /**
     * Checks if the DB is expired.
     *
     * @param destination Name DB destination.
     * @return true, the DB is expired, otherwise false.
     */
    boolean areItemsValid(String destination);

    /**
     * Evict all elements of the DB.
     *
     * @param clazz Class type of the items to be deleted.
     */
    @NonNull
    Observable<Boolean> evictAll(Class clazz);

    /**
     * Evict element of the DB.
     *
     * @param realmModel Element to deleted from the DB.
     * @param clazz      Class type of the items to be deleted.
     */
    void evict(final RealmObject realmModel, Class clazz);

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
    Observable<?> evictCollection(String idFieldName, List<Long> list, Class dataClass);

    /**
     * Get list of items according to the query passed.
     *
     * @param filterKey The key used to look for inside the DB.
     * @param query     The query used to look for inside the DB.
     * @param clazz     Class type of the items to be deleted.
     */
    @NonNull
    Observable<List> getWhere(Class clazz, String query, String filterKey);

    /**
     * Get list of items according to the query passed.
     *
     * @param realmQuery The query used to look for inside the DB.
     */
    @NonNull
    Observable<List> getWhere(RealmQuery realmQuery);

    /**
     * @return application Context.
     */
    Context getContext();

    void closeRealm();
}