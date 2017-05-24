package com.zeyad.usecases.app;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

import com.zeyad.usecases.app.screens.user_list.User;

/**
 * @author by ZIaDo on 5/23/17.
 */
@Dao
public interface RoomBaseDao {

//    @Query(value = ":query")
//    Flowable<User> getItem(String query);
//
//    @Query(value = ":query")
//    Flowable<List> getAllItems(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItemsReplace(User... objects);

//    @Insert(onConflict = OnConflictStrategy.ABORT)
//    void insertItemsAbort(User... objects);
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    void insertItemsIgnore(User... objects);
//
//    @Insert(onConflict = OnConflictStrategy.FAIL)
//    void insertItemsFail(User... objects);
//
//    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
//    void insertItemsRollback(User... objects);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItemsReplace(User... objects);

//    @Update(onConflict = OnConflictStrategy.ABORT)
//    void updateItemsAbort(User... objects);
//
//    @Update(onConflict = OnConflictStrategy.IGNORE)
//    void updateItemsIgnore(User... objects);
//
//    @Update(onConflict = OnConflictStrategy.FAIL)
//    void updateItemsFail(User... objects);
//
//    @Update(onConflict = OnConflictStrategy.ROLLBACK)
//    void updateItemsRollback(User... objects);

    @Delete
    void deleteItem(User object);
}
