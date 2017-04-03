package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.components.mvvm.BaseEvent;

import java.util.List;

/**
 * @author by ZIaDo on 3/27/17.
 */

final class DeleteUsersEvent extends BaseEvent {

    private final List<Long> selectedItemsIds;

    DeleteUsersEvent(List<Long> selectedItemsIds) {
        this.selectedItemsIds = selectedItemsIds;
    }

    List<Long> getSelectedItemsIds() {
        return selectedItemsIds;
    }
}
