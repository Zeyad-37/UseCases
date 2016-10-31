package com.zeyad.generic.usecase.dataaccesslayer.components.adapter;

import android.util.SparseBooleanArray;

/**
 * @author by zeyad on 20/05/16.
 */
public interface ItemBase<M> {
    void bindData(M data, SparseBooleanArray selectedItems, int position, boolean isEnabled);
}