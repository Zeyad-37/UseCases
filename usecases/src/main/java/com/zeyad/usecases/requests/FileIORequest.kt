package com.zeyad.usecases.requests

import android.os.Parcel
import android.os.Parcelable
import com.zeyad.usecases.Mockable
import java.io.File

/**
 * @author zeyad on 7/29/16.
 */
@Mockable
data class FileIORequest private constructor(val url: String = "",
                                             val file: File? = File(""),
                                             val dataClass: Class<*>? = Any::class.java,
                                             val keyFileMap: HashMap<String, File>? = hashMapOf(),
                                             val parameters: HashMap<String, Any> = hashMapOf()) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            File(""),
            Any::class.java,
            hashMapOf(),
            hashMapOf())

    constructor (uploadRequestBuilder: Builder) : this(
            uploadRequestBuilder.url,
            uploadRequestBuilder.file,
            uploadRequestBuilder.dataClass,
            uploadRequestBuilder.keyFileMap,
            uploadRequestBuilder.parameters
    )

    fun <M> getTypedResponseClass(): Class<M> = dataClass as Class<M>

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<FileIORequest> {
        override fun createFromParcel(parcel: Parcel) = FileIORequest(parcel)

        override fun newArray(size: Int) = arrayOfNulls<FileIORequest?>(size)
    }

    class Builder(internal val url: String, internal var file: File = File("")) {
        internal var onWifi: Boolean = false
        internal var whileCharging: Boolean = false
        internal var queuable: Boolean = false
        internal var dataClass: Class<*>? = null
        internal var parameters: HashMap<String, Any> = hashMapOf()
        internal var keyFileMap: HashMap<String, File>? = hashMapOf()

        fun responseType(dataClass: Class<*>): Builder {
            this.dataClass = dataClass
            return this
        }

        fun requestType(dataClass: Class<*>): Builder {
            this.dataClass = dataClass
            return this
        }

        fun keyFileMapToUpload(keyFileMap: HashMap<String, File>?): Builder {
            this.keyFileMap = keyFileMap
            return this
        }

        fun payLoad(parameters: HashMap<String, Any>): Builder {
            this.parameters = parameters
            return this
        }

        fun build(): FileIORequest {
            return FileIORequest(this)
        }
    }
}
