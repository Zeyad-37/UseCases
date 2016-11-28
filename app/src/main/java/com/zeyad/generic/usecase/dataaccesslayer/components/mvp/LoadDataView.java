package com.zeyad.generic.usecase.dataaccesslayer.components.mvp;

/**
 * Interface representing a View that will use to load data.
 */
public interface LoadDataView {
    /**
     * Show a view with a progress bar indicating a loading process.
     */
    void showLoading();

    /**
     * Hide a loading view.
     */
    void hideLoading();

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
}