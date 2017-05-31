package com.zeyad.usecases.app.screens.userList.events;

import com.zeyad.usecases.app.components.redux.BaseEvent;

import java.util.List;

/** @author by ZIaDo on 3/27/17. */
public final class DeleteUsersEvent extends BaseEvent {

    private final List<Long> selectedItemsIds;

    public DeleteUsersEvent(List<Long> selectedItemsIds) {
        this.selectedItemsIds = selectedItemsIds;
    }

    public List<Long> getSelectedItemsIds() {
        return selectedItemsIds;
    }
}
