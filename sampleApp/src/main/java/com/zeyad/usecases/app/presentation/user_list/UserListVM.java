package com.zeyad.usecases.app.presentation.user_list;

import com.zeyad.usecases.app.components.redux.BaseEvent;
import com.zeyad.usecases.app.components.redux.BaseViewModel;
import com.zeyad.usecases.app.components.redux.SuccessStateAccumulator;
import com.zeyad.usecases.app.presentation.user_list.events.DeleteUsersEvent;
import com.zeyad.usecases.app.presentation.user_list.events.GetPaginatedUsersEvent;
import com.zeyad.usecases.app.presentation.user_list.events.SearchUsersEvent;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;
import com.zeyad.usecases.api.IDataService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

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

    public Observable<List> getUsers(long lastId) {
        return lastId == 0 ? dataUseCase.getListOffLineFirst(new GetRequest
                .GetRequestBuilder(User.class, true)
                .url(String.format(USERS, lastId))
                .build()) : dataUseCase.getList(new GetRequest
                .GetRequestBuilder(User.class, true)
                .url(String.format(USERS, lastId))
                .build());
    }

    public Observable search(String query) {
        return dataUseCase.queryDisk(realm -> realm.where(User.class).beginsWith(User.LOGIN, query))
                .zipWith(dataUseCase.getObject(new GetRequest
                                .GetRequestBuilder(User.class, true)
                                .url(String.format(USER, query))
                                .build())
                                .onErrorReturn(o -> Collections.EMPTY_LIST)
                                .map(Collections::singletonList),
                        (list, singletonList) -> {
                            list.addAll((Collection) singletonList);
                            return new ArrayList<>(new HashSet<>(list));
                        })
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Long>> deleteCollection(List<Long> selectedItemsIds) {
        return dataUseCase.deleteCollection(new PostRequest
                .PostRequestBuilder(User.class, true)
                .payLoad(selectedItemsIds)
                .build())
                .map(o -> selectedItemsIds);
    }
}
