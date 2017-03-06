package com.zeyad.usecases.app.presentation.screens.user_detail;

import android.util.Log;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.app.presentation.screens.user_list.UserRealm;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.List;

import rx.Observable;

import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.builder;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.error;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.loading;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.onNext;
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
                                        .build())),
                (userRealm, repos) -> reduce(userDetailState, onNext(userRealm, (List<RepoRealm>) repos,
                        false)))
                .compose(applyStates()) : Observable.just(error(new IllegalArgumentException("User name can not be empty")));
        userDetailModelObservable.subscribe();
    }

    @Override
    public Observable.Transformer<UserDetailState, UserDetailState> applyStates() {
        return listObservable -> listObservable
                .startWith(loading())
                .onErrorResumeNext(throwable -> Observable.just(error(throwable)))
                .flatMap(userDetailState -> {
                    getState().onNext(reduce(getViewState(), userDetailState));
                    return Observable.just(getState().getValue());
                });
    }

    @Override
    public UserDetailState reduce(UserDetailState previous, UserDetailState changes) {
        if (previous == null)
            return changes;
        Log.d("Detail reduce states:", previous.getState() + " -> " + changes.getState());
        return builder(changes).setIsTwoPane(previous.isTwoPane())
                .setRepos(Utils.isNotEmpty(changes.getRepos()) ? Utils.union(previous.getRepos(),
                        changes.getRepos()) : previous.getRepos())
                .setUser(changes.getUser() != null ? changes.getUser() :
                        previous.getUser() != null ? previous.getUser() : new UserRealm())
                .build();
    }
}
