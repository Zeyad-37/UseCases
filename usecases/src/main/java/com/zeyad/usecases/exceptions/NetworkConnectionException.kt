package com.zeyad.usecases.exceptions

/**
 * Exception throw by the application when a there is a network connection exception.
 */
class NetworkConnectionException(message: String) : Exception(message), IErrorBundle {
    override fun message(): String {
        return localizedMessage
    }

    override fun exception(): Exception {
        return this
    }
}

