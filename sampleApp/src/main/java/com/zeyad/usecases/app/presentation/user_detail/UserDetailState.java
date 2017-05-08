package com.zeyad.usecases.app.presentation.user_detail;

import com.zeyad.usecases.app.presentation.user_list.User;

import org.parceler.Parcel;

import java.util.List;

/**
 * @author zeyad on 1/25/17.
 */
@Parcel
public class UserDetailState {
    final boolean isTwoPane;
    final User user;
    final List<Repository> repos;

    UserDetailState() {
        user = null;
        repos = null;
        isTwoPane = false;
    }

    private UserDetailState(Builder builder) {
        isTwoPane = builder.isTwoPane;
        user = builder.user;
        repos = builder.repos;
    }

    public static Builder builder() {
        return new Builder();
    }

    boolean isTwoPane() {
        return isTwoPane;
    }

    User getUser() {
        return user;
    }

    List<Repository> getRepos() {
        return repos;
    }

    public static class Builder {
        List<Repository> repos;
        User user;
        boolean isTwoPane;

        Builder() {
        }

        public Builder setRepos(List<Repository> value) {
            repos = value;
            return this;
        }

        public Builder setIsTwoPane(boolean value) {
            isTwoPane = value;
            return this;
        }

        public Builder setUser(User value) {
            user = value;
            return this;
        }

        public UserDetailState build() {
            return new UserDetailState(this);
        }
    }
}
