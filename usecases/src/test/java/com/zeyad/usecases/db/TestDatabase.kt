package com.zeyad.usecases.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.zeyad.usecases.TestModel

@Database(entities = [TestModel::class], version = 1)
abstract class TestDatabase : RoomDatabase() {

    abstract fun testDao(): TestDao
}