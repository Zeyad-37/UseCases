package com.zeyad.usecases.app.screens.user.list.events;

import com.zeyad.rxredux.core.redux.BaseEvent;

import java.util.List;

/** @author by ZIaDo on 3/27/17. */
public final class DeleteUsersEvent extends BaseEvent {

    private final List<String> selectedItemsIds;

    public DeleteUsersEvent(List<String> selectedItemsIds) {
        this.selectedItemsIds = selectedItemsIds;
    }

    public List<String> getSelectedItemsIds() {
        return selectedItemsIds;
    }
}
