package com.zeyad.usecases.app.presentation.screens.user_list.actions;

import com.zeyad.usecases.app.components.mvvm.BaseAction;

/**
 * @author by ZIaDo on 4/20/17.
 */

public class SearchUsersAction extends BaseAction {

    private final String query;

    public SearchUsersAction(String s) {
        query = s;
    }

    public String getQuery() {
        return query;
    }
}
