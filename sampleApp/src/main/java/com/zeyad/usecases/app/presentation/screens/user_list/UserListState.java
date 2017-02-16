package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.components.mvvm.BaseState;

import org.parceler.Parcel;

import java.util.List;

/**
 * @author by ZIaDo on 1/28/17.
 */
@Parcel
public class UserListState extends BaseState {
    public static final String SEARCH = "search";
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
        super(builder.isLoading, builder.error, builder.state);
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

    public static UserListState onSearch(List<UserRealm> users) {
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

    public static Builder builder(UserListState state) {
        return new Builder(state);
    }

    public List<UserRealm> getUsers() {
        return users;
    }

    public int getYScroll() {
        return yScroll;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public long getLastId() {
        return lastId;
    }

    static class Builder {
        List<UserRealm> users;
        int yScroll, currentPage;
        boolean isLoading;
        Throwable error;
        String state;
        long lastId;

        public Builder(String value) {
            state = value;
        }

        public Builder(UserListState userListState) {
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
