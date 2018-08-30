package com.zeyad.usecases.requests

import com.zeyad.usecases.Config

/**
 * @author zeyad on 7/29/16.
 */
data class GetRequest(private val url: String = "",
                      private val fullUrl: String = "",
                      val dataClass: Class<*>,
                      val idType: Class<*>,
                      val persist: Boolean = false,
                      val idColumnName: String = "id",
                      val itemId: Any = Any(),
                      val cache: Boolean = false) {

    fun <M> getTypedDataClass(): Class<M> = dataClass as Class<M>

    fun getCorrectUrl(): String =
            when {
                fullUrl.isNotBlank() -> fullUrl
                url.isNotBlank() -> Config.baseURL + url
                else -> ""
            }
}
