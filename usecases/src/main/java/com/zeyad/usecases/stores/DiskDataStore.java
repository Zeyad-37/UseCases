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

import io.reactivex.Completable;
import io.reactivex.Flowable;
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
    public <M> Flowable<M> dynamicGetObject(String url, String idColumnName, int itemId,
                                            @NonNull Class dataClass, boolean persist, boolean shouldCache) {
        if (Config.isWithCache() && Storo.contains(dataClass.getSimpleName() + itemId))
            return Utils.getInstance().toFlowable(Storo.get(dataClass.getSimpleName() + itemId, dataClass).async()
                    .map(object -> mEntityDataMapper.mapTo(object, dataClass)));
        else
            return mDataBaseManager.<M>getById(idColumnName, itemId, dataClass)
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
    public <M> Flowable<List<M>> dynamicGetList(String url, Class dataClass, boolean persist, boolean shouldCache) {
        return mDataBaseManager.getAll(dataClass);
    }

    @NonNull
    @Override
    public Flowable dynamicPatchObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                       @NonNull Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                .doOnEvent(object -> {
                    if (Config.isWithCache())
                        cacheObject(idColumnName, jsonObject, dataClass);
                }).toFlowable();
    }

    @NonNull
    @Override
    public <M> Flowable<List<M>> queryDisk(RealmQueryProvider queryFactory) {
        return mDataBaseManager.getQuery(queryFactory);
    }

    @NonNull
    @Override
    public Flowable dynamicDeleteCollection(String url, String idColumnName, JSONArray jsonArray,
                                            @NonNull Class dataClass, boolean persist, boolean queuable) {
        List<Long> convertToListOfId = Utils.getInstance().convertToListOfId(jsonArray);
        return mDataBaseManager.evictCollection(idColumnName, convertToListOfId, dataClass)
                .doOnEvent(object -> {
                    if (Config.isWithCache()) {
                        for (int i = 0, convertToListOfIdSize = convertToListOfId != null ? convertToListOfId.size() : 0;
                             i < convertToListOfIdSize; i++)
                            Storo.delete(dataClass.getSimpleName() + convertToListOfId.get(i));
                    }
                }).toFlowable();
    }

    @NonNull
    @Override
    public Completable dynamicDeleteAll(Class dataClass) {
        return mDataBaseManager.evictAll(dataClass);
    }

    @NonNull
    @Override
    public Flowable dynamicPostObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                      @NonNull Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                .doOnEvent(object -> {
                    if (Config.isWithCache())
                        cacheObject(idColumnName, jsonObject, dataClass);
                }).toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPostList(String url, String idColumnName, JSONArray jsonArray,
                                    Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.putAll(jsonArray, idColumnName, dataClass).toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPutObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                     @NonNull Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                .doOnEvent(object -> {
                    if (Config.isWithCache())
                        cacheObject(idColumnName, jsonObject, dataClass);
                }).toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPutList(String url, String idColumnName, JSONArray jsonArray,
                                   Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.putAll(jsonArray, idColumnName, dataClass).toFlowable();
    }

    private void cacheObject(String idColumnName, @NonNull JSONObject jsonObject, @NonNull Class dataClass) {
        Storo.put(dataClass.getSimpleName() + jsonObject.optString(idColumnName), gson
                .fromJson(jsonObject.toString(), dataClass))
                .setExpiry(Config.getCacheAmount(), Config.getCacheTimeUnit())
                .execute();
    }

    private void cacheList(String idColumnName, @NonNull JSONArray jsonArray, @NonNull Class dataClass) {
        for (int i = 0, size = jsonArray.length(); i < size; i++) {
            cacheObject(idColumnName, jsonArray.optJSONObject(i), dataClass);
        }
    }

    @NonNull
    @Override
    public <M> Flowable dynamicUploadFile(String url, File file, String key, HashMap<String, Object> parameters,
                                          boolean onWifi, boolean whileCharging, boolean queuable, Class domainClass) {
        return Flowable.error(new IllegalStateException(IO_DB_ERROR));
    }

    @NonNull
    @Override
    public Flowable dynamicDownloadFile(String url, File file, boolean onWifi, boolean whileCharging,
                                        boolean queuable) {
        return Flowable.error(new IllegalStateException(IO_DB_ERROR));
    }
}
