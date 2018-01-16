package com.zeyad.usecases.stores;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.usecases.db.DataBaseManager;
import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class DiskStore implements DataStore {
    private static final String IO_DB_ERROR = "Can not file IO to local DB";
    private final DataBaseManager mDataBaseManager;
    private final MemoryStore mMemoryStore;
    private final Utils mUtils;

    /**
     * Construct a {@link DataStore} based file system data store.
     *
     * @param realmManager A {@link DataBaseManager} to cache data retrieved from the api.
     */
    DiskStore(DataBaseManager realmManager, MemoryStore memoryStore) {
        mDataBaseManager = realmManager;
        mMemoryStore = memoryStore;
        mUtils = Utils.getInstance();
    }

    @NonNull
    @Override
    public <M> Flowable<List<M>> dynamicGetList(
            String url, String idColumnName, Class dataClass, boolean persist, boolean shouldCache) {
        return mDataBaseManager.<M> getAll(dataClass)
                .doOnNext(ms -> {
                    if (mUtils.withCache(shouldCache)) {
                        mMemoryStore.cacheList(idColumnName, new JSONArray(gson.toJson(ms)), dataClass);
                    }
                });
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicGetObject(String url, String idColumnName, Object itemId, Class itemIdType,
            @NonNull Class dataClass, boolean persist, boolean shouldCache) {
        return mDataBaseManager.<M> getById(idColumnName, itemId, itemIdType, dataClass)
                .doOnNext(m -> {
                    if (mUtils.withCache(shouldCache)) {
                        mMemoryStore.cacheObject(idColumnName,
                                new JSONObject(gson.toJson(m)), dataClass);
                    }
                });
    }

    @NonNull
    @Override
    public <M> Flowable<List<M>> queryDisk(RealmQueryProvider queryFactory) {
        return mDataBaseManager.getQuery(queryFactory);
    }

    @NonNull
    @Override
    public Flowable dynamicPatchObject(String url, String idColumnName, Class itemIdType,
                                       @NonNull JSONObject jsonObject, @NonNull Class dataClass,
                                       Class responseType, boolean persist, boolean cache) {
        return mDataBaseManager.put(jsonObject, idColumnName, itemIdType, dataClass)
                .doOnSuccess(object -> {
                    if (mUtils.withCache(cache)) {
                        mMemoryStore.cacheObject(idColumnName,
                                new JSONObject(gson.toJson(jsonObject)), dataClass);
                    }
                })
                .toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPostObject(String url, String idColumnName, Class itemIdType,
                                      @NonNull JSONObject jsonObject, @NonNull Class dataClass,
                                      Class responseType, boolean persist, boolean cache) {
        return mDataBaseManager.put(jsonObject, idColumnName, itemIdType, dataClass)
                .doOnSuccess(object -> {
                    if (mUtils.withCache(cache)) {
                        mMemoryStore.cacheObject(idColumnName,
                                new JSONObject(gson.toJson(jsonObject)), dataClass);
                    }
                })
                .toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPostList(String url, String idColumnName, Class itemIdType,
                                    JSONArray jsonArray, Class dataClass, Class responseType,
                                    boolean persist, boolean cache) {
        return mDataBaseManager.putAll(jsonArray, idColumnName, itemIdType, dataClass)
                .doOnSuccess(aBoolean -> mMemoryStore.cacheList(idColumnName, jsonArray, dataClass))
                .toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPutObject(String url, String idColumnName, Class itemIdType,
                                     @NonNull JSONObject jsonObject, @NonNull Class dataClass,
                                     Class responseType, boolean persist, boolean cache) {
        return mDataBaseManager.put(jsonObject, idColumnName, itemIdType, dataClass)
                .doOnSuccess(object -> {
                    if (mUtils.withCache(cache)) {
                        mMemoryStore.cacheObject(idColumnName,
                                new JSONObject(gson.toJson(jsonObject)), dataClass);
                    }
                })
                .toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicPutList(String url, String idColumnName, Class itemIdType,
                                   JSONArray jsonArray, Class dataClass, Class responseType,
                                   boolean persist, boolean cache) {
        return mDataBaseManager.putAll(jsonArray, idColumnName, itemIdType, dataClass)
                .doOnSuccess(aBoolean -> mMemoryStore.cacheList(idColumnName, jsonArray, dataClass))
                .toFlowable();
    }

    @NonNull
    @Override
    public Flowable dynamicDeleteCollection(String url, String idColumnName, Class itemIdType,
                                            JSONArray jsonArray, @NonNull Class dataClass,
                                            Class responseType, boolean persist, boolean cache) {
        List<String> stringIds = mUtils.convertToStringListOfId(jsonArray);
        return mDataBaseManager.evictCollection(idColumnName,
                mUtils.convertToListOfId(jsonArray, itemIdType), itemIdType, dataClass)
                               .doOnSuccess(object -> {
                                   if (mUtils.withCache(cache)) {
                                       mMemoryStore.deleteList(stringIds, dataClass);
                                   }
                               }).toFlowable();
    }

    @NonNull
    @Override
    public Single<Boolean> dynamicDeleteAll(Class dataClass) {
        return mDataBaseManager.evictAll(dataClass);
    }

    @NonNull
    @Override
    public Flowable dynamicDownloadFile(String url, File file) {
        return Flowable.error(new IllegalStateException(IO_DB_ERROR));
    }

    @NonNull
    @Override
    public <M> Flowable<M> dynamicUploadFile(String url,
            @NonNull HashMap<String, File> keyFileMap,
            @Nullable HashMap<String, Object> parameters,
            @NonNull Class responseType) {
        return Flowable.error(new IllegalStateException(IO_DB_ERROR));
    }
}
