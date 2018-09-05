package com.zeyad.usecases.network

import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * RestApi for retrieving data from the network.
 */
interface RestApi {

    @GET
    fun dynamicGetObject(@Url url: String): Flowable<Any>

    @GET
    fun dynamicGetObject(@Url url: String, shouldCache: Boolean): Flowable<Any>

    @GET
    fun dynamicGetList(@Url url: String): Flowable<List<Any>>

    @GET
    fun dynamicGetList(@Url url: String, shouldCache: Boolean): Flowable<List<Any>>

    @POST
    fun dynamicPost(@Url url: String, @Body body: RequestBody): Flowable<Any>

    @PUT
    fun dynamicPut(@Url url: String, @Body body: RequestBody): Flowable<Any>

    @PATCH
    fun dynamicPatch(@Url url: String, @Body requestBody: RequestBody): Flowable<Any>

    @DELETE
    fun dynamicDelete(@Url url: String): Flowable<Any>

    @Streaming
    @GET
    fun dynamicDownload(@Url fileUrl: String): Flowable<ResponseBody>

    @Multipart
    @POST
    fun dynamicUpload(@Url url: String, @PartMap partMap: Map<String, RequestBody>,
                      @Part file: List<MultipartBody.Part>): Flowable<Any>
}
