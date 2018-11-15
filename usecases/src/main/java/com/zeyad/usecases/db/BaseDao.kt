package com.zeyad.usecases.db

import android.arch.persistence.db.SupportSQLiteQuery
import android.arch.persistence.room.*
import io.reactivex.Single

interface BaseDao<E> {

    @RawQuery
    fun getItem(query: SupportSQLiteQuery): Single<E>

    @RawQuery
    fun getAllItems(query: SupportSQLiteQuery): Single<List<E>>

    @RawQuery
    fun getQuery(query: SupportSQLiteQuery): Single<E>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemsReplace(vararg objects: E): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemsReplace(objects: List<E>): List<Long>

    @Insert
    fun insertItemsAbort(vararg objects: E): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertItemsIgnore(vararg objects: E): List<Long>

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertItemsFail(vararg objects: E): List<Long>

    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    fun insertItemsRollback(vararg objects: E): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateItemsReplace(vararg objects: E): Int

    @Update(onConflict = OnConflictStrategy.ABORT)
    fun updateItemsAbort(vararg objects: E): Int

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateItemsIgnore(vararg objects: E): Int

    @Update(onConflict = OnConflictStrategy.FAIL)
    fun updateItemsFail(vararg objects: E): Int

    @Update(onConflict = OnConflictStrategy.ROLLBACK)
    fun updateItemsRollback(vararg objects: E): Int

    @Delete
    fun deleteItems(vararg objects: E): Int

    @Delete
    fun deleteItems(objects: List<E>): Int

    @RawQuery
    fun deleteAllItems(query: SupportSQLiteQuery): Int
}