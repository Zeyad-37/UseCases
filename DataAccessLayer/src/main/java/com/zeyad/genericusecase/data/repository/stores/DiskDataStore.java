package com.zeyad.genericusecase.data.repository.stores;

import android.support.annotation.NonNull;

import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.R;
import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.utils.ModelConverters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;

public class DiskDataStore implements DataStore {

    private DataBaseManager mDataBaseManager;
    private EntityMapper mEntityDataMapper;

    /**
     * Construct a {@link DataStore} based file system data store.
     *
     * @param realmManager A {@link DataBaseManager} to cache data retrieved from the api.
     */
    DiskDataStore(DataBaseManager realmManager, EntityMapper entityDataMapper) {
        mDataBaseManager = realmManager;
        mEntityDataMapper = entityDataMapper;
//        StoroBuilder.configure(8192)  // maximum size to allocate in bytes
//                .setDefaultCacheDirectory(Config.getInstance().getContext())
//                .initialize();
    }

    @NonNull
    @Override
    public Observable<?> dynamicGetObject(String url, String idColumnName, int itemId, Class domainClass,
                                          Class dataClass, boolean persist, boolean shouldCache) {
//        if (Storo.contains(dataClass.getSimpleName() + itemId))
//            return Storo.get(dataClass.getSimpleName() + itemId, dataClass).async();
//        else
        return mDataBaseManager.getById(idColumnName, itemId, dataClass)
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<List> dynamicGetList(String url, Class domainClass, Class dataClass, boolean persist,
                                           boolean shouldCache) {
//        if (Storo.contains(dataClass.getSimpleName()))
//            return Storo.get(dataClass.getSimpleName(), dataClass).async();
//        else
        return mDataBaseManager.getAll(dataClass)
                .map(realmModels -> mEntityDataMapper.transformAllToDomain(realmModels));
    }

    @NonNull
    @Override
    public Observable<?> searchDisk(String query, String column, Class domainClass, Class dataClass) {
        return mDataBaseManager.getWhere(dataClass, query, column)
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<?> searchDisk(RealmQuery query, Class domainClass) {
        return mDataBaseManager.getWhere(query)
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<?> dynamicDeleteCollection(String url, String idColumnName, JSONArray jsonArray,
                                                 Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.evictCollection(idColumnName,
                ModelConverters.convertToListOfId(jsonArray), dataClass);
//                .doOnNext(o -> {
//                    List<Long> convertToListOfId = ModelConverters.convertToListOfId(jsonArray);
//                    for (int i = 0, convertToListOfIdSize = convertToListOfId != null ? convertToListOfId.size() : 0;
//                         i < convertToListOfIdSize; i++)
//                        Storo.delete(dataClass.getSimpleName() + convertToListOfId.get(i));
//                });
    }

    @NonNull
    @Override
    public Observable<Boolean> dynamicDeleteAll(String url, Class dataClass, boolean persist) {
        return mDataBaseManager.evictAll(dataClass);
//                .doOnNext(o -> Storo.delete(dataClass.getSimpleName()));
    }

    @NonNull
    @Override
    public Observable<?> dynamicPostObject(String url, String idColumnName, JSONObject jsonObject,
                                           Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass);
//                .doOnNext(o -> Storo.put(dataClass.getSimpleName() + jsonObject.opt(idColumnName).toString(),
//                        new Gson().fromJson(jsonObject.toString(), dataClass)).execute());
    }

    @NonNull
    @Override
    public Observable<?> dynamicPostList(String url, String idColumnName, JSONArray jsonArray,
                                         Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.putAll(jsonArray, idColumnName, dataClass);
//                .doOnNext(o -> Storo.put(dataClass.getSimpleName(),
//                        new Gson().fromJson(jsonArray.toString(), dataClass)).execute());
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutObject(String url, String idColumnName, JSONObject jsonObject,
                                          Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.put(jsonObject, idColumnName, dataClass);
//                .doOnNext(o -> Storo.put(dataClass.getSimpleName() + jsonObject.opt(idColumnName).toString(),
//                        new Gson().fromJson(jsonObject.toString(), dataClass)).execute());
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutList(String url, String idColumnName, JSONArray jsonArray,
                                        Class domainClass, Class dataClass, boolean persist, boolean queuable) {
        return mDataBaseManager.putAll(jsonArray, idColumnName, dataClass);
//                .doOnNext(o -> Storo.put(dataClass.getSimpleName(),
//                        new Gson().fromJson(jsonArray.toString(), dataClass)).execute());
    }

    @NonNull
    @Override
    public Observable<?> dynamicUploadFile(String url, File file, String key, HashMap<String, Object> parameters,
                                           boolean onWifi, boolean whileCharging, boolean queuable, Class domainClass) {
        return Observable.error(new IllegalStateException(Config.getInstance().getContext().getString(R.string.io_to_db_error)));
    }

    @NonNull
    @Override
    public Observable<?> dynamicDownloadFile(String url, File file, boolean onWifi, boolean whileCharging,
                                             boolean queuable) {
        return Observable.error(new IllegalStateException(Config.getInstance().getContext().getString(R.string.io_to_db_error)));
    }
}
