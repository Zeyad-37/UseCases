package com.zeyad.usecases.stores

import android.content.Context
import com.zeyad.usecases.Config
import com.zeyad.usecases.db.DataBaseManager
import com.zeyad.usecases.db.RoomManager
import com.zeyad.usecases.mapper.DAOMapper
import com.zeyad.usecases.network.ApiConnection
import com.zeyad.usecases.utils.DataBaseManagerUtil
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock

@RunWith(JUnit4::class)
class DataStoreFactoryTest {

    private var mDataStoreFactory: DataStoreFactory? = null // class under test

    private val someValidUrl: String
        get() = "https://www.google.com"

    private val invalidUrl: String
        get() = ""

    @Before
    fun setUp() {
        com.zeyad.usecases.Config.context = mock(Context::class.java)
        mDataStoreFactory = DataStoreFactory(object : DataBaseManagerUtil {
            override fun getDataBaseManager(dataClass: Class<*>): DataBaseManager? {
                return mock(RoomManager::class.java)
            }
        }, mock(ApiConnection::class.java),
                DAOMapper())
    }

    @Test
    fun testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValid() {
        assertThat(mDataStoreFactory!!.dynamically(invalidUrl, Any::class.java),
                `is`(instanceOf(DiskStore::class.java)))
    }

    @Test
    fun testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidAndNetWorkNotAvailable() {
        assertThat(mDataStoreFactory!!.dynamically(invalidUrl, Any::class.java),
                `is`(instanceOf(DiskStore::class.java)))
    }

    @Test
    fun testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkNotAvailable() {
        assertThat(mDataStoreFactory!!.dynamically(invalidUrl, Any::class.java),
                `is`(instanceOf(DiskStore::class.java)))
    }

    @Test
    fun testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkIsAvailable() {
        assertThat(mDataStoreFactory!!.dynamically(invalidUrl, Any::class.java),
                `is`(instanceOf(DiskStore::class.java)))
    }

    @Test
    fun testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmpty() {
        assertThat(mDataStoreFactory!!.dynamically(someValidUrl, Any::class.java),
                `is`(instanceOf(CloudStore::class.java)))
    }

    @Test
    fun testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItem() {
        assertThat(mDataStoreFactory!!.dynamically(invalidUrl, Any::class.java),
                `is`(instanceOf(DiskStore::class.java)))
    }

    @Test
    fun testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItemAndNetWorkNotAvailable() {
        assertThat(mDataStoreFactory!!.dynamically(invalidUrl, Any::class.java),
                `is`(instanceOf(DiskStore::class.java)))
    }

    @Test
    fun testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkNotAvailable() {
        assertThat(mDataStoreFactory!!.dynamically(invalidUrl, Any::class.java),
                `is`(instanceOf(DiskStore::class.java)))
    }

    @Test
    fun testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkIsAvailable() {
        assertThat(mDataStoreFactory!!.dynamically(invalidUrl, Any::class.java),
                `is`(instanceOf(DiskStore::class.java)))
    }

    @Test
    fun testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmptyForSingleItem() {
        assertThat(mDataStoreFactory!!.dynamically(someValidUrl, Any::class.java),
                `is`(instanceOf(CloudStore::class.java)))
    }

    @Test
    @Throws(IllegalAccessException::class)
    fun testDiskMethod_ifExpectedDataStoreIsReturned_whenMockedEntityMapperIsPassed() {
        Config.withSQLite = true
        assertThat(mDataStoreFactory!!.disk(Any::class.java), `is`(notNullValue()))
    }

    @Test
    fun testDiskMethod_ifExpectedCloudStoreIsReturned_whenMockedEntityMapperIsPassed() {
        assertThat(mDataStoreFactory!!.cloud(Any::class.java), `is`(notNullValue()))
    }
}
