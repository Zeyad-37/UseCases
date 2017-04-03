package com.zeyad.usecases.app.presentation.screens.user_detail;

import com.zeyad.usecases.app.components.mvvm.ViewState;

import rx.Observable;

/**
 * @author zeyad on 1/10/17.
 */
interface UserDetailView {
    Observable<ViewState> getRepositories(UserDetailState userDetailState);
}
