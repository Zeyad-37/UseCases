package com.zeyad.usecases.app.components.mvvm;

/**
 * @author zeyad on 1/24/17.
 */
public class BaseState {

    public static final String LOADING = "loading", ERROR = "error", NEXT = "next";
    private final boolean isLoading;
    private final Throwable error;
    private final String state;

    public BaseState(boolean isLoading, Throwable error, String state) {
        this.isLoading = isLoading;
        this.error = error;
        this.state = state;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public Throwable getError() {
        return error;
    }

    public String getState() {
        return state;
    }
}
