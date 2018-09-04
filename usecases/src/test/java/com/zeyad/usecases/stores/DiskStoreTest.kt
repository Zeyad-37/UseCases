package com.zeyad.usecases.stores

import com.zeyad.usecases.Config
import com.zeyad.usecases.TestRealmModel
import com.zeyad.usecases.db.DataBaseManager
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subscribers.TestSubscriber
import junit.framework.Assert.assertEquals
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Matchers.any
import org.mockito.Matchers.anyList
import org.mockito.Matchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verifyZeroInteractions
import java.io.File
import java.util.*

@RunWith(JUnit4::class)
class DiskStoreTest { // TODO: 6/5/17 add cache verifications

    private var mDiskStore: DiskStore? = null
    private var dbManager: DataBaseManager? = null

    @Before
    fun setUp() {
        dbManager = mock(DataBaseManager::class.java)
        Config.withCache = false
        mDiskStore = DiskStore(dbManager!!, MemoryStore(Config.gson))
    }

    @Test
    fun testGetAll() {
        val testRealmObjects = ArrayList<TestRealmModel>()
        testRealmObjects.add(TestRealmModel())
        val observable = Flowable.just<List<TestRealmModel>>(testRealmObjects)
        `when`(dbManager!!.getAll<TestRealmModel>(any(Class::class.java))).thenReturn(observable)

        mDiskStore!!.dynamicGetList("", "", Any::class.java, false, false)

        Mockito.verify<DataBaseManager>(dbManager, times(1)).getAll<Any>(any(Class::class.java))
    }

    @Test
    fun testGetObject() {
        val observable = Flowable.just(TestRealmModel())
        `when`(dbManager!!.getById<TestRealmModel>(anyString(), any(), any(Class::class.java), any(Class::class.java)))
                .thenReturn(observable)

        mDiskStore!!.dynamicGetObject("", "", 0L, Long::class.javaPrimitiveType!!, Any::class.java, false, false)

        Mockito.verify<DataBaseManager>(dbManager, times(1))
                .getById<Any>(anyString(), any(), any(Class::class.java), any(Class::class.java))
    }

    @Test
    fun testSearchDiskRealmQuery() {
        //        when(dbManager.getQuery(any(RealmQueryProvider.class))).thenReturn(any(Flowable.class));

        //        mDiskStore.queryDisk(realm -> realm.where(TestRealmModel.class));

        //        Mockito.verify(dbManager, times(1)).getQuery(any(RealmQueryProvider.class));
    }

    @Test
    fun testDynamicDeleteAll() {
        `when`(dbManager!!.evictAll(any(Class::class.java))).thenReturn(Single.just(true))

        mDiskStore!!.dynamicDeleteAll(TestRealmModel::class.java)

        Mockito.verify<DataBaseManager>(dbManager, times(1)).evictAll(any(Class::class.java))
    }

    @Test
    fun testDynamicDeleteCollection() {
        `when`(dbManager!!.evictCollection(anyString(), anyList(), any(Class::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore!!.dynamicDeleteCollection(
                "", "", String::class.java, JSONArray(), Any::class.java, Any::class.java, false, false, false)

        Mockito.verify<DataBaseManager>(dbManager, times(1))
                .evictCollection(anyString(), anyList(), any(Class::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicPatchObject() {
        `when`(dbManager!!.put(any(JSONObject::class.java), anyString(), any(Class::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore!!.dynamicPatchObject(
                "", "", Int::class.javaPrimitiveType!!, JSONObject(), Any::class.java, Any::class.java, false, false, false)

        Mockito.verify<DataBaseManager>(dbManager, times(1))
                .put(any(JSONObject::class.java), anyString(), any(Class::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicPostObject() {
        `when`(dbManager!!.put(any(JSONObject::class.java), anyString(), any(Class::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore!!.dynamicPostObject(
                "", "", Int::class.javaPrimitiveType!!, JSONObject(), Any::class.java, Any::class.java, false, false, false)

        Mockito.verify<DataBaseManager>(dbManager, times(1))
                .put(any(JSONObject::class.java), anyString(), any(Class::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicPutObject() {
        `when`(dbManager!!.put(any(JSONObject::class.java), anyString(), any(Class::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore!!.dynamicPutObject(
                "", "", Int::class.javaPrimitiveType!!, JSONObject(), Any::class.java, Any::class.java, false, false, false)

        Mockito.verify<DataBaseManager>(dbManager, times(1))
                .put(any(JSONObject::class.java), anyString(), any(Class::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicPostList() {
        `when`(dbManager!!.putAll(any(JSONArray::class.java), anyString(), any(Class::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore!!.dynamicPostList(
                "", "", Int::class.javaPrimitiveType!!, JSONArray(), Any::class.java, Any::class.java, false, false, false)

        Mockito.verify<DataBaseManager>(dbManager, times(1))
                .putAll(any(JSONArray::class.java), anyString(), any(Class::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicPutList() {
        `when`(dbManager!!.putAll(any(JSONArray::class.java), anyString(), any(Class::class.java), any(Class::class.java)))
                .thenReturn(Single.just(true))

        mDiskStore!!.dynamicPutList(
                "", "", Int::class.javaPrimitiveType!!, JSONArray(), Any::class.java, Any::class.java, false, false, false)

        Mockito.verify<DataBaseManager>(dbManager, times(1))
                .putAll(any(JSONArray::class.java), anyString(), any(Class::class.java), any(Class::class.java))
    }

    @Test
    fun testDynamicDownloadFile() {
        val observable = mDiskStore!!.dynamicDownloadFile("", File(""), false, false, false)

        // Verify repository interactions
        verifyZeroInteractions(dbManager)

        val v: TestSubscriber<File> = TestSubscriber()
        observable.subscribe(v)
        v.assertErrorMessage("Can not file IO to local DB")
    }

    @Test(expected = IllegalStateException::class)
    fun testDynamicUploadFile() {
        val observable = mDiskStore!!.dynamicUploadFile<Any>(
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
