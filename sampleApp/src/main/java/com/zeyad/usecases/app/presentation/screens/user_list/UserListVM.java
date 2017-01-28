package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.List;

import rx.Observable;

import static com.zeyad.usecases.app.utils.Constants.URLS.USERS;

/**
 * @author zeyad on 11/1/16.
 */
class UserListVM extends BaseViewModel implements UserListView {

    private final IDataUseCase dataUseCase;
    UserListModelImmutable currentState;
    private int currentPage;
    private long lastId;

    UserListVM() {
        dataUseCase = DataUseCaseFactory.getInstance();
    }

    @Override
    public Observable<UserListModel> getUsers() {
//        Observable<List> result;
//        List lastList = dataUseCase.getLastList().getValue();
//        if (Utils.isNotEmpty(lastList) && lastList.get(0) instanceof UserRealm)
//            result = dataUseCase.getLastList().doOnRequest(aLong -> Log.d("getUsers", "Subject"));
//        else {
//            result = dataUseCase.getList(new GetRequest.GetRequestBuilder(UserRealm.class, true).build())
//                    .flatMap(list -> Utils.isNotEmpty(list) ? Observable.just(list) : getUserListFromServer()
//                            .doOnRequest(aLong -> Log.d("getUsers", "DB Empty, FromServer")))
//                    .onErrorResumeNext(throwable -> {
//                        throwable.printStackTrace();
//                        return getUserListFromServer().doOnRequest(aLong -> Log.d("getUsers", "DB Error, FromServer"));
//                    }).doOnRequest(aLong -> Log.d("getUsers", "fresherData"));
//        }
//        return result.compose(applyStates()).doOnNext(aLong -> Log.d("getUsers", "OnNextCalled"));
        return dataUseCase.getListOffLineFirst(new GetRequest
                .GetRequestBuilder(UserRealm.class, true)
                .url(String.format(USERS, currentPage, lastId)).build())
                .compose(applyStates());
    }

    @Override
    public Observable<List> getUserListFromServer() {
        return dataUseCase.getList(new GetRequest.GetRequestBuilder(UserRealm.class, true)
                .url(String.format(USERS, currentPage, lastId))
                .build());
    }

    private Observable.Transformer<List, UserListModel> applyStates() {
        return listObservable -> listObservable
                .flatMap(list -> Observable.just(UserListModel.onNext((List<UserRealm>) list)))
                .onErrorReturn(UserListModel::error)
                .startWith(UserListModel.loading());
    }

    private Observable.Transformer<List, UserListModelImmutable> applyStatesImmutable() {
        return listObservable -> listObservable
                .flatMap(list -> Observable.just(UserListModelImmutable.reduce(currentState,
                        UserListModelImmutable.onNext((List<UserRealm>) list))))
                .onErrorReturn(throwable -> UserListModelImmutable.reduce(currentState,
                        UserListModelImmutable.error(throwable)))
                .startWith(currentState = UserListModelImmutable.reduce(currentState,
                        UserListModelImmutable.loading()))
                .flatMap(userListModelImmutable -> {
                    currentState = userListModelImmutable;
                    return Observable.just(currentState);
                });
    }

    @Override
    public void incrementPage(long lastId) {
        this.lastId = lastId;
        currentPage++;
        getUserListFromServer().subscribe(list -> {
        }, Throwable::printStackTrace);
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
