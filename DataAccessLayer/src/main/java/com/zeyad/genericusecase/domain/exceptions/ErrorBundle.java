package com.zeyad.genericusecase.domain.exceptions;

import android.support.annotation.NonNull;

/**
 * Interface to represent a wrapper around an {@link Exception} to manage errors.
 */
public interface ErrorBundle {
    Exception getException();

    @NonNull
    String getErrorMessage();
}