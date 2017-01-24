package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.components.mvvm.BaseModel;
import com.zeyad.usecases.app.presentation.models.UserRealm;

import java.util.List;

/**
 * @author zeyad on 1/24/17.
 */

class UserListModel extends BaseModel {

    private final List<UserRealm> users;

    UserListModel(List<UserRealm> users, boolean isLoading, Throwable error) {
        super(isLoading, error);
        this.users = users;
    }

    public static UserListModel onNext(List<UserRealm> users) {
        return new UserListModel(users, false, null);
    }

    public static UserListModel error(Throwable error) {
        return new UserListModel(null, false, error);
    }

    public static UserListModel loading() {
        return new UserListModel(null, true, null);
    }

    List<UserRealm> getUsers() {
        return users;
    }
}
