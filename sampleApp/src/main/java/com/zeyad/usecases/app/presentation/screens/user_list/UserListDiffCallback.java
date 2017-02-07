package com.zeyad.usecases.app.presentation.screens.user_list;

import android.support.v7.util.DiffUtil;

import com.zeyad.usecases.app.components.adapter.ItemInfo;

import java.util.List;

/**
 * @author by ZIaDo on 2/5/17.
 */
class UserListDiffCallback extends DiffUtil.Callback {

    private List<ItemInfo> oldList;
    private List<ItemInfo<UserRealm>> newList;

    UserListDiffCallback(List<ItemInfo> oldList, List<ItemInfo<UserRealm>> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition) == oldList.get(oldItemPosition);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).getData().equals(oldList.get(oldItemPosition).getData());
    }
}
