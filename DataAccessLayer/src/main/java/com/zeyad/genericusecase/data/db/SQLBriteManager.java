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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * @author by ZIaDo on 10/2/16.
 */
public class SQLBriteManager implements DataBaseManager {

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

    static DataBaseManager getInstance() {
        if (sInstance == null)
            throw new NullPointerException("Instance have not been initialized yet. " +
                    "Please call init first or getInstance with context as an argument");
        return sInstance;
    }

    @NonNull
    @Override
    public Observable<?> getById(String idColumnName, int id, Class clazz) {
        return mBDb.createQuery(clazz.getSimpleName(), "Select * From " + clazz.getSimpleName()
                + "Where " + idColumnName + " = ?", String.valueOf(id));
    }

    @NonNull
    @Override
    public Observable getAll(Class clazz) {
        return mBDb.createQuery(clazz.getSimpleName(), "Select * From " + clazz.getSimpleName());
    }

    @NonNull
    @Override
    public Observable<?> put(RealmObject realmModel, Class dataClass) {
        return Observable.error(new IllegalStateException("This is not Realm's realm"));
    }

    @NonNull
    @Override
    public Observable<?> put(RealmModel realmModel, Class dataClass) {
        return Observable.error(new IllegalStateException("This is not Realm's realm"));
    }

    @NonNull
    @Override
    public Observable<?> put(JSONObject jsonObject, String idColumnName, Class dataClass) {
        return Observable.error(new IllegalStateException("This is not Realm's realm"));
    }

    @Override
    public Observable<?> put(ContentValues contentValues, Class dataClass) {
        writeToPreferences(System.currentTimeMillis(), DataBaseManager.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                + dataClass.getSimpleName(), "putContentValues");
        return Observable.defer(() -> Observable.just(mBDb.insert(dataClass.getSimpleName(),
                new ContentValues())));
    }

    @Override
    public void putAll(List<RealmObject> realmModels, Class dataClass) {
        throw new IllegalStateException("This is not Realm's realm");
    }

    @Override
    public void putAll(ContentValues[] contentValues, Class dataClass) {
        Observable result = Observable.empty();
        for (int i = 0, contentValuesLength = contentValues.length; i < contentValuesLength; i++)
            result.concatWith(put(contentValues[i], dataClass));
        result.subscribe(new Subscriber() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Object o) {

            }
        });
    }

    @NonNull
    @Override
    public Observable<?> putAll(JSONArray jsonArray, String idColumnName, Class dataClass) {
        return Observable.error(new IllegalStateException("This is not Realm's realm"));
    }

    @Override
    public boolean isCached(int itemId, String columnId, Class clazz) {
        return getById(columnId, itemId, clazz).toBlocking().first() != null;
    }

    @Override
    public boolean isItemValid(int itemId, @NonNull String columnId, @NonNull Class clazz) {
        return isCached(itemId, columnId, clazz) && areItemsValid(DataBaseManager.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                + clazz.getSimpleName());
    }

    @Override
    public boolean areItemsValid(String destination) {
        return (System.currentTimeMillis() - getFromPreferences(destination)) <= EXPIRATION_TIME;
    }

    @NonNull
    @Override
    public Observable evictAll(Class clazz) {
        writeToPreferences(System.currentTimeMillis(), DataBaseManager.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                + clazz.getSimpleName(), "evictAll");
        return Observable.defer(() -> Observable.just(mBDb.delete(clazz.getSimpleName(), "")));
    }

    @Override
    public void evict(RealmObject realmModel, Class clazz) {
        throw new IllegalStateException("This is not Realm's realm");
    }

    @Override
    public boolean evictById(Class clazz, String idFieldName, long idFieldValue) {
        writeToPreferences(System.currentTimeMillis(), DataBaseManager.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                + clazz.getSimpleName(), "evictById");
        return Observable.defer(() -> Observable.just(mBDb.delete(clazz.getSimpleName(), "")))
                .toBlocking().first() > 0;
    }

    @NonNull
    @Override
    public Observable<?> evictCollection(String idFieldName, List<Long> list, Class dataClass) {
        boolean result = true;
        for (int i = 0, listSize = list.size(); i < listSize; i++)
            result &= evictById(dataClass, idFieldName, list.get(i));
        return Observable.just(result);
    }

    @Override
    public Context getContext() {
        return Config.getInstance().getContext();
    }

    @NonNull
    @Override
    public Observable getWhere(@NonNull Class clazz, @NonNull String query, @Nullable String filterKey) {
        return mBDb.createQuery(clazz.getSimpleName(), query);
    }

    @NonNull
    @Override
    public Observable<List<?>> getWhere(RealmQuery realmQuery) {
        return Observable.error(new IllegalStateException("This is not Realm's realm"));
    }

    /**
     * Get a value from a user preferences file.
     *
     * @return A long representing the value retrieved from the preferences file.
     */
    long getFromPreferences(String destination) {
        return Config.getInstance().getContext().getSharedPreferences(Config.getInstance().getPrefFileName(),
                Context.MODE_PRIVATE).getLong(destination, 0);
    }

    /**
     * Write a value to a user preferences file.
     *
     * @param value  A long representing the value to be inserted.
     * @param source which method is making this call
     */
    void writeToPreferences(long value, String destination, String source) {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(Config.getInstance().getPrefFileName(),
                Context.MODE_PRIVATE).edit();
        if (editor == null)
            return;
        editor.putLong(destination, value);
        editor.apply();
        Log.d(TAG, source + " writeToPreferencesTo " + destination + ": " + value);
    }
}
