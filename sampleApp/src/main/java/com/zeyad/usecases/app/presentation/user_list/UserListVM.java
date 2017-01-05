package com.zeyad.usecases.app.presentation.user_list;

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
class UserListVM extends BaseViewModel implements UserListView {

    private final IDataUseCase genericUseCase;
    private int currentPage;

    UserListVM() {
        genericUseCase = DataUseCaseFactory.getInstance();
    }

    @Override
    public Observable getUserList() {
        return genericUseCase.getList(new GetRequest
                .GetRequestBuilder(AutoMap_UserModel.class, true)
                .presentationClass(UserModel.class)
                .url("users/?page=" + currentPage)
                .build());
    }

    void incrementPage() {
        currentPage++;
    }

    @Override
    public Bundle getState() {
        return null;
    }

    @Override
    public void restoreState(Bundle state) {
    }
}
