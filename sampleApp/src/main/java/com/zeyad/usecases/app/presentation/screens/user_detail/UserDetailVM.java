package com.zeyad.usecases.app.presentation.screens.user_detail;

import com.zeyad.usecases.app.components.redux.BaseViewModel;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import rx.Observable;

import static com.zeyad.usecases.app.utils.Constants.URLS.REPOSITORIES;

/**
 * @author zeyad on 1/10/17.
 */
class UserDetailVM extends BaseViewModel<UserDetailState> {
    private final IDataUseCase dataUseCase;

    UserDetailVM(IDataUseCase dataUseCase) {
        this.dataUseCase = dataUseCase;
    }

    Observable getRepositories(String userLogin) {
        return Utils.isNotEmpty(userLogin) ? dataUseCase.queryDisk(new GetRequest.GetRequestBuilder(null, false)
                .queryFactory(realm -> realm.where(RepoRealm.class).equalTo("owner.login", userLogin))
                .presentationClass(RepoRealm.class).build())
                .flatMap(list -> Utils.isNotEmpty(list) ? Observable.just(list) :
                        dataUseCase.getList(new GetRequest.GetRequestBuilder(RepoRealm.class, true)
                                .url(String.format(REPOSITORIES, userLogin)).build())) :
                Observable.error(new IllegalArgumentException("User name can not be empty"));
    }
}
