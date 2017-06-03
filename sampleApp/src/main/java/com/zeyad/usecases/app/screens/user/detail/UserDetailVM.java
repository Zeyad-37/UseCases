package com.zeyad.usecases.app.screens.user.detail;

import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.app.components.redux.BaseEvent;
import com.zeyad.usecases.app.components.redux.BaseViewModel;
import com.zeyad.usecases.app.components.redux.SuccessStateAccumulator;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.requests.GetRequest;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

import static com.zeyad.usecases.app.utils.Constants.URLS.REPOSITORIES;

/**
 * @author zeyad on 1/10/17.
 */
public class UserDetailVM extends BaseViewModel<UserDetailState> {

    private IDataService dataUseCase;

    @Override
    public void init(SuccessStateAccumulator<UserDetailState> successStateAccumulator,
                     UserDetailState initialState, Object... otherDependencies) {
        setSuccessStateAccumulator(successStateAccumulator);
        setInitialState(initialState);
        dataUseCase = (IDataService) otherDependencies[0];
    }

    public Flowable<List<Repository>> getRepositories(String userLogin) {
        return Utils.isNotEmpty(userLogin) ? dataUseCase.<Repository>queryDisk(realm ->
                realm.where(Repository.class).equalTo("owner.login", userLogin))
                .flatMap(list -> Utils.isNotEmpty(list) ? Flowable.just(list) :
                        dataUseCase.<Repository>getList(new GetRequest.Builder(Repository.class, true)
                                .url(String.format(REPOSITORIES, userLogin))
                                .build())) :
                Flowable.error(new IllegalArgumentException("User name can not be empty"));
    }

    @Override
    public Function<BaseEvent, Flowable<?>> mapEventsToExecutables() {
        return event -> getRepositories(((GetReposEvent) event).getLogin());
    }
}
