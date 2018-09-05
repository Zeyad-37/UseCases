package com.zeyad.usecases.services.jobs

import android.content.Context
import android.os.Environment
import android.support.test.rule.BuildConfig
import com.zeyad.usecases.TestRealmModel
import com.zeyad.usecases.requests.FileIORequest
import com.zeyad.usecases.stores.CloudStore
import io.reactivex.Flowable
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers.*
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(25))
class FileIOTest {
    private var cloudStore: CloudStore? = null
    private var mockContext: Context? = null
    // item under test
    private var fileIO: FileIO? = null

    private val validUrl: String
        get() = "http://www.google.com"

    private val validFile: File
        get() {
            val file = File(Environment.getExternalStorageDirectory(), "someFile.png")
            file.mkdir()
            return file
        }

    @Before
    fun setUp() {
        mockContext = mock(Context::class.java)
        cloudStore = createCloudDataStore()
    }

    @After
    fun tearDown() {
        reset<CloudStore>(cloudStore)
    }

    @Test
    fun testDownload() {
        fileIO = createFileIO(mockFileIoReq(true, true, validFile), true)
        fileIO!!.execute()
        //        verify(cloudStore).dynamicDownloadFile(anyString(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    }

    @Test
    fun testUpload() {
        fileIO = createFileIO(mockFileIoReq(true, true, validFile), false)
        fileIO!!.execute()
        //        verify(cloudStore).dynamicUploadFile(anyString(), (HashMap<String, File>) anyMap(),
        //                (HashMap<String, Object>) anyMap(), anyBoolean(), anyBoolean(), anyBoolean(), any());
    }

    @Test
    fun testReQueue() {
        val fileIOReq = mockFileIoReq(true, true, validFile)
        fileIO = createFileIO(fileIOReq, true)
        //        Mockito.doNothing()
        //                .when(utils)
        //                .queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class), anyInt());
        //        fileIO.queueIOFile();
        //        verify(utils,
        //                times(1)).queueFileIOCore(any(), anyBoolean(), any(FileIORequest.class), anyInt());
    }

    private fun mockFileIoReq(wifi: Boolean, isCharging: Boolean, file: File): FileIORequest {
        val fileIORequest = mock(FileIORequest::class.java)
        Mockito.`when`(fileIORequest.getTypedResponseClass<TestRealmModel>()).thenReturn(TestRealmModel::class.java)
        Mockito.`when`(fileIORequest.url).thenReturn(validUrl)
        Mockito.`when`<File>(fileIORequest.file).thenReturn(file)
        Mockito.`when`(fileIORequest.whileCharging).thenReturn(isCharging)
        Mockito.`when`(fileIORequest.onWifi).thenReturn(wifi)
        return fileIORequest
    }

    private fun createFileIO(fileIoReq: FileIORequest, isDownload: Boolean): FileIO {
        return FileIO(0, fileIoReq, mockContext!!, isDownload, createCloudDataStore())
    }

    private fun createCloudDataStore(): CloudStore {
        val cloudStore = mock(CloudStore::class.java)
        Mockito.`when`(cloudStore.dynamicDownloadFile(
                anyString(),
                com.zeyad.usecases.anyObject(),
                anyBoolean(),
                anyBoolean(),
                anyBoolean()))
                .thenReturn(Flowable.empty())
        Mockito.`when`(cloudStore.dynamicUploadFile(
                anyString(),
                anyMap() as HashMap<String, File>,
                anyMap() as HashMap<String, Any>,
                anyBoolean(),
                anyBoolean(),
                anyBoolean(),
                com.zeyad.usecases.anyObject<Class<Any>>()))
                .thenReturn(Flowable.empty<Any>())
        return cloudStore
    }
}
