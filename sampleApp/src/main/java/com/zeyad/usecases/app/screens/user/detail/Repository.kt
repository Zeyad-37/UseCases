package com.zeyad.usecases.app.screens.user.detail

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * @author zeyad on 1/25/17.
 */
@Entity
@Parcelize
open class Repository(@SerializedName("id")
                      @PrimaryKey var id: Int = 0,
                      @SerializedName("name")
                      var name: String = "",
                      @SerializedName("full_name")
                      @ColumnInfo(name = "full_name")
                      var fullName: String = ""
//        ,
//                      @SerializedName("owner")
//                      @IgnoredOnParcel
//                      var owner: User = User()) : Parcelable
) : Parcelable
