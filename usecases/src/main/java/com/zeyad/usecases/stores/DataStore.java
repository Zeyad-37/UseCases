package com.zeyad.usecases.stores;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.db.RealmQueryProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface DataStore {
    Gson gson = Config.getGson();

    @NonNull
    <M> Flowable<List<M>> dynamicGetList(
            String url, Class requestType, boolean persist, boolean shouldCache);

    /**
     * Get an {@link Flowable} which will emit a Object by its id.
     */
    @NonNull
    <M> Flowable<M> dynamicGetObject(
            String url,
            String idColumnName,
            @Nullable Long itemIdL,
            @Nullable String itemIdS,
            Class requestType,
            boolean persist,
            boolean shouldCache);

    /**
     * Search disk with a RealmQuery which returns an {@link Flowable} that will emit a list of
     * Object.
     */
    @NonNull
    <M> Flowable<List<M>> queryDisk(RealmQueryProvider queryFactory);

    /**
     * Patch a JSONObject which returns an {@link Flowable} that will emit a Object.
     */
    @NonNull
    <M> Flowable<M> dynamicPatchObject(
            String url,
            String idColumnName,
            @NonNull JSONObject jsonObject,
            Class requestType,
            Class responseType,
            boolean persist,
            boolean queuable);

    /**
     * Post a JSONObject which returns an {@link Flowable} that will emit a Object.
     */
    @NonNull
    <M> Flowable<M> dynamicPostObject(
            String url,
            String idColumnName,
            JSONObject keyValuePairs,
            Class requestType,
            Class responseType,
            boolean persist,
            boolean queuable);

    /**
     * Post a HashMap<String, Object> which returns an {@link Flowable} that will emit a list of
     * Object.
     */
    @NonNull
    <M> Flowable<M> dynamicPostList(
            String url,
            String idColumnName,
            JSONArray jsonArray,
            Class requestType,
            Class responseType,
            boolean persist,
            boolean queuable);

    /**
     * Put a HashMap<String, Object> disk with a RealmQuery which returns an {@link Flowable} that
     * will emit a Object.
     */
    @NonNull
    <M> Flowable<M> dynamicPutObject(
            String url,
            String idColumnName,
            JSONObject keyValuePairs,
            Class requestType,
            Class responseType,
            boolean persist,
            boolean queuable);

    /**
     * Put a HashMap<String, Object> disk with a RealmQuery which returns an {@link Flowable} that
     * will emit a list of Object.
     */
    @NonNull
    <M> Flowable<M> dynamicPutList(
            String url,
            String idColumnName,
            JSONArray jsonArray,
            Class requestType,
            Class responseType,
            boolean persist,
            boolean queuable);

    /**
     * Delete a HashMap<String, Object> from cloud which returns an {@link Flowable} that will emit
     * a Object.
     */
    @NonNull
    <M> Flowable<M> dynamicDeleteCollection(
            String url,
            String idColumnName,
            JSONArray jsonArray,
            Class requestType,
            Class responseType,
            boolean persist,
            boolean queuable);

    /**
     * Delete all items of the same type from cloud or disk which returns an {@link Completable}
     * that will emit a list of Object.
     */
    @NonNull
    Completable dynamicDeleteAll(Class requestType);

    @NonNull
    Flowable<File> dynamicDownloadFile(
            String url, File file, boolean onWifi, boolean whileCharging, boolean queuable);

    @NonNull
    <M> Flowable<M> dynamicUploadFile(String url, File file, String key, Map<String, Object> parameter,
                                      boolean onWifi, boolean whileCharging, boolean queuable, Class responseType);
}
