package com.zeyad.usecases.app;


import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

/**
 * @author by ZIaDo on 7/12/17.
 */
@Dao
public interface RoomBaseDao<T> {

    @RawQuery
    Flowable<T> getItem(String query);

    @RawQuery
    Flowable<List<T>> getAllItems(String query);

    @RawQuery
    Flowable<T> getItem(SupportSQLiteQuery query);

    @RawQuery
    Flowable<List<T>> getAllItems(SupportSQLiteQuery query);

    @RawQuery
    Flowable<T> getQuery(SupportSQLiteQuery query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItemsReplace(T... objects);

    @Insert()
    void insertItemsAbort(T... objects);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertItemsIgnore(T... objects);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insertItemsFail(T... objects);

    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    void insertItemsRollback(T... objects);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItemsReplace(T... objects);

    @Update(onConflict = OnConflictStrategy.ABORT)
    void updateItemsAbort(T... objects);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateItemsIgnore(T... objects);

    @Update(onConflict = OnConflictStrategy.FAIL)
    void updateItemsFail(T... objects);

    @Update(onConflict = OnConflictStrategy.ROLLBACK)
    void updateItemsRollback(T... objects);

    @Delete
    void deleteItem(T object);
}