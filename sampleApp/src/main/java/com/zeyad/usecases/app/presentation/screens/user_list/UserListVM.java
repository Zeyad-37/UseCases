package com.zeyad.usecases.app.presentation.screens.user_list;

import android.util.Log;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.app.utils.Constants;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.zeyad.usecases.app.utils.Constants.URLS.USERS;

/**
 * @author zeyad on 11/1/16.
 */
class UserListVM extends BaseViewModel implements UserListView {

    private final IDataUseCase dataUseCase;
    private int currentPage;
    private int counter = 0;

    UserListVM() {
        dataUseCase = DataUseCaseFactory.getInstance();
    }

    @Override
    public Observable<UserListModel> getUserList(boolean fromServer) {
        Observable result;
        if (fromServer) {
            result = getUserListFromServer().doOnRequest(aLong -> Log.d("getUserList", "FromServer"));
        } else {
            List lastList = dataUseCase.getLastList().getValue();
            if (Utils.isNotEmpty(lastList) && lastList.get(0) instanceof UserRealm)
                result = dataUseCase.getLastList().doOnRequest(aLong -> Log.d("getUserList", "Subject"));
            else result = fresherData().doOnRequest(aLong -> Log.d("getUserList", "fresherData"));
        }
        return result.compose(applyStates());
    }

    private Observable<List> fresherData() {
        Observable<List> networkObservable = getUserListFromServer();
        return dataUseCase.getList(new GetRequest.GetRequestBuilder(UserRealm.class, true).build())
                .flatMap(list -> Utils.isNotEmpty(list) ? Observable.just(list) : networkObservable
                        .doOnRequest(aLong -> Log.d("getUserList", "DB Empty, FromServer")))
                .onErrorResumeNext(throwable -> {
                    throwable.printStackTrace();
                    return networkObservable.doOnRequest(aLong -> Log.d("getUserList", "DB Error, FromServer"));
                });
    }

    private Observable<List> getUserListFromServer() {
        return dataUseCase.getList(new GetRequest.GetRequestBuilder(UserRealm.class, true)
                .url(String.format(USERS, currentPage))
                .build());
    }

    private Observable.Transformer applyStates() {
        return new Observable.Transformer<List<UserRealm>, UserListModel>() {
            @Override
            public Observable<UserListModel> call(Observable<List<UserRealm>> listObservable) {
                return listObservable.flatMap(list -> Observable.just(UserListModel.onNext(list)))
                        .onErrorReturn(UserListModel::error)
                        .startWith(UserListModel.loading());
            }
        };
    }

    @Override
    public Observable updateItemByItem() {
        return dataUseCase.getList(new GetRequest
                .GetRequestBuilder(UserRealm.class, true)
                .build())
                .flatMap((Func1<List, Observable<?>>) Observable::from)
                .flatMap((Func1<Object, Observable<?>>) userRealm -> dataUseCase.getObject(new GetRequest
                        .GetRequestBuilder(UserRealm.class, true)
                        .url(String.format(Constants.URLS.USER, ((UserRealm) userRealm).getLogin()))
                        .build()));
    }

    @Override
    public Observable<Long> writePeriodic() {
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
                });
    }

    @Override
    public void incrementPage() {
        setCurrentPage(getCurrentPage() + 1);
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        UserListActivity view = (UserListActivity) getView();
        if (view != null)
            view.getList(true);
    }

    @Override
    public void setView(UserListActivity userListActivity) {
        setView(userListActivity);
    }
}
