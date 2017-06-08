package com.zeyad.usecases.stores;

import android.support.annotation.NonNull;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.db.DataBaseManager;
import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class DiskStore implements DataStore {
    private static final String IO_DB_ERROR = "Can not file IO to local DB";
    private final boolean withCache;
    private final DataBaseManager mDataBaseManager;
    private final MemoryStore mMemoryStore;

    /**
     * Construct a {@link DataStore} based file system data store.
     *
     * @param realmManager A {@link DataBaseManager} to cache data retrieved from the api.
     */
    DiskStore(DataBaseManager realmManager, MemoryStore memoryStore) {
        mDataBaseManager = realmManager;
        withCache = Config.isWithCache();
        mMemoryStore = memoryStore;
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicGetObject(String url, String idColumnName, Object itemId, Class itemIdType,
                                            @NonNull Class dataClass, boolean persist, boolean shouldCache) {
        return mDataBaseManager.<M>getById(idColumnName, itemId, itemIdType, dataClass)
                .doOnEach(notification -> {
                    if (cachable(shouldCache)) {
                        mMemoryStore.cacheObject(idColumnName,
                                new JSONObject(gson.toJson(notification.getValue())), dataClass);
                    }
                });
    }

    @NonNull
    @Override
    public <M> Flowable<List<M>> dynamicGetList(
            String url, Class dataClass, boolean persist, boolean shouldCache) {
        return mDataBaseManager.getAll(dataClass);
    }

    @NonNull
    @Override
    public <M> Flowable<List<M>> queryDisk(RealmQueryProvider queryFactory) {
        return mDataBaseManager.getQuery(queryFactory);
    }

    @NonNull
    @Override
    public Single<Boolean> dynamicDeleteAll(Class dataClass) {
        return mDataBaseManager.evictAll(dataClass);
    }

    @NonNull
    @Override
    public Flowable dynamicDeleteCollection(String url, String idColumnName, JSONArray jsonArray,
                                            @NonNull Class dataClass, Class responseType, boolean persist,
                                            boolean cache, boolean queuable) {
        List<Long> convertToListOfId = Utils.getInstance().convertToListOfId(jsonArray);
        return mDataBaseManager.evictCollection(idColumnName, convertToListOfId, dataClass)
                .doOnSuccess(object -> {
                    if (cachable(cache)) {
                        mMemoryStore.deleteList(convertToListOfId, dataClass);
                    }
                }).toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPatchObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                       @NonNull Class dataClass, Class responseType, boolean persist,
                                       boolean cache, boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                .doOnSuccess(object -> {
                    if (cachable(cache)) {
                        mMemoryStore.cacheObject(idColumnName,
                                new JSONObject(gson.toJson(jsonObject)), dataClass);
                    }
                })
                .toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPostObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                      @NonNull Class dataClass, Class responseType, boolean persist,
                                      boolean cache, boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                .doOnSuccess(object -> {
                    if (cachable(cache)) {
                        mMemoryStore.cacheObject(idColumnName,
                                new JSONObject(gson.toJson(jsonObject)), dataClass);
                    }
                })
                .toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPostList(String url, String idColumnName, JSONArray jsonArray,
                                    Class dataClass, Class responseType, boolean persist,
                                    boolean cache, boolean queuable) {
        return mDataBaseManager.putAll(jsonArray, idColumnName, dataClass)
                .doOnSuccess(aBoolean -> mMemoryStore.cacheList(idColumnName, jsonArray, dataClass))
                .toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPutObject(String url, String idColumnName, @NonNull JSONObject jsonObject,
                                     @NonNull Class dataClass, Class responseType, boolean persist,
                                     boolean cache, boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                .doOnSuccess(object -> {
                    if (cachable(cache)) {
                        mMemoryStore.cacheObject(idColumnName,
                                new JSONObject(gson.toJson(jsonObject)), dataClass);
                    }
                })
                .toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPutList(String url, String idColumnName, JSONArray jsonArray, Class dataClass,
                                   Class responseType, boolean persist, boolean cache, boolean queuable) {
        return mDataBaseManager.putAll(jsonArray, idColumnName, dataClass)
                .doOnSuccess(aBoolean -> mMemoryStore.cacheList(idColumnName, jsonArray, dataClass))
                .toFlowable();
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
    public Flowable dynamicDownloadFile(
            String url, File file, boolean onWifi, boolean whileCharging, boolean queuable) {
        return Flowable.error(new IllegalStateException(IO_DB_ERROR));
    }

    private boolean cachable(boolean cache) {
        return withCache && cache;
    }
}
