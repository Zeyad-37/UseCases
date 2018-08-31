package com.zeyad.usecases.requests

import android.os.Parcel
import android.os.Parcelable
import com.zeyad.usecases.Config
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * @author zeyad on 7/29/16.
 */
class PostRequest private constructor(val fullUrl: String = "",
                                      val requestType: Class<*> = Any::class.java,
                                      private val responseType: Class<*> = Any::class.java,
                                      val persist: Boolean = false,
                                      val onWifi: Boolean = false,
                                      val whileCharging: Boolean = false,
                                      val queuable: Boolean = false,
                                      val cache: Boolean = false,
                                      val idColumnName: String,
                                      val idType: Class<*> = Any::class.java,
                                      val method: String = "",
                                      keyValuePairs: HashMap<String, Any>? = hashMapOf(),
                                      jsonArray: JSONArray? = JSONArray(),
                                      jsonObject: JSONObject? = JSONObject(),
                                      val `object`: Any? = Any(),
                                      private var payload: String = "") : Parcelable {
    init {
        val objectString = getObjectBundle(`object`, jsonObject, keyValuePairs).toString()
                .replace("\\{".toRegex(), "").replace("}".toRegex(), "")
        val arrayString = getArrayBundle(jsonArray, keyValuePairs).toString()
        payload = when {
            objectString.isEmpty() -> arrayString
            else -> objectString
        }
    }

    constructor(builder: Builder) : this(
            builder.url,
            builder.requestType,
            builder.responseType,
            builder.persist,
            builder.onWifi,
            builder.whileCharging,
            builder.queuable,
            builder.cache,
            builder.idColumnName,
            builder.idType,
            builder.method,
            builder.keyValuePairs,
            builder.jsonArray,
            builder.jsonObject,
            builder.`object`,
            "")

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            Any::class.java,
            Any::class.java,
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            Any::class.java,
            parcel.readString(),
            hashMapOf(),
            JSONArray(),
            JSONObject(),
            Any(),
            parcel.readString())

    fun <M> getTypedResponseClass(): Class<M> = responseType as Class<M>

    fun getObjectBundle(): JSONObject {
        return JSONObject(payload)
    }

    fun getArrayBundle(): JSONArray {
        return JSONArray(payload)
    }

    private fun getObjectBundle(any: Any?, jsonObject: JSONObject?,
                                keyValuePairs: HashMap<String, Any>?): JSONObject {
        return when {
            any != null -> try {
                JSONObject(Config.gson.toJson(any))
            } catch (e: JSONException) {
                JSONObject()
            }
            jsonObject != null -> jsonObject
            keyValuePairs != null -> JSONObject(keyValuePairs)
            else -> JSONObject()
        }
    }

    private fun getArrayBundle(jsonArray: JSONArray?, keyValuePairs: HashMap<String, Any>?): JSONArray {
        return when {
            jsonArray != null -> jsonArray
            keyValuePairs != null -> {
                val result = JSONArray()
                for (item in keyValuePairs.values) {
                    result.put(item)
                }
                result
            }
            `object` is List<*> -> {
                val result = JSONArray()
                for (item in `object`) {
                    result.put(item)
                }
                result
            }
            `object` is Array<*> -> {
                val result = JSONArray()
                for (item in `object`) {
                    result.put(item)
                }
                result
            }
            else -> JSONArray()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fullUrl)
        parcel.writeByte(if (persist) 1 else 0)
        parcel.writeByte(if (onWifi) 1 else 0)
        parcel.writeByte(if (whileCharging) 1 else 0)
        parcel.writeByte(if (queuable) 1 else 0)
        parcel.writeByte(if (cache) 1 else 0)
        parcel.writeString(idColumnName)
        parcel.writeString(method)
        parcel.writeString(payload)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<PostRequest> {
        override fun createFromParcel(parcel: Parcel) = PostRequest(parcel)

        override fun newArray(size: Int) = arrayOfNulls<PostRequest?>(size)

        const val POST = "post"
        const val DELETE = "delete"
        const val PUT = "put"
        const val PATCH = "patch"
    }

    class Builder(internal var requestType: Class<*>, internal var persist: Boolean) {
        internal var `object`: Any? = null
        internal var jsonArray: JSONArray? = null
        internal var jsonObject: JSONObject? = null
        internal var keyValuePairs: HashMap<String, Any>? = null
        internal var url: String = ""
        internal var idColumnName: String = ""
        internal var method: String = ""
        internal var responseType: Class<*> = Any::class.java
        internal var idType: Class<*> = Any::class.java
        internal var queuable: Boolean = false
        internal var cache: Boolean = false
        internal var onWifi: Boolean = false
        internal var whileCharging: Boolean = false

        fun url(url: String): Builder {
            this.url = Config.baseURL + url
            return this
        }

        fun fullUrl(url: String): Builder {
            this.url = url
            return this
        }

        fun responseType(responseType: Class<*>): Builder {
            this.responseType = responseType
            return this
        }

        fun queuable(onWifi: Boolean, whileCharging: Boolean): Builder {
            queuable = true
            this.onWifi = onWifi
            this.whileCharging = whileCharging
            return this
        }

        fun cache(): Builder {
            cache = true
            return this
        }

        fun idColumnName(idColumnName: String, type: Class<*>): Builder {
            this.idColumnName = idColumnName
            idType = type
            return this
        }

        fun payLoad(`object`: Any): Builder {
            this.`object` = `object`
            return this
        }

        fun payLoad(jsonObject: JSONObject): Builder {
            this.jsonObject = jsonObject
            return this
        }

        fun payLoad(jsonArray: JSONArray): Builder {
            this.jsonArray = jsonArray
            return this
        }

        fun payLoad(hashMap: HashMap<String, Any>): Builder {
            keyValuePairs = hashMap
            return this
        }

        fun method(method: String): Builder {
            this.method = method
            return this
        }

        fun build(): PostRequest {
            return PostRequest(this)
        }
    }
}