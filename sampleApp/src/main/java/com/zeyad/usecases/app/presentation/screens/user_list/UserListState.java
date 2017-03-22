package com.zeyad.usecases.app.presentation.screens.user_list;

import android.util.Log;

import com.zeyad.usecases.app.components.mvvm.BaseState;
import com.zeyad.usecases.app.utils.Utils;

import org.parceler.Parcel;

import java.util.List;

/**
 * @author by ZIaDo on 1/28/17.
 */
@Parcel
public class UserListState extends BaseState {
    private static final String SEARCH = "search";
    final List<UserRealm> users;
    final int yScroll;
    final int currentPage;
    final long lastId;

    UserListState() {
        super(false, null, "");
        users = null;
        yScroll = 0;
        currentPage = 0;
        lastId = 0;
    }

    private UserListState(Builder builder) {
        super(false, null, builder.state);
        users = builder.users;
        yScroll = builder.yScroll;
        currentPage = builder.currentPage;
        lastId = builder.lastId;
    }

    public static UserListState loading() {
        return UserListState.builder(LOADING)
                .setUsers(null)
                .setError(null)
                .setIsLoading(true)
                .build();
    }

    public static UserListState onNext(List<UserRealm> users) {
        return UserListState.builder(NEXT)
                .setUsers(users)
                .setError(null)
                .setIsLoading(false)
                .build();
    }

    static UserListState onSearch(List<UserRealm> users) {
        return UserListState.builder(SEARCH)
                .setUsers(users)
                .setError(null)
                .setIsLoading(false)
                .build();
    }

    public static UserListState error(Throwable error) {
        return UserListState.builder(ERROR)
                .setUsers(null)
                .setIsLoading(false)
                .setError(error)
                .build();
    }

    private static Builder builder(String state) {
        return new Builder(state);
    }

    private static Builder builder(UserListState state) {
        return new Builder(state);
    }

    List<UserRealm> getUsers() {
        return users;
    }

    int getYScroll() {
        return yScroll;
    }

    int getCurrentPage() {
        return currentPage;
    }

    long getLastId() {
        return lastId;
    }

    @Override
    public BaseState reduce(BaseState previous) {
        if (previous == null)
            return this;
        if (previous instanceof UserListState) {
            UserListState oldState = (UserListState) previous;
            Log.d("List reduce states:", previous.getState() + " -> " + getState());
            Builder builder = builder(this);
            builder.setyScroll(!Utils.isNotEmpty(oldState.getUsers()) ? 0 :
                    getYScroll() == 0 ?
                            oldState.getYScroll() : getYScroll())
                    .setCurrentPage(getState().equals(NEXT) ? oldState.getCurrentPage() + 1 :
                            getCurrentPage())
                    .setUsers(getState().equals(SEARCH) ? getUsers() :
                            Utils.isNotEmpty(getUsers()) ? Utils.union(oldState.getUsers(),
                                    getUsers()) : oldState.getUsers())
                    .setLastId(builder.users != null && builder.users.size() > 0 ?
                            builder.users.get(builder.users.size() - 1).getId() : 0);
            return builder.build();
        }
        return builder(this)
                .setyScroll(getYScroll())
                .setCurrentPage(getCurrentPage())
                .setUsers(getUsers())
                .setLastId(getUsers() != null && getUsers().size() > 0 ?
                        getUsers().get(getUsers().size() - 1).getId() : 0)
                .setError(previous.getError())
                .setIsLoading(previous.isLoading())
                .build();
    }

    static class Builder {
        List<UserRealm> users;
        int yScroll, currentPage;
        boolean isLoading;
        Throwable error;
        String state;
        long lastId;

        Builder(String value) {
            state = value;
        }

        Builder(UserListState userListState) {
            state = userListState.getState();
            error = userListState.getError();
            isLoading = userListState.isLoading();
        }

        Builder setUsers(List<UserRealm> value) {
            users = value;
            return this;
        }

        Builder setyScroll(int value) {
            yScroll = value;
            return this;
        }

        Builder setCurrentPage(int value) {
            currentPage = value;
            return this;
        }

        Builder setLastId(int value) {
            lastId = value;
            return this;
        }

        Builder setIsLoading(boolean value) {
            isLoading = value;
            return this;
        }

        Builder setError(Throwable value) {
            error = value;
            return this;
        }

        UserListState build() {
            return new UserListState(this);
        }
    }
}
