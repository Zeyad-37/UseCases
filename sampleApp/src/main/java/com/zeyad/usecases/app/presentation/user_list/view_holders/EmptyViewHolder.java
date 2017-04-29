package com.zeyad.usecases.app.presentation.screens.user_list.view_holders;

import android.view.View;

import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;

/**
 * @author zeyad on 11/29/16.
 */

public class EmptyViewHolder extends GenericRecyclerViewAdapter.ViewHolder {

    public EmptyViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindData(Object data, boolean isItemSelected, int position, boolean isEnabled) {
    }
}
