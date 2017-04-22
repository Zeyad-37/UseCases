package com.zeyad.usecases.app.presentation.screens.user_list;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by ZIaDo on 1/28/17.
 */
@Parcel
public class UserListState {
    final List<UserRealm> users;
    final long lastId;

    UserListState() {
        users = new ArrayList<>();
        lastId = 0;
    }

    private UserListState(Builder builder) {
        users = builder.users;
        lastId = builder.lastId;
    }

    public static Builder builder() {
        return new Builder();
    }

    List<UserRealm> getUsers() {
        return users;
    }

    long getLastId() {
        return lastId;
    }

    static class Builder {
        List<UserRealm> users;
        long lastId;

        Builder() {
        }

        Builder(UserListState userListState) {
            users = userListState.getUsers();
            lastId = userListState.getLastId();
        }

        Builder setUsers(List<UserRealm> value) {
            users = value;
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
