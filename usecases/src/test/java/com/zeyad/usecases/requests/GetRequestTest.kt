package com.zeyad.usecases.requests

import com.zeyad.usecases.TestRealmModel
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetRequestTest {

    private val DATA_CLASS = TestRealmModel::class.java
    private val TO_PERSIST = false
    private val ID_COLUMN_NAME = "id"
    private val URL = "www.google.com"
    private val SHOULD_CACHE = true
    private val ID = 1L
    private var mGetRequest: GetRequest? = null

    @Before
    fun setUp() {
        mGetRequest = GetRequest.Builder(DATA_CLASS, TO_PERSIST)
                .fullUrl(URL)
                .cache(ID_COLUMN_NAME)
                .id(ID, ID_COLUMN_NAME, Long::class.javaPrimitiveType!!)
                .build()
    }

    @After
    fun tearDown() {
        mGetRequest = null
    }

    @Test
    fun testGetUrl() {
        assertThat(mGetRequest!!.fullUrl, `is`(equalTo(URL)))
    }

    @Test
    fun testGetDataClass() {
        assertThat(mGetRequest!!.getTypedDataClass(), `is`(equalTo(DATA_CLASS)))
    }

    @Test
    fun testIsPersist() {
        assertThat(mGetRequest!!.persist, `is`(equalTo(TO_PERSIST)))
    }

    @Test
    fun testIsShouldCache() {
        assertThat(mGetRequest!!.cache, `is`(equalTo(SHOULD_CACHE)))
    }

    @Test
    fun testGetIdColumnName() {
        assertThat(mGetRequest!!.idColumnName, `is`(equalTo(ID_COLUMN_NAME)))
    }

    @Test
    fun testGetItemId() {
        assertThat(mGetRequest!!.itemId, `is`(equalTo<Any>(ID)))
    }
}

