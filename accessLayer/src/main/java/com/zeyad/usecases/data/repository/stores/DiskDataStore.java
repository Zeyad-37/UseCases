package com.zeyad.usecases.data.repository.stores;

import android.support.annotation.NonNull;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.utils.ModelConverters;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;
import st.lowlevel.storo.Storo;
import st.lowlevel.storo.StoroBuilder;

import static com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory.CACHE_SIZE;

public class DiskDataStore implements DataStore {
    private static final String IO_DB_ERROR = "Can not IO file to local DB";
    private DataBaseManager mDataBaseManager;
    private IDAOMapper mEntityDataMapper;

    /**
     * Construct a {@link DataStore} based file system data store.
     *
     * @param realmManager A {@link DataBaseManager} to cache data retrieved from the api.
     */
    DiskDataStore(DataBaseManager realmManager, IDAOMapper entityDataMapper) {
        mDataBaseManager = realmManager;
        mEntityDataMapper = entityDataMapper;
        if (DataUseCaseFactory.isWithCache())
            StoroBuilder.configure(CACHE_SIZE)
                    .setDefaultCacheDirectory(Config.getInstance().getContext())
                    .initialize();
    }

    @NonNull
    @Override
    public Observable<?> dynamicGetObject(String url, String idColumnName, int itemId, Class domainClass,
                                          Class dataClass, boolean persist, boolean shouldCache) {
        if (DataUseCaseFactory.isWithCache() && Storo.contains(dataClass.getSimpleName() + itemId))
            return Storo.get(dataClass.getSimpleName() + itemId, dataClass).async()
                    .map(realmModel -> mEntityDataMapper.mapToDomain(realmModel));
        else
            return mDataBaseManager.getById(idColumnName, itemId, dataClass)
                    .map(realmModel -> mEntityDataMapper.mapToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<List> dynamicGetList(String url, Class domainClass, Class dataClass, boolean persist,
                                           boolean shouldCache) {
        return mDataBaseManager.getAll(dataClass).map(realmModels -> mEntityDataMapper.mapAllToDomain(realmModels));
    }

    @NonNull
    @Override
    public Observable<?> searchDisk(String query, String column, Class domainClass, Class dataClass) {
        return mDataBaseManager.getWhere(dataClass, query, column)
                .map(realmModel -> mEntityDataMapper.mapToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<?> searchDisk(RealmQuery query, Class domainClass) {
        return mDataBaseManager.getWhere(query)
                .map(realmModel -> mEntityDataMapper.mapToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<?> dynamicDeleteCollection(String url, String idColumnName, JSONArray jsonArray,
                                                 Class dataClass, boolean persist, boolean queuable) {
        List<Long> convertToListOfId = ModelConverters.convertToListOfId(jsonArray);
        return mDataBaseManager.evictCollection(idColumnName, convertToListOfId, dataClass)
                .doOnNext(o -> {
                    if (DataUseCaseFactory.isWithCache()) {
                        for (int i = 0, convertToListOfIdSize = convertToListOfId != null ? convertToListOfId.size() : 0;
                             i < convertToListOfIdSize; i++)
                            Storo.delete(dataClass.getSimpleName() + convertToListOfId.get(i));
                    }
                });
    }

    @NonNull
    @Override
    public Observable<Boolean> dynamicDeleteAll(String url, Class dataClass, boolean persist) {
        return Observable.error(new IllegalAccessException(IO_DB_ERROR));
    }

    @NonNull
    @Override
    public Observable<?> dynamicPostObject(String url, String idColumnName, JSONObject jsonObject,
                                           Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                .doOnNext(o -> {
                    if (DataUseCaseFactory.isWithCache())
                        cacheObject(idColumnName, jsonObject, dataClass);
                });
    }

    @NonNull
    @Override
    public Observable<?> dynamicPostList(String url, String idColumnName, JSONArray jsonArray,
                                         Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.putAll(jsonArray, idColumnName, dataClass);
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutObject(String url, String idColumnName, JSONObject jsonObject,
                                          Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass)
                .doOnNext(o -> {
                    if (DataUseCaseFactory.isWithCache())
                        cacheObject(idColumnName, jsonObject, dataClass);
                });
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutList(String url, String idColumnName, JSONArray jsonArray,
                                        Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.putAll(jsonArray, idColumnName, dataClass);
    }

    private void cacheObject(String idColumnName, JSONObject jsonObject, Class dataClass) {
        Storo.put(dataClass.getSimpleName() + jsonObject.optString(idColumnName),
                gson.fromJson(jsonObject.toString(), dataClass)).execute();
    }

    private void cacheList(String idColumnName, JSONArray jsonArray, Class dataClass) {
        for (int i = 0, size = jsonArray.length(); i < size; i++) {
            cacheObject(idColumnName, jsonArray.optJSONObject(i), dataClass);
        }
    }

    @NonNull
    @Override
    public Observable<?> dynamicUploadFile(String url, File file, String key, HashMap<String, Object> parameters,
                                           boolean onWifi, boolean whileCharging, boolean queuable, Class domainClass) {
        return Observable.error(new IllegalStateException(IO_DB_ERROR));
    }

    @NonNull
    @Override
    public Observable<?> dynamicDownloadFile(String url, File file, boolean onWifi, boolean whileCharging,
                                             boolean queuable) {
        return Observable.error(new IllegalStateException(IO_DB_ERROR));
    }
}
