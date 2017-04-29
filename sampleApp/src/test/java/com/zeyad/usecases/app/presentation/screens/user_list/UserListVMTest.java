package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.components.redux.UIModel;
import com.zeyad.usecases.app.presentation.user_list.UserListVM;
import com.zeyad.usecases.app.presentation.user_list.UserRealm;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

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

    // TODO: 3/31/17 Add value assertions!

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
//        assertEquals(UserListState.class, observable.getValue().getClass());
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
        assertEquals(UIModel.class, observable.toBlocking().first().getClass());
    }

    @Test
    public void search() throws Exception {
        UserRealm userRealm = new UserRealm();
        userRealm.setLogin("testUser");
        userRealm.setId(1);
        userRealmList = new ArrayList<>();
        userRealmList.add(userRealm);
        Observable<List> observableUserRealm = Observable.just(userRealmList);

        when(mockDataUseCase.getObject(any(GetRequest.class))).thenReturn(observableUserRealm);
        when(mockDataUseCase.queryDisk(any(GetRequest.class)))
                .thenReturn(observableUserRealm);

        Observable<UIModel> observable = userListVM.search("m");

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).queryDisk(any(GetRequest.class));

        // Assert return type
//        TestSubscriber<UserListState> userListStateTestSubscriber = new TestSubscriber<>();
//        observable.subscribe(userListStateTestSubscriber);
//        userListStateTestSubscriber.awaitTerminalEvent();
//        userListStateTestSubscriber.assertValueCount(1);
//        userListStateTestSubscriber.getOnErrorEvents().get(0).printStackTrace();

    }

    @Test
    public void incrementPageShouldReturnNextPageObservable() throws Exception {
        UserRealm userRealm = new UserRealm();
        userRealm.setLogin("testUser");
        userRealm.setId(1);
        userRealmList = new ArrayList<>();
        userRealmList.add(userRealm);
        Observable<List> observableUserRealm = Observable.just(userRealmList);

        when(mockDataUseCase.getList(any())).thenReturn(observableUserRealm);

        Observable observable = userListVM.incrementPage(0);

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).getList(any(GetRequest.class));

        // Assert return type
//        assertEquals(UserListState.class, observable.getValue().getClass());
    }
}
