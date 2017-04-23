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
    void toggleViews(boolean toggle);

    /**
     * Show a retry view in case of an errorResult when retrieving data.
     *
     * @param message A string representing an errorResult.
     */
    void showErrorWithRetry(String message);

    /**
     * Show an errorResult message
     *
     * @param message A string representing an errorResult.
     */
    void showError(String message);

    /**
     * Renders the model of the view
     *
     * @param s the model to be rendered.
     */
    void renderState(S s);
}
