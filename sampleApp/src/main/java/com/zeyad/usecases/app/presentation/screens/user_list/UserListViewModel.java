package com.zeyad.usecases.app.presentation.screens.user_list;

import java.util.List;

import rx.Observable;

/**
 * @author zeyad on 11/1/16.
 */
interface UserListViewModel {

    Observable<UserListState> getState();

    Observable<UserListState> getState(Observable<UserListState> users);

//    void getUsers();

    Observable<UserListState> getUsers();

    Observable<UserListState> search(String query);

    Observable deleteCollection(List<Long> selectedItemsIds);

    Observable<UserListState> incrementPage();
}
