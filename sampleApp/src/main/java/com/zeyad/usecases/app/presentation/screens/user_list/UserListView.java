package com.zeyad.usecases.app.presentation.screens.user_list;

import java.util.List;

import rx.Observable;

/**
 * @author zeyad on 11/1/16.
 */
interface UserListView {

    Observable<UserListModel> getUserList();

    Observable<List> getUserListFromServer();

    Observable updateItemByItem();

    Observable writePeriodic();

    void incrementPage();

    int getCurrentPage();

    void setCurrentPage(int currentPage);

    void setView(UserListActivity userListActivity);
}
