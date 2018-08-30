package com.zeyad.usecases.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposables;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * {@link DataBaseManager} implementation.
 */
public class RealmManager implements DataBaseManager {

    private static final String NO_ID = "Could not find id!";

    /**
     * Gets an {@link Flowable} which will emit an Object
     *
     * @param idColumnName name of ID variable
     * @param itemId       ID value
     * @param itemIdType   type of the ID
     * @param dataClass    type of the data requested
     * @param <M>          type of the data requested
     * @return a {@link Flowable} containing an object of type M.
     */
    @NonNull
    @Override
    public <M> Flowable<M> getById(@NonNull final String idColumnName, final Object itemId,
                                   final Class itemIdType, Class dataClass) {
        return Flowable.defer(() -> {
            Realm realm = Realm.getDefaultInstance();
            return getItemById(realm, dataClass, idColumnName, itemId, itemIdType)
                    .map(realmModel -> (M) realm.copyFromRealm(realmModel));
        });
    }

    /**
     * Gets an {@link Flowable} which will emit a List of Objects.
     *
     * @param clazz Class type of the items to get.
     */
    @NonNull
    @Override
    public <M> Flowable<List<M>> getAll(Class clazz) {
        return Flowable.defer(() -> {
            Realm realm = Realm.getDefaultInstance();
            return realm.where(clazz).findAll().asFlowable()
                    .filter(o -> ((RealmResults) o).isLoaded())
                    .map(o -> realm.copyFromRealm((RealmResults) o))
                    .flatMap(o -> ((List) o).isEmpty() ?
                            Flowable.error(new IllegalAccessException(String
                                    .format("%s(s) were not found!", clazz.getSimpleName())))
                            : Flowable.just(o));
        });
    }

    /**
     * Takes a query to be executed and return a list of containing the result.
     *
     * @param queryFactory The query used to look for inside the DB.
     * @param <M>          the return type from the query
     * @return {@link List<M>} a result list that matches the given query.
     */
    @NonNull
    @Override
    public <M extends RealmModel> Flowable<List<M>> getQuery(@NonNull RealmQueryProvider<M> queryFactory) {
        return Flowable.defer(() -> {
            Realm realm = Realm.getDefaultInstance();
            return queryFactory.create(realm).findAll().asFlowable()
                    .filter(RealmResults::isLoaded)
                    .map(realm::copyFromRealm);
        });
    }

    /**
     * Puts and element into the DB.
     *
     * @param realmModel Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    @NonNull
    @Override
    // TODO: 6/13/17 Remove ? remove : create flow
    public <M extends RealmModel> Single<Boolean> put(@NonNull M realmModel, @NonNull Class dataClass) {
        return Single.fromCallable(() ->
                RealmObject.isValid(executeWriteOperationInRealm(Realm.getDefaultInstance(),
                        (ExecuteAndReturn<M>) realm -> realm.copyToRealmOrUpdate(realmModel))));
    }


    /**
     * Puts and element into the DB.
     *
     * @param jsonObject Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    @NonNull
    @Override
    public Single<Boolean> put(@Nullable JSONObject jsonObject, @Nullable String idColumnName,
                               Class itemIdType, @NonNull Class dataClass) {
        return Single.fromCallable(() -> {
            if (jsonObject == null) {
                throw new IllegalArgumentException("JSONObject is invalid");
            } else {
                final JSONObject updatedJSON =
                        updateJsonObjectWithIdValue(jsonObject, idColumnName, itemIdType, dataClass);
                return RealmObject.isValid(executeWriteOperationInRealm(Realm.getDefaultInstance(),
                        (ExecuteAndReturn<RealmModel>) realm ->
                                realm.createOrUpdateObjectFromJson(dataClass, updatedJSON)));
            }
        });
    }

    @NonNull
    @Override
    public <T extends RealmModel> Single<Boolean> putAll(List<T> realmObjects, Class dataClass) {
        return Single.fromCallable(() -> executeWriteOperationInRealm(Realm.getDefaultInstance(),
                (ExecuteAndReturn<List<T>>) realm ->
                        realm.copyToRealmOrUpdate(realmObjects)).size() == realmObjects.size());
    }

    /**
     * Puts and element into the DB.
     *
     * @param jsonArray    Element to insert in the DB.
     * @param idColumnName Name of the id field.
     * @param dataClass    Class type of the items to be put.
     */
    @NonNull
    @Override
    public Single<Boolean> putAll(@NonNull JSONArray jsonArray, String idColumnName, Class itemIdType,
                                  @NonNull Class dataClass) {
        return Single.fromCallable(() -> {
            final JSONArray updatedJSONArray =
                    updateJsonArrayWithIdValue(jsonArray, idColumnName, itemIdType, dataClass);
            executeWriteOperationInRealm(Realm.getDefaultInstance(), (Execute) realm ->
                    realm.createOrUpdateAllFromJson(dataClass, updatedJSONArray));
            return true;
        });
    }

    /**
     * Evict all elements of the DB.
     *
     * @param clazz Class type of the items to be deleted.
     */
    @NonNull
    @Override
    public Single<Boolean> evictAll(@NonNull Class clazz) {
        return Single.fromCallable(() -> {
            executeWriteOperationInRealm(Realm.getDefaultInstance(), (Execute) realm -> realm.delete(clazz));
            return true;
        });
    }

