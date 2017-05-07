package com.zeyad.usecases.app.presentation.user_detail;

import com.zeyad.usecases.app.components.redux.BaseEvent;
import com.zeyad.usecases.app.components.redux.BaseViewModel;
import com.zeyad.usecases.app.components.redux.SuccessStateAccumulator;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import rx.Observable;
import rx.functions.Func1;

import static com.zeyad.usecases.app.utils.Constants.URLS.REPOSITORIES;

/**
 * @author zeyad on 1/10/17.
 */
public class UserDetailVM extends BaseViewModel<UserDetailState> {
    private final IDataUseCase dataUseCase;

    public UserDetailVM(IDataUseCase dataUseCase, SuccessStateAccumulator<UserDetailState> successStateAccumulator,
                        UserDetailState initialState) {
        super(successStateAccumulator, initialState);
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
}
