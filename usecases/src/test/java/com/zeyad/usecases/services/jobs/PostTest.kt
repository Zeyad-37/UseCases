package com.zeyad.usecases.services.jobs

import android.content.Context
import android.support.test.rule.BuildConfig
import com.zeyad.usecases.TestRealmModel
import com.zeyad.usecases.anyObject
import com.zeyad.usecases.network.ApiConnection
import com.zeyad.usecases.requests.PostRequest
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Matchers.anyBoolean
import org.mockito.Matchers.anyListOf
import org.mockito.Matchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.Closeable
import java.io.IOException
import java.io.InputStream

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(25))
class PostTest {
    private val RESPONSE_BODY = mock(ResponseBody::class.java)
    private val INPUT_STREAM = mock(InputStream::class.java)
    private val TEST_MODEL = TestRealmModel(1, "123")
    private lateinit var mockedContext: Context
    private lateinit var apiConnection: ApiConnection

    private val validUrl: String
        get() = "http://www.google.com"

    private val validColumnName: String
        get() = "id"

    private val validDataClass: Class<*>
        get() = TestRealmModel::class.java

    private val listObservable: Flowable<List<*>>
        get() = Flowable.just(listOf(createTestModel()))

    private val objectObservable: Flowable<Any>
        get() = Flowable.just(createTestModel())

    private val responseBodyObservable: Flowable<ResponseBody>
        get() = Flowable.fromCallable { responseBody }


    private val responseBody: ResponseBody?
        @Throws(IOException::class)
        get() {
            `when`(RESPONSE_BODY!!.byteStream()).thenReturn(inputStreamReader)
            `when`(RESPONSE_BODY.contentLength()).thenReturn((1096 * 1096).toLong())
            return RESPONSE_BODY
        }

    private val inputStreamReader: InputStream
        @Throws(IOException::class)
        get() {
            `when`(INPUT_STREAM.read(any())).thenReturn(1096, 1096, 1096, -1)
            return INPUT_STREAM
        }

    @Before
    fun setUp() {
        mockedContext = mock(Context::class.java)
        apiConnection = createRestApi()
    }

    @After
    fun tearDown() {
        reset<Closeable>(RESPONSE_BODY, INPUT_STREAM)
    }

    @Test
    @Throws(JSONException::class)
    fun testPatchObject() {
        val post = createPost(mockedContext,
                createPostRequestForJsonObject(PostRequest.PATCH),
                apiConnection,
                3)
        post.execute()
        verify<ApiConnection>(apiConnection, times(1)).dynamicPatch<Any>(anyString(), anyObject())
    }

    @Test
    @Throws(JSONException::class)
    fun testPostObject() {
        val post = createPost(mockedContext,
                createPostRequestForJsonObject(PostRequest.POST),
                apiConnection,
                3)
        post.execute()
        verify<ApiConnection>(apiConnection, times(1)).dynamicPost<Any>(anyString(), anyObject())
    }

    @Test
    @Throws(JSONException::class)
    fun testPostList() {
        val post = createPost(
                mockedContext,
                createPostRequestForJsonArray(PostRequest.POST),
                apiConnection,
                3)
        post.execute()
        verify<ApiConnection>(apiConnection, times(1)).dynamicPost<Any>(anyString(), anyObject())
    }

    @Test
    @Throws(JSONException::class)
    fun testPutObject() {
        val post = createPost(mockedContext,
                createPostRequestForJsonObject(PostRequest.PUT),
                apiConnection,
                3)
        post.execute()
        verify<ApiConnection>(apiConnection, times(1)).dynamicPut<Any>(anyString(), anyObject())
    }

    @Test
    @Throws(JSONException::class)
    fun testPutList() {
        val post = createPost(mockedContext,
                createPostRequestForJsonArray(PostRequest.PUT),
                apiConnection,
                3)
        post.execute()
        verify<ApiConnection>(apiConnection, times(1)).dynamicPut<Any>(anyString(), anyObject())
    }

