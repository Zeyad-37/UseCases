package com.zeyad.generic.usecase.dataaccesslayer.presentation;

import com.zeyad.generic.usecase.dataaccesslayer.components.mvp.BasePresenter;
import com.zeyad.genericusecase.data.requests.GetRequest;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * @author zeyad on 11/1/16.
 */

public class RepoListVM extends BasePresenter implements RepoListView {

    String ENDPOINT = "https://api.ribot.io/";

    @Inject
    RepoListVM() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public Observable getRepoList() {
        return getGenericUseCase().getList(new GetRequest.GetRequestBuilder(Object.class, true)
                .presentationClass(Object.class)
                .url(ENDPOINT + "r/aww/new/.json")
                .build())
                .retryWhen(observable -> showDeleteFailedWithRetry())
                .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Throwable> observable) {
                        return observable.flatMap(o -> showDeleteFailedWithRetry());
                    }
                });
    }

    private Observable<?> showDeleteFailedWithRetry() {
//        Snackbar snackbar = Snackbar.make(null, "Can't load repos", Snackbar.LENGTH_LONG);
//        Observable<Integer> observable = RxSnackbar.actionClicked(snackbar, "Retry");
//        snackbar.show();
//        return observable;
        return Observable.empty();
    }
}
