package com.zeyad.usecases.app.presentation.screens.user_detail;

import com.zeyad.usecases.app.components.mvvm.UIModel;
import com.zeyad.usecases.app.presentation.screens.user_list.UserRealm;

import org.parceler.Parcel;

import java.util.List;

/**
 * @author zeyad on 1/25/17.
 */
@Parcel
public final class UserDetailState {
    final boolean isTwoPane;
    final UserRealm user;
    final List<RepoRealm> repos;

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

    static UIModel<UserDetailState> onNext(UserRealm user, List<RepoRealm> repos, boolean isTwoPane) {
        return new UIModel<>(UIModel.SUCCESS, false, null, true, UserDetailState.builder()
                .setUser(user)
                .setRepos(repos)
                .setIsTwoPane(isTwoPane)
                .build());
    }

    public static Builder builder() {
        return new Builder();
    }

    boolean isTwoPane() {
        return isTwoPane;
    }

    UserRealm getUser() {
        return user;
    }

    List<RepoRealm> getRepos() {
        return repos;
    }

    public static class Builder {
        List<RepoRealm> repos;
        UserRealm user;
        boolean isTwoPane;

        Builder() {
        }

        public Builder setRepos(List<RepoRealm> value) {
            repos = value;
            return this;
        }

        public Builder setIsTwoPane(boolean value) {
            isTwoPane = value;
            return this;
        }

        public Builder setUser(UserRealm value) {
            user = value;
            return this;
        }

        public UserDetailState build() {
            return new UserDetailState(this);
        }
    }
}
