package com.zeyad.generic.usecase.dataaccesslayer.components.mvp;

/**
 * @author by zeyad on 19/05/16.
 */
public interface GenericEditableItemView<M> extends GenericDetailView<M> {
    /**
     * Render an item in the UI.
     *
     * @param model The {@link M} that will be shown.
     */
    void renderItem(M model);

    /**
     * Show item edit form
     */
    void editItem(M model);

    /**
     * Submit an item to be edited.
     */
    void putItemSuccess(M model);

    /**
     * Retrieves the validated item to be submitted
     *
     * @return {@link M}
     */
    M getValidatedItem();
}