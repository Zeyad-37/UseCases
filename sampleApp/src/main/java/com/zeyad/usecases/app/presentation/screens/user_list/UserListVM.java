package com.zeyad.usecases.app.presentation.screens.user_list;

import android.util.Log;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.zeyad.usecases.app.components.mvvm.BaseState.NEXT;
import static com.zeyad.usecases.app.presentation.screens.user_list.UserListState.Builder;
import static com.zeyad.usecases.app.presentation.screens.user_list.UserListState.SEARCH;
import static com.zeyad.usecases.app.presentation.screens.user_list.UserListState.builder;
import static com.zeyad.usecases.app.presentation.screens.user_list.UserListState.error;
import static com.zeyad.usecases.app.presentation.screens.user_list.UserListState.loading;
import static com.zeyad.usecases.app.utils.Constants.URLS.USERS;

/**
 * @author zeyad on 11/1/16.
 */
class UserListVM extends BaseViewModel<UserListState> implements UserListViewModel {

    private final IDataUseCase dataUseCase;

    UserListVM() {
        dataUseCase = DataUseCaseFactory.getInstance();
    }

    UserListVM(IDataUseCase dataUseCase) {
        this.dataUseCase = dataUseCase;
    }

    @Override
    public Observable.Transformer<UserListState, UserListState> applyStates() {
        return listObservable -> listObservable
                .flatMap(userListState -> Observable.just(reduce(getViewState(), userListState)))
                .onErrorReturn(throwable -> reduce(getViewState(), error(throwable)))
                .startWith(reduce(getViewState(), loading()))
                .doOnEach(notification -> setViewState((UserListState) notification.getValue()));
    }

    @Override
    public UserListState reduce(UserListState previous, UserListState changes) {
        if (previous == null)
            return changes;
        Log.d("List reduce states:", previous.getState() + " -> " + changes.getState());
        Builder builder = builder(changes);
        builder.setyScroll(!Utils.isNotEmpty(previous.getUsers()) ? 0 :
                changes.getYScroll() == 0 ?
                        previous.getYScroll() : changes.getYScroll())
                .setCurrentPage(changes.getState().equals(NEXT) ? previous.getCurrentPage() + 1 :
                        changes.getCurrentPage())
                .setUsers(changes.getState().equals(SEARCH) ? changes.getUsers() :
                        Utils.isNotEmpty(changes.getUsers()) ? Utils.union(previous.getUsers(),
                                changes.getUsers()) : previous.getUsers())
                .setLastId(builder.users != null && builder.users.size() > 0 ?
                        builder.users.get(builder.users.size() - 1).getId() : 0);
        return builder.build();
    }

    //    @Override
    @SuppressWarnings("unused")
    public void getUsersExperimental() {
        getState(dataUseCase.getListOffLineFirst(new GetRequest
                .GetRequestBuilder(UserRealm.class, true)
                .url(String.format(USERS, getViewState() != null ? getViewState().getCurrentPage() : 0,
                        getViewState() != null ? getViewState().getLastId() : 0))
                .build())
                .map(UserListState::onNext)
                .compose(applyStates()));
    }

    @Override
    public Observable<UserListState> getUsers() {
        return dataUseCase.getListOffLineFirst(new GetRequest
                .GetRequestBuilder(UserRealm.class, true)
                .url(String.format(USERS, getViewState() != null ? getViewState().getCurrentPage() : 0,
                        getViewState() != null ? getViewState().getLastId() : 0))
                .build())
                .map(UserListState::onNext)
                .compose(applyStates());
    }

    @Override
    public Observable<UserListState> incrementPage() {
        if (getViewState() == null)
            return Observable.error(new IllegalStateException("View State is null!"));
        return dataUseCase.getList(new GetRequest.GetRequestBuilder(UserRealm.class, true)
                .url(String.format(USERS, getViewState().getCurrentPage() + 1, getViewState().getLastId()))
                .build())
                .map(UserListState::onNext)
                .compose(applyStates());
    }

    @Override
    public Observable<UserListState> search(String query) {
        return dataUseCase.queryDisk(realm -> realm.where(UserRealm.class)
                .beginsWith(UserRealm.LOGIN, query), UserRealm.class)
                .map(UserListState::onSearch)
                .compose(applyStates())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable deleteCollection(List<Long> selectedItemsIds) {
        return dataUseCase.deleteCollection(new PostRequest.PostRequestBuilder(UserRealm.class, true)
                .payLoad(selectedItemsIds)
                .build());
    }
}
