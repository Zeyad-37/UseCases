package com.zeyad.usecases.db;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zeyad.usecases.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;
import rx.Subscription;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * {@link DataBaseManager} implementation.
 */
public class RealmManager implements DataBaseManager {

    private static final String REALM_OBJECT_INVALID = "RealmObject is invalid",
            JSON_INVALID = "JSONObject is invalid", NO_ID = "Could not find id!";
    private static Handler backgroundHandler;

    public RealmManager(Handler backgroundHandler) {
        RealmManager.backgroundHandler = backgroundHandler;
    }

    public RealmManager(Looper backgroundLooper) {
        if (backgroundHandler == null)
            backgroundHandler = new Handler(backgroundLooper);
    }

    RealmManager() {
        backgroundHandler = new Handler();
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
                finalItemId = Utils.getInstance().getMaxId(dataClass, idColumnName);
            Realm realm = Realm.getDefaultInstance();
            return realm.where(dataClass).equalTo(idColumnName, finalItemId).findAll().asObservable()
                    .filter(results -> ((RealmResults) results).isLoaded())
                    .map(o -> realm.copyFromRealm((RealmResults) o))
                    .doOnUnsubscribe(() -> closeRealm(realm));
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
            Realm realm = Realm.getDefaultInstance();
            return realm.where(clazz).findAll().asObservable()
                    .filter(results -> ((RealmResults) results).isLoaded())
                    .map(o -> realm.copyFromRealm((RealmResults) o))
                    .doOnUnsubscribe(() -> closeRealm(realm));
        });
    }

    /**
     * Takes a query to be executed and return a list of containing the result.
     *
     * @param queryFactory The query used to look for inside the DB.
     * @param <T>          the return type from the query
     * @return {@link List<T>} a result list that matches the given query.
     */
    @NonNull
    @Override
    public <T extends RealmModel> Observable<List<T>> getQuery(RealmQueryProvider<T> queryFactory) {
        return Observable.defer(() -> {
            Realm realm = Realm.getDefaultInstance();
            return queryFactory.create(realm).findAll().asObservable()
                    .filter(RealmResults::isLoaded)
                    .map(realm::copyFromRealm)
                    .doOnUnsubscribe(() -> closeRealm(realm));
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
    public Observable<?> put(@Nullable RealmModel realmModel, @NonNull Class dataClass) {
        if (realmModel != null) {
            return Observable.defer(() -> {
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
            Realm realm = Realm.getDefaultInstance();
            try {
                executeWriteOperationInRealm(realm, () -> realm.createOrUpdateAllFromJson(dataClass, jsonArray));
                return Observable.just(Boolean.TRUE);
            } finally {
                closeRealm(realm);
            }
        });
    }

    @Override
    public <T extends RealmModel> Observable<?> putAll(List<T> realmObjects, Class dataClass) {
        return Observable.defer(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                executeWriteOperationInRealm(realm, () -> realm.copyToRealmOrUpdate(realmObjects));
                return Observable.just(Boolean.TRUE);
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
    public Observable<Boolean> evictAll(@NonNull Class clazz) {
        return Observable.defer(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                executeWriteOperationInRealm(realm, () -> realm.delete(clazz));
                return Observable.just(Boolean.TRUE);
            } finally {
                closeRealm(realm);
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
        Completable.defer(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                executeWriteOperationInRealm(realm, (Executor) realmModel::deleteFromRealm);
                return Completable.complete();
            } finally {
                closeRealm(realm);
            }
        }).subscribe(new CompletableSubscriber() {
            private Subscription subscription;

            @Override
            public void onCompleted() {
                subscription.unsubscribe();
                Log.d(TAG, clazz.getSimpleName() + " deleted!");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
                subscription.unsubscribe();
            }

            @Override
            public void onSubscribe(Subscription d) {
                subscription = d;
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

    /**
     * Evict a collection elements of the DB.
     *
     * @param idFieldName The id used to look for inside the DB.
     * @param list        List of ids to be deleted.
     * @param dataClass   Class type of the items to be deleted.
     */
    @NonNull
    @Override
    public Observable<Boolean> evictCollection(@NonNull String idFieldName, @NonNull List<Long> list,
                                               @NonNull Class dataClass) {
        return Observable.defer(() -> {
            boolean isDeleted = true;
            for (int i = 0, size = list.size(); i < size; i++)
                isDeleted = isDeleted && evictById(dataClass, idFieldName, list.get(i));
            return Observable.just(isDeleted);
        });
    }

    private void closeRealm(Realm realm) {
        backgroundHandler.post(() -> {
            if (!realm.isClosed()) {
                realm.close();
                Log.d(RealmManager.class.getSimpleName(), "realm instance closed!");
            }
        });
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
            jsonObject.put(idColumnName, Utils.getInstance().getNextId(dataClass, idColumnName));
        return jsonObject;
    }

    private interface Executor {
        void run();
    }

    private interface ExecuteAndReturn<T> {
        @NonNull
        T run();
    }
}
