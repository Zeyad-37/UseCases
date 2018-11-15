package com.zeyad.usecases.db

interface DaoResolver {
    fun <E> getDao(dataClass: Class<E>): BaseDao<E>
}