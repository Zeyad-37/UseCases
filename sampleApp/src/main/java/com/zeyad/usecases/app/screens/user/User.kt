package com.zeyad.usecases.app.screens.user

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.zeyad.usecases.app.R.id.id
import kotlinx.android.parcel.Parcelize

/**
 * @author zeyad on 1/10/17.
 */
@Entity
@Parcelize
open class User(@SerializedName(LOGIN)
                var login: String = "",
                @SerializedName(ID)
                @PrimaryKey var id: Int = 0,
                @SerializedName(AVATAR_URL)
                @ColumnInfo(name = AVATAR_URL)
                var avatarUrl: String = "") : Parcelable {

    companion object {
        const val LOGIN = "login"
        private const val ID = "id"
        private const val AVATAR_URL = "avatar_url"

        fun isEmpty(user: User): Boolean {
            return user.login.isEmpty() && id <= 0 && user.avatarUrl.isEmpty()
        }
    }
}
