package com.zeyad.usecases.app.presentation.user_list.events;

import com.zeyad.usecases.app.components.redux.BaseEvent;

/**
 * @author by ZIaDo on 4/21/17.
 */

public class UsersNextPageEvent extends BaseEvent {

    private final long lastId;

    public UsersNextPageEvent(long lastId) {
        this.lastId = lastId;
    }

    public long getLastId() {
        return lastId;
    }
}
