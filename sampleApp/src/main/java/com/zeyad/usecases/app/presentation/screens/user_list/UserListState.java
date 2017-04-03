package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.components.mvvm.ViewState;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import static com.zeyad.usecases.app.components.mvvm.ViewState.NEXT;

/**
 * @author by ZIaDo on 1/28/17.
 */
@Parcel
public class UserListState {
    private static final String SEARCH = "search";
    final List<UserRealm> users;
    final int yScroll;
    final int currentPage;
    final long lastId;

    UserListState() {
        users = new ArrayList<>();
        yScroll = 0;
        currentPage = 0;
        lastId = 0;
    }

    private UserListState(Builder builder) {
        users = builder.users;
        yScroll = builder.yScroll;
        currentPage = builder.currentPage;
        lastId = builder.lastId;
    }

    public static ViewState onNext(List<UserRealm> users) {
        return new ViewState(false, null, NEXT, UserListState.builder().setUsers(users).build());
    }

    static ViewState onSearch(List<UserRealm> users) {
        return new ViewState(false, null, SEARCH, UserListState.builder().setUsers(users).build());
    }

    private static Builder builder() {
        return new Builder();
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

    static class Builder {
        List<UserRealm> users;
        int yScroll, currentPage;
        long lastId;

        Builder() {
        }

        Builder(UserListState userListState) {
            users = userListState.getUsers();
            yScroll = userListState.getYScroll();
            currentPage = userListState.getCurrentPage();
            lastId = userListState.getLastId();
        }

        Builder setUsers(List<UserRealm> value) {
            users = value;
            return this;
        }

        Builder setYScroll(int value) {
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

        UserListState build() {
            return new UserListState(this);
        }
    }
}
