package com.zeyad.usecases.data.repository;

import android.support.annotation.NonNull;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.db.RealmManager;
import com.zeyad.usecases.data.mappers.IDAOMapperFactory;
import com.zeyad.usecases.data.repository.stores.DataStoreFactory;
import com.zeyad.usecases.domain.repositories.Data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import rx.Observable;

public class DataRepository implements Data {

    public static final String DEFAULT_ID_KEY = "id";
    private final DataStoreFactory mDataStoreFactory;
    private final IDAOMapperFactory mEntityMapperUtil;

    /**
     * Constructs a {@link Data}.
     *
     * @param dataStoreFactory A factory to construct different data source implementations.
     */
    public DataRepository(DataStoreFactory dataStoreFactory, IDAOMapperFactory entityMapperUtil) {
        mDataStoreFactory = dataStoreFactory;
        mEntityMapperUtil = entityMapperUtil;
        Config.getInstance().setDataStoreFactory(dataStoreFactory);
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
        try {
            return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass))
                    .dynamicGetList(url, domainClass, dataClass, persist, shouldCache);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> getObjectDynamicallyById(@NonNull String url, String idColumnName, int itemId,
                                                  Class domainClass, Class dataClass, boolean persist,
                                                  boolean shouldCache) {
        try {
            return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass))
                    .dynamicGetObject(url, idColumnName, itemId, domainClass, dataClass, persist,
                            shouldCache);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> postObjectDynamically(@NonNull String url, String idColumnName, JSONObject keyValuePairs,
                                               Class domainClass, @NonNull Class dataClass, boolean persist,
                                               boolean queuable) {
        try {
            return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass))
                    .dynamicPostObject(url, idColumnName, keyValuePairs, domainClass, dataClass, persist, queuable);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> postListDynamically(@NonNull String url, String idColumnName, JSONArray jsonArray,
                                             Class domainClass, @NonNull Class dataClass, boolean persist, boolean queuable) {
        try {
            return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass))
                    .dynamicPostList(url, idColumnName, jsonArray, domainClass, dataClass, persist, queuable);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> dynamicPatchObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                            Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        try {
            return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass))
                    .dynamicPatchObject(url, idColumnName, jsonObject, domainClass, dataClass, persist, queuable);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> deleteListDynamically(@NonNull String url, JSONArray jsonArray, Class domainClass,
                                               @NonNull Class dataClass, boolean persist, boolean queuable) {
        try {
            return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass))
                    .dynamicDeleteCollection(url, DataRepository.DEFAULT_ID_KEY, jsonArray,
                            dataClass, persist, queuable);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable<List> queryDisk(RealmManager.RealmQueryProvider queryFactory, Class domainClass) {
        try {
            return mDataStoreFactory.disk(mEntityMapperUtil.getDataMapper(domainClass))
                    .queryDisk(queryFactory, domainClass);
        } catch (IllegalAccessException e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> putObjectDynamically(@NonNull String url, String idColumnName, JSONObject keyValuePairs,
                                              Class domainClass, @NonNull Class dataClass, boolean persist,
                                              boolean queuable) {
        try {
            return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass))
                    .dynamicPutObject(url, idColumnName, keyValuePairs, domainClass, dataClass, persist,
                            queuable);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<?> putListDynamically(@NonNull String url, String idColumnName, JSONArray jsonArray,
                                            Class domainClass, @NonNull Class dataClass, boolean persist,
                                            boolean queuable) {
        try {
            return mDataStoreFactory.dynamically(url, mEntityMapperUtil.getDataMapper(dataClass))
                    .dynamicPutList(url, idColumnName, jsonArray, domainClass, dataClass, persist, queuable);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @NonNull
    @Override
    public Observable<Boolean> deleteAllDynamically(@NonNull String url, @NonNull Class dataClass, boolean persist) {
        try {
            return mDataStoreFactory.disk(mEntityMapperUtil.getDataMapper(dataClass))
                    .dynamicDeleteAll(url, dataClass, persist);
        } catch (IllegalAccessException e) {
            return Observable.error(e);
        }
    }
}
