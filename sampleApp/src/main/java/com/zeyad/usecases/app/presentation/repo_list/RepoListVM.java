package com.zeyad.usecases.app.presentation.repo_list;

import android.os.Bundle;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.app.models.AutoMap_UserModel;
import com.zeyad.usecases.app.models.UserModel;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import rx.Observable;

/**
 * @author zeyad on 11/1/16.
 */
class RepoListVM extends BaseViewModel implements RepoListView {

    private final IDataUseCase genericUseCase;

    RepoListVM() {
        genericUseCase = DataUseCaseFactory.getInstance();
    }

    @Override
    public Observable getUserList() {
        return genericUseCase.getList(new GetRequest
                .GetRequestBuilder(AutoMap_UserModel.class, true)
                .presentationClass(UserModel.class)
                .url("users/users.json")
                .build());
    }

    @Override
    public Bundle getState() {
        return null;
    }

    @Override
    public void restoreState(Bundle state) {
    }
}
