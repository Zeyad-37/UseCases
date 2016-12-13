package com.zeyad.usecases.data.db;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zeyad.usecases.data.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * {@link DataBaseManager} implementation.
 */
public class RealmManager implements DataBaseManager {

    private static final String REALM_OBJECT_INVALID = "RealmObject is invalid",
            JSON_INVALID = "JSONObject is invalid", NO_ID = "Could not find id!";
    private static DataBaseManager sInstance;

    private RealmManager() {
    }

    /**
     * Use this function to re-instantiate general realm manager or instance for the first time.
     * Previous instances would be deleted and new created
     */
    static void init() {
        sInstance = new RealmManager();
    }

    /**
     * @return RealmManager the implemented instance of the DatabaseManager.
     */
    static DataBaseManager getInstance() {
        if (sInstance == null)
            init();
        return sInstance;
    }

    private static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * Gets an {@link Observable} which will emit an Object.
     *
     * @param dataClass    Class type of the items to get.
     * @param idColumnName Name of the id field.
     * @param itemId       The user id to retrieve data.
     */
    @NonNull
    @Override
    public Observable<?> getById(@NonNull final String idColumnName, final int itemId, Class dataClass) {
        return Observable.defer(() -> {
            int finalItemId = itemId;
            if (finalItemId <= 0)
                finalItemId = Utils.getMaxId(dataClass, idColumnName);
            if (hasKitKat())
                try (Realm realm = Realm.getDefaultInstance()) {
                    return Observable.just(realm.copyFromRealm(realm.where(dataClass)
                            .equalTo(idColumnName, finalItemId).findFirst()));
                }
            else {
                Realm realm = Realm.getDefaultInstance();
                try {
                    return Observable.just(realm.copyFromRealm(realm.where(dataClass)
                            .equalTo(idColumnName, finalItemId).findFirst()));
                } finally {
                    closeRealm(realm);
                }
            }
        });
    }

    /**
     * Gets an {@link Observable} which will emit a List of Objects.
     *
     * @param clazz Class type of the items to get.
     */
    @NonNull
    @Override
    public Observable<List> getAll(Class clazz) {
        return Observable.defer(() -> {
            if (hasKitKat())
                try (Realm realm = Realm.getDefaultInstance()) {
                    return Observable.just(realm.copyFromRealm(realm.where(clazz).findAll()));
                }
            else {
                Realm realm = Realm.getDefaultInstance();
                try {
                    return Observable.just(realm.copyFromRealm(realm.where(clazz).findAll()));
                } finally {
                    closeRealm(realm);
                }
            }
        });
    }

    /**
     * Get list of items according to the query passed.
     *
     * @param filterKey The key used to look for inside the DB.
     * @param query     The query used to look for inside the DB.
     * @param clazz     Class type of the items to be deleted.
     */
    @NonNull
    @Override
    public Observable<List> getWhere(Class clazz, String query, @NonNull String filterKey) {
        return Observable.defer(() -> {
            if (hasKitKat())
                try (Realm realm = Realm.getDefaultInstance()) {
                    return Observable.just(realm.copyFromRealm(realm.where(clazz)
                            .beginsWith(filterKey, query, Case.INSENSITIVE).findAll()));
                }
            else {
                Realm realm = Realm.getDefaultInstance();
                try {
                    return Observable.just(realm.copyFromRealm(realm.where(clazz)
                            .beginsWith(filterKey, query, Case.INSENSITIVE).findAll()));
                } finally {
                    closeRealm(realm);
                }
            }
        });
    }

    /**
     * Get list of items according to the query passed.
     *
     * @param realmQuery The query used to look for inside the DB.
     */
    @NonNull
    @Override
    public Observable<List> getWhere(@NonNull RealmQuery realmQuery) {
        return Observable.defer(() -> {
            if (hasKitKat())
                try (Realm realm = Realm.getDefaultInstance()) {
                    return Observable.just(realm.copyFromRealm(realmQuery.findAll()));
                }
            else {
                Realm realm = Realm.getDefaultInstance();
                try {
                    return Observable.just(realm.copyFromRealm(realmQuery.findAll()));
                } finally {
                    closeRealm(realm);
                }
            }
        });
    }

    /**
     * Puts and element into the DB.
     *
     * @param realmObject Element to insert in the DB.
     * @param dataClass   Class type of the items to be put.
     */
    @NonNull
    @Override
    public Observable<?> put(@Nullable RealmObject realmObject, @NonNull Class dataClass) {
        if (realmObject != null) {
            return Observable.defer(() -> {
                if (hasKitKat())
                    try (Realm realm = Realm.getDefaultInstance()) {
                        RealmObject result = executeWriteOperationInRealm(realm, () -> realm.copyToRealmOrUpdate(realmObject));
                        if (RealmObject.isValid(result)) {
                            return Observable.just(Boolean.TRUE);
                        } else
                            return Observable.error(new IllegalArgumentException(REALM_OBJECT_INVALID));
                    }
                else {
                    Realm realm = Realm.getDefaultInstance();
                    try {
                        RealmObject result = executeWriteOperationInRealm(realm, () -> realm.copyToRealmOrUpdate(realmObject));
                        if (RealmObject.isValid(result)) {
                            return Observable.just(Boolean.TRUE);
                        } else
                            return Observable.error(new IllegalArgumentException(REALM_OBJECT_INVALID));
                    } finally {
                        closeRealm(realm);
                    }
                }
            });
        }
        return Observable.error(new IllegalArgumentException(REALM_OBJECT_INVALID));
    }

