package com.zeyad.usecases.db;

import android.os.Handler;
import android.os.Looper;
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
    private final Handler backgroundHandler;

    public RealmManager(Looper backgroundLooper) {
        backgroundHandler = new Handler(backgroundLooper);
    }

    /**
     * Gets an {@link Flowable} which will emit an Object
     * @param idColumnName name of ID variable
     * @param itemId ID value
     * @param itemIdType type of the ID
     * @param dataClass type of the data requested
     * @param <M> type of the data requested
     * @return a {@link Flowable} containing an object of type M.
     */
    @NonNull
    @Override
    public <M> Flowable<M> getById(@NonNull final String idColumnName, final Object itemId,
                                   final Class itemIdType, Class dataClass) {
        return Flowable.defer(() -> getRealm(Realm.getDefaultInstance()).flatMap(realm1 -> {
            RealmModel result;
            if (itemIdType.equals(long.class) || itemIdType.equals(Long.class)) {
                result = realm1.where(dataClass).equalTo(idColumnName, (long) itemId).findFirst();
            } else if (itemIdType.equals(int.class) || itemIdType.equals(Integer.class)) {
                result = realm1.where(dataClass).equalTo(idColumnName, (int) itemId).findFirst();
            } else if (itemIdType.equals(short.class) || itemIdType.equals(Short.class)) {
                result = realm1.where(dataClass).equalTo(idColumnName, (short) itemId).findFirst();
            } else if (itemIdType.equals(byte.class) || itemIdType.equals(Byte.class)) {
                result = realm1.where(dataClass).equalTo(idColumnName, (byte) itemId).findFirst();
            } else if (itemIdType.equals(String.class)) {
                result = realm1.where(dataClass).equalTo(idColumnName, String.valueOf(itemId)).findFirst();
            } else {
                return Flowable.error(new IllegalArgumentException("Unsupported ID type!"));
            }
            if (result == null) {
                return Flowable.error(new IllegalAccessException(String
                        .format("%s with ID: %s was not found!", dataClass.getSimpleName(), itemId)));
            }
            return Flowable.just((M) realm1.copyFromRealm(result));
        }));
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
            return getRealm(realm)
                    .map(realm1 -> realm1.where(clazz).findAll())
                    .filter(RealmResults::isLoaded)
                    .<List<M>>map(realm::copyFromRealm)
                    .flatMap(ms -> ms.isEmpty() ? Flowable.error(new IllegalAccessException(String
                            .format("%s were not found!", clazz.getSimpleName()))) : Flowable.just(ms));
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
            return getRealm(realm).map(realm1 -> queryFactory.create(realm).findAll())
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
    public <M extends RealmModel> Single<Boolean> put(@Nullable M realmModel, @NonNull Class dataClass) {
        return Single.fromCallable(() -> {
            if (realmModel == null) {
                throw new IllegalArgumentException("RealmObject is null");
            } else {
//                return getRealm(Realm.getDefaultInstance()).map(realm1 ->
//                        RealmObject.isValid(executeWriteOperationInRealm(realm1,
//                                () -> realm1.copyToRealmOrUpdate(realmModel))))
//                        .blockingFirst();
                Realm realm = Realm.getDefaultInstance();
                try {
                    RealmModel result =
                            executeWriteOperationInRealm(realm, () -> realm.copyToRealmOrUpdate(realmModel));
                    return RealmObject.isValid(result);
                } finally {
                    closeRealm(realm);
                }
            }
        });
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
                               @NonNull Class dataClass) {
        return Single.fromCallable(() -> {
            if (jsonObject == null) {
                throw new IllegalArgumentException("JSONObject is invalid");
            } else {
                updateJsonObjectWithIdValue(jsonObject, idColumnName, dataClass);
//                return getRealm(Realm.getDefaultInstance()).map(realm1 ->
//                        RealmObject.isValid(executeWriteOperationInRealm(realm1,
//                                () -> realm1.createOrUpdateObjectFromJson(dataClass, jsonObject))))
//                        .blockingFirst();
                Realm realm = Realm.getDefaultInstance();
                try {
                    RealmModel result = executeWriteOperationInRealm(realm,
                            () -> realm.createOrUpdateObjectFromJson(dataClass, jsonObject));
                    return RealmObject.isValid(result);
                } finally {
                    closeRealm(realm);
                }
            }
        });
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
    public Single<Boolean> putAll(@NonNull JSONArray jsonArray, String idColumnName, @NonNull Class dataClass) {
        return Single.fromCallable(() -> {
            updateJsonArrayWithIdValue(jsonArray, idColumnName, dataClass);
//            return getRealm(Realm.getDefaultInstance()).map(realm1 -> {
//                executeWriteOperationInRealm(realm1, () ->
//                        realm1.createOrUpdateAllFromJson(dataClass, jsonArray));
//                return true;
//            }).blockingFirst();
            Realm realm = Realm.getDefaultInstance();
            try {
                executeWriteOperationInRealm(realm,
                        () -> realm.createOrUpdateAllFromJson(dataClass, jsonArray));
                return true;
            } finally {
                closeRealm(realm);
            }
        });
    }

    @NonNull
    @Override
    public <T extends RealmModel> Single putAll(List<T> realmObjects, Class dataClass) {
        return Single.fromCallable(() -> {
//            return getRealm(Realm.getDefaultInstance()).map(realm1 -> {
//                executeWriteOperationInRealm(realm1,
//                        () -> realm1.copyToRealmOrUpdate(realmObjects));
//                return true;
//            }).blockingFirst();
            Realm realm = Realm.getDefaultInstance();
            try {
                executeWriteOperationInRealm(realm, () -> realm.copyToRealmOrUpdate(realmObjects));
                return true;
            } finally {
                closeRealm(realm);
            }
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
//            return getRealm(Realm.getDefaultInstance()).map(realm1 -> {
//                executeWriteOperationInRealm(realm1, () -> realm1.delete(clazz));
//                return true;
//            }).blockingFirst();
            Realm realm = Realm.getDefaultInstance();
            try {
                executeWriteOperationInRealm(realm, () -> realm.delete(clazz));
                return true;
            } finally {
                closeRealm(realm);
            }
        });
    }

    /**
     * Evict element by id of the DB.
     *
     * @param clazz        Class type of the items to be deleted.
     * @param idFieldName  The id used to look for inside the DB.
     * @param idFieldValue Name of the id field.
     */
    @Override
    public boolean evictById(@NonNull Class clazz, @NonNull String idFieldName, final long idFieldValue) {
        Realm realm = Realm.getDefaultInstance();
//        return getRealm(Realm.getDefaultInstance()).map(realm1 ->
//                realm1.where(clazz).equalTo(idFieldName, idFieldValue).findFirst())
//                .map(realmModel -> {
//                    if (realmModel == null) {
//                        return false;
//                    } else {
//                        executeWriteOperationInRealm(realm, () -> RealmObject.deleteFromRealm(realmModel));
//                        return !RealmObject.isValid(realmModel);
//                    }
//                })
//                .blockingFirst();
        try {
            RealmModel toDelete = realm.where(clazz).equalTo(idFieldName, idFieldValue).findFirst();
            if (toDelete == null) {
                return false;
            } else {
                //                executeWriteOperationInRealm(realm, () -> RealmObject.deleteFromRealm(toDelete));
                executeWriteOperationInRealm(realm, new Execute() {
                    @Override
                    public void run() {
                        RealmObject.deleteFromRealm(toDelete);
                    }
                });
                return !RealmObject.isValid(toDelete);
            }
        } finally {
            closeRealm(realm);
        }
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
    public Single<Boolean> evictCollection(@NonNull String idFieldName, @NonNull List<Long> list,
                                           @NonNull Class dataClass) {
        return Single.fromCallable(() -> Observable.fromIterable(list)
                .map(aLong -> evictById(dataClass, idFieldName, aLong))
                .reduce((aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .blockingGet());
    }

    private void closeRealm(@NonNull Realm realm) {
        backgroundHandler.post(() -> {
            if (!realm.isClosed()) {
                realm.close();
                Log.d(RealmManager.class.getSimpleName(), "realm instance closed!");
            }
        });
    }

    private void executeWriteOperationInRealm(@NonNull Realm realm, @NonNull Execute execute) {
        if (realm.isInTransaction()) {
            realm.cancelTransaction();
        }
        realm.beginTransaction();
        execute.run();
        realm.commitTransaction();
    }

    private <T> T executeWriteOperationInRealm(@NonNull Realm realm, @NonNull ExecuteAndReturn<T> executor) {
        T toReturnValue;
        if (realm.isInTransaction()) {
            realm.cancelTransaction();
        }
        realm.beginTransaction();
        toReturnValue = executor.run();
        realm.commitTransaction();
        return toReturnValue;
    }

    @NonNull
    private JSONArray updateJsonArrayWithIdValue(@NonNull JSONArray jsonArray, @Nullable String idColumnName,
                                                 Class dataClass) throws JSONException {
        if (idColumnName == null || idColumnName.isEmpty()) {
            throw new IllegalArgumentException(NO_ID);
        }
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            if (jsonArray.opt(i) instanceof JSONObject) {
                updateJsonObjectWithIdValue(jsonArray.optJSONObject(i), idColumnName, dataClass);
            }
        }
        return jsonArray;
    }

    // TODO: 6/6/17 Added String support
    @NonNull
    private JSONObject updateJsonObjectWithIdValue(@NonNull JSONObject jsonObject, @Nullable String idColumnName,
                                                   Class dataClass) throws JSONException {
        if (idColumnName == null || idColumnName.isEmpty()) {
            throw new IllegalArgumentException(NO_ID);
        }
        if (jsonObject.optInt(idColumnName) == 0) {
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

    private Flowable<Realm> getRealm(Realm realm) {
        return Flowable.create(emitter -> {
            RealmConfiguration realmConfiguration = realm.getConfiguration();
            Realm observableRealm = Realm.getInstance(realmConfiguration);
            final RealmChangeListener<Realm> listener = emitter::onNext;
            emitter.setDisposable(Disposables.fromRunnable(() -> {
                observableRealm.removeChangeListener(listener);
                observableRealm.close();
                Log.d(RealmManager.class.getSimpleName(), "realm instance closed!");
            }));
            observableRealm.addChangeListener(listener);
            emitter.onNext(observableRealm);
        }, BackpressureStrategy.BUFFER);
    }

    private interface Execute {
        void run();
    }

    private interface ExecuteAndReturn<T> {
        @NonNull
        T run();
    }
}
