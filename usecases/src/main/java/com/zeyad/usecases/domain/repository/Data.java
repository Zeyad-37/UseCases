package com.zeyad.usecases.domain.repository;

import android.support.annotation.NonNull;

import com.zeyad.usecases.data.db.RealmManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import rx.Observable;

/**
 * Interface that represents a Data for getting Objects from the data layer.
 */
public interface Data {

    /**
     * Get an {@link Observable} which will emit a collectionFromDisk of Items.
     */
    @NonNull
    Observable<List> getListDynamically(String url, Class dataClass, boolean persist,
                                        boolean shouldCache);

    /**
     * Get an {@link Observable} which will emit an Item.
     *
     * @param itemId The user id used to retrieve getDynamicallyById data.
     */
    @NonNull
    Observable<?> getObjectDynamicallyById(String url, String idColumnName, int itemId,
                                           Class dataClass, boolean persist, boolean shouldCache);

    @NonNull
    Observable<?> postObjectDynamically(String url, String idColumnName, JSONObject keyValuePairs,
                                        Class dataClass, boolean persist, boolean queuable);

    @NonNull
    Observable<?> postListDynamically(String url, String idColumnName, JSONArray jsonArray,
                                      Class dataClass, boolean persist, boolean queuable);

    @NonNull
    Observable<?> deleteListDynamically(String url, JSONArray jsonArray,
                                        Class dataClass, boolean persist, boolean queuable);

    @NonNull
    Observable<?> dynamicPatchObject(String url, String idColumnName, JSONObject jsonObject,
                                     Class dataClass, boolean persist, boolean queuable);

    @NonNull
    Observable<?> putObjectDynamically(String url, String idColumnName, JSONObject keyValuePairs,
                                       Class dataClass, boolean persist, boolean queuable);

    @NonNull
    Observable<?> putListDynamically(String url, String idColumnName, JSONArray jsonArray,
                                     Class dataClass, boolean persist, boolean queuable);

    @NonNull
    Observable<Boolean> deleteAllDynamically(String url, Class dataClass, boolean persist);

    @NonNull
    Observable<List> queryDisk(RealmManager.RealmQueryProvider queryFactory);

    @NonNull
    Observable<?> uploadFileDynamically(String url, File file, String key, HashMap<String, Object> parameters,
                                        boolean onWifi, boolean whileCharging, boolean queuable,
                                        Class dataClass);

    @NonNull
    Observable<?> downloadFileDynamically(String url, File file, boolean onWifi, boolean whileCharging,
                                          boolean queuable, Class dataClass);
}