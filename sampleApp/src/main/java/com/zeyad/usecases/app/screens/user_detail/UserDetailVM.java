package com.zeyad.usecases.app.screens.user_detail;

import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.app.components.redux.BaseEvent;
import com.zeyad.usecases.app.components.redux.BaseViewModel;
import com.zeyad.usecases.app.components.redux.SuccessStateAccumulator;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.requests.GetRequest;

import rx.Observable;
import rx.functions.Func1;

import static com.zeyad.usecases.app.utils.Constants.URLS.REPOSITORIES;

/**
 * @author zeyad on 1/10/17.
 */
public class UserDetailVM extends BaseViewModel<UserDetailState> {
    private final IDataService dataUseCase;

    public UserDetailVM(IDataService dataUseCase, SuccessStateAccumulator<UserDetailState> successStateAccumulator,
                        UserDetailState initialState) {
        super(successStateAccumulator, initialState);
        this.dataUseCase = dataUseCase;
    }

    public Observable getRepositories(String userLogin) {
        return Utils.isNotEmpty(userLogin) ? dataUseCase.queryDisk(realm -> realm.where(Repository.class)
                .equalTo("owner.login", userLogin))
                .flatMap(list -> Utils.isNotEmpty(list) ? Observable.just(list) :
                        dataUseCase.getList(new GetRequest.Builder(Repository.class, true)
                                .url(String.format(REPOSITORIES, userLogin)).build())) :
                Observable.error(new IllegalArgumentException("User name can not be empty"));
    }

    @Override
    public Func1<BaseEvent, Observable<?>> mapEventsToExecutables() {
        return event -> getRepositories(((GetReposEvent) event).getLogin());
    }
}
