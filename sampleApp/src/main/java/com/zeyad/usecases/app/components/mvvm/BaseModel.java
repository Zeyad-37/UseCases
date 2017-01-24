package com.zeyad.usecases.app.components.mvvm;

/**
 * @author zeyad on 1/24/17.
 */
public class BaseModel {

    private final boolean isLoading;
    private final Throwable error;

    public BaseModel(boolean isLoading, Throwable error) {
        this.isLoading = isLoading;
        this.error = error;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public Throwable getError() {
        return error;
    }
}
