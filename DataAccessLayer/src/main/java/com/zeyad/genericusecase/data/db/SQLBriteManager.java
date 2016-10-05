package com.zeyad.genericusecase.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @author by ZIaDo on 10/2/16.
 */
class SQLBriteManager implements DataBaseManager {

    private static DataBaseManager sInstance;
    private final String TAG = SQLBriteManager.class.getName();
    private BriteDatabase mBDb;

    private SQLBriteManager(SQLiteOpenHelper sqLiteOpenHelper) {
        mBDb = SqlBrite.create().wrapDatabaseHelper(sqLiteOpenHelper, Schedulers.io());
    }

    /**
     * Use this function to re-instantiate general realm manager or instance for the first time.
     * Previous instances would be deleted and new created
     */
    static void init(SQLiteOpenHelper sqLiteOpenHelper) {
        sInstance = new SQLBriteManager(sqLiteOpenHelper);
    }

    /**
     * @return SQLBriteManager the implemented instance of the DatabaseManager.
     */
    static DataBaseManager getInstance() {
        if (sInstance == null)
            throw new NullPointerException(Config.getInstance().getContext().getString(R.string.sqlbrite_uninitialized));
        return sInstance;
    }

    /**
     * Gets an {@link Observable} which will emit an Object.
     *
     * @param clazz        Class type of the items to get.
     * @param idColumnName Name of the id field.
     * @param id           The user id to retrieve data.
     */
    @NonNull
    @Override
    public Observable<?> getById(String idColumnName, int id, Class clazz) {
        return mBDb.createQuery(clazz.getSimpleName(), getContext().getString(R.string.select_from_where,
                clazz.getSimpleName(), idColumnName), String.valueOf(id));
    }

