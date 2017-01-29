package com.zeyad.usecases.app.components.mvvm;

import android.content.Context;

/**
 * Interface representing a View that will use to load data.
 */
public interface LoadDataView<M extends BaseModel> {
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

    /**
     * @return the context of the view if exists.
     */
    Context getViewContext();

    /**
     * @return the model of the view
     */
    M getModel();

    /**
     * Renders the model of the view
     *
     * @param m the model to be rendered.
     */
    void renderViewState(M m);
}