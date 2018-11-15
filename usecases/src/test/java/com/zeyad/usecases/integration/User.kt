package com.zeyad.usecases.integration

import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName


/**
 * @author zeyad on 1/10/17.
 */
class User internal constructor() {
    @SerializedName(ID)
    var id: Int = 0
    @PrimaryKey
    @SerializedName(LOGIN)
    var login: String? = null
    @SerializedName(AVATAR_URL)
    var avatarUrl: String? = null

    override fun hashCode(): Int {
        var result = if (login != null) login!!.hashCode() else 0
        result = 31 * result + id
        result = 31 * result + if (avatarUrl != null) avatarUrl!!.hashCode() else 0
        return result
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val user = o as User?
        return id == user!!.id && if (login != null)
            login == user.login
        else
            user.login == null && if (avatarUrl != null)
                avatarUrl == user.avatarUrl
            else
                user.avatarUrl == null
    }

    companion object {

        const val LOGIN = "login"
        const val ID = "id"
        const val AVATAR_URL = "avatar_url"
    }
}
