package com.zeyad.usecases.app.components.mvvm;

/**
 * @author zeyad on 1/24/17.
 */
public class ViewState<B> {

    public static final String LOADING = "loadingState", ERROR = "errorState", NEXT = "nextState";
    private final boolean isLoading;
    private final Throwable error;
    private final String state;
    private final B bundle;

    public ViewState(boolean isLoading, Throwable error, String state, B bundle) {
        this.isLoading = isLoading;
        this.error = error;
        this.state = state;
        this.bundle = bundle;
    }

    public static ViewState loadingState(Object bundle) {
        return new ViewState<>(true, null, LOADING, bundle);
    }

    public static ViewState errorState(Throwable error, Object bundle) {
        return new ViewState<>(false, error, ERROR, bundle);
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

    public B getBundle() {
        return bundle;
    }
}
