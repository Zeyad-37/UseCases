package com.zeyad.usecases.stores;

import android.support.annotation.NonNull;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.db.DataBaseManager;
import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.mapper.DAOMapper;
import com.zeyad.usecases.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import rx.Completable;
import rx.Observable;
import st.lowlevel.storo.Storo;

public class DiskDataStore implements DataStore {
    private static final String IO_DB_ERROR = "Can not IO file to local DB";
    private DataBaseManager mDataBaseManager;
    private DAOMapper mEntityDataMapper;

    /**
     * Construct a {@link DataStore} based file system data store.
     *
     * @param realmManager A {@link DataBaseManager} to cache data retrieved from the api.
     */
    DiskDataStore(DataBaseManager realmManager, DAOMapper entityDataMapper) {
        mDataBaseManager = realmManager;
        mEntityDataMapper = entityDataMapper;
    }

    @NonNull
    @Override
    public <M> Observable<M> dynamicGetObject(String url, String idColumnName, int itemId,
                                              Class dataClass, boolean persist, boolean shouldCache) {
        if (Config.isWithCache() && Storo.contains(dataClass.getSimpleName() + itemId))
            return Storo.get(dataClass.getSimpleName() + itemId, dataClass).async()
                    .map(object -> mEntityDataMapper.mapTo(object, dataClass));
        else
            return (Observable<M>) mDataBaseManager.getById(idColumnName, itemId, dataClass)
                    .doOnEach(notification -> {
                        try {
                            if (Config.isWithCache() && !Storo.contains(dataClass.getSimpleName() + itemId))
                                cacheObject(idColumnName, new JSONObject(gson.toJson(notification.getValue())),
                                        dataClass);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
    }

    @NonNull
    @Override
    public <M> Observable<List<M>> dynamicGetList(String url, Class dataClass, boolean persist, boolean shouldCache) {
        return mDataBaseManager.getAll(dataClass);
    }

    @Override
    public Observable<Object> dynamicPatchObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                                 Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                .doOnEach(object -> {
                    if (Config.isWithCache())
                        cacheObject(idColumnName, jsonObject, dataClass);
                }).toObservable();
    }

    @NonNull
    @Override
    public <M> Observable<List<M>> queryDisk(RealmQueryProvider queryFactory) {
        return mDataBaseManager.getQuery(queryFactory);
    }

    @NonNull
    @Override
    public Observable<Object> dynamicDeleteCollection(String url, String idColumnName, JSONArray jsonArray,
                                                      Class dataClass, boolean persist, boolean queuable) {
        List<Long> convertToListOfId = Utils.getInstance().convertToListOfId(jsonArray);
        return mDataBaseManager.evictCollection(idColumnName, convertToListOfId, dataClass)
                .doOnEach(object -> {
                    if (Config.isWithCache()) {
                        for (int i = 0, convertToListOfIdSize = convertToListOfId != null ? convertToListOfId.size() : 0;
                             i < convertToListOfIdSize; i++)
                            Storo.delete(dataClass.getSimpleName() + convertToListOfId.get(i));
                    }
                }).toObservable();
    }

    @NonNull
    @Override
    public Completable dynamicDeleteAll(Class dataClass) {
        return mDataBaseManager.evictAll(dataClass);
    }

    @NonNull
    @Override
    public Observable<Object> dynamicPostObject(String url, String idColumnName, JSONObject jsonObject,
                                                Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                .doOnEach(object -> {
                    if (Config.isWithCache())
                        cacheObject(idColumnName, jsonObject, dataClass);
                }).toObservable();
    }

    @NonNull
    @Override
    public Observable<Object> dynamicPostList(String url, String idColumnName, JSONArray jsonArray,
                                              Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.putAll(jsonArray, idColumnName, dataClass).toObservable();
    }

    @NonNull
    @Override
    public Observable<Object> dynamicPutObject(String url, String idColumnName, JSONObject jsonObject,
                                               Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                .doOnEach(object -> {
                    if (Config.isWithCache())
                        cacheObject(idColumnName, jsonObject, dataClass);
                }).toObservable();
    }

    @NonNull
    @Override
    public Observable<Object> dynamicPutList(String url, String idColumnName, JSONArray jsonArray,
                                             Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.putAll(jsonArray, idColumnName, dataClass).toObservable();
    }

    private void cacheObject(String idColumnName, JSONObject jsonObject, Class dataClass) {
        Storo.put(dataClass.getSimpleName() + jsonObject.optString(idColumnName), gson
                .fromJson(jsonObject.toString(), dataClass))
                .setExpiry(Config.getCacheAmount(), Config.getCacheTimeUnit())
                .execute();
    }

    private void cacheList(String idColumnName, JSONArray jsonArray, Class dataClass) {
        for (int i = 0, size = jsonArray.length(); i < size; i++) {
            cacheObject(idColumnName, jsonArray.optJSONObject(i), dataClass);
        }
    }

    @NonNull
    @Override
    public Observable<Object> dynamicUploadFile(String url, File file, String key, HashMap<String, Object> parameters,
                                                boolean onWifi, boolean whileCharging, boolean queuable, Class domainClass) {
        return Observable.error(new IllegalStateException(IO_DB_ERROR));
    }

    @NonNull
    @Override
    public Observable<Object> dynamicDownloadFile(String url, File file, boolean onWifi, boolean whileCharging,
                                                  boolean queuable) {
        return Observable.error(new IllegalStateException(IO_DB_ERROR));
    }
}
