package com.zeyad.usecases

import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * @author by ZIaDo on 2/13/17.
 */
class TestRealmModel {
    @SerializedName("id")
    @PrimaryKey
    var id: Int = 0

    @SerializedName("value")
    var value: String? = null

    constructor(id: Int, value: String) {
        this.id = id
        this.value = value
        if (id <= 0) throw IllegalArgumentException("id should be greater than 0")
    }

    constructor()

    override fun toString(): String {
        return "TestRealmModel{" + "id=" + id + ", value='" + value + '\''.toString() + '}'.toString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is TestRealmModel) return false
        val testRealmObject = o as TestRealmModel?
        return id == testRealmObject!!.id && value == testRealmObject.value
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + value!!.hashCode()
        return result
    }
}
