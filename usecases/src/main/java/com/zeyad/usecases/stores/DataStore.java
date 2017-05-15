package com.zeyad.usecases.stores;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.db.RealmQueryProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import rx.Completable;
import rx.Observable;

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface DataStore {
    Gson gson = Config.getGson();

    @NonNull
    Observable<List> dynamicGetList(String url, Class dataClass, boolean persist,
                                    boolean shouldCache);

    /**
     * Get an {@link Observable} which will emit a ? by its id.
     */
    @NonNull
    Observable<?> dynamicGetObject(String url, String idColumnName, int itemId,
                                   Class dataClass, boolean persist, boolean shouldCache);

    /**
     * Patch a JSONObject which returns an {@link Observable} that will emit a ?.
     */
    Observable<?> dynamicPatchObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                     Class dataClass, boolean persist, boolean queuable);

    /**
     * Post a JSONObject which returns an {@link Observable} that will emit a ?.
     */
    @NonNull
    Observable<?> dynamicPostObject(String url, String idColumnName, JSONObject keyValuePairs,
                                    Class dataClass, boolean persist, boolean queuable);

    /**
     * Post a HashMap<String, Object> which returns an {@link Observable} that will emit a list of ?.
     */
    @NonNull
    Observable<?> dynamicPostList(String url, String idColumnName, JSONArray jsonArray,
                                  Class dataClass, boolean persist, boolean queuable);

    /**
     * Put a HashMap<String, Object> disk with a RealmQuery which returns an {@link Observable}
     * that will emit a ?.
     */
    @NonNull
    Observable<?> dynamicPutObject(String url, String idColumnName, JSONObject keyValuePairs,
                                   Class dataClass, boolean persist, boolean queuable);

    /**
     * Put a HashMap<String, Object> disk with a RealmQuery which returns an {@link Observable}
     * that will emit a list of ?.
     */
    @NonNull
    Observable<?> dynamicPutList(String url, String idColumnName, JSONArray jsonArray,
                                 Class dataClass, boolean persist, boolean queuable);

    /**
     * Delete a HashMap<String, Object> from cloud which returns an {@link Observable} that will emit a ?.
     */
    @NonNull
    Observable<?> dynamicDeleteCollection(String url, String idColumnName, JSONArray jsonArray,
                                          Class dataClass, boolean persist, boolean queuable);

    /**
     * Delete all items of the same type from cloud or disk which returns an {@link Completable}
     * that will emit a list of ?.
     */
    @NonNull
    Completable dynamicDeleteAll(Class dataClass);

    /**
     * Search disk with a RealmQuery which returns an {@link Observable} that will emit a list of ?.
     */
    @NonNull
    Observable<List> queryDisk(RealmQueryProvider queryFactory);

    @NonNull
    Observable<?> dynamicDownloadFile(String url, File file, boolean onWifi, boolean whileCharging,
                                      boolean queuable);

    @NonNull
    Observable<?> dynamicUploadFile(String url, File file, String key, HashMap<String, Object> parameter,
                                    boolean onWifi, boolean whileCharging, boolean queuable, Class domainClass);
}
