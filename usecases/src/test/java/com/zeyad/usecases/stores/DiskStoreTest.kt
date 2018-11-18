package com.zeyad.usecases.stores

import com.zeyad.usecases.Config
import com.zeyad.usecases.TestModel
import com.zeyad.usecases.anyObject
import com.zeyad.usecases.db.DataBaseManager
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import junit.framework.Assert.assertEquals
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Matchers
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
        val testRealmObjects = ArrayList<TestModel>()
        testRealmObjects.add(TestModel())
        val observable = Flowable.just<List<TestModel>>(testRealmObjects)
        `when`(dbManager.getAll(anyObject<Class<TestModel>>()))
                .thenReturn(observable)

        mDiskStore.dynamicGetList("", "", Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .getAll(anyObject<Class<TestModel>>())
    }

    @Test
    fun testGetObject() {
        val observable = Flowable.just(TestModel())
        `when`(dbManager.getById(anyString(), anyObject(), anyObject<Class<TestModel>>()))
                .thenReturn(observable)

        mDiskStore.dynamicGetObject("", "", 0L, TestModel::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .getById(anyString(), anyObject(), anyObject<Class<TestModel>>())
    }

    @Test
    fun testSearchDiskQuery() {
        `when`(dbManager.getQuery(anyString(), anyObject<Class<TestModel>>()))
                .thenReturn(Flowable.just(TestModel()))

        mDiskStore.queryDisk("", TestModel::class.java)

        verify(dbManager, times(1))
                .getQuery(Matchers.anyString(), anyObject<Class<TestModel>>())
    }

    @Test
    fun testDynamicDeleteAll() {
        `when`(dbManager.evictAll(anyObject<Class<TestModel>>())).thenReturn(Single.just(true))

        mDiskStore.dynamicDeleteAll(TestModel::class.java)

        verify<DataBaseManager>(dbManager, times(1)).evictAll(anyObject<Class<TestModel>>())
    }

    @Test
    fun testDynamicDeleteCollection() {
        // Todo("When implemented")
//        `when`(dbManager.evictCollection(anyList() as List<TestModel>, anyObject<Class<TestModel>>()))
//                .thenReturn(Single.just(true))
//
//        mDiskStore.dynamicDeleteCollection(
//                "", "", String::class.java, JSONArray(), Any::class.java, Any::class.java, false, false)
//
//        verify<DataBaseManager>(dbManager, times(1))
//                .evictCollection(anyList() as List<TestModel>, anyObject<Class<TestModel>>())
    }

    @Test
    fun testDynamicPatchObject() {
        `when`(dbManager.put(anyObject<JSONObject>(), anyObject<Class<TestModel>>()))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPatchObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .put(anyObject<JSONObject>(), anyObject<Class<TestModel>>())
    }

    @Test
    fun testDynamicPostObject() {
        `when`(dbManager.put(anyObject<JSONObject>(), anyObject<Class<TestModel>>()))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPostObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .put(anyObject<JSONObject>(), anyObject<Class<TestModel>>())
    }

    @Test
    fun testDynamicPutObject() {
        `when`(dbManager.put(anyObject<JSONObject>(), anyObject<Class<TestModel>>()))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPutObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .put(anyObject<JSONObject>(), anyObject<Class<TestModel>>())
    }

    @Test
    fun testDynamicPostList() {
        `when`(dbManager.putAll(anyObject<JSONArray>(), anyObject<Class<TestModel>>()))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPostList(
                "", "", JSONArray(), Any::class.java, Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .putAll(anyObject<JSONArray>(), anyObject<Class<TestModel>>())
    }

    @Test
    fun testDynamicPutList() {
        `when`(dbManager.putAll(anyObject<JSONArray>(), anyObject<Class<TestModel>>()))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPutList(
                "", "", JSONArray(), Any::class.java, Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .putAll(anyObject<JSONArray>(), anyObject<Class<TestModel>>())
    }

    @Test
    fun testDynamicDownloadFile() {
        val observable = mDiskStore.dynamicDownloadFile("", File(""))
        // Verify repository interactions
        verifyZeroInteractions(dbManager)

        val v: TestObserver<File> = TestObserver()
        observable.subscribe(v)
        v.assertErrorMessage("Can not file IO to local DB")
    }

    @Test(expected = IllegalStateException::class)
    fun testDynamicUploadFile() {
        val observable = mDiskStore.dynamicUploadFile(
                "", HashMap(), HashMap(), Any::class.java)

        // Verify repository interactions
        verifyZeroInteractions(dbManager)

        // Assert return type
        val expected = IllegalStateException("Can not IO file to local DB")
        assertEquals(
                expected.message,
                (observable.blockingGet() as IllegalStateException).message)
    }
}
