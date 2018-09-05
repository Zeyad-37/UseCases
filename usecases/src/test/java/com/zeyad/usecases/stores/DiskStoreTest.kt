package com.zeyad.usecases.stores

import com.zeyad.usecases.Config
import com.zeyad.usecases.TestRealmModel
import com.zeyad.usecases.db.DataBaseManager
import com.zeyad.usecases.db.RealmQueryProvider
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subscribers.TestSubscriber
import io.realm.Realm
import io.realm.RealmQuery
import junit.framework.Assert.assertEquals
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import java.io.File
import java.util.*

@RunWith(JUnit4::class)
class DiskStoreTest { // TODO: 6/5/17 add cache verifications

    private lateinit var mDiskStore: DiskStore
    private lateinit var dbManager: DataBaseManager

    @Before
    fun setUp() {
        dbManager = mock(DataBaseManager::class.java)
        Config.withCache = false
        mDiskStore = DiskStore(dbManager, MemoryStore(Config.gson))
    }

    @Test
    fun testGetAll() {
        val testRealmObjects = ArrayList<TestRealmModel>()
        testRealmObjects.add(TestRealmModel())
        val observable = Flowable.just<List<TestRealmModel>>(testRealmObjects)
        `when`(dbManager.getAll<TestRealmModel>(any(Class::class.java))).thenReturn(observable)

        mDiskStore.dynamicGetList("", "", Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1)).getAll<Any>(any(Class::class.java))
    }

    @Test
    fun testGetObject() {
        val observable = Flowable.just(TestRealmModel())
        `when`(dbManager.getById<TestRealmModel>(anyString(), any(), any(Class::class.java), any(Class::class.java)))
                .thenReturn(observable)

        mDiskStore.dynamicGetObject("", "", 0L, Long::class.javaPrimitiveType!!, Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .getById<Any>(anyString(), any(), any(Class::class.java), any(Class::class.java))
    }

    @Test
    fun testSearchDiskRealmQuery() {
        `when`(dbManager.getQuery(anyObject<RealmQueryProvider<TestRealmModel>>()))
                .thenReturn(Flowable.just(listOf(TestRealmModel())))

        mDiskStore.queryDisk(object : RealmQueryProvider<TestRealmModel> {
            override fun create(realm: Realm): RealmQuery<TestRealmModel> =
                    realm.where(TestRealmModel::class.java)
        })

        verify(dbManager, times(1)).getQuery(anyObject<RealmQueryProvider<TestRealmModel>>())
    }

    @Test
    fun testDynamicDeleteAll() {
        `when`(dbManager.evictAll(any(Class::class.java))).thenReturn(Single.just(true))

        mDiskStore.dynamicDeleteAll(TestRealmModel::class.java)

        verify<DataBaseManager>(dbManager, times(1)).evictAll(any(Class::class.java))
    }

    @Test
    fun testDynamicDeleteCollection() {
        `when`(dbManager.evictCollection(anyString(), anyList(), any(Class::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicDeleteCollection(
                "", "", String::class.java, JSONArray(), Any::class.java, Any::class.java, false, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .evictCollection(anyString(), anyList(), any(Class::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicPatchObject() {
        `when`(dbManager.put(any(JSONObject::class.java), anyString(), any(Class::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPatchObject(
                "", "", Int::class.javaPrimitiveType!!, JSONObject(), Any::class.java, Any::class.java, false, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .put(any(JSONObject::class.java), anyString(), any(Class::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicPostObject() {
        `when`(dbManager.put(any(JSONObject::class.java), anyString(), any(Class::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPostObject(
                "", "", Int::class.javaPrimitiveType!!, JSONObject(), Any::class.java, Any::class.java, false, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .put(any(JSONObject::class.java), anyString(), any(Class::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicPutObject() {
        `when`(dbManager.put(any(JSONObject::class.java), anyString(), any(Class::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPutObject(
                "", "", Int::class.javaPrimitiveType!!, JSONObject(), Any::class.java, Any::class.java, false, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .put(any(JSONObject::class.java), anyString(), any(Class::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicPostList() {
        `when`(dbManager.putAll(any(JSONArray::class.java), anyString(), any(Class::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPostList(
                "", "", Int::class.javaPrimitiveType!!, JSONArray(), Any::class.java, Any::class.java, false, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .putAll(any(JSONArray::class.java), anyString(), any(Class::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicPutList() {
        `when`(dbManager.putAll(any(JSONArray::class.java), anyString(), any(Class::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPutList(
                "", "", Int::class.javaPrimitiveType!!, JSONArray(), Any::class.java, Any::class.java, false, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .putAll(any(JSONArray::class.java), anyString(), any(Class::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicDownloadFile() {
        val observable = mDiskStore.dynamicDownloadFile("", File(""), false, false, false)

        // Verify repository interactions
        verifyZeroInteractions(dbManager)

        val v: TestSubscriber<File> = TestSubscriber()
        observable.subscribe(v)
        v.assertErrorMessage("Can not file IO to local DB")
    }

    @Test(expected = IllegalStateException::class)
    fun testDynamicUploadFile() {
        val observable = mDiskStore.dynamicUploadFile<Any>(
                "", HashMap(), HashMap(), false, false, false, Any::class.java)

        // Verify repository interactions
        verifyZeroInteractions(dbManager)

        // Assert return type
        val expected = IllegalStateException("Can not IO file to local DB")
        assertEquals(
                expected.message,
                (observable.first(expected).blockingGet() as IllegalStateException).message)
    }
}
