package com.zeyad.usecases.mapper

import com.google.gson.Gson
import com.zeyad.usecases.Config
import java.util.*

class DAOMapper(private val gson: Gson = Config.gson) {

    fun <M> mapTo(`object`: Any?, domainClass: Class<*>): M {
        return gson.fromJson<Any>(gson.toJson(`object`), domainClass) as M
    }

    /**
     * Transform a {Entity}ies into an {Model}s.
     *
     * @param list Objects to be transformed.
     * @return {Model} if valid {Entity} otherwise empty Object.
     */
    fun <M> mapAllTo(list: List<*>, domainClass: Class<*>): M {
        val size = list.size
        val objects = ArrayList<Any>(size)
        for (i in 0 until size) {
            objects.add(mapTo(list[i], domainClass))
        }
        return objects as M
    }
}
