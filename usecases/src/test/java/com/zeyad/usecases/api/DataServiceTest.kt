package com.zeyad.usecases.api

import com.zeyad.usecases.TestRealmModel
import com.zeyad.usecases.requests.FileIORequest
import com.zeyad.usecases.requests.GetRequest
import com.zeyad.usecases.requests.PostRequest
import com.zeyad.usecases.stores.*
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers.any
import org.mockito.Matchers.anyBoolean
import org.mockito.Matchers.anyMap
import org.mockito.Matchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.io.File
import java.util.*

/**
 * @author by ZIaDo on 5/9/17.
 */
class DataServiceTest {

    private lateinit var dataService: DataService
    private lateinit var dataStoreFactory: DataStoreFactory
    private lateinit var getRequest: GetRequest<Any>
    private lateinit var postRequest: PostRequest
    private lateinit var flowable: Flowable<Any>
    private lateinit var fileFlowable: Flowable<File>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        flowable = Flowable.just(true)
        fileFlowable = Flowable.just(File(""))
        postRequest = PostRequest.Builder(Any::class.java, false).build()
        getRequest = GetRequest.Builder(TestRealmModel::class.java, false)
                .url("")
                .id(37, "id", Int::class.javaPrimitiveType)
                .cache("id")
                .build()
        dataStoreFactory = mock(DataStoreFactory::class.java)
        `when`<DataStore>(dataStoreFactory.dynamically(anyString(), any(Class::class.java)))
                .thenReturn(mock(CloudStore::class.java))
        `when`<DataStore>(dataStoreFactory.disk(any())).thenReturn(mock(DiskStore::class.java))
        `when`<DataStore>(dataStoreFactory.cloud(any())).thenReturn(mock(CloudStore::class.java))
        `when`(dataStoreFactory.memory()).thenReturn(mock(MemoryStore::class.java))
        dataService = DataService(
                dataStoreFactory, AndroidSchedulers.mainThread(), mock(Scheduler::class.java))
        com.zeyad.usecases.Config.setWithCache(false)
        com.zeyad.usecases.Config.setWithSQLite(true)
    }

    @Test
    @Throws(Exception::class)
    fun getList() {
        `when`(dataStoreFactory
                .dynamically(anyString(), any(Class::class.java))
                .dynamicGetList<Any>(anyString(), anyString(), any(Class::class.java), anyBoolean(), anyBoolean()))
                .thenReturn(Flowable.just(Collections.EMPTY_LIST))

        dataService.getList(getRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), any(Class::class.java)), times(1))
                .dynamicGetList<Any>(anyString(), anyString(), any(Class::class.java), anyBoolean(), anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun getObject() {
        `when`(dataStoreFactory
                .dynamically(anyString(), any(Class::class.java))
                .dynamicGetObject<Any>(
                        anyString(),
                        anyString(),
                        any(),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.getObject(getRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), any(Class::class.java)), times(1))
                .dynamicGetObject<Any>(
                        anyString(),
                        anyString(),
                        any<Any>(),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean())
    }

//    @Test
//    @Throws(Exception::class)
//    fun getListOffLineFirst() {
//        `when`(dataStoreFactory
//                .cloud(Any::class.java)
//                .dynamicGetList<Any>(anyString(), anyString(), any(Class::class.java), anyBoolean(), anyBoolean()))
//                .thenReturn(Flowable.just(Collections.EMPTY_LIST))
//        `when`(dataStoreFactory
//                .disk(Any::class.java)
//                .dynamicGetList<Any>(anyString(), anyString(), any(Class::class.java), anyBoolean(), anyBoolean()))
//                .thenReturn(Flowable.just(Collections.EMPTY_LIST))
//        `when`(dataStoreFactory
//                .memory()
//                .getAllItems<Any>(any<Class<Any>>(Class<Any>::class.java)))
//                .thenReturn(Single.just<List<Boolean>>(listOf(true)))
//
//        dataService.getListOffLineFirst<Any>(getRequest)
//
//        verify<DataStore>(dataStoreFactory.cloud(Any::class.java), times(1))
//                .dynamicGetList<Any>(anyString(), anyString(), any(Class::class.java), anyBoolean(), anyBoolean())
//        verify<DataStore>(dataStoreFactory.disk(Any::class.java), times(1))
//                .dynamicGetList<Any>(anyString(), anyString(), any(Class::class.java), anyBoolean(), anyBoolean())
//    }

    @Test
    @Throws(Exception::class)
    fun getObjectOffLineFirst() {
        `when`(dataStoreFactory.cloud(Any::class.java)
                .dynamicGetObject<Any>(
                        anyString(),
                        anyString(),
                        any(),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)
        `when`(dataStoreFactory
                .disk(Any::class.java)
                .dynamicGetObject<Any>(
                        anyString(),
                        anyString(),
                        any(),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        `when`(dataStoreFactory
                .memory()
                .getItem<Any>(anyString(), any(Class::class.java)))
                .thenReturn(Single.just(true))

        dataService.getObjectOffLineFirst(getRequest)

        verify<DataStore>(dataStoreFactory.cloud(Any::class.java), times(1))
                .dynamicGetObject<Any>(
                        anyString(),
                        anyString(),
                        any<Any>(),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean())
        verify<DataStore>(dataStoreFactory.disk(Any::class.java), times(1))
                .dynamicGetObject<Any>(
                        anyString(),
                        anyString(),
                        any<Any>(),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun patchObject() {
        `when`(dataStoreFactory.dynamically(anyString(), any(Class::class.java))
                .dynamicPatchObject<Any>(
                        anyString(),
                        anyString(),
                        any(Class::class.java),
                        any(JSONObject::class.java),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.patchObject<Any>(postRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), any(Class::class.java)), times(1))
                .dynamicPatchObject<Any>(
                        anyString(),
                        anyString(),
                        any(Class::class.java),
                        any(JSONObject::class.java),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun postObject() {
        `when`(dataStoreFactory
                .dynamically(anyString(), any(Class::class.java))
                .dynamicPostObject<Any>(
                        anyString(),
                        anyString(),
                        any(Class::class.java),
                        any(JSONObject::class.java),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.postObject<Any>(postRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), any(Class::class.java)), times(1))
                .dynamicPostObject<Any>(
                        anyString(),
                        anyString(),
                        any(Class::class.java),
                        any(JSONObject::class.java),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun postList() {
        `when`(dataStoreFactory
                .dynamically(anyString(), any(Class::class.java))
                .dynamicPostList<Any>(
                        anyString(),
                        anyString(),
                        any(Class::class.java),
                        any(JSONArray::class.java),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.postList<Any>(postRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), any(Class::class.java)), times(1))
                .dynamicPostList<Any>(
                        anyString(),
                        anyString(),
                        any(Class::class.java),
                        any(JSONArray::class.java),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun putObject() {
        `when`(dataStoreFactory
                .dynamically(anyString(), any(Class::class.java))
                .dynamicPutObject<Any>(
                        anyString(),
                        anyString(),
                        any(Class::class.java),
                        any(JSONObject::class.java),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.putObject<Any>(postRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), any(Class::class.java)), times(1))
                .dynamicPutObject<Any>(
                        anyString(),
                        anyString(),
                        any(Class::class.java),
                        any(JSONObject::class.java),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun putList() {
        `when`(dataStoreFactory
                .dynamically(anyString(), any(Class::class.java))
                .dynamicPutList<Any>(
                        anyString(),
                        anyString(),
                        any(Class::class.java),
                        any(JSONArray::class.java),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.putList<Any>(postRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), any(Class::class.java)), times(1))
                .dynamicPutList<Any>(
                        anyString(),
                        anyString(),
                        any(Class::class.java),
                        any(JSONArray::class.java),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun deleteItemById() {
        `when`(dataStoreFactory
                .dynamically(anyString(), any(Class::class.java))
                .dynamicDeleteCollection<Any>(
                        anyString(),
                        anyString(),
                        any(Class::class.java),
                        any(JSONArray::class.java),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.deleteItemById<Any>(postRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), any(Class::class.java)), times(1))
                .dynamicDeleteCollection<Any>(
                        anyString(),
                        anyString(),
                        any(Class::class.java),
                        any(JSONArray::class.java),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun deleteCollection() {
        `when`(dataStoreFactory
                .dynamically(anyString(), any(Class::class.java))
                .dynamicDeleteCollection<Any>(
                        anyString(),
                        anyString(),
                        any(Class::class.java),
                        any(JSONArray::class.java),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable)

        dataService.deleteCollectionByIds<Any>(postRequest)

        verify<DataStore>(dataStoreFactory.dynamically(anyString(), any(Class::class.java)), times(1))
                .dynamicDeleteCollection<Any>(
                        anyString(),
                        anyString(),
                        any(Class::class.java),
                        any(JSONArray::class.java),
                        any(Class::class.java),
                        any(Class::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean())
    }

    @Test
    @Throws(Exception::class)
    fun deleteAll() {
        `when`(dataStoreFactory.disk(Any::class.java).dynamicDeleteAll(any(Class::class.java)))
                .thenReturn(Single.just(true))

        dataService.deleteAll(postRequest)

        verify<DataStore>(dataStoreFactory.disk(Any::class.java), times(1)).dynamicDeleteAll(any(Class::class.java))
    }
// Todo fix
//    @Test
//    @Throws(Exception::class)
//    fun queryDisk() {
//        `when`(dataStoreFactory.disk(Any::class.java)
//                .queryDisk<Any>(any<RealmQueryProvider<TestRealmModel>>(RealmQueryProvider<TestRealmModel>::class.java)))
//                .thenReturn(flowable)
//
//        dataService.queryDisk(RealmQueryProvider<TestRealmModel> { realm -> realm.where(TestRealmModel::class.java) })
//
//        verify<DataStore>(dataStoreFactory.disk(Any::class.java), times(1))
//                .queryDisk(any<RealmQueryProvider<TestRealmModel>>(RealmQueryProvider<TestRealmModel>::class.java))
//    }

    @Test
    @Throws(Exception::class)
    fun uploadFile() {
        `when`<Flowable<Any>>(dataStoreFactory
                .cloud(Any::class.java)
                .dynamicUploadFile<Any>(
                        anyString(),
                        anyMap() as HashMap<String, File>,
                        anyMap() as HashMap<String, Any>,
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean(),
                        any(Class::class.java)))
                .thenReturn(flowable)

        dataService.uploadFile<Any>(
                FileIORequest.Builder("").responseType(Any::class.java).build())

        verify<DataStore>(dataStoreFactory.cloud(Any::class.java), times(1))
                .dynamicUploadFile<Any>(
                        anyString(),
                        anyMap() as HashMap<String, File>,
                        anyMap() as HashMap<String, Any>,
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean(),
                        any(Class::class.java))
    }

    @Test
    @Throws(Exception::class)
    fun downloadFile() {
        `when`<Flowable<File>>(dataStoreFactory
                .cloud(Any::class.java)
                .dynamicDownloadFile(
                        anyString(),
                        any(File::class.java),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(fileFlowable)

        dataService.downloadFile(
                FileIORequest.Builder("").responseType(Any::class.java).build())

        verify<DataStore>(dataStoreFactory.cloud(Any::class.java), times(1))
                .dynamicDownloadFile(
                        anyString(), any(File::class.java), anyBoolean(), anyBoolean(), anyBoolean())
    }
}

