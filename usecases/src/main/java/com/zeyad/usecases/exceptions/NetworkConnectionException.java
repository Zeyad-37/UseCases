package com.zeyad.usecases.exceptions;

/**
 * Exception throw by the application when a there is a network connection exception.
 */
public class NetworkConnectionException extends Exception implements IErrorBundle {

    public NetworkConnectionException() {
        super();
    }

    public NetworkConnectionException(final String message) {
        super(message);
    }

    public NetworkConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NetworkConnectionException(final Throwable cause) {
        super(cause);
    }

    @Override
    public Exception getException() {
        return this;
    }
}