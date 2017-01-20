package com.zeyad.usecases.app.presentation.screens.user_list;

import android.os.Bundle;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.app.presentation.models.UserModel;
import com.zeyad.usecases.app.presentation.models.UserRealm;
import com.zeyad.usecases.app.utils.Constants;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.utils.Utils;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import rx.Observable;

/**
 * @author zeyad on 11/1/16.
 */
class UserListVM extends BaseViewModel implements UserListView {

    private static final String CURRENT_PAGE = "currentPage", Y_SCROLL = "yScroll";
    private final IDataUseCase dataUseCase;
    private int currentPage, yScroll;

    UserListVM() {
        dataUseCase = DataUseCaseFactory.getInstance();
    }

    @Override
    public Observable getUserList() {
        Observable networkObservable = dataUseCase.getList(new GetRequest
//                .GetRequestBuilder(AutoMap_UserModel.class, true)
                .GetRequestBuilder(UserRealm.class, true)
                .presentationClass(UserModel.class)
                .url(String.format(Constants.URLS.USERS, currentPage))
                .build());
        return dataUseCase.getList(new GetRequest
//                .GetRequestBuilder(AutoMap_UserModel.class, true)
                .GetRequestBuilder(UserRealm.class, true)
                .presentationClass(UserModel.class)
                .build())
                .onErrorResumeNext(throwable -> {
                    throwable.printStackTrace();
                    return networkObservable;
                })
                .flatMap(list -> {
                    if (Utils.isNotEmpty(list))
                        return Observable.just(list);
                    else return networkObservable;
                });
    }

    void incrementPage() {
        currentPage++;
    }

    void setYScroll(int yScroll) {
        this.yScroll = yScroll;
    }

    @Override
    public Bundle getState() {
        Bundle outState = new Bundle(2);
        outState.putInt(CURRENT_PAGE, currentPage);
        outState.putInt(Y_SCROLL, yScroll);
        return outState;
    }

    @Override
    public void restoreState(Bundle state) {
        if (state != null) {
            UserListActivity userListActivity = ((UserListActivity) getView());
            currentPage = state.getInt(CURRENT_PAGE, 0);
            yScroll = state.getInt(Y_SCROLL, 0);
            userListActivity.userRecycler.scrollToPosition(yScroll);
        }
    }
}
