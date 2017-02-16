package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.data.db.RealmManager;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static com.zeyad.usecases.app.components.mvvm.BaseState.ERROR;
import static com.zeyad.usecases.app.components.mvvm.BaseState.LOADING;
import static com.zeyad.usecases.app.components.mvvm.BaseState.NEXT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author by ZIaDo on 2/7/17.
 */
public class UserListVMTest {

    private IDataUseCase mockDataUseCase;
    private List<UserRealm> userRealmList;
    private UserListVM userListVM;

    @Before
    public void setUp() throws Exception {
        mockDataUseCase = mock(IDataUseCase.class);
        userListVM = new UserListVM(mockDataUseCase);
    }

    @Test
    public void returnUserListStateObservableWhenGetUserIsCalled() {
        UserRealm userRealm = new UserRealm();
        userRealm.setLogin("testUser");
        userRealm.setId(1);
        userRealmList = new ArrayList<>();
        userRealmList.add(userRealm);
        Observable<List> observableUserRealm = Observable.just(userRealmList);

        when(mockDataUseCase.getListOffLineFirst(any()))
                .thenReturn(observableUserRealm);

        Observable observable = userListVM.getUsers();

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).getListOffLineFirst(any(GetRequest.class));

        // Assert return type
        assertEquals(UserListState.class, observable.toBlocking().first().getClass());
    }

    @Test
    public void deleteCollection() throws Exception {
        Observable<Boolean> observableUserRealm = Observable.just(true);

        when(mockDataUseCase.deleteCollection(any(PostRequest.class)))
                .thenReturn(observableUserRealm);

        Observable observable = userListVM.deleteCollection(new ArrayList<>());

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).deleteCollection(any(PostRequest.class));

        // Assert return type
        assertEquals(Boolean.class, observable.toBlocking().first().getClass());
    }

    @Test
    public void search() throws Exception {
        UserRealm userRealm = new UserRealm();
        userRealm.setLogin("testUser");
        userRealm.setId(1);
        userRealmList = new ArrayList<>();
        userRealmList.add(userRealm);
        Observable<List> observableUserRealm = Observable.just(userRealmList);

        when(mockDataUseCase.queryDisk(any(RealmManager.RealmQueryProvider.class), any(Class.class)))
                .thenReturn(observableUserRealm);

        Observable<UserListState> observable = userListVM.search("m");

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).queryDisk(any(RealmManager.RealmQueryProvider.class),
                any(Class.class));

        // Assert return type
//        TestSubscriber<UserListState> userListStateTestSubscriber = new TestSubscriber<>();
//        observable.subscribe(userListStateTestSubscriber);
//        userListStateTestSubscriber.awaitTerminalEvent();
//        userListStateTestSubscriber.assertValueCount(1);
//        userListStateTestSubscriber.getOnErrorEvents().get(0).printStackTrace();

    }

    @Test
    public void reduceFromLoadingToOnNext() throws Exception {
        UserListState previous = UserListState.loading();
        UserListState changes = UserListState.onNext(new ArrayList<>());
        UserListState result = userListVM.reduce(previous, changes);

        assertEquals(result.getState(), NEXT);
    }

    @Test
    public void reduceFromLoadingToError() throws Exception {
        UserListState previous = UserListState.loading();
        UserListState changes = UserListState.error(new Throwable());
        UserListState result = userListVM.reduce(previous, changes);

        assertEquals(result.getState(), ERROR);
    }

    @Test
    public void reduceFromOnNextToLoading() throws Exception {
        UserListState previous = UserListState.onNext(new ArrayList<>());
        UserListState changes = UserListState.loading();
        UserListState result = userListVM.reduce(previous, changes);

        assertEquals(result.getState(), LOADING);
    }

    @Test
    public void reduceFromOnNextToOnNext() throws Exception {
        UserListState previous = UserListState.onNext(new ArrayList<>());
        UserListState changes = UserListState.onNext(new ArrayList<>());
        UserListState result = userListVM.reduce(previous, changes);

        assertEquals(result.getState(), NEXT);
    }

    @Test(expected = IllegalStateException.class)
    public void incrementPageShouldReturnExceptionOnNullViewState() throws Exception {
        UserRealm userRealm = new UserRealm();
        userRealm.setLogin("testUser");
        userRealm.setId(1);
        userRealmList = new ArrayList<>();
        userRealmList.add(userRealm);
        Observable<List> observableUserRealm = Observable.just(userRealmList);

        when(mockDataUseCase.getList(any())).thenReturn(observableUserRealm);

        Observable observable = userListVM.incrementPage();

        // Verify repository interactions
        verify(mockDataUseCase, times(0)).getList(any(GetRequest.class));

        // Assert return type
        assertEquals(new IllegalStateException(), observable.toBlocking().first());
    }

    @Test
    public void incrementPageShouldReturnNextPageObservable() throws Exception {
        UserRealm userRealm = new UserRealm();
        userRealm.setLogin("testUser");
        userRealm.setId(1);
        userRealmList = new ArrayList<>();
        userRealmList.add(userRealm);
        Observable<List> observableUserRealm = Observable.just(userRealmList);

        userListVM.setViewState(UserListState.loading());

        when(mockDataUseCase.getList(any())).thenReturn(observableUserRealm);

        Observable observable = userListVM.incrementPage();

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).getList(any(GetRequest.class));

        // Assert return type
        assertEquals(UserListState.class, observable.toBlocking().first().getClass());
    }
}
