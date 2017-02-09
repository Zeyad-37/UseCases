package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.ItemInfo;
import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.zeyad.usecases.app.components.mvvm.BaseState.ERROR;
import static com.zeyad.usecases.app.components.mvvm.BaseState.LOADING;
import static com.zeyad.usecases.app.components.mvvm.BaseState.NEXT;
import static com.zeyad.usecases.app.presentation.screens.user_list.UserListState.Builder;
import static com.zeyad.usecases.app.presentation.screens.user_list.UserListState.builder;
import static com.zeyad.usecases.app.presentation.screens.user_list.UserListState.error;
import static com.zeyad.usecases.app.presentation.screens.user_list.UserListState.loading;
import static com.zeyad.usecases.app.presentation.screens.user_list.UserListState.onNext;
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
    public Observable.Transformer<List, UserListState> applyStates() {
        return listObservable -> listObservable
                .flatMap(list -> Observable.just(reduce(getViewState(), onNext(list))))
                .onErrorReturn(throwable -> reduce(getViewState(), error(throwable)))
                .startWith(reduce(getViewState(), loading()))
                .doOnEach(notification -> setViewState((UserListState) notification.getValue()));
    }

    @Override
    public UserListState reduce(UserListState previous, UserListState changes) {
        if (previous == null)
            return changes;
//        Log.d("List reduce states:", previous.getState() + " -> " + changes.getState());
        Builder builder = builder();
        if ((previous.getState().equals(LOADING) && changes.getState().equals(NEXT)) ||
                (previous.getState().equals(NEXT) && changes.getState().equals(NEXT))) {
            builder.setIsLoading(false)
                    .setError(null)
                    .setCurrentPage(previous.getCurrentPage() + 1)
                    .setState(NEXT);
        } else if (previous.getState().equals(LOADING) && changes.getState().equals(ERROR)) {
            builder.setIsLoading(false)
                    .setError(changes.getError())
                    .setState(ERROR);
        } else if ((previous.getState().equals(ERROR) && changes.getState().equals(LOADING)) ||
                (previous.getState().equals(NEXT) && changes.getState().equals(LOADING))) {
            builder.setError(null)
                    .setIsLoading(true)
                    .setState(LOADING);
        } else return changes;
        builder.setyScroll(!Utils.isNotEmpty(previous.getUsers()) ? 0 : changes.getyScroll() == 0 ?
                previous.getyScroll() : changes.getyScroll())
                .setUsers(Utils.isNotEmpty(changes.getUsers()) ? Utils.union(previous.getUsers(),
                        changes.getUsers()) : previous.getUsers())
                .setCurrentPage(changes.getCurrentPage())
                .setLastId(builder.users != null && builder.users.size() > 0 ?
                        builder.users.get(builder.users.size() - 1).getId() : 0);
        return builder.build();
    }

    @Override
    public Observable<UserListState> getUsers() {
        return dataUseCase.getListOffLineFirst(new GetRequest
                .GetRequestBuilder(UserRealm.class, true)
                .url(String.format(USERS, getViewState() != null ? getViewState().getCurrentPage() : 0,
                        getViewState() != null ? getViewState().getLastId() : 0))
                .build())
                .compose(applyStates());
    }

    @Override
    public Observable<UserListState> incrementPage() {
        if (getViewState() == null)
            return Observable.error(new IllegalStateException("View State is null!"));
        return dataUseCase.getList(new GetRequest.GetRequestBuilder(UserRealm.class, true)
                .url(String.format(USERS, getViewState().getCurrentPage() + 1, getViewState().getLastId()))
                .build())
                .compose(applyStates());
    }

    @Override
    public Observable deleteCollection(List<Long> selectedItemsIds) {
        return dataUseCase.deleteCollection(new PostRequest.PostRequestBuilder(UserRealm.class, true)
                .payLoad(selectedItemsIds)
                .build());
    }

    @Override
    public Observable<List<ItemInfo>> search(String query) {
        return dataUseCase.queryDisk(realm -> realm.where(UserRealm.class)
                .beginsWith(UserRealm.LOGIN, query), UserRealm.class)
                .flatMap((Func1<List, Observable<?>>) Observable::from)
                .map(o -> new ItemInfo(o, R.layout.user_item_layout))
                .toList();
    }
}
