package com.zeyad.usecases.app.presentation.screens.user_detail;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.utils.Utils;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.zeyad.usecases.app.components.mvvm.BaseModel.NEXT;
import static com.zeyad.usecases.app.utils.Constants.URLS.REPOSITORIES;

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
        if (Utils.isNotEmpty(lastList) && lastList.get(0) instanceof RepoRealm)
            return dataUseCase.getLastList()
                    .flatMap(list -> Observable.just(new UserDetailModel(null, (List<RepoRealm>) list,
                            false, null, NEXT)));
        else
            return getUserDetailModelObservable(user);
    }

    private Observable<UserDetailModel> getUserDetailModelObservable(String user) {
        Observable networkObservable = dataUseCase.getList(new GetRequest.GetRequestBuilder(RepoRealm.class, true)
                .url(String.format(REPOSITORIES, user))
                .build());
        if (Utils.isNotEmpty(user))
            return dataUseCase.getList(new GetRequest
                    .GetRequestBuilder(RepoRealm.class, true)
                    .build())
                    .flatMap((Func1<List, Observable<?>>) list -> Utils.isNotEmpty(list) ? Observable.just(list) : networkObservable)
                    .onErrorResumeNext(throwable -> {
                        throwable.printStackTrace();
                        return networkObservable;
                    })
                    .flatMap(list -> Observable.just(new UserDetailModel(null, (List<RepoRealm>) list,
                            false, null, NEXT)))
                    .onErrorReturn(throwable -> UserDetailModel.error(throwable))
                    .startWith(UserDetailModel.loading());
        else
            return Observable.just(UserDetailModel.error(new IllegalArgumentException("User name can not be empty")));
    }

    @Override
    public UserDetailModel reduce(UserDetailModel previous, UserDetailModel changes) {
        return null;
    }

    @Override
    public Observable.Transformer<?, UserDetailModel> applyStatesImmutable() {
        return null;
    }
}
