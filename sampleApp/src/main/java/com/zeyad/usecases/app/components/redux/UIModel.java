package com.zeyad.usecases.app.components.redux;

/**
 * @author zeyad on 1/24/17.
 */
final class UIModel<S> {
    static final String LOADING = "loading", ERROR = "error", SUCCESS = "success";
    private static final String IDLE = "idle";
    private final boolean isLoading, isSuccessful;
    private final Throwable error;
    private final String state;
    private final S bundle;

    private UIModel(String state, boolean isLoading, Throwable error, boolean isSuccessful, S bundle) {
        this.isLoading = isLoading;
        this.error = error;
        this.isSuccessful = isSuccessful;
        this.bundle = bundle;
        this.state = state;
    }

    static <B> UIModel<B> idleState(B bundle) {
        return new UIModel<>(IDLE, false, null, false, bundle);
    }

    static <B> UIModel<B> loadingState(B bundle) {
        return new UIModel<>(LOADING, true, null, false, bundle);
    }

    static <B> UIModel<B> errorState(Throwable error) {
        return new UIModel<>(ERROR, false, error, false, null);
    }

    static <B> UIModel<B> successState(B bundle) {
        return new UIModel<>(SUCCESS, false, null, true, bundle);
    }

    boolean isLoading() {
        return isLoading;
    }

    Throwable getError() {
        return error;
    }

    S getBundle() {
        return bundle;
    }

    boolean isSuccessful() {
        return isSuccessful;
    }

    @Override
    public String toString() {
        return "State: " + state
                + ", Error: " + (error != null ? error.toString() : "null")
                + ", Bundle type: " + (bundle != null ? bundle.getClass().getSimpleName() : "null")
                + ", Key Selector: " + state + (bundle != null ? bundle.toString() : "");
    }
}
