package com.zeyad.usecases.app.presentation.screens.user_detail;

import com.zeyad.usecases.app.presentation.screens.user_list.UserRealm;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static com.zeyad.usecases.app.components.mvvm.BaseState.ERROR;
import static com.zeyad.usecases.app.components.mvvm.BaseState.LOADING;
import static com.zeyad.usecases.app.components.mvvm.BaseState.NEXT;
import static com.zeyad.usecases.app.components.mvvm.BaseState.loading;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.onNext;
import static org.junit.Assert.assertEquals;

/**
 * @author by ZIaDo on 3/11/17.
 */
public class UserDetailStateTest {

    private UserRealm userRealm;

    @Before
    public void setUp() throws Exception {
        userRealm = new UserRealm();
        userRealm.setLogin("testUser");
        userRealm.setId(1);
    }

    @Test
    public void reduceFromLoadingToOnNext() throws Exception {
        UserDetailState previous = (UserDetailState) loading();
        UserDetailState changes = onNext(userRealm, new ArrayList<>(), false);
        UserDetailState result = (UserDetailState) changes.reduce(previous);

        assertEquals(result.getState(), NEXT);
    }

    @Test
    public void reduceFromLoadingToError() throws Exception {
        UserDetailState result = (UserDetailState) loading().reduce(UserDetailState.error(new Throwable()));

        assertEquals(ERROR, result.getState());
    }

    @Test
    public void reduceFromOnNextToLoading() throws Exception {
        UserDetailState result = (UserDetailState) UserDetailState.onNext(userRealm, new ArrayList<>(),
                false).reduce(UserDetailState.loading());

        assertEquals(result.getState(), LOADING);
        assertEquals(result.getUser(), userRealm);
    }

    @Test
    public void reduceFromOnNextToOnNext() throws Exception {
        UserDetailState result = (UserDetailState) onNext(userRealm, new ArrayList<>(), false).reduce(
                onNext(userRealm, new ArrayList<>(), false));

        assertEquals(result.getState(), NEXT);
        assertEquals(result.getUser(), userRealm);
    }
}