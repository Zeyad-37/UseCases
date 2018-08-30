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
class PostRequest(private val url: String = "",
                  private val fullUrl: String = "",
                  val requestType: Class<*> = Any::class.java,
                  val responseType: Class<*> = Any::class.java,
                  val persist: Boolean = false,
                  val onWifi: Boolean = false,
                  val whileCharging: Boolean = false,
                  val queuable: Boolean = false,
                  val cache: Boolean = false,
                  val idColumnName: String,
                  val idType: Class<*> = Any::class.java,
                  val method: String = "",
                  keyValuePairs: HashMap<String, Any> = hashMapOf(),
                  jsonArray: JSONArray = JSONArray(),
                  jsonObject: JSONObject = JSONObject(),
                  val `object`: Any = Any(),
                  private var payload: String = "") : Parcelable {
    init {
        val objectString = getObjectBundle(jsonObject, keyValuePairs).toString()
        val arrayString = getArrayBundle(jsonArray, keyValuePairs).toString()
        payload = when {
            objectString.replace("\\{".toRegex(), "").replace("\\}".toRegex(), "").isEmpty()
            -> arrayString
            else -> objectString
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
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

    fun getCorrectUrl(): String {
        return when {
            fullUrl.isNotBlank() -> fullUrl
            url.isNotBlank() -> Config.baseURL + url
            else -> ""
        }
    }

    fun getObjectBundle(): JSONObject {
        return JSONObject(payload)
    }

    fun getArrayBundle(): JSONArray {
        return JSONArray(payload)
    }

    private fun getObjectBundle(jsonObject: JSONObject?, keyValuePairs: HashMap<String, Any>?): JSONObject {
        return when {
            `object` != null -> try {
                JSONObject(Config.gson.toJson(`object`))
            } catch (e: JSONException) {
                JSONObject()
            }
            jsonObject != null -> jsonObject
            keyValuePairs != null -> JSONObject(keyValuePairs)
            else -> JSONObject()
        }
    }

    private fun getArrayBundle(jsonArray: JSONArray?, keyValuePairs: HashMap<String, Any>?): JSONArray {
        when {
            jsonArray != null -> return jsonArray
            keyValuePairs != null -> {
                val result = JSONArray()
                for (item in keyValuePairs.values) {
                    result.put(item)
                }
                return result
            }
            `object` is List<*> -> {
                val result = JSONArray()
                for (item in `object`) {
                    result.put(item)
                }
                return result
            }
            `object` is Array<*> -> {
                val result = JSONArray()
                for (item in `object`) {
                    result.put(item)
                }
                return result
            }
            else -> return JSONArray()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
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
}