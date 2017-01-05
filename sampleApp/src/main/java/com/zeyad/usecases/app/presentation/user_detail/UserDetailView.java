package com.zeyad.usecases.app.presentation.user_detail;

import rx.Observable;

/**
 * @author zeyad on 1/5/17.
 */
interface UserDetailView {
    Observable getUserByLogin(String login);
}
