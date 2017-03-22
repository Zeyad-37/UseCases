package com.zeyad.usecases.app.presentation.screens.user_detail;

import com.zeyad.usecases.app.components.mvvm.BaseState;
import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.app.presentation.screens.user_list.UserRealm;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.List;

import rx.Observable;

import static com.zeyad.usecases.app.utils.Constants.URLS.REPOSITORIES;

/**
 * @author zeyad on 1/10/17.
 */
public class UserDetailVM extends BaseViewModel<UserDetailState> implements UserDetailView {
    private final IDataUseCase dataUseCase;

    UserDetailVM() {
        dataUseCase = DataUseCaseFactory.getInstance();
    }

    public UserDetailVM(IDataUseCase dataUseCase) {
        this.dataUseCase = dataUseCase;
    }

    @Override
    public void getRepositories(UserDetailState userDetailState) {
        Observable<UserDetailState> userDetailModelObservable;
        UserRealm user = userDetailState.getUser();
        String userLogin = user.getLogin();
        userDetailModelObservable = Utils.isNotEmpty(userLogin) ? Observable.zip(Observable.just(user),
                dataUseCase.queryDisk(realm -> realm.where(RepoRealm.class)
                        .equalTo("owner.login", userLogin), RepoRealm.class)
                        .flatMap(list -> Utils.isNotEmpty(list) ? Observable.just(list) :
                                dataUseCase.getList(new GetRequest
                                        .GetRequestBuilder(RepoRealm.class, true)
                                        .url(String.format(REPOSITORIES, userLogin))
                                        .build())), (userRealm, repos) ->
                        UserDetailState.onNext(userRealm, (List<RepoRealm>) repos, false).reduce(userDetailState))
                .compose(applyStates()) : Observable.just((UserDetailState) BaseState.error(new IllegalArgumentException("User name can not be empty")));
        userDetailModelObservable.subscribe();
    }
}
