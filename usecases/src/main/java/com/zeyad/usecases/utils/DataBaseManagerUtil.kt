package com.zeyad.usecases.utils

import com.zeyad.usecases.db.DataBaseManager

interface DataBaseManagerUtil {
    fun getDataBaseManager(dataClass: Class<*>): DataBaseManager?
}