    /**
     * Puts and element into the DB.
     *
     * @param realmModel Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    @NonNull
    @Override
    public Observable<?> put(@Nullable RealmModel realmModel, @NonNull Class dataClass) {
        if (realmModel != null) {
            return Observable.defer(() -> {
                if (hasKitKat())
                    try (Realm realm = Realm.getDefaultInstance()) {
                        RealmModel result = executeWriteOperationInRealm(realm, () -> realm.copyToRealmOrUpdate(realmModel));
                        if (RealmObject.isValid(result)) {
                            return Observable.just(Boolean.TRUE);
                        } else
                            return Observable.error(new IllegalArgumentException(REALM_OBJECT_INVALID));
                    }
                else {
                    Realm realm = Realm.getDefaultInstance();
                    try {
                        RealmModel result = executeWriteOperationInRealm(realm, () -> realm.copyToRealmOrUpdate(realmModel));
                        if (RealmObject.isValid(result)) {
                            return Observable.just(Boolean.TRUE);
                        } else
                            return Observable.error(new IllegalArgumentException(REALM_OBJECT_INVALID));
                    } finally {
                        closeRealm(realm);
                    }
                }
            });
        }
        return Observable.error(new IllegalArgumentException(REALM_OBJECT_INVALID));
    }

    /**
     * Puts and element into the DB.
     *
     * @param jsonObject Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    @NonNull
    @Override
    public Observable<?> put(@Nullable JSONObject jsonObject, @Nullable String idColumnName, @NonNull Class dataClass) {
        if (jsonObject != null) {
            return Observable.defer(() -> {
                try {
                    updateJsonObjectWithIdValue(jsonObject, idColumnName, dataClass);
                } catch (@NonNull JSONException | IllegalArgumentException e) {
                    return Observable.error(e);
                }
                if (hasKitKat())
                    try (Realm realm = Realm.getDefaultInstance()) {
                        RealmModel result = executeWriteOperationInRealm(realm, () -> realm.createOrUpdateObjectFromJson(dataClass, jsonObject));
                        if (RealmObject.isValid(result)) {
                            return Observable.just(Boolean.TRUE);
                        } else
                            return Observable.error(new IllegalArgumentException(REALM_OBJECT_INVALID));
                    }
                else {
                    Realm realm = Realm.getDefaultInstance();
                    try {
                        RealmModel result = executeWriteOperationInRealm(realm, () -> realm.createOrUpdateObjectFromJson(dataClass, jsonObject));
                        if (RealmObject.isValid(result)) {
                            return Observable.just(Boolean.TRUE);
                        } else
                            return Observable.error(new IllegalArgumentException(REALM_OBJECT_INVALID));
                    } finally {
                        closeRealm(realm);
                    }
                }
            });
        } else
            return Observable.defer(() -> Observable.error(new IllegalArgumentException(JSON_INVALID)));
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
    public Observable<?> putAll(@NonNull JSONArray jsonArray, String idColumnName, @NonNull Class dataClass) {
        return Observable.defer(() -> {
            try {
                updateJsonArrayWithIdValue(jsonArray, idColumnName, dataClass);
            } catch (@NonNull JSONException | IllegalArgumentException e) {
                return Observable.error(e);
            }
            if (hasKitKat())
                try (Realm realm = Realm.getDefaultInstance()) {
                    executeWriteOperationInRealm(realm, () -> realm.createOrUpdateAllFromJson(dataClass, jsonArray));
                    return Observable.just(Boolean.TRUE);
                }
            else {
                Realm realm = Realm.getDefaultInstance();
                try {
                    executeWriteOperationInRealm(realm, () -> realm.createOrUpdateAllFromJson(dataClass, jsonArray));
                    return Observable.just(Boolean.TRUE);
                } finally {
                    closeRealm(realm);
                }
            }
        });
    }

    /**
     * Puts and element into the DB.
     *
     * @param realmModels Element to insert in the DB.
     * @param dataClass   Class type of the items to be put.
     */
    @Override
    public Observable<?> putAll(@NonNull List<RealmObject> realmModels, @NonNull Class dataClass) {
        return Observable.defer(() -> {
            if (hasKitKat())
                try (Realm realm = Realm.getDefaultInstance()) {
                    executeWriteOperationInRealm(realm, () -> realm.copyToRealmOrUpdate(realmModels));
                    return Observable.just(Boolean.TRUE);
                }
            else {
                Realm realm = Realm.getDefaultInstance();
                try {
                    executeWriteOperationInRealm(realm, () -> realm.copyToRealmOrUpdate(realmModels));
                    return Observable.just(Boolean.TRUE);
                } finally {
                    closeRealm(realm);
                }
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
    public Observable<Boolean> evictAll(@NonNull Class clazz) {
        return Observable.defer(() -> {
            if (hasKitKat())
                try (Realm realm = Realm.getDefaultInstance()) {
                    executeWriteOperationInRealm(realm, () -> realm.delete(clazz));
                    return Observable.just(Boolean.TRUE);
                }
            else {
                Realm realm = Realm.getDefaultInstance();
                try {
                    executeWriteOperationInRealm(realm, () -> realm.delete(clazz));
                    return Observable.just(Boolean.TRUE);
                } finally {
                    closeRealm(realm);
                }
            }
        });
    }

    /**
     * Evict element of the DB.
     *
     * @param realmModel Element to deleted from the DB.
     * @param clazz      Class type of the items to be deleted.
     */
    @Override
    public void evict(@NonNull final RealmObject realmModel, @NonNull Class clazz) {
        Observable.defer(() -> {
            if (hasKitKat())
                try (Realm realm = Realm.getDefaultInstance()) {
                    executeWriteOperationInRealm(realm, (Executor) realmModel::deleteFromRealm);
                    return Observable.just(Boolean.TRUE);
                }
            else {
                Realm realm = Realm.getDefaultInstance();
                try {
                    executeWriteOperationInRealm(realm, (Executor) realmModel::deleteFromRealm);
                    return Observable.just(Boolean.TRUE);
                } finally {
                    closeRealm(realm);
                }
            }
        }).subscribeOn(Schedulers.immediate())
                .subscribe(new EvictSubscriberClass(clazz));
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
        if (hasKitKat())
            try (Realm realm = Realm.getDefaultInstance()) {
                RealmModel toDelete = realm.where(clazz).equalTo(idFieldName, idFieldValue).findFirst();
                if (toDelete != null) {
                    executeWriteOperationInRealm(realm, () -> RealmObject.deleteFromRealm(toDelete));
                    return !RealmObject.isValid(toDelete);
                } else return false;
            }
        else {
            Realm realm = Realm.getDefaultInstance();
            try {
                RealmModel toDelete = realm.where(clazz).equalTo(idFieldName, idFieldValue).findFirst();
                if (toDelete != null) {
                    executeWriteOperationInRealm(realm, () -> RealmObject.deleteFromRealm(toDelete));
                    return !RealmObject.isValid(toDelete);
                } else return false;
            } finally {
                closeRealm(realm);
            }
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
    public Observable<?> evictCollection(@NonNull String idFieldName, @NonNull List<Long> list,
                                         @NonNull Class dataClass) {
        return Observable.defer(() -> {
            boolean isDeleted = true;
            for (int i = 0, size = list.size(); i < size; i++)
                isDeleted = isDeleted && evictById(dataClass, idFieldName, list.get(i));
            return Observable.just(isDeleted);
        });
    }

    private void closeRealm(Realm realm) {
        try {
            if (!realm.isClosed())
                realm.close();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void executeWriteOperationInRealm(@NonNull Realm realm, @NonNull Executor executor) {
        if (realm.isInTransaction())
            realm.cancelTransaction();
        realm.beginTransaction();
        executor.run();
        realm.commitTransaction();
    }

    private <T> T executeWriteOperationInRealm(@NonNull Realm realm, @NonNull ExecuteAndReturn<T> executor) {
        T toReturnValue;
        if (realm.isInTransaction())
            realm.cancelTransaction();
        realm.beginTransaction();
        toReturnValue = executor.run();
        realm.commitTransaction();
        return toReturnValue;
    }

    @NonNull
    private JSONArray updateJsonArrayWithIdValue(@NonNull JSONArray jsonArray, @Nullable String idColumnName,
                                                 Class dataClass) throws JSONException {
        if (idColumnName == null || idColumnName.isEmpty())
            throw new IllegalArgumentException(NO_ID);
        for (int i = 0, length = jsonArray.length(); i < length; i++)
            if (jsonArray.opt(i) instanceof JSONObject)
                updateJsonObjectWithIdValue(jsonArray.optJSONObject(i), idColumnName, dataClass);
        return jsonArray;
    }

    @NonNull
    private JSONObject updateJsonObjectWithIdValue(@NonNull JSONObject jsonObject, @Nullable String idColumnName,
                                                   Class dataClass) throws JSONException {
        if (idColumnName == null || idColumnName.isEmpty())
            throw new IllegalArgumentException(NO_ID);
        if (jsonObject.optInt(idColumnName) == 0)
            jsonObject.put(idColumnName, Utils.getNextId(dataClass, idColumnName));
        return jsonObject;
    }

    private interface Executor {
        void run();
    }

    private interface ExecuteAndReturn<T> {
        @NonNull
        T run();
    }

    private static class EvictSubscriberClass extends Subscriber<Object> {

        private final Class mClazz;

        EvictSubscriberClass(Class clazz) {
            mClazz = clazz;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(@NonNull Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(Object o) {
            Log.d(RealmManager.class.getName(), mClazz.getName() + " deleted!");
        }

    }
}