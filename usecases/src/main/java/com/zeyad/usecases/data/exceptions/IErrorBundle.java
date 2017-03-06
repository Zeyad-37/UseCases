package com.zeyad.usecases.data.exceptions;

/**
 * @author zeyad on 11/30/16.
 */

public interface IErrorBundle {

    String getMessage();

    Exception getException();
}
