package com.zeyad.usecases.app.presentation.screens.user_detail;

import android.util.Log;

import com.zeyad.usecases.app.components.mvvm.BaseState;
import com.zeyad.usecases.app.presentation.screens.user_list.UserRealm;
import com.zeyad.usecases.app.utils.Utils;

import org.parceler.Parcel;

import java.util.List;

/**
 * @author zeyad on 1/25/17.
 */
@Parcel
public final class UserDetailState extends BaseState {
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
                .build();
    }

    public static Builder builder(String state) {
        return new Builder(state);
    }

    private static Builder builder(UserDetailState state) {
        return new Builder(state);
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

    @Override
    public BaseState reduce(BaseState previous) {
        if (previous == null)
            return this;
        if (previous instanceof UserDetailState) {
            UserDetailState oldState = (UserDetailState) previous;
            Log.d("Detail reduce states:", oldState.getState() + " -> " + getState());
            return builder(this).setIsTwoPane(oldState.isTwoPane())
                    .setRepos(Utils.isNotEmpty(this.getRepos()) ? Utils.union(oldState.getRepos(),
                            this.getRepos()) : oldState.getRepos())
                    .setUser(this.getUser() != null ? this.getUser() :
                            oldState.getUser() != null ? oldState.getUser() : new UserRealm())
                    .build();
        }
        return builder(this)
                .setRepos(getRepos())
                .setUser(getUser())
                .setError(previous.getError())
                .setIsLoading(previous.isLoading())
                .build();
    }

    public static class Builder {
        List<RepoRealm> repos;
        UserRealm user;
        boolean isLoading, isTwoPane;
        Throwable error;
        String state;

        Builder(String value) {
            state = value;
        }

        Builder(UserDetailState userDetailState) {
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
