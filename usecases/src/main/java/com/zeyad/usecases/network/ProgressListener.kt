package com.zeyad.usecases.network

interface ProgressListener {
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}