    /**
     * Evict a collection elements of the DB.
     *
     * @param idFieldName The id used to look for inside the DB.
     * @param list        List of ids to be deleted.
     * @param dataClass   Class type of the items to be deleted.
     */
    @NonNull
    @Override
    public Single<Boolean> evictCollection(@NonNull String idFieldName, @NonNull List<Object> list,
                                           final Class itemIdType, @NonNull Class dataClass) {
        return Single.fromCallable(() -> list.isEmpty() ? false : Observable.fromIterable(list)
                .map(id -> evictById(dataClass, idFieldName, id, itemIdType))
                .reduce((aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .blockingGet());
    }

    /**
     * Evict element by id of the DB.
     *
     * @param dataClass    Class type of the items to be deleted.
     * @param idColumnName The id used to look for inside the DB.
     * @param itemId       Name of the id field.
     * @param itemIdType   Class type of the item id to be deleted.
     */
    @Override
    public boolean evictById(@NonNull Class dataClass, @NonNull String idColumnName, final Object itemId,
                             final Class itemIdType) {
        Realm realm = Realm.getDefaultInstance();
        return getItemById(realm, dataClass, idColumnName, itemId, itemIdType)
                .map(realmModel -> {
                    if (realmModel == null) {
                        return false;
                    } else {
                        executeWriteOperationInRealm(realm, (Execute) unused -> RealmObject.deleteFromRealm
                                (realmModel));
                        return !RealmObject.isValid(realmModel);
                    }
                })
                .blockingFirst();
    }

    private Flowable<RealmModel> getItemById(Realm realm, @NonNull Class dataClass, @NonNull String idColumnName,
                                             final Object itemId, final Class itemIdType) {
        Object result;
        if (itemIdType.equals(long.class) || itemIdType.equals(Long.class)) {
            result = realm.where(dataClass).equalTo(idColumnName, (long) itemId).findFirst();
        } else if (itemIdType.equals(int.class) || itemIdType.equals(Integer.class)) {
            result = realm.where(dataClass).equalTo(idColumnName, (int) itemId).findFirst();
        }
        //        else if (itemIdType.equals(short.class) || itemIdType.equals(Short.class)) {
        //            result = realm.where(dataClass).equalTo(idColumnName, (short) itemId).findFirst();
        //        }
        else if (itemIdType.equals(byte.class) || itemIdType.equals(Byte.class)) {
            result = realm.where(dataClass).equalTo(idColumnName, (byte) itemId).findFirst();
        } else if (itemIdType.equals(String.class)) {
            result = realm.where(dataClass).equalTo(idColumnName, String.valueOf(itemId)).findFirst();
        } else {
            return Flowable.error(new IllegalArgumentException("Unsupported ID type!"));
        }
        if (result == null) {
            return Flowable.error(new IllegalAccessException(String
                    .format("%s with ID: %s was not found!", dataClass.getSimpleName(), itemId)));
        }
        return Flowable.just((RealmModel) result);
    }

    private void executeWriteOperationInRealm(@NonNull Realm realm, @NonNull Execute execute) {
        if (realm.isInTransaction()) {
            realm.cancelTransaction();
        }
        realm.beginTransaction();
        execute.run(realm);
        realm.commitTransaction();
    }

    private <T> T executeWriteOperationInRealm(@NonNull Realm realm, @NonNull ExecuteAndReturn<T> executor) {
        T toReturnValue;
        if (realm.isInTransaction()) {
            realm.cancelTransaction();
        }
        realm.beginTransaction();
        toReturnValue = executor.runAndReturn(realm);
        realm.commitTransaction();
        return toReturnValue;
    }

    @NonNull
    private JSONArray updateJsonArrayWithIdValue(@NonNull JSONArray jsonArray, @Nullable String idColumnName,
                                                 Class itemIdType, Class dataClass) throws JSONException {
        if (idColumnName == null || idColumnName.isEmpty()) {
            throw new IllegalArgumentException(NO_ID);
        }
        int length = jsonArray.length();
        JSONArray updatedJSONArray = new JSONArray();
        for (int i = 0; i < length; i++) {
            if (jsonArray.opt(i) instanceof JSONObject) {
                updatedJSONArray.put(updateJsonObjectWithIdValue(jsonArray.optJSONObject(i), idColumnName, itemIdType,
                        dataClass));
            }
        }
        return updatedJSONArray;
    }

    @NonNull
    private JSONObject updateJsonObjectWithIdValue(@NonNull JSONObject jsonObject, @Nullable String idColumnName,
                                                   Class itemIdType, Class dataClass) throws JSONException {
        if (idColumnName == null || idColumnName.isEmpty()) {
            throw new IllegalArgumentException(NO_ID);
        }
        if (itemIdType.equals(String.class)) {
            return jsonObject;
        } else if (jsonObject.optInt(idColumnName) == 0) {
            jsonObject.put(idColumnName, getNextId(dataClass, idColumnName));
        }
        return jsonObject;
    }

    private int getNextId(Class clazz, String column) {
        Realm realm = Realm.getDefaultInstance();
        try {
            Number currentMax = realm.where(clazz).max(column);
            return currentMax != null ? currentMax.intValue() + 1 : 1;
        } finally {
            realm.close();
        }
    }

    @Deprecated
    private Flowable<Realm> getRealm(Realm realm) {
        return Flowable.create(emitter -> {
            RealmConfiguration realmConfiguration = realm.getConfiguration();
            Realm observableRealm = Realm.getInstance(realmConfiguration);
            final RealmChangeListener<Realm> listener = emitter::onNext;
            emitter.setDisposable(Disposables.fromRunnable(() -> {
                observableRealm.removeChangeListener(listener);
                observableRealm.close();
                Log.d(RealmManager.class.getSimpleName(), "Realm instance closed!");
            }));
            observableRealm.addChangeListener(listener);
            emitter.onNext(observableRealm);
        }, BackpressureStrategy.BUFFER);
    }

    private interface Execute {
        void run(Realm realm);
    }

    private interface ExecuteAndReturn<T> {
        @NonNull
        T runAndReturn(Realm realm);
    }
}
