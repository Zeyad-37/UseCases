package com.zeyad.usecases.stores

import com.zeyad.usecases.Config
import com.zeyad.usecases.TestRealmModel
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
        val testRealmObjects = ArrayList<TestRealmModel>()
        testRealmObjects.add(TestRealmModel())
        val observable = Flowable.just<List<TestRealmModel>>(testRealmObjects)
        `when`(dbManager.getAll(any(Class::class.java) as Class<TestRealmModel>))
                .thenReturn(observable)

        mDiskStore.dynamicGetList("", "", Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .getAll(any(Class::class.java) as Class<TestRealmModel>)
    }

    @Test
    fun testGetObject() {
        val observable = Flowable.just(TestRealmModel())
        `when`(dbManager.getById(anyString(), any(), any(Class::class.java) as Class<TestRealmModel>))
                .thenReturn(observable)

        mDiskStore.dynamicGetObject("", "", 0L, TestRealmModel::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .getById(anyString(), any(), any(Class::class.java) as Class<TestRealmModel>)
    }

    @Test
    fun testSearchDiskRealmQuery() {
        `when`(dbManager.getQuery(anyString(), any(Class::class.java) as Class<TestRealmModel>))
                .thenReturn(Flowable.just(TestRealmModel()))

        mDiskStore.queryDisk("", TestRealmModel::class.java)

        verify(dbManager, times(1))
                .getQuery(Matchers.anyString(), any(Class::class.java) as Class<TestRealmModel>)
    }

    @Test
    fun testDynamicDeleteAll() {
        `when`(dbManager.evictAll(any(Class::class.java))).thenReturn(Single.just(true))

        mDiskStore.dynamicDeleteAll(TestRealmModel::class.java)

        verify<DataBaseManager>(dbManager, times(1)).evictAll(any(Class::class.java))
    }

    @Test
    fun testDynamicDeleteCollection() {
        `when`(dbManager.evictCollection(anyList() as List<TestRealmModel>, any(Class::class.java) as Class<TestRealmModel>))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicDeleteCollection(
                "", "", String::class.java, JSONArray(), Any::class.java, Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .evictCollection(anyList() as List<TestRealmModel>, any(Class::class.java) as Class<TestRealmModel>)
    }

    @Test
    fun testDynamicPatchObject() {
        `when`(dbManager.put(any(JSONObject::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPatchObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .put(any(JSONObject::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicPostObject() {
        `when`(dbManager.put(any(JSONObject::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPostObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .put(any(JSONObject::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicPutObject() {
        `when`(dbManager.put(any(JSONObject::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPutObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .put(any(JSONObject::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicPostList() {
        `when`(dbManager.putAll(any(JSONArray::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPostList(
                "", "", JSONArray(), Any::class.java, Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .putAll(any(JSONArray::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicPutList() {
        `when`(dbManager.putAll(any(JSONArray::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore.dynamicPutList(
                "", "", JSONArray(), Any::class.java, Any::class.java, false, false)

        verify<DataBaseManager>(dbManager, times(1))
                .putAll(any(JSONArray::class.java), any(Class::class.java))
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
