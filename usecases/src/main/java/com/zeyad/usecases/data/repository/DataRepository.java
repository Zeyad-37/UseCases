package com.zeyad.usecases.data.repository;

import android.support.annotation.NonNull;

import com.zeyad.usecases.data.db.RealmManager;
import com.zeyad.usecases.data.repository.stores.DataStoreFactory;
import com.zeyad.usecases.domain.repository.Data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import rx.Observable;

public class DataRepository implements Data {

    public static final String DEFAULT_ID_KEY = "id";
    private final DataStoreFactory mDataStoreFactory;

    /**
     * Constructs a {@link Data}.
     *
     * @param dataStoreFactory A factory to construct different data source implementations.
     */
    public DataRepository(DataStoreFactory dataStoreFactory) {
        mDataStoreFactory = dataStoreFactory;
    }

    /**
     * Returns a list of object of desired type by providing the classes.
     * If the url is empty, it will fetch the data from the local db.
     * If persist is true its going to save the list to the database, given it fetched from the cloud.
     *
     * @param url         end point.
     * @param dataClass   The data class representation of the object.
     * @param persist     boolean to decide whether to persist the result or not
     * @param shouldCache boolean to decide whether to cache network response or not
     * @return A list, if available.
     */
    @NonNull
    @Override
    public Observable<List> getListDynamically(@NonNull String url, @NonNull Class dataClass,
                                               boolean persist, boolean shouldCache) {
        try {
            return mDataStoreFactory.dynamically(url)
                    .dynamicGetList(url, dataClass, persist, shouldCache);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> getObjectDynamicallyById(@NonNull String url, String idColumnName, int itemId,
                                                  Class dataClass, boolean persist,
                                                  boolean shouldCache) {
        try {
            return mDataStoreFactory.dynamically(url)
                    .dynamicGetObject(url, idColumnName, itemId, dataClass, persist,
                            shouldCache);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> postObjectDynamically(@NonNull String url, String idColumnName, JSONObject keyValuePairs,
                                               @NonNull Class dataClass, boolean persist,
                                               boolean queuable) {
        try {
            return mDataStoreFactory.dynamically(url)
                    .dynamicPostObject(url, idColumnName, keyValuePairs, dataClass, persist, queuable);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> postListDynamically(@NonNull String url, String idColumnName, JSONArray jsonArray,
                                             @NonNull Class dataClass, boolean persist, boolean queuable) {
        try {
            return mDataStoreFactory.dynamically(url)
                    .dynamicPostList(url, idColumnName, jsonArray, dataClass, persist, queuable);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> dynamicPatchObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                            Class dataClass, boolean persist, boolean queuable) {
        try {
            return mDataStoreFactory.dynamically(url)
                    .dynamicPatchObject(url, idColumnName, jsonObject, dataClass, persist, queuable);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> deleteListDynamically(@NonNull String url, JSONArray jsonArray,
                                               @NonNull Class dataClass, boolean persist, boolean queuable) {
        try {
            return mDataStoreFactory.dynamically(url)
                    .dynamicDeleteCollection(url, DataRepository.DEFAULT_ID_KEY, jsonArray,
                            dataClass, persist, queuable);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<List> queryDisk(RealmManager.RealmQueryProvider queryFactory) {
        try {
            return mDataStoreFactory.disk().queryDisk(queryFactory);
        } catch (IllegalAccessException e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> putObjectDynamically(@NonNull String url, String idColumnName, JSONObject keyValuePairs,
                                              @NonNull Class dataClass, boolean persist,
                                              boolean queuable) {
        try {
            return mDataStoreFactory.dynamically(url)
                    .dynamicPutObject(url, idColumnName, keyValuePairs, dataClass, persist, queuable);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> putListDynamically(@NonNull String url, String idColumnName, JSONArray jsonArray,
                                            @NonNull Class dataClass, boolean persist,
                                            boolean queuable) {
        try {
            return mDataStoreFactory.dynamically(url)
                    .dynamicPutList(url, idColumnName, jsonArray, dataClass, persist, queuable);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<Boolean> deleteAllDynamically(@NonNull String url, @NonNull Class dataClass, boolean persist) {
        try {
            return mDataStoreFactory.disk().dynamicDeleteAll(dataClass);
        } catch (IllegalAccessException e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> uploadFileDynamically(String url, File file, String key, HashMap<String, Object> parameters,
                                               boolean onWifi, boolean whileCharging, boolean queuable,
                                               Class dataClass) {
        return mDataStoreFactory.cloud()
                .dynamicUploadFile(url, file, key, parameters, onWifi, queuable, whileCharging, dataClass);
    }


    @NonNull
    @Override
    public Observable<?> downloadFileDynamically(String url, File file, boolean onWifi, boolean whileCharging,
                                                 boolean queuable, Class dataClass) {
        return mDataStoreFactory.cloud()
                .dynamicDownloadFile(url, file, onWifi, whileCharging, queuable);
    }
}
