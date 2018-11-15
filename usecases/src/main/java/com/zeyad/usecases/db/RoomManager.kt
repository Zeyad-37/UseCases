package com.zeyad.usecases.db

import android.arch.persistence.db.SimpleSQLiteQuery
import android.arch.persistence.room.RoomDatabase
import com.google.gson.Gson
import com.zeyad.usecases.Config
import com.zeyad.usecases.Mockable
import io.reactivex.Flowable
import io.reactivex.Single
import org.json.JSONArray
import org.json.JSONObject

@Mockable
class RoomManager(private val db: RoomDatabase, private val daoResolver: DaoResolver) : DataBaseManager {

    override fun <E> getQuery(query: String, clazz: Class<E>): Flowable<E> {
        return singleTypedTransactionBlock {
            daoResolver.getDao(clazz).getQuery(SimpleSQLiteQuery(query))
        }.toFlowable()
    }

    override fun <E> getById(idColumnName: String, itemId: Any, clazz: Class<E>): Flowable<E> {
        return singleTypedTransactionBlock {
            daoResolver.getDao(clazz)
                    .getItem(SimpleSQLiteQuery("Select * From ${clazz.simpleName} " +
                            "Where $idColumnName=$itemId"))
        }.toFlowable()
    }

    override fun <E> getAll(clazz: Class<E>): Flowable<List<E>> {
        return singleTypedTransactionBlock {
            daoResolver.getDao(clazz)
                    .getAllItems(SimpleSQLiteQuery("Select * From ${clazz.simpleName}"))
        }.toFlowable()
    }

    override fun <E> put(entity: E, clazz: Class<E>): Single<Any> {
        return singleTransactionBlock {
            Single.just(daoResolver.getDao(clazz).insertItemsReplace(listOf(entity)).isNotEmpty())
        }
    }

    override fun <E> put(jsonObject: JSONObject, clazz: Class<E>): Single<Any> {
        return singleTransactionBlock {
            Single.just(daoResolver.getDao(clazz)
                    .insertItemsReplace(Config.gson.fromJson(jsonObject.toString(), clazz)).isNotEmpty())
        }
    }

    override fun <E> putAll(entities: List<E>, clazz: Class<E>): Single<Any> {
        return singleTransactionBlock {
            Single.just(daoResolver.getDao(clazz).insertItemsReplace(entities).isNotEmpty())
        }
    }

    override fun <E> putAll(jsonArray: JSONArray, clazz: Class<E>): Single<Any> {
        return singleTransactionBlock {
            Single.just(daoResolver.getDao(clazz)
                    .insertItemsReplace(Gson().fromJson(jsonArray.toString(), clazz)).isNotEmpty())
        }
    }

    override fun <E> evictAll(clazz: Class<E>): Single<Any> {
        return singleTransactionBlock {
            Single.just(daoResolver.getDao(clazz)
                    .deleteAllItems(SimpleSQLiteQuery("DELETE FROM ${clazz.simpleName}")) > 0)
        }
    }

    override fun <E> evictCollection(list: List<E>, clazz: Class<E>): Single<Any> {
        return singleTransactionBlock { Single.just(daoResolver.getDao(clazz).deleteItems(list) > 0) }
    }

    override fun <E> evictCollectionById(list: List<Any>, clazz: Class<E>, idFieldName: String): Single<Any> {
        return singleTransactionBlock {
            Single.just(list).flatMap { evictById(clazz, idFieldName, it) }
        }
    }

    override fun <E> evictById(clazz: Class<E>, idFieldName: String, idFieldValue: Any): Single<Any> {
        return singleTransactionBlock {
            Single.just(daoResolver.getDao(clazz).deleteItems(getById(idFieldName, idFieldValue,
                    clazz).blockingFirst()) > 0)
        }
    }

    private inline fun <E> singleTypedTransactionBlock(r: RoomDatabase.() -> Single<E>): Single<E> {
        db.beginTransaction()
        try {
            val single = r.invoke(db)
            db.setTransactionSuccessful()
            return single
        } finally {
            db.endTransaction()
        }
    }

    private inline fun singleTransactionBlock(r: RoomDatabase.() -> Single<Any>): Single<Any> {
        db.beginTransaction()
        try {
            val single = r.invoke(db)
            db.setTransactionSuccessful()
            return single
        } finally {
            db.endTransaction()
        }
    }
}
