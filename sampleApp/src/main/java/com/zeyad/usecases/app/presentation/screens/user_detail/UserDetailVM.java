package com.zeyad.usecases.app.presentation.screens.user_detail;

import android.util.Log;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.domain.interactors.data.DataUseCase;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.List;

import rx.Observable;

import static com.zeyad.usecases.app.components.mvvm.BaseModel.ERROR;
import static com.zeyad.usecases.app.components.mvvm.BaseModel.LOADING;
import static com.zeyad.usecases.app.components.mvvm.BaseModel.NEXT;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailModel.INITIAL;

/**
 * @author zeyad on 1/10/17.
 */
class UserDetailVM extends BaseViewModel<UserDetailFragment, UserDetailModel> implements UserDetailView {
    private final IDataUseCase dataUseCase;

    UserDetailVM() {
        dataUseCase = DataUseCaseFactory.getInstance();
    }

    @Override
    public Observable<UserDetailModel> getRepositories(String user) {
        List lastList = dataUseCase.getLastList().getValue();
        Observable<UserDetailModel> userDetailModelObservable;
        if (Utils.isNotEmpty(lastList) && lastList.get(0) instanceof RepoRealm)
            userDetailModelObservable = dataUseCase.getLastList()
                    .flatMap(list -> Observable.just(UserDetailModel.onNext(null, (List<RepoRealm>) list, false)));
        else if (Utils.isNotEmpty(user))
            userDetailModelObservable = dataUseCase.searchDisk(new GetRequest
                    .GetRequestBuilder(RepoRealm.class, true)
                    .realmQuery(DataUseCase.getRealmQuery(RepoRealm.class).equalTo("owner.login", user))
                    .build()).compose(applyStates());
//            userDetailModelObservable = dataUseCase.getListOffLineFirst(new GetRequest
//                    .GetRequestBuilder(RepoRealm.class, true)
//                    .url(String.format(REPOSITORIES, user))
//                    .build()).compose(applyStates());
        else
            userDetailModelObservable = Observable.just(UserDetailModel
                    .error(new IllegalArgumentException("User name can not be empty")));
        return userDetailModelObservable;
    }

    @Override
    public UserDetailModel reduce(UserDetailModel previous, UserDetailModel changes) {
        if (previous == null)
            return changes;
        Log.d("reduce states:", previous.getState() + ", " + changes.getState());
        UserDetailModel.Builder builder = UserDetailModel.builder();
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

    @Override
    public Observable.Transformer<List, UserDetailModel> applyStates() {
        UserDetailModel currentState = getView().getModel();
        return listObservable -> listObservable
                .flatMap(list -> Observable.just(reduce(currentState, UserDetailModel.onNext(null,
                        (List<RepoRealm>) list, false))))
                .onErrorReturn(throwable -> reduce(currentState,
                        UserDetailModel.error(throwable)))
                .startWith(reduce(currentState, UserDetailModel.loading()));

    }
}
