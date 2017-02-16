package com.zeyad.usecases.app.presentation.screens.user_detail;

import com.zeyad.usecases.app.components.mvvm.BaseState;
import com.zeyad.usecases.app.presentation.screens.user_list.UserRealm;

import org.parceler.Parcel;

import java.util.List;

/**
 * @author zeyad on 1/25/17.
 */
@Parcel
public class UserDetailState extends BaseState {
    public static final String INITIAL = "initial";
    final boolean isTwoPane;
    final UserRealm user;
    final List<RepoRealm> repos;

    UserDetailState() {
        super(false, null, "");
        user = null;
        repos = null;
        isTwoPane = false;
    }

    private UserDetailState(Builder builder) {
        super(builder.isLoading, builder.error, builder.state);
        isTwoPane = builder.isTwoPane;
        user = builder.user;
        repos = builder.repos;
    }

    public static UserDetailState error(Throwable error) {
        return UserDetailState.builder(ERROR)
                .setUser(null)
                .setRepos(null)
                .setIsTwoPane(false)
                .setIsLoading(false)
                .setError(error)
                .build();
    }

    public static UserDetailState loading() {
        return UserDetailState.builder(LOADING)
                .setUser(null)
                .setRepos(null)
                .setIsTwoPane(false)
                .setError(null)
                .setIsLoading(true)
                .build();
    }

    public static UserDetailState onNext(UserRealm user, List<RepoRealm> repos, boolean isTwoPane) {
        return UserDetailState.builder(NEXT)
                .setUser(user)
                .setRepos(repos)
                .setIsTwoPane(isTwoPane)
                .setError(null)
                .setIsLoading(false)
                .build();
    }

    public static Builder builder(String state) {
        return new Builder(state);
    }

    public static Builder builder(UserDetailState state) {
        return new Builder(state);
    }

    public boolean isTwoPane() {
        return isTwoPane;
    }

    public UserRealm getUser() {
        return user;
    }

    public List<RepoRealm> getRepos() {
        return repos;
    }

    public static class Builder {
        List<RepoRealm> repos;
        UserRealm user;
        boolean isLoading, isTwoPane;
        Throwable error;
        String state;

        public Builder(String value) {
            state = value;
        }

        public Builder(UserDetailState userDetailState) {
            state = userDetailState.getState();
            isLoading = userDetailState.isLoading();
            error = userDetailState.getError();
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

        public Builder setIsLoading(boolean value) {
            isLoading = value;
            return this;
        }

        public Builder setError(Throwable value) {
            error = value;
            return this;
        }

        public UserDetailState build() {
            return new UserDetailState(this);
        }
    }
}
