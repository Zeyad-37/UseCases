package com.zeyad.usecases.app.presentation.screens.user_list;

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
import static com.zeyad.usecases.app.utils.Constants.URLS.USERS;

/**
 * @author zeyad on 11/1/16.
 */
class UserListVM extends BaseViewModel<UserListActivity, UserListState> implements UserListView {

    private final IDataUseCase dataUseCase;
    private int currentPage;
    private long lastId;

    UserListVM() {
        dataUseCase = DataUseCaseFactory.getInstance();
    }

    @Override
    public Observable<UserListState> getUsers() {
        return dataUseCase.getListOffLineFirst(new GetRequest
                .GetRequestBuilder(UserRealm.class, true)
                .url(String.format(USERS, currentPage, lastId)).build())
                .compose(applyStates());
    }

    @Override
    public Observable.Transformer<List, UserListState> applyStates() {
        UserListState currentState = getView().getState();
        return listObservable -> listObservable
                .flatMap(list -> Observable.just(reduce(currentState,
                        UserListState.onNext((List<UserRealm>) list))))
                .onErrorReturn(throwable -> reduce(currentState,
                        UserListState.error(throwable)))
                .startWith(reduce(currentState, UserListState.loading()));
    }

    @Override
    public UserListState reduce(UserListState previous, UserListState changes) {
        if (previous == null)
            return changes;
        UserListState.Builder builder = UserListState.builder();
        if ((previous.getState().equals(LOADING) && changes.getState().equals(NEXT)) ||
                (previous.getState().equals(NEXT) && changes.getState().equals(NEXT))) {
            builder.setIsLoading(false)
                    .setError(null)
                    .setyScroll(!Utils.isNotEmpty(previous.getUsers()) ? 0 : changes.getyScroll() == 0 ?
                            previous.getyScroll() : changes.getyScroll())
                    .setUsers(Utils.isNotEmpty(changes.getUsers()) ? Utils.union(previous.getUsers(),
                            changes.getUsers()) : previous.getUsers())
                    .setState(NEXT);
        } else if (previous.getState().equals(LOADING) && changes.getState().equals(ERROR)) {
            builder.setIsLoading(false)
                    .setError(changes.getError())
                    .setyScroll(!Utils.isNotEmpty(previous.getUsers()) ? 0 : changes.getyScroll() == 0 ?
                            previous.getyScroll() : changes.getyScroll())
                    .setUsers(Utils.isNotEmpty(changes.getUsers()) ? Utils.union(previous.getUsers(),
                            changes.getUsers()) : previous.getUsers())
                    .setState(ERROR);
        } else if ((previous.getState().equals(ERROR) && changes.getState().equals(LOADING)) ||
                (previous.getState().equals(NEXT) && changes.getState().equals(LOADING))) {
            builder.setError(null)
                    .setIsLoading(true)
                    .setState(LOADING)
                    .setyScroll(!Utils.isNotEmpty(previous.getUsers()) ? 0 : changes.getyScroll() == 0 ?
                            previous.getyScroll() : changes.getyScroll())
                    .setUsers(Utils.isNotEmpty(changes.getUsers()) ? Utils.union(previous.getUsers(),
                            changes.getUsers()) : previous.getUsers());
        } else
            throw new IllegalStateException("Don't know how to reduce the partial state " + changes.toString());
        return builder.build();
    }

    @Override
    public void incrementPage(long lastId) {
        this.lastId = lastId;
        currentPage++;
        dataUseCase.getList(new GetRequest.GetRequestBuilder(UserRealm.class, true)
                .url(String.format(USERS, currentPage, lastId))
                .build()).subscribe(list -> {
        }, Throwable::printStackTrace);
    }

    @Override
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
