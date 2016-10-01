package com.zeyad.genericusecase.data.repository;

import android.support.annotation.NonNull;

import com.zeyad.genericusecase.data.repository.generalstore.DataStoreFactory;
import com.zeyad.genericusecase.data.utils.IEntityMapperUtil;
import com.zeyad.genericusecase.domain.repository.Repository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;

// TODO: 13/05/16 Document!
public class DataRepository implements Repository {

    public static final String DEFAULT_ID_TO_BE_REPLACED = "id";
    public static final boolean DEFAULT_TO_CACHE = false;
    private final DataStoreFactory mDataStoreFactory;
    private final IEntityMapperUtil mEntityMapperUtil;

    /**
     * Constructs a {@link Repository}.
     *
     * @param dataStoreFactory A factory to construct different data source implementations.
     */
    public DataRepository(DataStoreFactory dataStoreFactory, IEntityMapperUtil entityMapperUtil) {
        mDataStoreFactory = dataStoreFactory;
        mEntityMapperUtil = entityMapperUtil;
    }


    /**
     * Returns a list of object of desired type by providing the classes.
     * If the url is empty, it will fetch the data from the local db.
     * If persist is true its going to save the list to the database, given it fetched from the cloud.
     *
     * @param url         end point.
     * @param domainClass The domain class representation of the object.
     * @param dataClass   The data class representation of the object.
     * @param persist     boolean to decide whether to persist the result or not
     * @param shouldCache boolean to decide whether to cache network response or not
     * @return A list, if available.
     */
    @NonNull
    @Override
    public Observable<List> getListDynamically(@NonNull String url, Class domainClass, @NonNull Class dataClass,
                                               boolean persist, boolean shouldCache) {
        return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass), dataClass)
                .dynamicGetList(url, domainClass, dataClass, persist, shouldCache);
    }

    @NonNull
    @Override
    public Observable<?> getObjectDynamicallyById(@NonNull String url, String idColumnName, int itemId,
                                                  Class domainClass, Class dataClass, boolean persist,
                                                  boolean shouldCache) {
        return mDataStoreFactory.dynamically(url, idColumnName, itemId, mEntityMapperUtil.getDataMapper(dataClass), dataClass)
                .dynamicGetObject(url, idColumnName, itemId, domainClass, dataClass, persist,
                        shouldCache);
    }

    @NonNull
    @Override
    public Observable<?> postObjectDynamically(@NonNull String url, String idColumnName, JSONObject keyValuePairs,
                                               Class domainClass, @NonNull Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass), dataClass)
                .dynamicPostObject(url, idColumnName, keyValuePairs, domainClass, dataClass, persist);
    }

    @NonNull
    @Override
    public Observable<?> postListDynamically(@NonNull String url, String idColumnName, JSONArray jsonArray,
                                             Class domainClass, @NonNull Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass), dataClass)
                .dynamicPostList(url, idColumnName, jsonArray, domainClass, dataClass, persist);
    }

    @NonNull
    @Override
    public Observable<?> deleteListDynamically(@NonNull String url, JSONArray jsonArray,
                                               Class domainClass, @NonNull Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass), dataClass)
                .dynamicDeleteCollection(url, com.zeyad.genericusecase.data.repository.DataRepository.DEFAULT_ID_TO_BE_REPLACED, jsonArray,
                        dataClass, persist);
    }

    @NonNull
    @Override
    public Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass) {
        return mDataStoreFactory.disk(mEntityMapperUtil.getDataMapper(dataClass)).searchDisk(query, column,
                domainClass, dataClass);
    }

    @NonNull
    @Override
    public Observable<List> searchDisk(RealmQuery query, Class domainClass) {
        return mDataStoreFactory.disk(mEntityMapperUtil.getDataMapper(domainClass)).searchDisk(query, domainClass);
    }

    @NonNull
    @Override
    public Observable<?> uploadFileDynamically(String url, File file, boolean onWifi, boolean whileCharging,
                                               Class domainClass, Class dataClass) {
        return mDataStoreFactory.cloud(mEntityMapperUtil.getDataMapper(dataClass))
                .dynamicUploadFile(url, file, onWifi, whileCharging, domainClass);
    }

    @NonNull
    @Override
    public Observable<?> downloadFileDynamically(String url, File file, boolean onWifi, boolean whileCharging, Class domainClass, Class dataClass) {
        return mDataStoreFactory.cloud(mEntityMapperUtil.getDataMapper(dataClass))
                .dynamicDownloadFile(url, file, onWifi, whileCharging);
    }

    @NonNull
    @Override
    public Observable<?> putObjectDynamically(@NonNull String url, String idColumnName, JSONObject keyValuePairs,
                                              Class domainClass, @NonNull Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass), dataClass)
                .dynamicPutObject(url, idColumnName, keyValuePairs, domainClass, dataClass, persist);
    }

    @NonNull
    @Override
    public Observable<?> putListDynamically(@NonNull String url, String idColumnName, JSONArray jsonArray,
                                            Class domainClass, @NonNull Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass), dataClass)
                .dynamicPutList(url, idColumnName, jsonArray, domainClass, dataClass, persist);
    }

    @NonNull
    @Override
    public Observable<Boolean> deleteAllDynamically(@NonNull String url, @NonNull Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass), dataClass)
                .dynamicDeleteAll(url, dataClass, persist);
    }
}
