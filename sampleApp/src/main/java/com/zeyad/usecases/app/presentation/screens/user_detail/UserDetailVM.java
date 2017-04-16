package com.zeyad.usecases.app.presentation.screens.user_detail;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.app.components.mvvm.ViewState;
import com.zeyad.usecases.app.presentation.screens.user_list.UserRealm;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.List;

import rx.Observable;

import static com.zeyad.usecases.app.utils.Constants.URLS.REPOSITORIES;

/**
 * @author zeyad on 1/10/17.
 */
class UserDetailVM extends BaseViewModel<UserDetailState> implements UserDetailView {
    private final IDataUseCase dataUseCase;

    UserDetailVM(IDataUseCase dataUseCase) {
        this.dataUseCase = dataUseCase;
    }

    @Override
    public Observable<ViewState> getRepositories(UserDetailState userDetailState) {
        UserRealm user = userDetailState.getUser();
        String userLogin = user.getLogin();
        return Utils.isNotEmpty(userLogin) ? Observable.zip(Observable.just(user),
                dataUseCase.queryDisk(realm -> realm.where(RepoRealm.class)
                        .equalTo("owner.login", userLogin), RepoRealm.class)
                        .flatMap(list -> Utils.isNotEmpty(list) ? Observable.just(list) :
                                dataUseCase.getList(new GetRequest.GetRequestBuilder(RepoRealm.class, true)
                                        .url(String.format(REPOSITORIES, userLogin)).build())),
                (userRealm, repos) -> {
                    UserRealm finalUser = userRealm != null ? userRealm :
                            userDetailState.getUser() != null ? userDetailState.getUser() : new UserRealm();
                    List<RepoRealm> finalRepos = Utils.isNotEmpty(repos) ? Utils.union(userDetailState.getRepos(), repos)
                            : userDetailState.getRepos();
                    return UserDetailState.onNext(finalUser, finalRepos, false);
                })
                .compose(stateTransformer()) :
                Observable.just(ViewState.errorState(new IllegalArgumentException("User name can not be empty"),
                        getViewStateBundle()));
    }
}
