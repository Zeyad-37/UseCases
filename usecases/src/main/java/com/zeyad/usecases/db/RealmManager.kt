package com.zeyad.usecases.db

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmResults
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * [DataBaseManager] implementation.
 */
class RealmManager : DataBaseManager {

    /**
     * Gets an [Flowable] which will emit an Object
     *
     * @param idColumnName name of ID variable
     * @param itemId       ID value
     * @param itemIdType   type of the ID
     * @param dataClass    type of the data requested
     * @param <M>          type of the data requested
     * @return a [Flowable] containing an object of type M.
    </M> */
    override fun <M : RealmModel> getById(idColumnName: String, itemId: Any,
                                          itemIdType: Class<*>, dataClass: Class<M>): Flowable<M> {
        return Flowable.defer {
            val realm = Realm.getDefaultInstance()
            getItemById(realm, dataClass, idColumnName, itemId, itemIdType)
                    .map { realmModel -> realm.copyFromRealm(realmModel) as M }
        }
    }

    /**
     * Gets an [Flowable] which will emit a List of Objects.
     *
     * @param clazz Class type of the items to get.
     */
    override fun <M : RealmModel> getAll(clazz: Class<M>): Flowable<List<M>> {
        return Flowable.defer {
            val realm = Realm.getDefaultInstance()
            realm.where(clazz).findAll().asFlowable()
                    .filter { (it as RealmResults<M>).isLoaded }
                    .map { realm.copyFromRealm(it) }
                    .flatMap { o ->
                        if (o.isEmpty())
                            Flowable.error(IllegalAccessException(String
                                    .format("%s(s) were not found!", clazz.simpleName)))
                        else
                            Flowable.just<List<M>>(o)
                    }
        }
    }

    /**
     * Takes a query to be executed and return a list of containing the result.
     *
     * @param queryFactory The query used to look for inside the DB.
     * @param <M>          the return type from the query
     * @return [<] a result list that matches the given query.
    </M> */
    override fun <M : RealmModel> getQuery(queryFactory: RealmQueryProvider<M>): Flowable<List<M>> {
        return Flowable.defer {
            val realm = Realm.getDefaultInstance()
            queryFactory.create(realm).findAll().asFlowable()
                    .filter { it.isLoaded }
                    .map<List<M>> { realm.copyFromRealm(it) }
        }
    }

    /**
     * Puts and element into the DB.
     *
     * @param jsonObject Element to insert in the DB.
     * @param dataClass  Class type of the items to be put.
     */
    override fun <M : RealmModel> put(jsonObject: JSONObject, idColumnName: String,
                                      itemIdType: Class<*>, dataClass: Class<M>): Single<Boolean> {
        return Single.fromCallable {
            val updatedJSON = updateJsonObjectWithIdValue(jsonObject, idColumnName, itemIdType, dataClass)
            RealmObject.isValid(executeWriteOperationInRealm(Realm.getDefaultInstance(),
                    { realm: Realm -> realm.createOrUpdateObjectFromJson(dataClass, updatedJSON) } as ExecuteAndReturn<M>))
        }
    }

    override fun <M : RealmModel> putAll(realmObjects: List<M>, dataClass: Class<M>): Single<Boolean> {
        return Single.fromCallable {
            executeWriteOperationInRealm(Realm.getDefaultInstance(),
                    { realm: Realm -> realm.copyToRealmOrUpdate(realmObjects) } as ExecuteAndReturn<List<M>>).size == realmObjects.size
        }
    }

    /**
     * Puts and element into the DB.
     *
     * @param jsonArray    Element to insert in the DB.
     * @param idColumnName Name of the id field.
     * @param dataClass    Class type of the items to be put.
     */
    override fun <M : RealmModel> putAll(jsonArray: JSONArray, idColumnName: String, itemIdType: Class<*>,
                                         dataClass: Class<M>): Single<Boolean> {
        return Single.fromCallable {
            val updatedJSONArray = updateJsonArrayWithIdValue(jsonArray, idColumnName, itemIdType, dataClass)
            executeWriteOperationInRealm(Realm.getDefaultInstance(), { realm: Realm ->
                realm
                        .createOrUpdateAllFromJson(dataClass, updatedJSONArray)
            } as Execute)
            true
        }
    }

    /**
     * Evict all elements of the DB.
     *
     * @param clazz Class type of the items to be deleted.
     */
    override fun <M : RealmModel> evictAll(clazz: Class<M>): Single<Boolean> {
        return Single.fromCallable {
            executeWriteOperationInRealm(Realm.getDefaultInstance(), { realm: Realm -> realm.delete(clazz) } as Execute)
            true
        }
    }

    /**
     * Evict a collection elements of the DB.
     *
     * @param idFieldName The id used to look for inside the DB.
     * @param list        List of ids to be deleted.
     * @param dataClass   Class type of the items to be deleted.
     */
    override fun <M : RealmModel> evictCollection(idFieldName: String, list: List<Any>,
                                                  itemIdType: Class<*>, dataClass: Class<M>): Single<Boolean> {
        return Single.fromCallable {
            if (list.isEmpty())
                false
            else
                Observable.fromIterable(list)
                        .map { id -> evictById(dataClass, idFieldName, id, itemIdType) }
                        .reduce { aBoolean, aBoolean2 -> aBoolean && aBoolean2 }
                        .blockingGet()
        }
    }

