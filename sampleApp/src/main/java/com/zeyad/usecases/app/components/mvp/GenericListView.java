package com.zeyad.usecases.app.components.mvp;

import android.support.v7.widget.RecyclerView;

import com.zeyad.usecases.app.components.mvvm.LoadDataView;

import java.util.List;

/**
 * @author by zeyad on 16/05/16.
 */
public interface GenericListView<M, H extends RecyclerView.ViewHolder> extends LoadDataView {

    /**
     * Render a list in the UI.
     *
     * @param viewModelList The list of {@link M} that will be shown.
     */
    void renderItemList(List<M> viewModelList);

    /**
     * View a {@link M} profile/details.
     *
     * @param viewModel The item that will be shown.
     */
    void viewItemDetail(M viewModel, H holder);
}