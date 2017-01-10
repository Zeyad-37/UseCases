package com.zeyad.usecases.app.presentation.screens.user_detail;

import rx.Observable;

/**
 * @author zeyad on 1/10/17.
 */
interface UserDetailView {
    Observable getRepositories(String user);
}
