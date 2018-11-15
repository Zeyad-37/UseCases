package com.zeyad.usecases.app

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.zeyad.usecases.app.screens.user.User
import com.zeyad.usecases.app.screens.user.detail.Repository

@Database(entities = [User::class, Repository::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun repoDao(): RepoDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "app.room")
//                    .addCallback(object : RoomDatabase.Callback() {
//                        override fun onCreate(db: SupportSQLiteDatabase) {
//                            super.onCreate(db)
//                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
//                            WorkManager.getInstance().enqueue(request)
//                        }
//                    })
                    .build()
        }
    }
}