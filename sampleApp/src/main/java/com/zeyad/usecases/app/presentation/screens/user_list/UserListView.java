package com.zeyad.usecases.app.presentation.screens.user_list;

import rx.Observable;

/**
 * @author zeyad on 11/1/16.
 */
interface UserListView {

    Observable<UserListState> getUsers();

    void incrementPage(long lastId);

    void setCurrentPage(int currentPage);
}
