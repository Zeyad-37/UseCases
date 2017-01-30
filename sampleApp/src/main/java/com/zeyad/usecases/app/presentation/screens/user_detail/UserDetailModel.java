package com.zeyad.usecases.app.presentation.screens.user_detail;

import com.zeyad.usecases.app.components.mvvm.BaseModel;
import com.zeyad.usecases.app.presentation.screens.user_list.UserRealm;

import org.parceler.Parcel;

import java.util.List;

/**
 * @author zeyad on 1/25/17.
 */
@Parcel
public class UserDetailModel extends BaseModel {
    public static final String INITIAL = "initial";
    private final boolean isTwoPane;
    private final UserRealm user;
    private final List<RepoRealm> repos;

    UserDetailModel(UserRealm user, List<RepoRealm> repos, boolean isTwoPane, boolean isLoading,
                    Throwable error, String state) {
        super(isLoading, error, state);
        this.user = user;
        this.repos = repos;
        this.isTwoPane = isTwoPane;
    }

    public UserDetailModel() {
        super(false, null, null);
        user = null;
        repos = null;
        isTwoPane = false;
    }

    public UserDetailModel(Builder builder) {
        super(builder.isLoading, builder.error, builder.state);
        isTwoPane = builder.isTwoPane;
        user = builder.user;
        repos = builder.repos;
    }

    public static UserDetailModel error(Throwable error) {
        return UserDetailModel.builder()
                .setUser(null)
                .setRepos(null)
                .setIsTwoPane(false)
                .setIsLoading(false)
                .setError(error)
                .setState(ERROR)
                .build();
    }

    static UserDetailModel loading() {
        return UserDetailModel.builder()
                .setUser(null)
                .setRepos(null)
                .setIsTwoPane(false)
                .setError(null)
                .setIsLoading(true)
                .setState(LOADING)
                .build();
    }

    public static UserDetailModel onNext(UserRealm user, List<RepoRealm> repos, boolean isTwoPane) {
        return UserDetailModel.builder()
                .setUser(user)
                .setRepos(repos)
                .setIsTwoPane(isTwoPane)
                .setError(null)
                .setIsLoading(false)
                .setState(NEXT)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isTwoPane() {
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
        boolean isLoading, isTwoPane;
        Throwable error;
        String state;

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

        public Builder setState(String value) {
            state = value;
            return this;
        }

        public UserDetailModel build() {
            return new UserDetailModel(this);
        }
    }
}
