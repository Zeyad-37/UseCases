package com.zeyad.usecases.requests

import android.support.test.rule.BuildConfig
import com.zeyad.usecases.TestRealmModel
import com.zeyad.usecases.integration.Success
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.json.JSONException
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [25])
class PostRequestTest {

    private val TO_PERSIST = false
    private val DATA_CLASS = TestRealmModel::class.java
    private val ID_COLUMN_NAME = "id"
    private val URL = "www.google.com"
    //    private final JSONArray JSON_ARRAY = new JSONArray();
    private var JSON_OBJECT: JSONObject? = null
    private val HASH_MAP = HashMap<String, Any>()
    private var mPostRequest: PostRequest? = null

    @Before
    @Throws(JSONException::class)
    fun setUp() {
        val success = Success(true)
        HASH_MAP["success"] = true
        JSON_OBJECT = JSONObject(HASH_MAP)
        JSON_OBJECT = JSONObject("{}")
        JSON_OBJECT!!.put("success", true)
        mPostRequest = PostRequest.Builder(DATA_CLASS, TO_PERSIST)
                //                .payLoad(HASH_MAP)
                //                .payLoad(JSON_ARRAY)
                .idColumnName(ID_COLUMN_NAME, Int::class.javaPrimitiveType!!)
                .responseType(DATA_CLASS)
                .payLoad(success)
                .fullUrl(URL)
                .build()
    }

    @After
    fun tearDown() {
        mPostRequest = null
    }

    @Test
    fun testGetUrl() {
        assertThat(mPostRequest!!.fullUrl, `is`(equalTo(URL)))
    }

    @Test
    fun testGetDataClass() {
        assertThat(mPostRequest!!.getTypedResponseClass(), `is`(equalTo(DATA_CLASS)))
    }

    @Test
    fun testIsPersist() {
        assertThat(mPostRequest!!.persist, `is`(equalTo(TO_PERSIST)))
    }

    @Test
    fun testGetIdColumnName() {
        assertThat(mPostRequest!!.idColumnName, `is`(equalTo(ID_COLUMN_NAME)))
    }
}

