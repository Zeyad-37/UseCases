package com.zeyad.genericusecase.data.exceptions;

/**
 * Exception throw by the application when a User searchDisk can't return a valid result.
 */
public class DataNotFoundException extends Exception {

    public DataNotFoundException() {
        super();
    }

    public DataNotFoundException(final String message) {
        super(message);
    }

    public DataNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DataNotFoundException(final Throwable cause) {
        super(cause);
    }
}