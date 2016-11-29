package com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_list;

import android.os.Bundle;

import com.zeyad.generic.usecase.dataaccesslayer.components.mvvm.BaseViewModel;
import com.zeyad.generic.usecase.dataaccesslayer.models.data.RepoRealm;
import com.zeyad.generic.usecase.dataaccesslayer.models.ui.RepoModel;
import com.zeyad.genericusecase.data.requests.GetRequest;
import com.zeyad.genericusecase.domain.interactors.generic.GenericUseCaseFactory;
import com.zeyad.genericusecase.domain.interactors.generic.IGenericUseCase;

import rx.Observable;

/**
 * @author zeyad on 11/1/16.
 */
class RepoListVM extends BaseViewModel implements RepoListView {

    private static final String USER_NAME = "userName";
    private final IGenericUseCase genericUseCase;
    private String userName;

    RepoListVM() {
        genericUseCase = GenericUseCaseFactory.getInstance();
    }

    @Override
    public Observable getRepoList(String name) {
        userName = name;
        return genericUseCase.getList(new GetRequest.GetRequestBuilder(RepoRealm.class, true)
                .presentationClass(RepoModel.class)
                .url("users/" + name + "/repos")
                .build());
    }

    @Override
    public Bundle getState() {
        Bundle bundle = new Bundle();
        bundle.putString(USER_NAME, userName);
        return bundle;
    }

    @Override
    public void restoreState(Bundle state) {
        userName = state.getString(USER_NAME, "");
    }
}
