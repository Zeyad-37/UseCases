package com.zeyad.usecases.app.presentation.screens.user_list.actions;

import com.zeyad.usecases.app.components.mvvm.BaseAction;

/**
 * @author by ZIaDo on 4/21/17.
 */

public class UsersNextPageAction extends BaseAction {
    private final long lastId;

    public UsersNextPageAction(long lastId) {
        this.lastId = lastId;
    }

    public long getLastId() {
        return lastId;
    }
}
