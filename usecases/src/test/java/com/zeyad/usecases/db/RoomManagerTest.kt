package com.zeyad.usecases.db

import android.support.test.rule.BuildConfig
import com.google.gson.Gson
import com.zeyad.usecases.TestModel
import com.zeyad.usecases.anyObject
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.subscribers.TestSubscriber
import junit.framework.Assert.assertEquals
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.powermock.api.mockito.PowerMockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * @author by ZIaDo on 2/15/17.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [25])
class RoomManagerTest {

    private lateinit var roomManager: RoomManager

    @Before
    fun before() {
        val testDao = mock(TestDao::class.java)
        `when`(testDao.getAllItems(anyObject())).thenReturn(Single.just(listOf(TestModel())))
        `when`(testDao.getItem(anyObject())).thenReturn(Single.just(TestModel()))
        `when`(testDao.getQuery(anyObject())).thenReturn(Single.just(listOf(TestModel())))
        `when`(testDao.insertItemsReplace(anyObject<TestModel>())).thenReturn(listOf(1))
        `when`(testDao.updateItemsReplace(anyObject())).thenReturn(1)
        `when`(testDao.deleteItems(anyObject<TestModel>())).thenReturn(1)
        `when`(testDao.deleteAllItems(anyObject())).thenReturn(1)
        val testDB = mock(TestDatabase::class.java)
        `when`(testDB.testDao()).thenReturn(testDao)
        roomManager = RoomManager(testDB, object : DaoResolver {
            override fun <E> getDao(dataClass: Class<E>): BaseDao<E> {
                return testDB.testDao() as BaseDao<E>
            }
        })
    }

    private fun applyTestSingleSubscriber(single: Single<*>) {
        val testSubscriber = TestObserver<Any>()
        single.subscribe(testSubscriber)
        testSubscriber.assertComplete()
    }

    @Test
    fun getById() {
        val flowable = roomManager.getById("id", 1L, TestModel::class.java)

        applyTestFlowableSubscriber(flowable)

        assertEquals(flowable.firstElement().blockingGet().javaClass, TestModel::class.java)
    }

    @Test
    fun getAll() {
        applyTestFlowableSubscriber(roomManager.getAll(TestModel::class.java))
    }

    private fun applyTestFlowableSubscriber(flowable: Flowable<*>) {
        val testSubscriber = TestSubscriber<Any>()
        flowable.subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertSubscribed()
        testSubscriber.assertComplete()
    }

    @Test
    fun getQuery() {
        val flowable = roomManager.getQuery("SELECT * FROM TestModel", TestModel::class.java)

        applyTestFlowableSubscriber(flowable)

        assertEquals(TestModel::class.java, flowable.firstElement().blockingGet()[0].javaClass)
    }

    @Test
    @Throws(Exception::class)
    fun putJSONObject() {
        applyTestSingleSubscriber(roomManager.put(JSONObject(Gson().toJson(TestModel())), TestModel::class.java))
    }

    @Test
    fun putAllJSONArray() {
        applyTestSingleSubscriber(roomManager.putAll(JSONArray(Gson().toJson(listOf(TestModel()))),
                TestModel::class.java))
    }

    @Test
    fun putAllRealmObject() {
        applyTestSingleSubscriber(roomManager.putAll(listOf(), TestModel::class.java))
    }

    @Test
    fun evictAll() {
        applyTestSingleSubscriber(roomManager.evictAll(TestModel::class.java))
    }

    @Test
    fun evictCollection() {
        applyTestSingleSubscriber(roomManager.evictCollection(listOf(), TestModel::class.java))
    }

    @Test
    fun evictById() {
        assertEquals(roomManager.evictById(TestModel::class.java, "id", 1).blockingGet(), true)
    }
}
