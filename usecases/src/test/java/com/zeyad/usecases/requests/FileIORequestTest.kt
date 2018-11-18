package com.zeyad.usecases.requests

import com.zeyad.usecases.TestModel
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

    private val URL = "www.google.com"
    private val FILE = Mockito.mock(File::class.java)
    private val DATA_CLASS = TestModel::class.java
    private var mFileIORequest: FileIORequest? = null

    @Before
    fun setUp() {
        mFileIORequest = FileIORequest.Builder(URL, FILE)
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
    fun testGetFile() {
        assertThat<File>(mFileIORequest!!.file, `is`(equalTo(FILE)))
    }
}
