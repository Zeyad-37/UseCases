package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.components.adapter.ItemInfo;

import java.util.List;

import rx.Observable;

/**
 * @author zeyad on 11/1/16.
 */
interface UserListView {

    Observable<UserListState> getUsers();

    void incrementPage(long lastId);

    void setCurrentPage(int currentPage);

    void deleteCollection(List<Long> selectedItemsIds);

    Observable<List<ItemInfo<UserRealm>>> search(String s);
}
