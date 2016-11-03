package com.zeyad.genericusecase.domain.repository;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;

/**
 * Interface that represents a Repository for getting Objects from the data layer.
 */
public interface Repository {

    /**
     * Get an {@link Observable} which will emit a collectionFromDisk of Items.
     */
    @NonNull
    Observable<List> getListDynamically(String url, Class domainClass, Class dataClass, boolean persist,
                                        boolean shouldCache);

    /**
     * Get an {@link Observable} which will emit an Item.
     *
     * @param itemId The user id used to retrieve getDynamicallyById data.
     */
    @NonNull
    Observable<?> getObjectDynamicallyById(String url, String idColumnName, int itemId, Class domainClass,
                                           Class dataClass, boolean persist, boolean shouldCache);

    @NonNull
    Observable<?> postObjectDynamically(String url, String idColumnName, JSONObject keyValuePairs,
                                        Class domainClass, Class dataClass, boolean persist, boolean queuable);

    @NonNull
    Observable<?> postListDynamically(String url, String idColumnName, JSONArray jsonArray,
                                      Class domainClass, Class dataClass, boolean persist, boolean queuable);

    @NonNull
    Observable<?> deleteListDynamically(String url, JSONArray jsonArray, Class domainClass,
                                        Class dataClass, boolean persist, boolean queuable);

    @NonNull
    Observable<?> putObjectDynamically(String url, String idColumnName, JSONObject keyValuePairs,
                                       Class domainClass, Class dataClass, boolean persist, boolean queuable);

    @NonNull
    Observable<?> putListDynamically(String url, String idColumnName, JSONArray jsonArray,
                                     Class domainClass, Class dataClass, boolean persist, boolean queuable);

    @NonNull
    Observable<Boolean> deleteAllDynamically(String url, Class dataClass, boolean persist);

    @NonNull
    Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass);

    @NonNull
    Observable<List> searchDisk(RealmQuery query, Class domainClass);

    @NonNull
    Observable<?> uploadFileDynamically(String url, File file, boolean onWifi, boolean whileCharging,
                                        boolean queuable, Class domainClass, Class dataClass);

    @NonNull
    Observable<?> downloadFileDynamically(String url, File file, boolean onWifi, boolean whileCharging,
                                          boolean queuable, Class domainClass, Class dataClass);
}