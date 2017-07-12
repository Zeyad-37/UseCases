package com.zeyad.usecases.app;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

/**
 * @author by ZIaDo on 7/12/17.
 */
@Dao
public interface RoomBaseDao<T> {

//    @Query(value = ":query")
    //    Flowable<T> getItem(String query);
//
//    @Query(value = ":query")
    //    Flowable<List<T>> getAllItems(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItemsReplace(T... objects);

    //    @Insert(onConflict = OnConflictStrategy.ABORT)
    //    void insertItemsAbort(T... objects);
    //
    //    @Insert(onConflict = OnConflictStrategy.IGNORE)
    //    void insertItemsIgnore(T... objects);
    //
    //    @Insert(onConflict = OnConflictStrategy.FAIL)
    //    void insertItemsFail(T... objects);
    //
    //    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    //    void insertItemsRollback(T... objects);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItemsReplace(T... objects);

    //    @Update(onConflict = OnConflictStrategy.ABORT)
    //    void updateItemsAbort(T... objects);
    //
    //    @Update(onConflict = OnConflictStrategy.IGNORE)
    //    void updateItemsIgnore(T... objects);
    //
    //    @Update(onConflict = OnConflictStrategy.FAIL)
    //    void updateItemsFail(T... objects);
    //
    //    @Update(onConflict = OnConflictStrategy.ROLLBACK)
    //    void updateItemsRollback(T... objects);

    @Delete
    void deleteItem(T object);
}