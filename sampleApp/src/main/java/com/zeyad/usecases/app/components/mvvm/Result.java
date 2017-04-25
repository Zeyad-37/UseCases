package com.zeyad.usecases.app.components.mvvm;

/**
 * @author by ZIaDo on 4/19/17.
 */
public class Result<B> {

    static final Result IN_FLIGHT = new Result<>(true, null, false, null);

    private final boolean isLoading, isSuccessful;
    private final Throwable error;
    private final B bundle;

    private Result(boolean isLoading, Throwable error, boolean isSuccessful, B bundle) {
        this.isLoading = isLoading;
        this.error = error;
        this.isSuccessful = isSuccessful;
        this.bundle = bundle;
    }

    static <B> Result<B> errorResult(Throwable error) {
        return new Result<>(false, error, false, null);
    }

    static <B> Result<B> successResult(B bundle) {
        return new Result<>(false, null, true, bundle);
    }

    public boolean isLoading() {
        return isLoading;
    }

    public Throwable getError() {
        return error;
    }

    public B getBundle() {
        return bundle;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
