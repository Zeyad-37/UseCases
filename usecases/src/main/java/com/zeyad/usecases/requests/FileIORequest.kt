package com.zeyad.usecases.requests

import android.os.Parcel
import android.os.Parcelable
import java.io.File

/**
 * @author zeyad on 7/29/16.
 */
data class FileIORequest(val url: String = "",
                         val onWifi: Boolean = false,
                         val whileCharging: Boolean = false,
                         val queuable: Boolean = false,
                         val file: File? = File(""),
                         val dataClass: Class<*>? = Any::class.java,
                         val keyFileMap: HashMap<String, File>? = hashMapOf(),
                         val parameters: HashMap<String, Any> = hashMapOf()) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            File(""),
            Any::class.java,
            hashMapOf(),
            hashMapOf())

    fun <M> getTypedResponseClass(): Class<M> = dataClass as Class<M>

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeByte(if (onWifi) 1 else 0)
        parcel.writeByte(if (whileCharging) 1 else 0)
        parcel.writeByte(if (queuable) 1 else 0)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<FileIORequest> {
        override fun createFromParcel(parcel: Parcel) = FileIORequest(parcel)

        override fun newArray(size: Int) = arrayOfNulls<FileIORequest?>(size)
    }
}


