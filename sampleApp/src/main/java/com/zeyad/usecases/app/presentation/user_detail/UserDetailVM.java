package com.zeyad.usecases.app.presentation.user_detail;

import com.zeyad.usecases.app.components.redux.BaseEvent;
import com.zeyad.usecases.app.components.redux.BaseViewModel;
import com.zeyad.usecases.app.components.redux.Result;
import com.zeyad.usecases.app.components.redux.UIModel;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.zeyad.usecases.app.utils.Constants.URLS.REPOSITORIES;

/**
 * @author zeyad on 1/10/17.
 */
public class UserDetailVM extends BaseViewModel<UserDetailState> {
    private final IDataUseCase dataUseCase;

    public UserDetailVM(IDataUseCase dataUseCase) {
        this.dataUseCase = dataUseCase;
    }

    public Observable getRepositories(String userLogin) {
        return Utils.isNotEmpty(userLogin) ? dataUseCase.queryDisk(new GetRequest.GetRequestBuilder(null, false)
                .queryFactory(realm -> realm.where(Repository.class).equalTo("owner.login", userLogin))
                .presentationClass(Repository.class).build())
                .flatMap(list -> Utils.isNotEmpty(list) ? Observable.just(list) :
                        dataUseCase.getList(new GetRequest.GetRequestBuilder(Repository.class, true)
                                .url(String.format(REPOSITORIES, userLogin)).build())) :
                Observable.error(new IllegalArgumentException("User name can not be empty"));
    }

    @Override
    public Func1<BaseEvent, Observable<?>> mapEventsToExecutables() {
        return event -> getRepositories(((GetReposEvent) event).getLogin());
    }

    @Override
    public Func2<UIModel<UserDetailState>, Result<?>, UIModel<UserDetailState>> stateAccumulator() {
        return (currentUIModel, newUIModel) -> {
            UserDetailState bundle = currentUIModel.getBundle();
            if (newUIModel.isLoading())
                currentUIModel = UIModel.loadingState(UserDetailState.builder()
                        .setRepos(bundle.getRepos())
                        .setUser(bundle.getUser())
                        .setIsTwoPane(bundle.isTwoPane())
                        .build());
            else if (newUIModel.isSuccessful()) {
                currentUIModel = UIModel.successState(UserDetailState.builder()
                        .setRepos((List<Repository>) newUIModel.getBundle())
                        .setUser(bundle.getUser())
                        .setIsTwoPane(bundle.isTwoPane())
                        .build());
            } else currentUIModel = UIModel.errorState(newUIModel.getError());
            return currentUIModel;
        };
    }
}
