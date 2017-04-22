package com.zeyad.usecases.app.presentation.screens.user_list.actions;

import com.zeyad.usecases.app.components.mvvm.BaseAction;

import java.util.List;

/**
 * @author by ZIaDo on 4/19/17.
 */

public class DeleteUserAction extends BaseAction {
    private final List<Long> selectedItemsIds;

    public DeleteUserAction(List<Long> selectedItemsIds) {
        this.selectedItemsIds = selectedItemsIds;
    }

    public List<Long> getSelectedItemsIds() {
        return selectedItemsIds;
    }
}
