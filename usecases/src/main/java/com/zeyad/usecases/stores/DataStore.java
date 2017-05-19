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
    <M> Observable<List<M>> dynamicGetList(String url, Class dataClass, boolean persist, boolean shouldCache);

    /**
     * Get an {@link Observable} which will emit a Object by its id.
     */
    @NonNull
    <M> Observable<M> dynamicGetObject(String url, String idColumnName, int itemId,
                                       Class dataClass, boolean persist, boolean shouldCache);

    /**
     * Search disk with a RealmQuery which returns an {@link Observable} that will emit a list of Object.
     */
    @NonNull
    <M> Observable<List<M>> queryDisk(RealmQueryProvider queryFactory);

    /**
     * Patch a JSONObject which returns an {@link Observable} that will emit a Object.
     */
    <M> Observable<M> dynamicPatchObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                         Class dataClass, boolean persist, boolean queuable);

    /**
     * Post a JSONObject which returns an {@link Observable} that will emit a Object.
     */
    @NonNull
    <M> Observable<M> dynamicPostObject(String url, String idColumnName, JSONObject keyValuePairs,
                                        Class dataClass, boolean persist, boolean queuable);

    /**
     * Post a HashMap<String, Object> which returns an {@link Observable} that will emit a list of Object.
     */
    @NonNull
    <M> Observable<M> dynamicPostList(String url, String idColumnName, JSONArray jsonArray,
                                      Class dataClass, boolean persist, boolean queuable);

    /**
     * Put a HashMap<String, Object> disk with a RealmQuery which returns an {@link Observable}
     * that will emit a Object.
     */
    @NonNull
    <M> Observable<M> dynamicPutObject(String url, String idColumnName, JSONObject keyValuePairs,
                                       Class dataClass, boolean persist, boolean queuable);

    /**
     * Put a HashMap<String, Object> disk with a RealmQuery which returns an {@link Observable}
     * that will emit a list of Object.
     */
    @NonNull
    <M> Observable<M> dynamicPutList(String url, String idColumnName, JSONArray jsonArray,
                                     Class dataClass, boolean persist, boolean queuable);

    /**
     * Delete a HashMap<String, Object> from cloud which returns an {@link Observable} that will emit a Object.
     */
    @NonNull
    <M> Observable<M> dynamicDeleteCollection(String url, String idColumnName, JSONArray jsonArray,
                                              Class dataClass, boolean persist, boolean queuable);

    /**
     * Delete all items of the same type from cloud or disk which returns an {@link Completable}
     * that will emit a list of Object.
     */
    @NonNull
    Completable dynamicDeleteAll(Class dataClass);

    @NonNull
    Observable<File> dynamicDownloadFile(String url, File file, boolean onWifi, boolean whileCharging,
                                         boolean queuable);

    @NonNull
    <M> Observable<M> dynamicUploadFile(String url, File file, String key, HashMap<String, Object> parameter,
                                        boolean onWifi, boolean whileCharging, boolean queuable, Class domainClass);
}