    @Test
    @Throws(JSONException::class)
    fun testDeleteObject() {
        val post = createPost(mockedContext,
                createPostRequestForJsonObject(PostRequest.DELETE),
                apiConnection,
                3)
        post.execute()
        verify<ApiConnection>(apiConnection, times(1)).dynamicDelete<Any>(anyString())
    }

    @Test
    @Throws(JSONException::class)
    fun testDeleteList() {
        val post = createPost(mockedContext,
                createPostRequestForJsonArray(PostRequest.DELETE),
                apiConnection,
                3)
        post.execute()
        verify<ApiConnection>(apiConnection, times(1)).dynamicDelete<Any>(anyString())
    }

    @Test
    @Throws(JSONException::class)
    fun testReQueue() {
        val post = createPost(mockedContext,
                createPostRequestForJsonArray(PostRequest.DELETE),
                apiConnection,
                0)
        //        Mockito.doNothing().when(utils).queuePostCore(any(), any(PostRequest.class), anyInt());
        //        post.queuePost();
        //        verify(utils, times(1)).queuePostCore(any(), any(PostRequest.class), anyInt());
    }

    //--------------------------------------------------------------------------------------------//

    private fun createPost(
            context: Context?, postRequest: PostRequest, apiConnection: ApiConnection?, trailCount: Int): Post {
        return Post(context!!, postRequest, apiConnection!!, trailCount)
    }

    private fun createRestApi(): ApiConnection {
        val OBJECT_OBSERVABLE = objectObservable
        apiConnection = mock(ApiConnection::class.java)
        `when`(apiConnection.dynamicDownload(anyString())).thenReturn(responseBodyObservable)
        `when`(apiConnection.dynamicDelete<Any>(anyString())).thenReturn(OBJECT_OBSERVABLE)
        `when`(apiConnection.dynamicGetObject<Any>(anyObject(), anyBoolean())).thenReturn(OBJECT_OBSERVABLE)
        `when`(apiConnection.dynamicGetObject<Any>(anyObject())).thenReturn(OBJECT_OBSERVABLE)
        //        when(apiConnection.dynamicGetList(any())).thenReturn(getListObservable());
        //        when(apiConnection.dynamicGetList(any(), anyBoolean())).thenReturn(getListObservable());
        `when`(apiConnection.dynamicPost<Any>(anyObject(), anyObject())).thenReturn(OBJECT_OBSERVABLE)
        `when`(apiConnection.dynamicPut<Any>(anyObject(), anyObject())).thenReturn(OBJECT_OBSERVABLE)
        `when`(apiConnection.dynamicPatch<Any>(anyObject(), anyObject())).thenReturn(OBJECT_OBSERVABLE)
        `when`(apiConnection.dynamicUpload<Any>(anyObject(), anyObject(),
                anyListOf<MultipartBody.Part>(MultipartBody.Part::class.java)))
                .thenReturn(OBJECT_OBSERVABLE)
        return apiConnection
    }

    private fun createTestModel(): TestRealmModel {
        return TEST_MODEL
    }

    @Throws(JSONException::class)
    private fun createPostRequestForJsonObject(method: String): PostRequest {
        return PostRequest.Builder(validDataClass, false)
                .payLoad(JSONObject("{\"login\": \"Zeyad-37\", \"id\": 5938141, \"avatar_url\": " + "\"https://avatars2.githubusercontent.com/u/5938141?v=3\"}"))
                .idColumnName(validColumnName, Int::class.javaPrimitiveType!!)
                .url(validUrl)
                .method(method)
                .build()
    }

    @Throws(JSONException::class)
    private fun createPostRequestForJsonArray(method: String): PostRequest {
        return PostRequest.Builder(validDataClass, false)
                .payLoad(JSONArray("[ \"Ford\", \"BMW\", \"Fiat\" ]"))
                .idColumnName(validColumnName, Int::class.javaPrimitiveType!!)
                .url(validUrl)
                .method(method)
                .build()
    }
}
