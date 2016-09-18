package com.zeyad.genericusecase.domain.exceptions;

import android.support.annotation.NonNull;

/**
 * Wrapper around Exceptions used to manage default errors.
 */
public class DefaultErrorBundle implements ErrorBundle {

    private static final String DEFAULT_ERROR_MSG = "Unknown error";

    private final Exception exception;

    public DefaultErrorBundle(Exception exception) {
        this.exception = exception;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @NonNull
    @Override
    public String getErrorMessage() {
        return (exception != null) ? exception.getMessage() : DEFAULT_ERROR_MSG;
    }
}