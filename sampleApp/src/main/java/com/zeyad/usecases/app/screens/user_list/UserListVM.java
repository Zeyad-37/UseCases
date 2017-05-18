package com.zeyad.usecases.app.screens.user_list;

import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.app.components.redux.BaseEvent;
import com.zeyad.usecases.app.components.redux.BaseViewModel;
import com.zeyad.usecases.app.components.redux.SuccessStateAccumulator;
import com.zeyad.usecases.app.screens.user_list.events.DeleteUsersEvent;
import com.zeyad.usecases.app.screens.user_list.events.GetPaginatedUsersEvent;
import com.zeyad.usecases.app.screens.user_list.events.SearchUsersEvent;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.zeyad.usecases.app.utils.Constants.URLS.USER;
import static com.zeyad.usecases.app.utils.Constants.URLS.USERS;

/**
 * @author zeyad on 11/1/16.
 */
public class UserListVM extends BaseViewModel<UserListState> {

    private final IDataService dataUseCase;

    public UserListVM(IDataService dataUseCase, SuccessStateAccumulator<UserListState> successStateAccumulator) {
        super(successStateAccumulator, null);
        this.dataUseCase = dataUseCase;
    }

    @Override
    public Func1<BaseEvent, Observable<?>> mapEventsToExecutables() {
        return event -> {
            Observable executable = Observable.empty();
            if (event instanceof GetPaginatedUsersEvent)
                executable = getUsers(((GetPaginatedUsersEvent) event).getLastId());
            else if (event instanceof DeleteUsersEvent)
                executable = deleteCollection(((DeleteUsersEvent) event).getSelectedItemsIds());
            else if (event instanceof SearchUsersEvent)
                executable = search(((SearchUsersEvent) event).getQuery());
            return executable;
        };
    }

    public Observable<List<User>> getUsers(long lastId) {
        return lastId == 0 ? dataUseCase.getListOffLineFirst(new GetRequest.Builder(User.class, true)
                .url(String.format(USERS, lastId))
                .build()) : dataUseCase.getList(new GetRequest.Builder(User.class, true)
                .url(String.format(USERS, lastId))
                .build());
    }

    public Observable<List<User>> search(String query) {
        return dataUseCase.<User>queryDisk(realm -> realm.where(User.class).beginsWith(User.LOGIN, query))
                .zipWith(dataUseCase.<User>getObject(new GetRequest.Builder(User.class, true)
                                .url(String.format(USER, query))
                                .build())
                                .onErrorReturn(throwable -> null)
                                .map(user -> user != null ? Collections.singletonList(user) : Collections.emptyList()),
                        (Func2<List<User>, List<User>, List<User>>) (users, singleton) -> {
                            users.addAll(singleton);
                            return new ArrayList<>(new HashSet<>(users));
                        });
    }

    public Observable<List<Long>> deleteCollection(List<Long> selectedItemsIds) {
        return dataUseCase.deleteCollectionByIds(new PostRequest.Builder(User.class, true)
                .payLoad(selectedItemsIds)
                .build())
                .map(o -> selectedItemsIds);
    }
}
