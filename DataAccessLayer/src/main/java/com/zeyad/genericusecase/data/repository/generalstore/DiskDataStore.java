package com.zeyad.genericusecase.data.repository.generalstore;

import android.support.annotation.NonNull;

import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.utils.ModelConverters;
import com.zeyad.genericusecase.data.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;

public class DiskDataStore implements DataStore {

    private DataBaseManager mRealmManager;
    private EntityMapper mEntityDataMapper;
    public final String TAG = DiskDataStore.class.getName();

    /**
     * Construct a {@link DataStore} based file system data store.
     *
     * @param realmManager A {@link DataBaseManager} to cache data retrieved from the api.
     */
    public DiskDataStore(DataBaseManager realmManager, EntityMapper entityDataMapper) {
        mRealmManager = realmManager;
        mEntityDataMapper = entityDataMapper;
    }

    @NonNull
    @Override
    public Observable<List> dynamicGetList(String url, Class domainClass, Class dataClass, boolean persist,
                                           boolean shouldCache) {
        return mRealmManager.getAll(dataClass)
                .map(realmModels -> mEntityDataMapper.transformAllToDomain(realmModels))
                .compose(Utils.logSources(TAG, mRealmManager));
    }

    @NonNull
    @Override
    public Observable<?> dynamicGetObject(String url, String idColumnName, int itemId, Class domainClass,
                                          Class dataClass, boolean persist, boolean shouldCache) {
        return mRealmManager.getById(idColumnName, itemId, dataClass)
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass) {
        return mRealmManager.getWhere(dataClass, query, column)
                .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<List> searchDisk(RealmQuery query, Class domainClass) {
        return mRealmManager.getWhere(query)
                .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<?> dynamicDownloadFile(String url, File file, boolean onWifi, boolean whileCharging) {
        return Observable.error(new IllegalStateException("Can't download file to local DB!"));
    }

    @NonNull
    @Override
    public Observable<?> dynamicDeleteCollection(String url, String idColumnName, JSONArray jsonArray,
                                                 Class dataClass, boolean persist) {
        return Observable.defer(() -> mRealmManager.evictCollection(idColumnName,
                ModelConverters.convertToListOfId(jsonArray), dataClass));
    }

    @NonNull
    @Override
    public Observable<Boolean> dynamicDeleteAll(String url, Class dataClass, boolean persist) {
        return mRealmManager.evictAll(dataClass);
    }

    @NonNull
    @Override
    public Observable<?> dynamicPostObject(String url, String idColumnName, JSONObject keyValuePairs,
                                           Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> mRealmManager.put(keyValuePairs, idColumnName, dataClass));
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutObject(String url, String idColumnName, JSONObject jsonObject,
                                          Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> mRealmManager.put(jsonObject, idColumnName, dataClass));
    }

    @NonNull
    @Override
    public Observable<?> dynamicUploadFile(String url, File file, boolean onWifi, boolean whileCharging,
                                           Class domainClass) {
        return Observable.error(new IllegalStateException("Can't upload file to local DB!"));
    }

    @NonNull
    @Override
    public Observable<?> dynamicPostList(String url, String idColumnName, JSONArray jsonArray,
                                         Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> mRealmManager.putAll(jsonArray, idColumnName, dataClass));
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutList(String url, String idColumnName, JSONArray jsonArray,
                                        Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> mRealmManager.putAll(jsonArray, idColumnName, dataClass));
    }
}
