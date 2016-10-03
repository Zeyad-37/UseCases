package com.zeyad.genericusecase.domain.exceptions;

import android.support.annotation.NonNull;

import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.R;

/**
 * Wrapper around Exceptions used to manage default errors.
 */
public class DefaultErrorBundle implements ErrorBundle {

    private static final String DEFAULT_ERROR_MSG = Config.getInstance().getContext().getString(R.string.unknown_error);

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