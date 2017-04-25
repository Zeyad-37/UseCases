package com.zeyad.usecases.app.components.mvvm;

/**
 * @author zeyad on 1/24/17.
 */
public class UIModel<B> {
    public static final String LOADING = "loading", ERROR = "error", SUCCESS = "success",
            IDLE = "idle";
    public static final UIModel idleState = new UIModel<>(IDLE, false, null, false, null);

    private final boolean isLoading, isSuccessful;
    private final Throwable error;
    private final String state;
    private final B bundle;

    private UIModel(String state, boolean isLoading, Throwable error, boolean isSuccessful, B bundle) {
        this.isLoading = isLoading;
        this.error = error;
        this.isSuccessful = isSuccessful;
        this.bundle = bundle;
        this.state = state;
    }

    public static <B> UIModel<B> loadingState(B bundle) {
        return new UIModel<>(LOADING, true, null, false, bundle);
    }

    public static <B> UIModel<B> idleState(B bundle) {
        return new UIModel<>(IDLE, false, null, false, bundle);
    }

    public static UIModel errorState(Throwable error) {
        return new UIModel<>(ERROR, false, error, false, null);
    }

    public static <B> UIModel<B> successState(B bundle) {
        return new UIModel<>(SUCCESS, false, null, true, bundle);
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

    public String getState() {
        return state;
    }
}
