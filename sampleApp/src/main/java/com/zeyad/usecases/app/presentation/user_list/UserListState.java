package com.zeyad.usecases.app.presentation.user_list;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by ZIaDo on 1/28/17.
 */
@Parcel
public class UserListState {
    final List<User> users;

    UserListState() {
        users = new ArrayList<>();
    }

    private UserListState(Builder builder) {
        users = builder.users;
    }

    static Builder builder() {
        return new Builder();
    }

    List<User> getUsers() {
        return users;
    }

    static class Builder {
        List<User> users;

        Builder() {
        }

        public Builder setUsers(List<User> value) {
            users = value;
            return this;
        }

        UserListState build() {
            return new UserListState(this);
        }
    }
}
