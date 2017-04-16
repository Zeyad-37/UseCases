package com.zeyad.usecases.app.components.mvvm;

/**
 * Interface representing a View that will use to load data.
 */
interface LoadDataView<S> {
    /**
     * Show or hide a view with a progress bar indicating a loading process.
     *
     * @param toggle whether to show or hide the loading view.
     */
    void toggleLoading(boolean toggle);

    /**
     * Show a retry view in case of an errorState when retrieving data.
     *
     * @param message A string representing an errorState.
     */
    void showErrorWithRetry(String message);

    /**
     * Show an errorState message
     *
     * @param message A string representing an errorState.
     */
    void showError(String message);

    /**
     * Renders the model of the view
     *
     * @param s the model to be rendered.
     */
    void renderState(S s);
}
