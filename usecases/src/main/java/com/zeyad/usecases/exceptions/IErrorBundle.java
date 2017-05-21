package com.zeyad.usecases.exceptions;

import android.support.annotation.NonNull;

/**
 * @author zeyad on 11/30/16.
 */

public interface IErrorBundle {

    @NonNull
    String getMessage();

    Exception getException();
}
