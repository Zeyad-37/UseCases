package com.zeyad.usecases.app.presentation.screens.user_detail;

import android.util.Log;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.List;

import rx.Observable;

import static com.zeyad.usecases.app.components.mvvm.BaseState.ERROR;
import static com.zeyad.usecases.app.components.mvvm.BaseState.LOADING;
import static com.zeyad.usecases.app.components.mvvm.BaseState.NEXT;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.INITIAL;
import static com.zeyad.usecases.app.utils.Constants.URLS.REPOSITORIES;

/**
 * @author zeyad on 1/10/17.
 */
class UserDetailVM extends BaseViewModel<UserDetailFragment, UserDetailState> implements UserDetailView {
    private final IDataUseCase dataUseCase;

    UserDetailVM() {
        dataUseCase = DataUseCaseFactory.getInstance();
    }

    @Override
    public Observable<UserDetailState> getRepositories(String userLogin) {
        Observable<UserDetailState> userDetailModelObservable;
        if (Utils.isNotEmpty(userLogin)) {
            return dataUseCase.searchDisk(realm -> realm.where(RepoRealm.class)
                    .equalTo("owner.login", userLogin), RepoRealm.class)
                    .flatMap(list -> Utils.isNotEmpty(list) ? Observable.just(list) :
                            dataUseCase.getList(new GetRequest.GetRequestBuilder(RepoRealm.class, true)
                                    .url(String.format(REPOSITORIES, userLogin))
                                    .build()).doOnSubscribe(() -> Log.d("DB empty", "Calling Server")))
                    .compose(applyStates());
        } else
            userDetailModelObservable = Observable.just(UserDetailState
                    .error(new IllegalArgumentException("User name can not be empty")));
        return userDetailModelObservable;
    }

    @Override
    public Observable.Transformer<List, UserDetailState> applyStates() {
        UserDetailState currentState = getViewState();
        return listObservable -> listObservable
                .flatMap(list -> Observable.just(reduce(currentState, UserDetailState.onNext(null,
                        (List<RepoRealm>) list, false))))
                .onErrorReturn(throwable -> reduce(currentState,
                        UserDetailState.error(throwable)))
                .startWith(reduce(currentState, UserDetailState.loading()));
    }

    @Override
    public UserDetailState reduce(UserDetailState previous, UserDetailState changes) {
        if (previous == null)
            return changes;
        Log.d("reduce states:", previous.getState() + " -> " + changes.getState());
        UserDetailState.Builder builder = UserDetailState.builder();
        if ((previous.getState().equals(LOADING) && changes.getState().equals(NEXT)) ||
                (previous.getState().equals(NEXT) && changes.getState().equals(NEXT))) {
            builder.setIsLoading(false)
                    .setError(null)
                    .setIsTwoPane(previous.isTwoPane())
                    .setRepos(Utils.isNotEmpty(changes.getRepos()) ? Utils.union(previous.getRepos(),
                            changes.getRepos()) : previous.getRepos())
                    .setUser(previous.getUser())
                    .setState(NEXT);
        } else if (previous.getState().equals(LOADING) && changes.getState().equals(ERROR)) {
            builder.setIsLoading(false)
                    .setError(changes.getError())
                    .setIsTwoPane(previous.isTwoPane())
                    .setRepos(Utils.isNotEmpty(changes.getRepos()) ? Utils.union(previous.getRepos(),
                            changes.getRepos()) : previous.getRepos())
                    .setUser(previous.getUser())
                    .setState(ERROR);
        } else if (previous.getState().equals(INITIAL) || (previous.getState().equals(ERROR)
                && changes.getState().equals(LOADING)) || (previous.getState().equals(NEXT)
                && changes.getState().equals(LOADING))) {
            builder.setError(null)
                    .setIsLoading(true)
                    .setState(LOADING)
                    .setIsTwoPane(previous.isTwoPane())
                    .setRepos(Utils.isNotEmpty(changes.getRepos()) ? Utils.union(previous.getRepos(),
                            changes.getRepos()) : previous.getRepos())
                    .setUser(previous.getUser());
        } else
            throw new IllegalStateException("Don't know how to reduce the partial state " + changes.toString());
        return builder.build();
    }
}
