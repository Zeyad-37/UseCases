package com.zeyad.usecases.app.components.redux;

import static com.zeyad.usecases.app.components.redux.UIModel.ERROR;
import static com.zeyad.usecases.app.components.redux.UIModel.LOADING;
import static com.zeyad.usecases.app.components.redux.UIModel.SUCCESS;

/**
 * @author by ZIaDo on 4/19/17.
 */
final class Result<B> {

    private final boolean isLoading, isSuccessful;
    private final Throwable error;
    private final String state;
    private final ResultBundle<?, B> bundle;

    private Result(String state, boolean isLoading, Throwable error, boolean isSuccessful,
                   ResultBundle<?, B> bundle) {
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

    static <B> Result<B> successResult(ResultBundle<?, B> bundle) {
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

    B getBundle() {
        return bundle != null && bundle.getBundle() != null ? bundle.getBundle() : (B) new Object();
    }

    String getEvent() {
        return bundle == null ? "" : bundle.getEvent();
    }
}
