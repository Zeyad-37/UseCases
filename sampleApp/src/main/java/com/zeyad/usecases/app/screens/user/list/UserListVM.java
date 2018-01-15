package com.zeyad.usecases.app.screens.user.list;

import com.zeyad.gadapter.ItemInfo;
import com.zeyad.rxredux.core.redux.BaseEvent;
import com.zeyad.rxredux.core.redux.BaseViewModel;
import com.zeyad.rxredux.core.redux.StateReducer;
import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.app.screens.user.list.events.DeleteUsersEvent;
import com.zeyad.usecases.app.screens.user.list.events.GetPaginatedUsersEvent;
import com.zeyad.usecases.app.screens.user.list.events.SearchUsersEvent;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

import static com.zeyad.usecases.app.utils.Constants.URLS.USER;
import static com.zeyad.usecases.app.utils.Constants.URLS.USERS;

/**
 * @author zeyad on 11/1/16.
 */
public class UserListVM extends BaseViewModel<UserListState> {

    private IDataService dataUseCase;

    @Override
    public void init(Object... otherDependencies) {
        if (dataUseCase == null) {
            dataUseCase = (IDataService) otherDependencies[0];
        }
    }

    @Override
    public StateReducer<UserListState> stateReducer() {
        return (newResult, event, currentStateBundle) -> {
            List resultList = (List) newResult;
            List<User> users;
            if (currentStateBundle == null || currentStateBundle.getUsers() == null)
                users = new ArrayList<>();
            else users = Observable.fromIterable(currentStateBundle.getUsers())
                    .map(ItemInfo::<User>getData).toList().blockingGet();
            List<User> searchList = new ArrayList<>();
            switch (event) {
                case "GetPaginatedUsersEvent":
                    users.addAll(resultList);
                    break;
                case "SearchUsersEvent":
                    searchList.addAll(resultList);
                    break;
                case "DeleteUsersEvent":
                    users = Observable.fromIterable(users)
                            .filter(user -> !resultList.contains((long) user.getId()))
                            .distinct()
                            .toList()
                            .blockingGet();
                    break;
                default:
                    break;
            }
            int lastId = users.get(users.size() - 1).getId();
            users = new ArrayList<>(new HashSet<>(users));
            Collections.sort(users, (user1, user2) ->
                    String.valueOf(user1.getId()).compareTo(String.valueOf(user2.getId())));
            return UserListState.builder().users(users).searchList(searchList).lastId(lastId).build();
        };
    }

    @Override
    protected Function<BaseEvent, Flowable<?>> mapEventsToActions() {
        return event -> {
            Flowable action = Flowable.empty();
            if (event instanceof GetPaginatedUsersEvent) {
                action = getUsers(((GetPaginatedUsersEvent) event).getPayLoad());
            } else if (event instanceof DeleteUsersEvent) {
                action = deleteCollection(((DeleteUsersEvent) event).getPayLoad());
            } else if (event instanceof SearchUsersEvent) {
                action = search(((SearchUsersEvent) event).getPayLoad());
            }
            return action;
        };
    }

    public Flowable<User> getUser() {
        return dataUseCase.getObjectOffLineFirst(new GetRequest.Builder(User.class, true)
                .url(String.format(USER, "Zeyad-37"))
                .id("Zeyad-37", User.LOGIN, String.class)
                .cache(User.LOGIN)
                .build());
    }

    public Flowable<List<User>> getUsers(long lastId) {
        return lastId == 0 ?
                dataUseCase.getListOffLineFirst(new GetRequest.Builder(User.class, true)
                        .url(String.format(USERS, lastId))
                        .cache(User.LOGIN)
                        .build()) :
                dataUseCase.getList(new GetRequest.Builder(User.class, true)
                        .url(String.format(USERS, lastId))
                        .build());
    }

    public Flowable<List<User>> search(String query) {
        return dataUseCase.<User>queryDisk(realm -> realm.where(User.class).beginsWith(User.LOGIN, query))
                .zipWith(dataUseCase.<User>getObject(new GetRequest.Builder(User.class, false)
                                .url(String.format(USER, query))
                                .build())
                                .onErrorReturnItem(new User())
                                .filter(user -> user.getId() != 0)
                                .map(user -> user != null ?
                                        Collections.singletonList(user) : Collections.emptyList()),
                        (BiFunction<List<User>, List<User>, List<User>>) (users, singleton) -> {
                            users.addAll(singleton);
                            return new ArrayList<>(new HashSet<>(users));
                        });
    }

    public Flowable<List<String>> deleteCollection(List<String> selectedItemsIds) {
        return dataUseCase.deleteCollectionByIds(new PostRequest.Builder(User.class, true)
                //                .url(USER)
                .payLoad(selectedItemsIds)
                .idColumnName(User.LOGIN, String.class)
                .cache()
                //                .queuable(false, false)
                .build())
                .map(o -> selectedItemsIds);
    }
}
