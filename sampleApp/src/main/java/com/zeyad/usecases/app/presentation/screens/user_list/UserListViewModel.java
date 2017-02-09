package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.components.adapter.ItemInfo;

import java.util.List;

import rx.Observable;

/**
 * @author zeyad on 11/1/16.
 */
interface UserListViewModel {

    Observable<UserListState> getUsers();

    Observable<UserListState> getState(Observable<UserListState> users);

    Observable deleteCollection(List<Long> selectedItemsIds);

    Observable<List<ItemInfo>> search(String s);

    Observable<UserListState> incrementPage();
}
