package com.zeyad.usecases.app.presentation.screens.user_detail;

import com.zeyad.usecases.app.presentation.screens.user_list.UserRealm;
import com.zeyad.usecases.data.db.RealmManager;
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
    private UserRealm userRealm;
    private UserDetailState userDetailState;
    private UserDetailVM userDetailVM;

    // TODO: 3/31/17 Add value assertions!

    @Before
    public void setUp() throws Exception {
        mockDataUseCase = mock(IDataUseCase.class);
        userDetailVM = new UserDetailVM(mockDataUseCase);

        userRealm = new UserRealm();
        userRealm.setLogin("testUser");
        userRealm.setId(1);
        userDetailState = UserDetailState.builder()
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

        Observable observable = userDetailVM.getRepositories(userDetailState);

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).queryDisk(any(RealmManager.RealmQueryProvider.class),
                any(Class.class));

        // Assert return type
//        assertEquals(UserDetailState.class, observable.toBlocking().first().getClass());
    }
}
