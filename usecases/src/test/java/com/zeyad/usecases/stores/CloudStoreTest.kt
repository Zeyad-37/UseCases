package com.zeyad.usecases.stores

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build
import android.os.HandlerThread
import android.support.test.rule.BuildConfig
import com.zeyad.usecases.Config.gson
import com.zeyad.usecases.TestModel
import com.zeyad.usecases.anyObject
import com.zeyad.usecases.db.DataBaseManager
import com.zeyad.usecases.db.RoomManager
import com.zeyad.usecases.exceptions.NetworkConnectionException
import com.zeyad.usecases.mapper.DAOMapper
import com.zeyad.usecases.network.ApiConnection
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.TestObserver
import io.reactivex.subscribers.TestSubscriber
import junit.framework.Assert.assertEquals
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.*

/**
 * @author by ZIaDo on 2/14/17.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [25])
class CloudStoreTest { // TODO: 6/5/17 add disk and cache verifications
    private lateinit var cloudStore: CloudStore
    private lateinit var mockContext: Context
    private lateinit var mockApiConnection: ApiConnection
    private lateinit var mockDataBaseManager: DataBaseManager
    private lateinit var observable: Single<Any>
    private lateinit var fileFlowable: Single<ResponseBody>
    private val errorMessage = "Could not reach server!"

    @Before
    fun setUp() {
        observable = Single.just(Any())
        fileFlowable = Single.just(ResponseBody.create(null, ""))
        mockContext = mock(Context::class.java)
        com.zeyad.usecases.Config.context = mockContext
        mockApiConnection = mock(ApiConnection::class.java)
        mockDataBaseManager = mock(RoomManager::class.java)
        changeStateOfNetwork(mockContext, true)
        `when`(mockDataBaseManager.put(anyObject<JSONObject>(), anyObject<Class<TestModel>>()))
                .thenReturn(Single.just(true))
        `when`(mockDataBaseManager.putAll(anyObject<JSONArray>(), anyObject<Class<TestModel>>()))
                .thenReturn(Single.just(true))
        `when`(mockDataBaseManager.putAll<Any>(anyListOf(Any::class.java), anyObject()))
                .thenReturn(Single.just(true))
        cloudStore = CloudStore(mockApiConnection, mockDataBaseManager, DAOMapper(),
                MemoryStore(gson, HashMap()))
        val backgroundThread = HandlerThread("backgroundThread")
        backgroundThread.start()
        com.zeyad.usecases.Config.withCache = false
        com.zeyad.usecases.Config.withSQLite = true
        com.zeyad.usecases.Config
                .backgroundThread = AndroidSchedulers.from(backgroundThread.looper)
    }

    @Test
    fun dynamicGetObject() {
        `when`(mockApiConnection.dynamicGetObject<Any>(anyString(), anyBoolean()))
                .thenReturn(Flowable.just(Any()))

        val testSubscriber = TestSubscriber<Any>()
        cloudStore.dynamicGetObject("", "", 0L, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicGetObject<Any>(anyString(), anyBoolean())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicGetObjectCanWillPersist() {
        `when`(mockApiConnection.dynamicGetObject<Any>(anyString(), anyBoolean())).thenReturn(Flowable.just(Any()))

        val testSubscriber = TestSubscriber<Any>()
        cloudStore.dynamicGetObject("", "", 0L, Any::class.java, true, false)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicGetObject<Any>(anyString(), anyBoolean())
        verifyDBInteractions(0, 0, 1, 0)
    }

    @Test
    fun dynamicGetList() {
        val testRealmObjects = ArrayList<TestModel>()
        testRealmObjects.add(TestModel())
        val observable = Flowable.just<List<TestModel>>(testRealmObjects)
        `when`(mockApiConnection.dynamicGetList<TestModel>(anyString(), anyBoolean())).thenReturn(observable)

        val testSubscriber = TestSubscriber<Any>()
        cloudStore.dynamicGetList("", "", Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicGetList<Any>(anyString(), anyBoolean())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicGetListCanWillPersist() {
        val observable = Flowable.just(listOf(TestModel()))
        `when`(mockApiConnection.dynamicGetList<TestModel>(anyString(), anyBoolean())).thenReturn(observable)

        val testSubscriber = TestSubscriber<List<*>>()
        cloudStore.dynamicGetList("", "", Any::class.java, true, false).subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicGetList<Any>(anyString(), anyBoolean())
        verifyDBInteractions(0, 1, 0, 0)
    }

    @Test
    fun dynamicPatchObject() {
        `when`(mockApiConnection.dynamicPatch<Any>(anyString(), anyObject()))
                .thenReturn(observable)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPatchObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicPatch<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicPatchObjectCanWillPersist() {
        com.zeyad.usecases.Config.withSQLite = true
        `when`(mockApiConnection.dynamicPatch<Any>(anyString(), anyObject()))
                .thenReturn(observable)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPatchObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, true, true)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicPatch<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 1, 0)
    }

    @Test
    fun dynamicPatchObjectNoNetwork() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPatchObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertErrorMessage(errorMessage)

        verify<ApiConnection>(mockApiConnection, times(0)).dynamicPatch<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicPatchObjectNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPatchObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertError(NetworkConnectionException::class.java)
        verify<ApiConnection>(mockApiConnection, times(0)).dynamicPatch<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicPostObject() {
        `when`(mockApiConnection.dynamicPost<Any>(anyString(), anyObject())).thenReturn(observable)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPostObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicPost<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicPostObjectCanWillPersist() {
        `when`(mockApiConnection.dynamicPost<Any>(anyString(), anyObject()))
                .thenReturn(observable)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPostObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, true, false)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicPost<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 1, 0)
    }

    @Test
    fun dynamicPostObjectNoNetwork() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPostObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertErrorMessage(errorMessage)

        verify<ApiConnection>(mockApiConnection, times(0)).dynamicPost<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicPostObjectNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPostObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertError(NetworkConnectionException::class.java)

        verify<ApiConnection>(mockApiConnection, times(0)).dynamicPost<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicPostList() {
        `when`(mockApiConnection.dynamicPost<Any>(anyString(), anyObject()))
                .thenReturn(observable)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPostList("", "", JSONArray(), Any::class.java, Any::class.java,
                false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicPost<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicPostListCanWillPersist() {
        `when`(mockApiConnection.dynamicPost<Any>(anyString(), anyObject()))
                .thenReturn(observable)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPostList("", "", JSONArray(), Any::class.java, Any::class.java,
                true, false)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicPost<Any>(anyString(), anyObject())
        verifyDBInteractions(1, 0, 0, 0)
    }

    @Test
    fun dynamicPostListNoNetwork() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPostList("", "", JSONArray(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertErrorMessage(errorMessage)

        verify<ApiConnection>(mockApiConnection, times(0)).dynamicPost<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicPostListNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPostList("", "", JSONArray(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertError(NetworkConnectionException::class.java)

        verify<ApiConnection>(mockApiConnection, times(0)).dynamicPost<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicPutObject() {
        `when`(mockApiConnection.dynamicPut<Any>(anyString(), anyObject()))
                .thenReturn(observable)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPutObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicPut<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicPutObjectCanWillPersist() {
        `when`(mockApiConnection.dynamicPut<Any>(anyString(), anyObject()))
                .thenReturn(observable)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPutObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, true, false)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicPut<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 1, 0)
    }

    @Test
    fun dynamicPutObjectNoNetwork() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPutObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertErrorMessage(errorMessage)

        verify<ApiConnection>(mockApiConnection, times(0)).dynamicPut<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicPutObjectNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPutObject(
                "", "", JSONObject(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertError(NetworkConnectionException::class.java)

        verify<ApiConnection>(mockApiConnection, times(0)).dynamicPut<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicPutList() {
        `when`(mockApiConnection.dynamicPut<Any>(anyString(), anyObject()))
                .thenReturn(observable)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPutList("", "", JSONArray(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicPut<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicPutListCanWillPersist() {
        `when`(mockApiConnection.dynamicPut<Any>(anyString(), anyObject()))
                .thenReturn(observable)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPutList("", "", JSONArray(), Any::class.java, Any::class.java, true, false)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicPut<Any>(anyString(), anyObject())
        verifyDBInteractions(1, 0, 0, 0)
    }

    @Test
    fun dynamicPutListNoNetwork() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPutList("", "", JSONArray(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertErrorMessage(errorMessage)

        verify<ApiConnection>(mockApiConnection, times(0)).dynamicPut<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicPutListNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicPutList("", "", JSONArray(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertError(NetworkConnectionException::class.java)

        verify<ApiConnection>(mockApiConnection, times(0)).dynamicPut<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicDeleteCollection() {
        `when`(mockApiConnection.dynamicDelete<Any>(anyString(), anyObject()))
                .thenReturn(observable)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicDeleteCollection(
                "", "", String::class.java, JSONArray(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicDelete<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicDeleteCollectionCanWillPersist() {
        `when`(mockApiConnection.dynamicDelete<Any>(anyString(), anyObject())).thenReturn(observable)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicDeleteCollection(
                "", "", String::class.java, JSONArray(), Any::class.java, Any::class.java, true, false)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicDelete<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicDeleteCollectionNoNetwork() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicDeleteCollection(
                "", "", String::class.java, JSONArray(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertErrorMessage(errorMessage)

        verify<ApiConnection>(mockApiConnection, times(0)).dynamicDelete<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicDeleteCollectionNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicDeleteCollection(
                "", "", String::class.java, JSONArray(), Any::class.java, Any::class.java, false, false)
                .subscribe(testSubscriber)

        testSubscriber.assertError(NetworkConnectionException::class.java)

        verify<ApiConnection>(mockApiConnection, times(0)).dynamicDelete<Any>(anyString(), anyObject())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test(expected = IllegalStateException::class)
    fun dynamicDeleteAll() {
        val completable = cloudStore.dynamicDeleteAll(Any::class.java)

        val testObserver = TestObserver<Boolean>()
        completable.subscribe(testObserver)

        testObserver.assertErrorMessage("Can not delete all from cloud data store!")

        // Verify repository interactions
        verifyZeroInteractions(mockApiConnection)
        verifyZeroInteractions(mockDataBaseManager)

        // Assert return type
        assertEquals(IllegalStateException::class.java, completable.blockingGet().javaClass)
    }

    @Test
    fun dynamicUploadFile() {
        `when`(mockApiConnection.dynamicUpload<Any>(anyString(), anyMap() as Map<String, RequestBody>,
                anyListOf<MultipartBody.Part>(MultipartBody.Part::class.java)))
                .thenReturn(observable)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicUploadFile(
                "", HashMap(), HashMap(), Any::class.java)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1))
                .dynamicUpload<Any>(anyString(), anyMap() as Map<String, RequestBody>, anyListOf<MultipartBody.Part>
                (MultipartBody.Part::class.java))
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicUploadFileNoNetwork() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicUploadFile(
                "", HashMap(), HashMap(), Any::class.java)
                .subscribe(testSubscriber)

        testSubscriber.assertNoValues()
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicUploadFileNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicUploadFile(
                "", HashMap(), HashMap(), Any::class.java)
                .subscribe(testSubscriber)

        testSubscriber.assertError(NetworkConnectionException::class.java)
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicDownloadFile() {
        `when`(mockApiConnection.dynamicDownload(anyString())).thenReturn(fileFlowable)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicDownloadFile("", File(""))
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1)

        verify<ApiConnection>(mockApiConnection, times(1)).dynamicDownload(anyString())
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicDownloadFileNoNetwork() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicDownloadFile("", File(""))
                .subscribe(testSubscriber)

        testSubscriber.assertNoValues()
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test
    fun dynamicDownloadFileNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false)

        val testSubscriber = TestObserver<Any>()
        cloudStore.dynamicDownloadFile("", File(""))
                .subscribe(testSubscriber)

        testSubscriber.assertError(NetworkConnectionException::class.java)
        verifyDBInteractions(0, 0, 0, 0)
    }

    @Test(expected = RuntimeException::class)
    fun queryDisk() {
        val observable = cloudStore.queryDisk("", TestModel::class.java)

        // Verify repository interactions
        verifyZeroInteractions(mockApiConnection)
        verifyZeroInteractions(mockDataBaseManager)

        // Assert return type
        val expected = RuntimeException()
        assertEquals(expected.javaClass, observable.firstOrError().blockingGet().javaClass)
    }

    private fun verifyDBInteractions(putAllJ: Int, putAllL: Int, putJ: Int, evict: Int) {
        verify<DataBaseManager>(mockDataBaseManager, times(putAllJ))
                .putAll(anyObject<JSONArray>(), anyObject<Class<TestModel>>())
        verify<DataBaseManager>(mockDataBaseManager, times(putAllL)).putAll<TestModel>(Matchers
                .anyListOf(TestModel::class.java), anyObject())
        verify<DataBaseManager>(mockDataBaseManager, times(putJ))
                .put(anyObject<JSONObject>(), anyObject<Class<TestModel>>())
        verify<DataBaseManager>(mockDataBaseManager, atLeast(evict)).evictById(anyObject<Class<TestModel>>(),
                anyString(), anyObject())
    }

    private fun changeStateOfNetwork(mockedContext: Context, toEnable: Boolean): Context {
        val connectivityManager = Mockito.mock(ConnectivityManager::class.java)
        Mockito.`when`(mockedContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(connectivityManager)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val network = Mockito.mock(Network::class.java)
            val networks = arrayOf(network)
            Mockito.`when`(connectivityManager.allNetworks).thenReturn(networks)
            val networkInfo = Mockito.mock(NetworkInfo::class.java)
            Mockito.`when`(connectivityManager.getNetworkInfo(network)).thenReturn(networkInfo)
            Mockito.`when`<NetworkInfo.State>(networkInfo.state)
                    .thenReturn(if (toEnable) NetworkInfo.State.CONNECTED else NetworkInfo.State.DISCONNECTED)
        } else {
            val networkInfo = Mockito.mock(NetworkInfo::class.java)
            Mockito.`when`(connectivityManager.allNetworkInfo)
                    .thenReturn(arrayOf(networkInfo))
            Mockito.`when`<NetworkInfo.State>(networkInfo.state)
                    .thenReturn(if (toEnable) NetworkInfo.State.CONNECTED else NetworkInfo.State.DISCONNECTED)
        }
        return mockedContext
    }
}
