package com.zeyad.usecases.app.presentation.screens.user_detail;

import android.util.Log;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.app.presentation.screens.user_list.UserRealm;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.domain.interactors.data.DataUseCase;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.zeyad.usecases.app.components.mvvm.BaseState.ERROR;
import static com.zeyad.usecases.app.components.mvvm.BaseState.LOADING;
import static com.zeyad.usecases.app.components.mvvm.BaseState.NEXT;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.Builder;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.INITIAL;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.builder;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.error;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.loading;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.onNext;
import static com.zeyad.usecases.app.utils.Constants.URLS.REPOSITORIES;

/**
 * @author zeyad on 1/10/17.
 */
class UserDetailVM extends BaseViewModel<UserDetailState> implements UserDetailView {
    private final IDataUseCase dataUseCase;

    UserDetailVM() {
        dataUseCase = DataUseCaseFactory.getInstance();
    }

    @Override
    public Observable<UserDetailState> getRepositories(UserDetailState userDetailState) {
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
                                        .build()))
                        .unsubscribeOn(AndroidSchedulers.from(DataUseCase.getHandlerThread().getLooper())),
                (userRealm, repos) -> reduce(userDetailState, onNext(userRealm, repos, false)))
                .compose(applyStates()) : Observable.just(error(new IllegalArgumentException("User name can not be empty")));
        return userDetailModelObservable;
    }

    @Override
    public Observable.Transformer<UserDetailState, UserDetailState> applyStates() {
        return listObservable -> listObservable
                .flatMap(userDetailState -> Observable.just(reduce(getViewState(), userDetailState)))
                .onErrorReturn(throwable -> reduce(getViewState(), error(throwable)))
                .startWith(reduce(getViewState(), loading()))
                .doOnEach(notification -> setViewState((UserDetailState) notification.getValue()));
    }

    // FIXME: 2/9/17
    @Override
    public UserDetailState reduce(UserDetailState previous, UserDetailState changes) {
        if (previous == null)
            return changes;
        Log.d("Detail reduce states:", previous.getState() + " -> " + changes.getState());
        Builder builder = builder();
        if ((previous.getState().equals(LOADING) && changes.getState().equals(NEXT)) ||
                (previous.getState().equals(NEXT) && changes.getState().equals(NEXT))) {
            builder.setIsLoading(false)
                    .setError(null)
                    .setState(NEXT);
        } else if (previous.getState().equals(LOADING) && changes.getState().equals(ERROR)) {
            builder.setIsLoading(false)
                    .setError(changes.getError())
                    .setState(ERROR);
        } else if (previous.getState().equals(INITIAL) || (previous.getState().equals(ERROR)
                && changes.getState().equals(LOADING)) || (previous.getState().equals(NEXT)
                && changes.getState().equals(LOADING))) {
            builder.setError(null)
                    .setIsLoading(true)
                    .setState(LOADING);
        } else return changes;
        builder.setIsTwoPane(previous.isTwoPane())
                .setRepos(Utils.isNotEmpty(changes.getRepos()) ? Utils.union(previous.getRepos(),
                        changes.getRepos()) : previous.getRepos())
                .setUser(changes.getUser());
        return builder.build();
    }
}
