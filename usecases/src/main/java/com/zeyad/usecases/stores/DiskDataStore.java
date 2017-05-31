package com.zeyad.usecases.stores;

import android.support.annotation.NonNull;
import android.util.Log;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.db.DataBaseManager;
import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.mapper.DAOMapper;
import com.zeyad.usecases.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import st.lowlevel.storo.Storo;

public class DiskDataStore implements DataStore {
    private static final String IO_DB_ERROR = "Can not file IO to local DB";
    private final boolean withCache;
    private final DataBaseManager mDataBaseManager;
    private final DAOMapper mEntityDataMapper;

    /**
     * Construct a {@link DataStore} based file system data store.
     *
     * @param realmManager A {@link DataBaseManager} to cache data retrieved from the api.
     */
    DiskDataStore(DataBaseManager realmManager, DAOMapper entityDataMapper) {
        mDataBaseManager = realmManager;
        mEntityDataMapper = entityDataMapper;
        withCache = Config.isWithCache();
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicGetObject(String url, String idColumnName, Long itemIdL, String itemIdS,
                                            @NonNull Class dataClass, boolean persist, boolean shouldCache) {
        String key = dataClass.getSimpleName() + (itemIdL == null ? itemIdS : String.valueOf(itemIdL));
        return withCache && Storo.contains(key) ? Utils.getInstance().toFlowable(Storo.get(key, dataClass).async()
                .map(object -> mEntityDataMapper.mapTo(object, dataClass))) :
                mDataBaseManager.<M>getById(idColumnName, itemIdL, itemIdS, dataClass)
                        .doOnEach(notification -> {
                            try {
                                if (withCache && !Storo.contains(key)) {
                                    cacheObject(idColumnName, new JSONObject(gson.toJson(notification.getValue())),
                                            dataClass);
                                }
                            } catch (JSONException e) {
                                Log.e("DiskDataStore", "", e);
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
    public <M> Flowable<List<M>> queryDisk(RealmQueryProvider queryFactory) {
        return mDataBaseManager.getQuery(queryFactory);
    }

    @NonNull
    @Override
    public Completable dynamicDeleteAll(Class dataClass) {
        return mDataBaseManager.evictAll(dataClass);
    }

    @NonNull
    @Override
    public Flowable dynamicDeleteCollection(String url, String idColumnName, JSONArray jsonArray,
                                            @NonNull Class dataClass, Class responseType, boolean persist,
                                            boolean queuable) {
        List<Long> convertToListOfId = Utils.getInstance().convertToListOfId(jsonArray);
        return mDataBaseManager.evictCollection(idColumnName, convertToListOfId, dataClass)
                .doOnEvent(object -> {
                    if (withCache) {
                        int convertToListOfIdSize = convertToListOfId != null ? convertToListOfId.size() : 0;
                        for (int i = 0; i < convertToListOfIdSize; i++) {
                            Storo.delete(dataClass.getSimpleName() + convertToListOfId.get(i));
                        }
                    }
                }).toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPatchObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                       @NonNull Class dataClass, Class responseType, boolean persist,
                                       boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                .doOnEvent(object -> {
                    if (withCache) {
                        cacheObject(idColumnName, jsonObject, dataClass);
                    }
                }).toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPostObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                      @NonNull Class dataClass, Class responseType, boolean persist,
                                      boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                .doOnEvent(object -> {
                    if (withCache) {
                        cacheObject(idColumnName, jsonObject, dataClass);
                    }
                }).toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPostList(String url, String idColumnName, JSONArray jsonArray,
                                    Class dataClass, Class responseType, boolean persist, boolean queuable) {
        return mDataBaseManager.putAll(jsonArray, idColumnName, dataClass).toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPutObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                     @NonNull Class dataClass, Class responseType, boolean persist,
                                     boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                .doOnEvent(object -> {
                    if (withCache) {
                        cacheObject(idColumnName, jsonObject, dataClass);
                    }
                }).toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPutList(String url, String idColumnName, JSONArray jsonArray, Class dataClass,
                                   Class responseType, boolean persist, boolean queuable) {
        return mDataBaseManager.putAll(jsonArray, idColumnName, dataClass).toFlowable();
    }

    private void cacheObject(String idColumnName, @NonNull JSONObject jsonObject, @NonNull Class dataClass) {
        Storo.put(dataClass.getSimpleName() + jsonObject.optString(idColumnName), gson
                .fromJson(jsonObject.toString(), dataClass))
                .setExpiry(Config.getCacheAmount(), Config.getCacheTimeUnit())
                .execute();
    }

    private void cacheList(String idColumnName, @NonNull JSONArray jsonArray, @NonNull Class dataClass) {
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            cacheObject(idColumnName, jsonArray.optJSONObject(i), dataClass);
        }
    }

    @NonNull
    @Override
    public <M> Flowable dynamicUploadFile(String url, File file, String key, Map<String, Object> parameters,
                                          boolean onWifi, boolean whileCharging, boolean queuable,
                                          Class domainClass) {
        return Flowable.error(new IllegalStateException(IO_DB_ERROR));
    }

    @NonNull
    @Override
    public Flowable dynamicDownloadFile(String url, File file, boolean onWifi, boolean whileCharging,
                                        boolean queuable) {
        return Flowable.error(new IllegalStateException(IO_DB_ERROR));
    }
}
