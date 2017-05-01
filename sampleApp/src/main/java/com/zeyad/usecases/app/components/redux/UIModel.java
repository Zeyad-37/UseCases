package com.zeyad.usecases.app.components.redux;

/**
 * @author zeyad on 1/24/17.
 */
public class UIModel<B> {
    static final String LOADING = "loading", ERROR = "error", SUCCESS = "success";
    private static final String IDLE = "idle";
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

    static <B> UIModel<B> idleState() {
        return new UIModel<>(IDLE, false, null, false, null);
    }

    public static <B> UIModel<B> idleState(B bundle) {
        return new UIModel<>(IDLE, false, null, false, bundle);
    }

    public static <B> UIModel<B> loadingState(B bundle) {
        return new UIModel<>(LOADING, true, null, false, bundle);
    }

    public static <B> UIModel<B> errorState(Throwable error) {
        return new UIModel<>(ERROR, false, error, false, null);
    }

    public static <B> UIModel<B> successState(B bundle) {
        return new UIModel<>(SUCCESS, false, null, true, bundle);
    }

    boolean isLoading() {
        return isLoading;
    }

    Throwable getError() {
        return error;
    }

    public B getBundle() {
        return bundle;
    }

    boolean isSuccessful() {
        return isSuccessful;
    }

    String getState() {
        return state;
    }

    @Override
    public String toString() {
        return "State: " + state + ", Error: " + (error != null ? error.toString() : "null") +
                ", Bundle type: " + (bundle != null ? bundle.getClass().getSimpleName() : "null") +
                ", Key Selector: " + state + (bundle != null ? bundle.toString() : "");
    }
}
