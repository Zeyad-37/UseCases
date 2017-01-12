package com.zeyad.usecases.app.presentation.screens.user_detail;

import android.os.Bundle;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.app.presentation.models.RepoModel;
import com.zeyad.usecases.app.presentation.screens.user_list.UserListActivity;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.utils.Utils;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import rx.Observable;

/**
 * @author zeyad on 1/10/17.
 */
class UserDetailVM extends BaseViewModel implements UserDetailView {
    private static final String CURRENT_USER = "currentUser";
    private final IDataUseCase dataUseCase;
    private String user;

    UserDetailVM() {
        dataUseCase = DataUseCaseFactory.getInstance();
    }

    @Override
    public Observable getRepositories(String user) {
        if (Utils.isNotEmpty(user)) {
            this.user = user;
            return dataUseCase.getList(new GetRequest
//                .GetRequestBuilder(AutoMap_RepoModel.class, true)
                    .GetRequestBuilder(RepoModel.class, true)
                    .presentationClass(RepoModel.class)
                    .build())
                    .flatMap(list -> {
                        if (Utils.isNotEmpty(list))
                            return Observable.just(list);
                        else return dataUseCase.getList(new GetRequest
//                .GetRequestBuilder(AutoMap_RepoModel.class, true)
//                .GetRequestBuilder(RepoRealm.class, true)
                                .GetRequestBuilder(RepoModel.class, true)
                                .presentationClass(RepoModel.class)
                                .url("users/" + user + "/repos")
                                .build());
                    });
        } else return Observable.error(new IllegalArgumentException("User name can not be empty"));
    }

    @Override
    public Bundle getState() {
        Bundle outState = new Bundle(2);
        outState.putString(CURRENT_USER, user);
        return outState;
    }

    @Override
    public void restoreState(Bundle state) {
        if (state != null) {
            UserListActivity userListActivity = ((UserListActivity) getView());
            user = state.getString(CURRENT_USER, "");
        }
    }
}
