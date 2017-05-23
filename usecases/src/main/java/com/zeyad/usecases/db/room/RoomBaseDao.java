package com.zeyad.usecases.db.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

/**
 * @author by ZIaDo on 5/23/17.
 */
@Dao
public interface RoomBaseDao {

    @Query(value = ":query")
    Flowable<Object> getItem(String query);

    @Query(value = ":query")
    Flowable<List> getAllItems(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItemsReplace(Object... objects);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertItemsAbort(Object... objects);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertItemsIgnore(Object... objects);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insertItemsFail(Object... objects);

    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    void insertItemsRollback(Object... objects);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItemsReplace(Object... objects);

    @Update(onConflict = OnConflictStrategy.ABORT)
    void updateItemsAbort(Object... objects);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateItemsIgnore(Object... objects);

    @Update(onConflict = OnConflictStrategy.FAIL)
    void updateItemsFail(Object... objects);

    @Update(onConflict = OnConflictStrategy.ROLLBACK)
    void updateItemsRollback(Object... objects);

    @Delete
    void deleteItem(Object object);
}
