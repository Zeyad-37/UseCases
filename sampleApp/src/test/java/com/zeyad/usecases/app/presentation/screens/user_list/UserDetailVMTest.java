package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailVM;
import com.zeyad.usecases.data.db.RealmManager;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static com.zeyad.usecases.app.components.mvvm.BaseState.ERROR;
import static com.zeyad.usecases.app.components.mvvm.BaseState.LOADING;
import static com.zeyad.usecases.app.components.mvvm.BaseState.NEXT;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.INITIAL;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.error;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.loading;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.onNext;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author by ZIaDo on 2/9/17.
 */

public class UserDetailVMTest {

    private IDataUseCase mockDataUseCase;
    private UserRealm userRealm;
    private UserDetailState userDetailState;
    private UserDetailVM userDetailVM;

    @Before
    public void setUp() throws Exception {
        mockDataUseCase = mock(IDataUseCase.class);
        userDetailVM = new UserDetailVM(mockDataUseCase);

        userRealm = new UserRealm();
        userRealm.setLogin("testUser");
        userRealm.setId(1);
        userDetailState = UserDetailState.builder(INITIAL)
                .setUser(userRealm)
                .setIsTwoPane(false)
                .setRepos(new ArrayList<>())
                .build();
    }

    @Test
    public void getRepositories() throws Exception {
        List<UserRealm> userRealmList = new ArrayList<>();
        userRealmList.add(userRealm);
        Observable<List> observableUserRealm = Observable.just(userRealmList);

        when(mockDataUseCase.queryDisk(any(RealmManager.RealmQueryProvider.class), any(Class.class)))
                .thenReturn(observableUserRealm);

        userDetailVM.getRepositories(userDetailState);
        Observable observable = userDetailVM.getState();

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).queryDisk(any(RealmManager.RealmQueryProvider.class),
                any(Class.class));

        // Assert return type
//        assertEquals(UserDetailState.class, observable.toBlocking().first().getClass());
    }

    @Test
    public void reduceFromLoadingToOnNext() throws Exception {
        UserDetailState previous = loading();
        UserDetailState changes = onNext(userRealm, new ArrayList<>(), false);
        UserDetailState result = userDetailVM.reduce(previous, changes);

        assertEquals(result.getState(), NEXT);
    }

    @Test
    public void reduceFromLoadingToError() throws Exception {
        UserDetailState result = userDetailVM.reduce(loading(), error(new Throwable()));

        assertEquals(ERROR, result.getState());
    }

    @Test
    public void reduceFromOnNextToLoading() throws Exception {
        UserDetailState result = userDetailVM.reduce(onNext(userRealm, new ArrayList<>(), false), loading());

        assertEquals(result.getState(), LOADING);
        assertEquals(result.getUser(), userRealm);
    }

    @Test
    public void reduceFromOnNextToOnNext() throws Exception {
        UserDetailState result = userDetailVM.reduce(onNext(userRealm, new ArrayList<>(), false),
                onNext(userRealm, new ArrayList<>(), false));

        assertEquals(result.getState(), NEXT);
        assertEquals(result.getUser(), userRealm);
    }
}
