package com.zeyad.generic.usecase.dataaccesslayer.presentation;

import android.os.Bundle;

import com.zeyad.generic.usecase.dataaccesslayer.components.mvvm.BaseViewModel;
import com.zeyad.generic.usecase.dataaccesslayer.di.PerActivity;
import com.zeyad.genericusecase.data.requests.GetRequest;
import com.zeyad.genericusecase.domain.interactors.generic.IGenericUseCase;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * @author zeyad on 11/1/16.
 */
@PerActivity
public class RepoListVM extends BaseViewModel implements RepoListView {

    final IGenericUseCase genericUseCase;
    String ENDPOINT = "https://api.ribot.io/";

    RepoListVM(IGenericUseCase genericUseCase) {
        this.genericUseCase = genericUseCase;
    }

    @Override
    public Observable<List> getRepoList() {
        return genericUseCase.getList(new GetRequest.GetRequestBuilder(Object.class, true)
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

    @Override
    public Bundle getState() {
        Bundle bundle = new Bundle();
        return null;
    }

    @Override
    public void restoreState(Bundle state) {

    }
}
