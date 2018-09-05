package com.zeyad.usecases.services

import android.content.Context
import android.os.Bundle
import android.support.test.rule.BuildConfig
import com.zeyad.usecases.anyObject
import com.zeyad.usecases.network.ApiConnection
import com.zeyad.usecases.requests.PostRequest
import com.zeyad.usecases.requests.PostRequest.CREATOR.POST
import com.zeyad.usecases.stores.CloudStore
import io.reactivex.Flowable
import io.reactivex.observers.TestObserver
import okhttp3.RequestBody
import org.json.JSONArray
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.internal.matchers.Any
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * @author by ZIaDo on 5/20/17.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [25])
class GenericJobServiceLogicTest {
    @Test
    fun startJob() {
        com.zeyad.usecases.Config.context = mock(Context::class.java)
        val apiConnection = mock(ApiConnection::class.java)
        com.zeyad.usecases.Config.apiConnection = apiConnection
        `when`(apiConnection.dynamicPost<kotlin.Any>(anyString(), anyObject<RequestBody>()))
                .thenReturn(Flowable.just<kotlin.Any>(listOf<kotlin.Any>()))

        val genericJobServiceLogic = GenericJobServiceLogic()
        val testSubscriber = TestObserver<Any>()

        val extras = Bundle(2)
        extras.putString(GenericJobService.JOB_TYPE, GenericJobService.POST)
        extras.putParcelable(GenericJobService.PAYLOAD, PostRequest.Builder(Any::class.java, true)
                .idColumnName("id", Int::class.javaPrimitiveType!!)
                .payLoad(JSONArray())
                .url("")
                .method(POST)
                .build())
        genericJobServiceLogic.startJob(extras, mock(CloudStore::class.java), "")
                .subscribe(TestObserver<Any>())
        testSubscriber.assertNoErrors()
        //        testSubscriber.assertComplete();
    }
}

