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

    UserRealm userRealm;
    List<RepoRealm> repoModels;
    boolean isTwoPane;

    UserDetailModel(UserRealm userRealm, List<RepoRealm> repoModels, boolean isLoading, Throwable error) {
        super(isLoading, error);
        this.userRealm = userRealm;
        this.repoModels = repoModels;
    }

    public UserDetailModel() {
        super(false, null);
        userRealm = null;
        repoModels = null;
    }

    public static UserDetailModel error(Throwable error) {
        return new UserDetailModel(null, null, false, error);
    }

    static UserDetailModel loading() {
        return new UserDetailModel(null, null, true, null);
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

    public boolean isTwoPane() {
        return isTwoPane;
    }

    public void setTwoPane(boolean twoPane) {
        isTwoPane = twoPane;
    }
}
