package com.zeyad.usecases.app.screens.screens.user_detail;

import com.zeyad.rxredux.core.redux.SuccessStateAccumulator;
import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.app.screens.user.detail.Repository;
import com.zeyad.usecases.app.screens.user.detail.UserDetailState;
import com.zeyad.usecases.app.screens.user.detail.UserDetailVM;
import com.zeyad.usecases.db.RealmQueryProvider;

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

/** @author by ZIaDo on 2/9/17. */
public class UserDetailVMTest {

    private IDataService mockDataUseCase;
    private Repository repository;
    private UserDetailVM userDetailVM;

    @Before
    public void setUp() throws Exception {
        mockDataUseCase = mock(IDataService.class);
        userDetailVM = new UserDetailVM();
        userDetailVM.init(
                mock(SuccessStateAccumulator.class), mock(UserDetailState.class), mockDataUseCase);

        repository = new Repository();
        repository.setFullName("testUser");
        repository.setId(1);
    }

    @Test
    public void getRepositories() throws Exception {
        List<Repository> repositories = new ArrayList<>();
        repositories.add(repository);
        Flowable<List<Repository>> observableUserRealm = Flowable.just(repositories);

        when(mockDataUseCase.<Repository>queryDisk(any(RealmQueryProvider.class)))
                .thenReturn(observableUserRealm);

        TestSubscriber<List<Repository>> subscriber = new TestSubscriber<>();
        userDetailVM.getRepositories("Zoz").subscribe(subscriber);

        // Verify repository interactions
        verify(mockDataUseCase, times(1)).queryDisk(any(RealmQueryProvider.class));

        subscriber.assertComplete();
        subscriber.assertNoErrors();
        subscriber.assertValue(repositories);
    }
}
