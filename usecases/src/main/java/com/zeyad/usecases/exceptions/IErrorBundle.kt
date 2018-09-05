package com.zeyad.usecases.exceptions

/**
 * @author zeyad on 11/30/16.
 */
interface IErrorBundle {

    fun message(): String

    fun exception(): Exception
}
