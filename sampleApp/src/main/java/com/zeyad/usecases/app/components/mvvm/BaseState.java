package com.zeyad.usecases.app.components.mvvm;

/**
 * @author zeyad on 1/24/17.
 */
public class BaseState {

    public static final String LOADING = "loading", ERROR = "error", NEXT = "next";
    final boolean isLoading;
    final Throwable error;
    final String state;

    public BaseState(boolean isLoading, Throwable error, String state) {
        this.isLoading = isLoading;
        this.error = error;
        this.state = state;
    }

    public static BaseState loading() {
        return new BaseState(true, null, LOADING);
    }

    public static BaseState error(Throwable error) {
        return new BaseState(false, error, ERROR);
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

    public BaseState reduce(BaseState previous) {
        return this;
    }
}
