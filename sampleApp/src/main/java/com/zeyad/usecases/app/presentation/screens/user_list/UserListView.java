package com.zeyad.usecases.app.presentation.screens.user_list;

import java.util.List;

import rx.Observable;

/**
 * @author zeyad on 11/1/16.
 */
interface UserListView {

    Observable<UserListModel> getUserListFromDB();

    Observable<List> getUserListFromServer();

    Observable writePeriodic();

    void incrementPage(long lastId);

    int getCurrentPage();

    void setCurrentPage(int currentPage);
}
