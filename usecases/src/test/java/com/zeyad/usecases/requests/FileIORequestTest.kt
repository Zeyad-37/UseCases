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
import org.mockito.Mockito
import java.io.File

@RunWith(JUnit4::class)
class FileIORequestTest {

    private val ON_WIFI = false
    private val QUEUABLE = false
    private val WHILE_CHARGING = false
    private val URL = "www.google.com"
    private val FILE = Mockito.mock(File::class.java)
    private val DATA_CLASS = TestRealmModel::class.java
    private var mFileIORequest: FileIORequest? = null

    @Before
    fun setUp() {
        mFileIORequest = FileIORequest.Builder(URL)
                .file(FILE)
                //                        .queuable(ON_WIFI, WHILE_CHARGING)
                .responseType(DATA_CLASS)
                .build()
    }

    @After
    fun tearDown() {
        mFileIORequest = null
    }

    @Test
    fun testGetUrl() {
        assertThat(mFileIORequest!!.url, `is`(equalTo(URL)))
    }

    @Test
    fun testIsPersist() {
        assertThat(mFileIORequest!!.queuable, `is`(equalTo(QUEUABLE)))
    }

    @Test
    fun testGetFile() {
        assertThat<File>(mFileIORequest!!.file, `is`(equalTo(FILE)))
    }

    @Test
    fun testOnWifi() {
        assertThat(mFileIORequest!!.onWifi, `is`(equalTo(ON_WIFI)))
    }

    @Test
    fun testWhileChargingGetFile() {
        assertThat(mFileIORequest!!.whileCharging, `is`(equalTo(WHILE_CHARGING)))
    }
}
