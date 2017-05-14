package com.zeyad.usecases.app.components.redux;

import static com.zeyad.usecases.app.components.redux.UIModel.ERROR;
import static com.zeyad.usecases.app.components.redux.UIModel.LOADING;
import static com.zeyad.usecases.app.components.redux.UIModel.SUCCESS;

/**
 * @author by ZIaDo on 4/19/17.
 */
public class Result<B> {

    private final boolean isLoading, isSuccessful;
    private final Throwable error;
    private final String state;
    private final B bundle;

    private Result(String state, boolean isLoading, Throwable error, boolean isSuccessful, B bundle) {
        this.isLoading = isLoading;
        this.error = error;
        this.isSuccessful = isSuccessful;
        this.bundle = bundle;
        this.state = state;
    }

    static <B> Result<B> loadingResult() {
        return new Result<>(LOADING, true, null, false, null);
    }

    static <B> Result<B> errorResult(Throwable error) {
        return new Result<>(ERROR, false, error, false, null);
    }

    static <B> Result<B> successResult(B bundle) {
        return new Result<>(SUCCESS, false, null, true, bundle);
    }

    boolean isLoading() {
        return isLoading;
    }

    Throwable getError() {
        return error;
    }

    boolean isSuccessful() {
        return isSuccessful;
    }

    public B getBundle() {
        return bundle;
    }

    @Override
    public String toString() {
        return "State: " + state + ", Error: " + (error != null ? error.toString() : "null") +
                ", Bundle type: " + (bundle != null ? bundle.getClass().getSimpleName() : "null") +
                ", Key Selector: " + state + (bundle != null ? bundle.toString() : "");
    }
}
