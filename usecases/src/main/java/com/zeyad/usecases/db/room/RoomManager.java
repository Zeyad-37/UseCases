package com.zeyad.usecases.db.room;

import android.support.annotation.NonNull;

import com.zeyad.usecases.db.DataBaseManager;
import com.zeyad.usecases.db.RealmQueryProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.realm.RealmModel;

/**
 * @author ZIaDo on 1/16/18.
 */

class RoomManager implements DataBaseManager {
    @NonNull
    @Override
    public <M> Flowable<M> getById(@NonNull String idColumnName, Object itemId, Class itemIdType, Class dataClass) {
        return null;
    }

    @NonNull
    @Override
    public <M> Flowable<List<M>> getAll(Class clazz) {
        return null;
    }

    @NonNull
    @Override
    public <M extends RealmModel> Flowable<List<M>> getQuery(RealmQueryProvider<M> queryFactory) {
        return null;
    }

    @NonNull
    @Override
    public <M extends RealmModel> Single<Boolean> put(M realmModel, Class dataClass) {
        return null;
    }

    @NonNull
    @Override
    public Single<Boolean> put(JSONObject jsonObject, String idColumnName, Class itemIdType, Class dataClass) {
        return null;
    }

    @NonNull
    @Override
    public <M extends RealmModel> Single<Boolean> putAll(List<M> realmObjects, Class dataClass) {
        return null;
    }

    @NonNull
    @Override
    public Single<Boolean> putAll(JSONArray jsonArray, String idColumnName, Class itemIdType, Class dataClass) {
        return null;
    }

    @NonNull
    @Override
    public Single<Boolean> evictAll(Class clazz) {
        return null;
    }

    @NonNull
    @Override
    public Single<Boolean> evictCollection(String idFieldName, List<Object> list, Class itemIdType, Class dataClass) {
        return null;
    }

    @Override
    public boolean evictById(Class clazz, String idFieldName, Object idFieldValue, Class itemIdType) {
        return false;
    }
}
