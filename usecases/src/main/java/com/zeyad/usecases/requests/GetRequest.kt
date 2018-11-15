package com.zeyad.usecases.requests

import com.zeyad.usecases.Config

/**
 * @author zeyad on 7/29/16.
 */
data class GetRequest private constructor(val fullUrl: String = "",
                                          val dataClass: Class<*>,
                                          val persist: Boolean = false,
                                          val idColumnName: String = "id",
                                          val itemId: Any = Any(),
                                          val cache: Boolean = false) {

    constructor(builder: Builder) : this(builder.url,
            builder.dataClass,
            builder.persist,
            builder.idColumnName,
            builder.itemId,
            builder.shouldCache)

    fun <M> getTypedDataClass(): Class<M> = dataClass as Class<M>

    class Builder(internal val dataClass: Class<*>, internal val persist: Boolean = false) {
        internal var itemId: Any = Any()
        internal var shouldCache: Boolean = false
        internal var idColumnName: String = ""
        internal var url: String = ""

        fun url(url: String): Builder {
            this.url = Config.baseURL + url
            return this
        }

        fun fullUrl(url: String): Builder {
            this.url = url
            return this
        }

        fun cache(idColumnName: String): Builder {
            shouldCache = true
            this.idColumnName = idColumnName
            return this
        }

        fun id(id: Any, idColumnName: String): Builder {
            itemId = id
            this.idColumnName = idColumnName
            return this
        }

        fun build(): GetRequest {
            return GetRequest(this)
        }
    }
}
