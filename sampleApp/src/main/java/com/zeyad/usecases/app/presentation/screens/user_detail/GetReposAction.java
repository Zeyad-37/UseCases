package com.zeyad.usecases.app.presentation.screens.user_detail;

import com.zeyad.usecases.app.components.mvvm.BaseAction;

/**
 * @author by ZIaDo on 4/22/17.
 */

public class GetReposAction extends BaseAction {

    private final String login;

    public GetReposAction(String login) {

        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
