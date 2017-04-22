package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.components.mvvm.UIModel;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by ZIaDo on 1/28/17.
 */
@Parcel
public class UserListState {
    private static final String SEARCH = "search";
    final List<UserRealm> users;
    final int yScroll;
    final long lastId;

    UserListState() {
        users = new ArrayList<>();
        yScroll = 0;
        lastId = 0;
    }

    private UserListState(Builder builder) {
        users = builder.users;
        yScroll = builder.yScroll;
        lastId = builder.lastId;
    }

    static UIModel<UserListState> onNext(List<UserRealm> users) {
        return new UIModel<>(UIModel.SUCCESS, false, null, true, UserListState.builder().setUsers(users).build());
    }

    static UIModel<UserListState> onSearch(List<UserRealm> users) {
        return new UIModel<>(UIModel.SUCCESS, false, null, true, UserListState.builder().setUsers(users).build());
    }

    public static Builder builder() {
        return new Builder();
    }

    List<UserRealm> getUsers() {
        return users;
    }

    int getYScroll() {
        return yScroll;
    }

    long getLastId() {
        return lastId;
    }

    static class Builder {
        List<UserRealm> users;
        int yScroll;
        long lastId;

        Builder() {
        }

        Builder(UserListState userListState) {
            users = userListState.getUsers();
            yScroll = userListState.getYScroll();
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

        Builder setLastId(int value) {
            lastId = value;
            return this;
        }

        UserListState build() {
            return new UserListState(this);
        }
    }
}
