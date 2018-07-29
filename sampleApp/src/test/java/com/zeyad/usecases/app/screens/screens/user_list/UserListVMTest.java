package com.zeyad.usecases.app.screens.screens.user_list;

import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.app.screens.user.list.User;
import com.zeyad.usecases.app.screens.user.list.UserListVM;
import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author by ZIaDo on 2/7/17.
 */
public class UserListVMTest {

    private IDataService mockDataUseCase;
    private List<User> userList;
    private UserListVM userListVM;

    @Before
    public void setUp() throws Exception {
        mockDataUseCase = mock(IDataService.class);
        userListVM = new UserListVM();
        userListVM.init(mockDataUseCase);
    }

    @Test
    public void returnUserListStateObservableWhenGetUserIsCalled() {
        User user = new User();
        user.setLogin("testUser");
        user.setId(1);
        userList = new ArrayList<>();
        userList.add(user);
        Flowable<List<User>> observableUserRealm = Flowable.just(userList);

        when(mockDataUseCase.<User>getListOffLineFirst(any())).thenReturn(observableUserRealm);

        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userListVM.getUsers(0).subscribe(subscriber);

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).getListOffLineFirst(any(GetRequest.class));

        subscriber.assertComplete();
        subscriber.assertNoErrors();
        subscriber.assertValue(userList);
    }

    @Test
    public void deleteCollection() throws Exception {
        List<String> ids = new ArrayList<>();
        ids.add("1");
        ids.add("2");

        when(mockDataUseCase.deleteCollectionByIds(any(PostRequest.class)))
                .thenReturn(Flowable.just(ids));

        TestSubscriber<List<String>> subscriber = new TestSubscriber<>();
        userListVM.deleteCollection(ids).subscribe(subscriber);

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).deleteCollectionByIds(any(PostRequest.class));

        subscriber.assertComplete();
        subscriber.assertNoErrors();
        subscriber.assertValue(ids);
    }

    @Test
    public void search() throws Exception {
        User user = new User();
        user.setLogin("testUser");
        user.setId(1);
        userList = new ArrayList<>();
        userList.add(user);

        when(mockDataUseCase.<User>getObject(any(GetRequest.class))).thenReturn(Flowable.just(user));
        when(mockDataUseCase.<User>queryDisk(any(RealmQueryProvider.class)))
                .thenReturn(Flowable.just(userList));

        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userListVM.search("Zoz").subscribe(subscriber);

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).queryDisk(any(RealmQueryProvider.class));

        subscriber.assertComplete();
        subscriber.assertNoErrors();
        List<User> expectedResult = new ArrayList<>();
        expectedResult.add(user);
        subscriber.assertValue(expectedResult);
    }
}
