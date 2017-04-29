package com.zeyad.usecases.app.presentation.user_detail;

import com.zeyad.usecases.app.components.redux.BaseEvent;

/**
 * @author by ZIaDo on 4/22/17.
 */

class GetReposEvent extends BaseEvent {
    private final String login;

    GetReposEvent(String login) {

        this.login = login;
    }

    String getLogin() {
        return login;
    }
}