    /**
     * Gets an {@link Observable} which will emit a List of Objects.
     *
     * @param clazz Class type of the items to get.
     */
    @NonNull
    @Override
    public Observable getAll(Class clazz) {
        return mBDb.createQuery(clazz.getSimpleName(), getContext().getString(R.string.select_from,
                clazz.getSimpleName()));
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
    public Observable getWhere(@NonNull Class clazz, @NonNull String query, @Nullable String filterKey) {
        return mBDb.createQuery(clazz.getSimpleName(), query, filterKey);
    }

    /**
     * Get list of items according to the query passed.
     *
     * @param realmQuery The query used to look for inside the DB.
     */
    @NonNull
    @Override
    public Observable<List<?>> getWhere(RealmQuery realmQuery) {
        return Observable.error(new IllegalStateException(getContext().getString(R.string.not_realm)));
    }

    /**
     * Puts and element into the DB.
     *
     * @param realmModel Element to insert in the DB.
     * @param dataClass   Class type of the items to be put.
     */
    @NonNull
    @Override
    public Observable<?> put(RealmObject realmModel, Class dataClass) {
        return Observable.error(new IllegalStateException(getContext().getString(R.string.not_realm)));
    }

    /**
     * Puts and element into the DB.
     *
     * @param realmModel Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    @NonNull
    @Override
    public Observable<?> put(RealmModel realmModel, Class dataClass) {
        return Observable.error(new IllegalStateException(getContext().getString(R.string.not_realm)));
    }

    /**
     * Puts and element into the DB.
     *
     * @param jsonObject Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    @NonNull
    @Override
    public Observable<?> put(JSONObject jsonObject, String idColumnName, Class dataClass) {
        return Observable.error(new IllegalStateException(getContext().getString(R.string.not_realm)));
    }

    /**
     * Puts and element into the DB.
     *
     * @param contentValues Element to insert in the DB.
     * @param dataClass     Class type of the items to be put.
     */
    @Override
    public Observable<?> put(ContentValues contentValues, Class dataClass) {
        return Observable.defer(() -> {
            writeToPreferences(System.currentTimeMillis(), DataBaseManager.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                    + dataClass.getSimpleName(), "putContentValues");
            return Observable.just(mBDb.insert(dataClass.getSimpleName(), contentValues));
        });
    }

    /**
     * Puts and element into the DB.
     *
     * @param realmModels Element to insert in the DB.
     * @param dataClass    Class type of the items to be put.
     */
    @Override
    public void putAll(List<RealmObject> realmModels, Class dataClass) {
        throw new IllegalStateException(getContext().getString(R.string.not_realm));
    }

    /**
     * Puts and element into the DB.
     *
     * @param contentValues Element to insert in the DB.
     * @param dataClass     Class type of the items to be put.
     */
    @NonNull
    @Override
    public Observable putAll(ContentValues[] contentValues, Class dataClass) {
        Observable result = Observable.empty();
        for (ContentValues contentValue : contentValues)
            result.concatWith(put(contentValue, dataClass));
        return result;
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
    public Observable<?> putAll(JSONArray jsonArray, String idColumnName, Class dataClass) {
        return Observable.error(new IllegalStateException(getContext().getString(R.string.not_realm)));
    }

    /**
     * Checks if an element (User) exists in the DB.
     *
     * @param itemId   The id used to look for inside the DB.
     * @param columnId Name of the id field.
     * @param clazz    Class type of the items to check.
     * @return true if the element is cached, otherwise false.
     */
    @Override
    public boolean isCached(int itemId, String columnId, Class clazz) {
        return getById(columnId, itemId, clazz).toBlocking().first() != null;
    }

    /**
     * Checks if the DB is expired.
     *
     * @param itemId   The id used to look for inside the DB.
     * @param columnId Name of the id field.
     * @param clazz    Class type of the items to check.
     * @return true, the DB is expired, otherwise false.
     */
    @Override
    public boolean isItemValid(int itemId, @NonNull String columnId, @NonNull Class clazz) {
        return isCached(itemId, columnId, clazz) && areItemsValid(DataBaseManager.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                + clazz.getSimpleName());
    }

    /**
     * Checks if the DB is expired.
     *
     * @param destination Name DB destination.
     * @return true, the DB is expired, otherwise false.
     */
    @Override
    public boolean areItemsValid(String destination) {
        return (System.currentTimeMillis() - getFromPreferences(destination)) <= EXPIRATION_TIME;
    }

    /**
     * Evict all elements of the DB.
     *
     * @param clazz Class type of the items to be deleted.
     */
    @NonNull
    @Override
    public Observable evictAll(Class clazz) {
        return Observable.defer(() -> {
            writeToPreferences(System.currentTimeMillis(), DataBaseManager.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE
                    + clazz.getSimpleName(), "evictAll");
            return Observable.just(mBDb.delete(clazz.getSimpleName(), ""));
        });
    }

    /**
     * Evict element of the DB.
     *
     * @param realmModel Element to deleted from the DB.
     * @param clazz      Class type of the items to be deleted.
     */
    @Override
    public void evict(RealmObject realmModel, Class clazz) {
        throw new IllegalStateException(getContext().getString(R.string.not_realm));
    }

    /**
     * Evict element by id of the DB.
     *
     * @param clazz        Class type of the items to be deleted.
     * @param idFieldName  The id used to look for inside the DB.
     * @param idFieldValue Name of the id field.
     */
    @Override
    public boolean evictById(Class clazz, String idFieldName, long idFieldValue) {
        return Observable.defer(() -> {
            writeToPreferences(System.currentTimeMillis(), DataBaseManager.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                    + clazz.getSimpleName(), "evictById");
            return Observable.just(mBDb.delete(clazz.getSimpleName(), getContext().getString(R.string.where,
                    idFieldName), String.valueOf(idFieldValue)));
        }).toBlocking().first() > 0;
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
    public Observable<?> evictCollection(String idFieldName, List<Long> list, Class dataClass) {
        boolean result = true;
        for (int i = 0, listSize = list.size(); i < listSize; i++)
            result &= evictById(dataClass, idFieldName, list.get(i));
        return Observable.just(result);
    }

    /**
     * @return application Context.
     */
    @Override
    public Context getContext() {
        return Config.getInstance().getContext();
    }

    /**
     * Get a value from a user preferences file.
     *
     * @return A long representing the value retrieved from the preferences file.
     */
    private long getFromPreferences(String destination) {
        return Config.getInstance().getContext().getSharedPreferences(Config.getInstance().getPrefFileName(),
                Context.MODE_PRIVATE).getLong(destination, 0);
    }

    /**
     * Write a value to a user preferences file.
     *
     * @param value  A long representing the value to be inserted.
     * @param source which method is making this call
     */
    private void writeToPreferences(long value, String destination, String source) {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(Config.getInstance().getPrefFileName(),
                Context.MODE_PRIVATE).edit();
        if (editor == null)
            return;
        editor.putLong(destination, value);
        editor.apply();
        Log.d(TAG, source + " writeToPreferencesTo " + destination + ": " + value);
    }
}
