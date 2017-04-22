package com.zeyad.usecases.app.presentation.screens.user_detail;

import com.zeyad.usecases.app.components.mvvm.BaseEvent;

/**
 * @author by ZIaDo on 4/22/17.
 */

public class GetReposEvent extends BaseEvent {
    private final String login;

    public GetReposEvent(String login) {

        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
