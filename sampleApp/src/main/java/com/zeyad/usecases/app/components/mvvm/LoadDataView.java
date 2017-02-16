package com.zeyad.usecases.app.components.mvvm;

/**
 * Interface representing a View that will use to load data.
 */
public interface LoadDataView<S extends BaseState> {
    /**
     * Show or hide a view with a progress bar indicating a loading process.
     *
     * @param toggle whether to show or hide the loading view.
     */
    void toggleLoading(boolean toggle);

    /**
     * Show a retry view in case of an error when retrieving data.
     *
     * @param message A string representing an error.
     */
    void showErrorWithRetry(String message);

    /**
     * Show an error message
     *
     * @param message A string representing an error.
     */
    void showError(String message);

    /**
     * Renders the model of the view
     *
     * @param s the model to be rendered.
     */
    void renderState(S s);
}