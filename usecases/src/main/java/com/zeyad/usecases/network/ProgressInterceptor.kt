package com.zeyad.usecases.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author by ZIaDo.
 */
abstract class ProgressInterceptor(private val progressListener: ProgressListener) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val responseBuilder = originalResponse.newBuilder()
        if (isFileIO(originalResponse)) {
            responseBuilder.body(ProgressResponseBody(originalResponse.body(), progressListener))
        } else {
            responseBuilder.body(originalResponse.body())
        }
        return responseBuilder.build()
    }

    abstract fun isFileIO(originalResponse: Response): Boolean
}
