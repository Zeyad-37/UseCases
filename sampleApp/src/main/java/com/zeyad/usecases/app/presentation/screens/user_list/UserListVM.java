package com.zeyad.usecases.app.presentation.screens.user_list;

import android.os.Bundle;
import android.util.Log;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.app.presentation.models.UserRealm;
import com.zeyad.usecases.app.utils.Constants;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.data.utils.Utils;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author zeyad on 11/1/16.
 */
class UserListVM extends BaseViewModel implements UserListView {

    private static final String CURRENT_PAGE = "currentPage", Y_SCROLL = "yScroll";
    private final IDataUseCase dataUseCase;
    int counter = 0;
    private int currentPage, yScroll;

    UserListVM() {
        dataUseCase = DataUseCaseFactory.getInstance();
    }

    @Override
    public Observable getUserList() {
        Observable networkObservable = dataUseCase.getList(new GetRequest
//                .GetRequestBuilder(AutoMap_UserModel.class, true)
                .GetRequestBuilder(UserRealm.class, true)
                .presentationClass(UserRealm.class)
                .url(String.format(Constants.URLS.USERS, currentPage))
                .build());
        return dataUseCase.getList(new GetRequest
//                .GetRequestBuilder(AutoMap_UserModel.class, true)
                .GetRequestBuilder(UserRealm.class, true)
                .presentationClass(UserRealm.class)
                .build())
                .onErrorResumeNext(throwable -> {
                    throwable.printStackTrace();
                    return networkObservable;
                })
                .flatMap((Func1<List, Observable<?>>) list -> {
                    if (Utils.isNotEmpty(list))
                        return Observable.just(list);
                    else return networkObservable;
                });
    }

    @SuppressWarnings("NewApi")
    Subscription writePeriodic() {
        return Observable.interval(2000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .takeWhile(aLong -> counter < 37)
                .observeOn(Schedulers.io())
                .doOnNext(aLong -> {
                    UserRealm userRealm = new UserRealm();
                    userRealm.setId(counter);
                    userRealm.setLogin(String.valueOf(counter + 1));
                    getCompositeSubscription().add(dataUseCase.postObject(new PostRequest
                            .PostRequestBuilder(UserRealm.class, true)
                            .idColumnName(UserRealm.ID)
                            .payLoad(userRealm)
                            .build())
                            .subscribe(new Subscriber() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(Object o) {
                                    counter++;
                                    Log.i("writePeriodic", "Realm write successful [" + counter
                                            + "] :: [" + userRealm.getLogin() + "].");
                                }
                            }));
                }).subscribe(aLong -> {
                }, Throwable::printStackTrace, () -> {
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
