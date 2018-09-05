package com.zeyad.usecases.api

import android.support.test.rule.BuildConfig
import com.zeyad.usecases.TestRealmModel
import com.zeyad.usecases.db.RealmQueryProvider
import com.zeyad.usecases.requests.FileIORequest
import com.zeyad.usecases.requests.GetRequest
import com.zeyad.usecases.requests.PostRequest
import com.zeyad.usecases.stores.*
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.Realm
import io.realm.RealmQuery
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers.*
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.*

/**
 * @author by ZIaDo on 5/9/17.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [25])
class DataServiceTest {

    private lateinit var dataService: DataService
    private lateinit var dataStoreFactory: DataStoreFactory
    private lateinit var getRequest: GetRequest
    private lateinit var postRequest: PostRequest
    private lateinit var flowable: Flowable<Any>
    private lateinit var fileFlowable: Flowable<File>

    private fun <T> anyObject(): T {
        return Mockito.anyObject<T>()
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        flowable = Flowable.just(true)
        fileFlowable = Flowable.just(File(""))
        postRequest = PostRequest.Builder(TestRealmModel::class.java, false).payLoad(Any()).build()
        getRequest = GetRequest.Builder(TestRealmModel::class.java, false)
                .url("")
                .id(37, "id", Int::class.java)
                .cache("id")
                .build()
        dataStoreFactory = mock(DataStoreFactory::class.java)
        `when`<DataStore>(dataStoreFactory.dynamically(anyString(), anyObject()))
                .thenReturn(mock(CloudStore::class.java))
        `when`<DataStore>(dataStoreFactory.disk(anyObject())).thenReturn(mock(DiskStore::class.java))
        `when`<DataStore>(dataStoreFactory.cloud(anyObject())).thenReturn(mock(CloudStore::class.java))
        `when`(dataStoreFactory.memory()).thenReturn(mock(MemoryStore::class.java))
        dataService = DataService(
                dataStoreFactory, AndroidSchedulers.mainThread(), mock(Scheduler::class.java))
        com.zeyad.usecases.Config.withCache = false
        com.zeyad.usecases.Config.withSQLite = true
    }

    @Test
    @Throws(Exception::class)
    fun getList() {
        `when`(dataStoreFactory
                .dynamically(anyString(), anyObject())
                .dynamicGetList<Any>(anyString(),
                        anyString(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(Flowable.just(mutableListOf()))

        dataService.getList<TestRealmModel>(getRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), anyObject()), times(1))
                .dynamicGetList<Any>(anyString(),
                        anyString(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun getObject() {
        `when`(dataStoreFactory
                .dynamically(anyString(), anyObject())
                .dynamicGetObject<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.getObject<TestRealmModel>(getRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), anyObject()), times(1))
                .dynamicGetObject<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun getListOffLineFirst() {
        `when`(dataStoreFactory
                .cloud(Any::class.java)
                .dynamicGetList<Any>(anyString(), anyString(), anyObject(), anyBoolean(), anyBoolean()))
                .thenReturn(Flowable.just(listOf()))
        `when`(dataStoreFactory
                .disk(Any::class.java)
                .dynamicGetList<Any>(anyString(), anyString(), anyObject(), anyBoolean(), anyBoolean()))
                .thenReturn(Flowable.just(listOf()))
        `when`(dataStoreFactory
                .memory()?.getAllItems(anyObject<Class<Any>>()))
                .thenReturn(Single.just<List<Any>>(listOf(true)))

        dataService.getListOffLineFirst<Any>(getRequest)

        verify<DataStore>(dataStoreFactory.cloud(Any::class.java), times(1))
                .dynamicGetList<Any>(anyString(), anyString(), anyObject(), anyBoolean(), anyBoolean())
        verify<DataStore>(dataStoreFactory.disk(Any::class.java), times(1))
                .dynamicGetList<Any>(anyString(), anyString(), anyObject(), anyBoolean(), anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun getObjectOffLineFirst() {
        `when`(dataStoreFactory.cloud(Any::class.java)
                .dynamicGetObject<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)
        `when`(dataStoreFactory
                .disk(Any::class.java)
                .dynamicGetObject<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        `when`(dataStoreFactory
                .memory()
                ?.getItem<Any>(anyString(), anyObject()))
                .thenReturn(Single.just(true))

        dataService.getObjectOffLineFirst<TestRealmModel>(getRequest)

        verify<DataStore>(dataStoreFactory.cloud(Any::class.java), times(1))
                .dynamicGetObject<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean())
        verify<DataStore>(dataStoreFactory.disk(Any::class.java), times(1))
                .dynamicGetObject<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun patchObject() {
        `when`(dataStoreFactory.dynamically(anyString(), anyObject())
                .dynamicPatchObject<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.patchObject<TestRealmModel>(postRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), anyObject()), times(1))
                .dynamicPatchObject<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun postObject() {
        `when`(dataStoreFactory
                .dynamically(anyString(), anyObject())
                .dynamicPostObject<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.postObject<TestRealmModel>(postRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), anyObject()), times(1))
                .dynamicPostObject<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun postList() {
        `when`(dataStoreFactory
                .dynamically(anyString(), anyObject())
                .dynamicPostList<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.postList<TestRealmModel>(postRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), anyObject()), times(1))
                .dynamicPostList<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun putObject() {
        `when`(dataStoreFactory
                .dynamically(anyString(), anyObject())
                .dynamicPutObject<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.putObject<TestRealmModel>(postRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), anyObject()), times(1))
                .dynamicPutObject<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun putList() {
        `when`(dataStoreFactory
                .dynamically(anyString(), anyObject())
                .dynamicPutList<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.putList<TestRealmModel>(postRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), anyObject()), times(1))
                .dynamicPutList<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun deleteItemById() {
        `when`(dataStoreFactory
                .dynamically(anyString(), anyObject())
                .dynamicDeleteCollection<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.deleteItemById<TestRealmModel>(postRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), anyObject()), times(1))
                .dynamicDeleteCollection<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun deleteCollection() {
        `when`(dataStoreFactory
                .dynamically(anyString(), anyObject())
                .dynamicDeleteCollection<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.deleteCollectionByIds<TestRealmModel>(postRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), anyObject()), times(1))
                .dynamicDeleteCollection<Any>(
                        anyString(),
                        anyString(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun deleteAll() {
        `when`(dataStoreFactory.disk(Any::class.java).dynamicDeleteAll(anyObject()))
                .thenReturn(Single.just(true))

        dataService.deleteAll(postRequest)

        verify<DataStore>(dataStoreFactory.disk(Any::class.java), times(1)).dynamicDeleteAll(anyObject())
    }

    @Test
    @Throws(Exception::class)
    fun queryDisk() {
        `when`(dataStoreFactory.disk(Any::class.java)
                .queryDisk(anyObject<RealmQueryProvider<TestRealmModel>>()))
                .thenReturn(Flowable.just(listOf(TestRealmModel())))

        dataService.queryDisk(object : RealmQueryProvider<TestRealmModel> {
            override fun create(realm: Realm): RealmQuery<TestRealmModel> =
                    realm.where(TestRealmModel::class.java)
        })

        verify<DataStore>(dataStoreFactory.disk(Any::class.java), times(1))
                .queryDisk(anyObject<RealmQueryProvider<TestRealmModel>>())
    }

    @Test
    @Throws(Exception::class)
    fun uploadFile() {
        `when`<Flowable<Any>>(dataStoreFactory
                .cloud(Any::class.java)
                .dynamicUploadFile(
                        anyString(),
                        anyMap() as HashMap<String, File>,
                        anyMap() as HashMap<String, Any>,
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean(),
                        anyObject()))
                .thenReturn(flowable)

        dataService.uploadFile<Any>(
                FileIORequest.Builder("", File("")).responseType(Any::class.java).build())

        verify<DataStore>(dataStoreFactory.cloud(Any::class.java), times(1))
                .dynamicUploadFile<Any>(
                        anyString(),
                        anyMap() as HashMap<String, File>,
                        anyMap() as HashMap<String, Any>,
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean(),
                        anyObject())
    }

    @Test
    @Throws(Exception::class)
    fun downloadFile() {
        `when`<Flowable<File>>(dataStoreFactory
                .cloud(Any::class.java)
                .dynamicDownloadFile(
                        anyString(),
                        anyObject(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(fileFlowable)

        dataService.downloadFile(
                FileIORequest.Builder("", File("")).responseType(Any::class.java).build())

        verify<DataStore>(dataStoreFactory.cloud(Any::class.java), times(1))
                .dynamicDownloadFile(
                        anyString(), anyObject(), anyBoolean(), anyBoolean(), anyBoolean())
    }
}

