package com.zeyad.usecases.app.presentation.screens.user_list;

import org.junit.Test;

import java.util.ArrayList;

import static com.zeyad.usecases.app.components.mvvm.BaseState.ERROR;
import static com.zeyad.usecases.app.components.mvvm.BaseState.LOADING;
import static com.zeyad.usecases.app.components.mvvm.BaseState.NEXT;
import static org.junit.Assert.assertEquals;

/**
 * @author by ZIaDo on 3/11/17.
 */
public class UserListStateTest {

    @Test
    public void reduceFromLoadingToOnNext() throws Exception {
        UserListState previous = UserListState.loading();
        UserListState changes = UserListState.onNext(new ArrayList<>());
        UserListState result = (UserListState) changes.reduce(previous);

        assertEquals(result.getState(), NEXT);
    }

    @Test
    public void reduceFromLoadingToError() throws Exception {
        UserListState changes = UserListState.error(new Throwable());
        UserListState result = (UserListState) changes.reduce(UserListState.loading());

        assertEquals(result.getState(), ERROR);
    }

    @Test
    public void reduceFromOnNextToLoading() throws Exception {
        UserListState previous = UserListState.onNext(new ArrayList<>());
        UserListState result = (UserListState) UserListState.loading().reduce(previous);

        assertEquals(result.getState(), LOADING);
    }

    @Test
    public void reduceFromOnNextToOnNext() throws Exception {
        UserListState previous = UserListState.onNext(new ArrayList<>());
        UserListState changes = UserListState.onNext(new ArrayList<>());
        UserListState result = (UserListState) changes.reduce(previous);

        assertEquals(result.getState(), NEXT);
    }
}
