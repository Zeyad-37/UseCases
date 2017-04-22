package com.zeyad.usecases.app.components.mvvm;

/**
 * @author by ZIaDo on 4/19/17.
 */
public class BaseResult<B> {

    public static final BaseResult IN_FLIGHT = new BaseResult<>(true, null, false, null);

    private final boolean isLoading, isSuccessful;
    private final Throwable error;
    private final B bundle;

    public BaseResult(boolean isLoading, Throwable error, boolean isSuccessful, B bundle) {
        this.isLoading = isLoading;
        this.error = error;
        this.isSuccessful = isSuccessful;
        this.bundle = bundle;
    }

    public static BaseResult errorResult(Throwable error) {
        return new BaseResult<>(false, error, false, null);
    }

    public static BaseResult successResult(Object bundle) {
        return new BaseResult<>(false, null, true, bundle);
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
