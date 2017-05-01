package com.zeyad.usecases.app.presentation.user_list;

import android.util.Log;

import com.zeyad.usecases.app.components.redux.BaseEvent;
import com.zeyad.usecases.app.components.redux.BaseViewModel;
import com.zeyad.usecases.app.components.redux.Result;
import com.zeyad.usecases.app.components.redux.UIModel;
import com.zeyad.usecases.app.presentation.user_list.events.DeleteUsersEvent;
import com.zeyad.usecases.app.presentation.user_list.events.GetPaginatedUsersEvent;
import com.zeyad.usecases.app.presentation.user_list.events.SearchUsersEvent;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.zeyad.usecases.app.utils.Constants.URLS.USER;
import static com.zeyad.usecases.app.utils.Constants.URLS.USERS;

/**
 * @author zeyad on 11/1/16.
 */
public class UserListVM extends BaseViewModel<UserListState> {

    private final IDataUseCase dataUseCase;
    private long lastId;

    public UserListVM(IDataUseCase dataUseCase) {
        this.dataUseCase = dataUseCase;
        lastId = -1;
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

    @Override
    public Func2<UIModel<UserListState>, Result<?>, UIModel<UserListState>> stateAccumulator() {
        return (currentUIModel, result) -> {
            Log.d("State Accumulator", "CurrentUIModel: " + currentUIModel.toString());
            Log.d("State Accumulator", "Result: " + result.toString());
            UserListState currentBundle = currentUIModel.getBundle();
            if (result.isLoading())
                currentUIModel = UIModel.loadingState(currentBundle);
            else if (result.isSuccessful()) {
                List resultList = (List) result.getBundle();
                List<User> users = currentBundle == null ? new ArrayList<>() : currentBundle.getUsers();
                if (resultList.get(0).getClass().equals(User.class)) {
                    users.addAll(resultList);
                } else {
                    final Iterator<User> each = users.iterator();
                    while (each.hasNext()) if (resultList.contains((long) each.next().getId()))
                        each.remove();
                }
                lastId = users.get(users.size() - 1).getId();
                users = new ArrayList<>(new HashSet<>(users));
                Collections.sort(users, (user1, user2) -> String.valueOf(user1.getId())
                        .compareTo(String.valueOf(user2.getId())));
                currentUIModel = UIModel.successState(UserListState.builder().setUsers(users).build());
            } else currentUIModel = UIModel.errorState(result.getError());
            return currentUIModel;
        };
    }

    public Observable<List> getUsers(long lastId) {
        if (this.lastId == lastId) {
            return dataUseCase.getList(new GetRequest
                    .GetRequestBuilder(User.class, true)
                    .url(String.format(USERS, lastId))
                    .build());
        } else {
            return dataUseCase.getListOffLineFirst(new GetRequest
                    .GetRequestBuilder(User.class, true)
                    .url(String.format(USERS, lastId))
                    .build());
        }
    }

    public Observable search(String query) {
        return dataUseCase.queryDisk(new GetRequest.GetRequestBuilder(null, false)
                .queryFactory(realm -> realm.where(User.class).beginsWith(User.LOGIN, query))
                .presentationClass(User.class).build())
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

    long getLastId() {
        return lastId;
    }
}
