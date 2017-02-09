package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.components.mvvm.BaseState;

import org.parceler.Parcel;

import java.util.List;

/**
 * @author by ZIaDo on 1/28/17.
 */
@Parcel
public class UserListState extends BaseState {

    final List<UserRealm> users;
    final int yScroll;
    final int currentPage;
    final long lastId;

    public UserListState() {
        super(false, null, null);
        users = null;
        yScroll = 0;
        currentPage = 0;
        lastId = 0;
    }

    public UserListState(List<UserRealm> users, int yScroll, int currentPage, long lastId, boolean isLoading,
                         Throwable error, String state) {
        super(isLoading, error, state);
        this.users = users;
        this.yScroll = yScroll;
        this.currentPage = currentPage;
        this.lastId = lastId;
    }

    public UserListState(Builder builder) {
        super(builder.isLoading, builder.error, builder.state);
        users = builder.users;
        yScroll = builder.yScroll;
        currentPage = builder.currentPage;
        lastId = builder.lastId;
    }

    public static UserListState loading() {
        return UserListState.builder()
                .setUsers(null)
                .setError(null)
                .setIsLoading(true)
                .setState(LOADING)
                .build();
    }

    public static UserListState onNext(List<UserRealm> users) {
        return UserListState.builder()
                .setUsers(users)
                .setError(null)
                .setIsLoading(false)
                .setState(NEXT)
                .build();
    }

    public static UserListState error(Throwable error) {
        return UserListState.builder()
                .setUsers(null)
                .setIsLoading(false)
                .setError(error)
                .setState(ERROR)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<UserRealm> getUsers() {
        return users;
    }

    public int getyScroll() {
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

        Builder setState(String value) {
            state = value;
            return this;
        }

        UserListState build() {
            return new UserListState(this);
        }
    }
}
