package com.zeyad.usecases

import org.mockito.Mockito

fun <T> anyObject(): T {
    return Mockito.anyObject<T>()
}