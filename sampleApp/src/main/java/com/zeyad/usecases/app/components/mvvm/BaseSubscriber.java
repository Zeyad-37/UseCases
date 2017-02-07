package com.zeyad.usecases.app.components.mvvm;

import com.zeyad.usecases.app.components.exceptions.ErrorMessageFactory;

import rx.Subscriber;

/**
 * @author zeyad on 11/28/16.
 */

public class BaseSubscriber<V extends LoadDataView, M extends BaseState> extends Subscriber<M> {
    public final static int NO_ERROR = 0, ERROR = 1, ERROR_WITH_RETRY = 2;
    private V view;
    private int errorPolicy;

    public BaseSubscriber(V view, int errorPolicy) {
        this.view = view;
        if (errorPolicy < 0 || errorPolicy > 2)
            errorPolicy = 0;
        this.errorPolicy = errorPolicy;
    }

    public BaseSubscriber(V view) {
        this.view = view;
        this.errorPolicy = 1;
    }

    @Override
    public void onCompleted() {
        view.toggleLoading(false);
    }

    @Override
    public void onError(Throwable throwable) {
        view.toggleLoading(false);
        throwable.printStackTrace();
        switch (errorPolicy) {
            case ERROR:
                view.showError(ErrorMessageFactory.create((Exception) throwable));
                break;
            case ERROR_WITH_RETRY:
                view.showErrorWithRetry(ErrorMessageFactory.create((Exception) throwable));
                break;
            default:
                break;
        }
    }

    @Override
    public void onNext(M m) {
        view.toggleLoading(m.isLoading());
        if (m.getError() != null)
            onError(m.getError());
        view.renderState(m);
    }
}
