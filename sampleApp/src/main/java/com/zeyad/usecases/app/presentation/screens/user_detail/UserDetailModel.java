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

    boolean isTwoPane;
    UserRealm userRealm;
    List<RepoRealm> repoModels;

    UserDetailModel(UserRealm userRealm, List<RepoRealm> repoModels, boolean isLoading, Throwable error,
                    String state) {
        super(isLoading, error, state);
        this.userRealm = userRealm;
        this.repoModels = repoModels;
    }

    public UserDetailModel() {
        super(false, null, null);
        userRealm = null;
        repoModels = null;
    }

    public static UserDetailModel error(Throwable error) {
        return new UserDetailModel(null, null, false, error, null);
    }

    static UserDetailModel loading() {
        return new UserDetailModel(null, null, true, null, null);
    }

    public boolean isTwoPane() {
        return isTwoPane;
    }

    public void setTwoPane(boolean twoPane) {
        isTwoPane = twoPane;
    }

    UserRealm getUserRealm() {
        return userRealm;
    }

    public UserDetailModel setUserRealm(UserRealm userRealm) {
        this.userRealm = userRealm;
        return this;
    }

    List<RepoRealm> getRepoModels() {
        return repoModels;
    }

    public UserDetailModel setRepoModels(List<RepoRealm> repoModels) {
        this.repoModels = repoModels;
        return this;
    }
}
