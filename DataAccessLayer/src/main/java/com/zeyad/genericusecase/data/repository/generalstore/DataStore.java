package com.zeyad.genericusecase.data.repository.generalstore;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface DataStore {

    @NonNull
    Observable<List> dynamicGetList(final String url, Class domainClass, Class dataClass, boolean persist,
                                    boolean shouldCache);

    /**
     * Get an {@link Observable} which will emit a ? by its id.
     */
    @NonNull
    Observable<?> dynamicGetObject(final String url, final String idColumnName, final int itemId,
                                   Class domainClass, Class dataClass, boolean persist, boolean shouldCache);

    /**
     * Post a JSONObject which returns an {@link Observable} that will emit a ?.
     */
    @NonNull
    Observable<?> dynamicPostObject(final String url, String idColumnName, final JSONObject keyValuePairs, Class domainClass,
                                    Class dataClass, boolean persist);

    /**
     * Post a HashMap<String, Object> which returns an {@link Observable} that will emit a list of ?.
     */
    @NonNull
    Observable<?> dynamicPostList(final String url, String idColumnName, final JSONArray jsonArray,
                                  Class domainClass, Class dataClass, boolean persist);

    /**
     * Put a HashMap<String, Object> disk with a RealmQuery which returns an {@link Observable}
     * that will emit a ?.
     */
    @NonNull
    Observable<?> dynamicPutObject(final String url, String idColumnName, final JSONObject keyValuePairs,
                                   Class domainClass, Class dataClass, boolean persist);

    Observable<?> dynamicUploadFile(final String url, final File file, boolean onWifi, boolean whileCharging,
                                    Class domainClass);

    /**
     * Put a HashMap<String, Object> disk with a RealmQuery which returns an {@link Observable}
     * that will emit a list of ?.
     */
    Observable<?> dynamicPutList(final String url, String idColumnName, final JSONArray jsonArray,
                                 Class domainClass, Class dataClass, boolean persist);

    /**
     * Delete a HashMap<String, Object> from cloud which returns an {@link Observable} that will emit a ?.
     */
    @NonNull
    Observable<?> dynamicDeleteCollection(final String url, String idColumnName, final JSONArray jsonArray,
                                          Class dataClass, boolean persist);

    /**
     * Delete all items of the same type from cloud or disk which returns an {@link Observable}
     * that will emit a list of ?.
     */
    @NonNull
    Observable<Boolean> dynamicDeleteAll(String url, Class dataClass, boolean persist);

    /**
     * Search disk with a query which returns an {@link Observable} that will emit a list of ?.
     */
    @NonNull
    Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass);

    /**
     * Search disk with a RealmQuery which returns an {@link Observable} that will emit a list of ?.
     */
    @NonNull
    Observable<List> searchDisk(RealmQuery query, Class domainClass);

    Observable<?> dynamicDownloadFile(String url, File file, boolean onWifi, boolean whileCharging);
}
