package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.components.mvvm.ViewState;

import java.util.List;

import rx.Observable;

/**
 * @author zeyad on 11/1/16.
 */
interface UserListViewModel {

    Observable<ViewState> getUsers();

    Observable<ViewState> incrementPage();

    Observable<ViewState> search(String query);

    Observable<ViewState> deleteCollection(List<Long> selectedItemsIds);
}