    /**
     * Evict element by id of the DB.
     *
     * @param clazz    Class type of the items to be deleted.
     * @param idFieldName The id used to look for inside the DB.
     * @param idFieldValue       Name of the id field.
     * @param itemIdType   Class type of the item id to be deleted.
     */
    override fun <M : RealmModel> evictById(clazz: Class<M>, idFieldName: String, idFieldValue: Any,
                                            itemIdType: Class<*>): Boolean {
        val realm = Realm.getDefaultInstance()
        return getItemById(realm, clazz, idFieldName, idFieldValue, itemIdType)
                .map { realmModel ->
                    executeWriteOperationInRealm(realm, { RealmObject.deleteFromRealm(realmModel) } as Execute)
                    getItemById(realm, clazz, idFieldName, idFieldValue, itemIdType)
                            .map { !RealmObject.isValid(realmModel) }
                            .blockingFirst()
                }
                .blockingFirst()
    }

    private fun <M : RealmModel> getItemById(realm: Realm, dataClass: Class<M>, idColumnName: String,
                                             itemId: Any, itemIdType: Class<*>): Flowable<RealmModel> {
        val result: Any? = if (itemIdType == Long::class.javaPrimitiveType || itemIdType == Long::class.java) {
            realm.where<M>(dataClass).equalTo(idColumnName, itemId as Long).findFirst()
        } else if (itemIdType == Int::class.javaPrimitiveType || itemIdType == Int::class.java) {
            realm.where<M>(dataClass).equalTo(idColumnName, itemId as Int).findFirst()
        } else if (itemIdType == Byte::class.javaPrimitiveType || itemIdType == Byte::class.java) {
            realm.where<M>(dataClass).equalTo(idColumnName, itemId as Byte).findFirst()
        } else if (itemIdType == String::class.java) {
            realm.where<M>(dataClass).equalTo(idColumnName, itemId.toString()).findFirst()
        } else {
            return Flowable.error(IllegalArgumentException("Unsupported ID type!"))
        }//        else if (itemIdType.equals(short.class) || itemIdType.equals(Short.class)) {
        //            result = realm.where(dataClass).equalTo(idColumnName, (short) itemId).findFirst();
        //        }
        return if (result == null) {
            Flowable.error(IllegalAccessException(String
                    .format("%s with ID: %s was not found!", dataClass.simpleName, itemId)))
        } else Flowable.just((result as RealmModel?)!!)
    }

    private fun executeWriteOperationInRealm(realm: Realm, execute: Execute) {
        if (realm.isInTransaction) {
            realm.cancelTransaction()
        }
        realm.beginTransaction()
        execute.run(realm)
        realm.commitTransaction()
    }

    private fun <T> executeWriteOperationInRealm(realm: Realm, executor: ExecuteAndReturn<T>): T {
        val toReturnValue: T = executor.runAndReturn(realm)
        if (realm.isInTransaction) {
            realm.cancelTransaction()
        }
        realm.beginTransaction()
        realm.commitTransaction()
        return toReturnValue
    }

    @Throws(JSONException::class)
    private fun <M : RealmModel> updateJsonArrayWithIdValue(jsonArray: JSONArray, idColumnName: String?,
                                                            itemIdType: Class<*>, dataClass: Class<M>): JSONArray {
        if (idColumnName == null || idColumnName.isEmpty()) {
            throw IllegalArgumentException(NO_ID)
        }
        val length = jsonArray.length()
        val updatedJSONArray = JSONArray()
        for (i in 0 until length) {
            if (jsonArray.opt(i) is JSONObject) {
                updatedJSONArray.put(updateJsonObjectWithIdValue(jsonArray.optJSONObject(i), idColumnName, itemIdType,
                        dataClass))
            }
        }
        return updatedJSONArray
    }

    @Throws(JSONException::class)
    private fun <M : RealmModel> updateJsonObjectWithIdValue(jsonObject: JSONObject, idColumnName: String?,
                                                             itemIdType: Class<*>, dataClass: Class<M>): JSONObject {
        if (idColumnName == null || idColumnName.isEmpty()) {
            throw IllegalArgumentException(NO_ID)
        }
        if (itemIdType == String::class.java) {
            return jsonObject
        } else if (jsonObject.optInt(idColumnName) == 0) {
            jsonObject.put(idColumnName, getNextId(dataClass, idColumnName))
        }
        return jsonObject
    }

    private fun <M : RealmModel> getNextId(clazz: Class<M>, column: String): Int {
        Realm.getDefaultInstance().use { realm ->
            val currentMax = realm.where(clazz).max(column)
            return if (currentMax != null) currentMax.toInt() + 1 else 1
        }
    }

    private interface Execute {
        fun run(realm: Realm)
    }

    private interface ExecuteAndReturn<T> {
        fun runAndReturn(realm: Realm): T
    }

    companion object {

        private const val NO_ID = "Could not find id!"
    }
}
