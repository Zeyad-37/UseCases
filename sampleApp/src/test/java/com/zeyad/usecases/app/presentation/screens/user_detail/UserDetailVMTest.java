package com.zeyad.usecases.app.presentation.screens.user_detail;

import com.zeyad.usecases.app.presentation.user_detail.UserDetailState;
import com.zeyad.usecases.app.presentation.user_detail.UserDetailVM;
import com.zeyad.usecases.app.presentation.user_list.User;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

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
    private User user;
    private UserDetailState userDetailState;
    private UserDetailVM userDetailVM;

    // TODO: 3/31/17 Add value assertions!

    @Before
    public void setUp() throws Exception {
        mockDataUseCase = mock(IDataUseCase.class);
        userDetailVM = new UserDetailVM(mockDataUseCase, any(), any());

        user = new User();
        user.setLogin("testUser");
        user.setId(1);
        userDetailState = UserDetailState.builder()
                .setUser(user)
                .setIsTwoPane(false)
                .setRepos(new ArrayList<>())
                .build();
    }

    @Test
    public void getRepositories() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(user);
        Observable<List> observableUserRealm = Observable.just(userList);

        when(mockDataUseCase.queryDisk(any(GetRequest.class)))
                .thenReturn(observableUserRealm);

        Observable observable = userDetailVM.getRepositories(user.getLogin());

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).queryDisk(any(GetRequest.class));

        // Assert return type
//        assertEquals(UserDetailState.class, observable.toBlocking().first().getClass());
    }
}
