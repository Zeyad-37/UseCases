package com.zeyad.usecases.app.components.mvvm;

import com.zeyad.usecases.app.components.exceptions.ErrorMessageFactory;

import rx.Subscriber;

/**
 * @author zeyad on 11/28/16.
 */

public class BaseSubscriber<V extends LoadDataView, S extends BaseState> extends Subscriber<S> {
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
        if (errorPolicy == ERROR)
            view.showError(ErrorMessageFactory.create((Exception) throwable));
        else if (errorPolicy == ERROR_WITH_RETRY)
            view.showErrorWithRetry(ErrorMessageFactory.create((Exception) throwable));
    }

    @Override
    public void onNext(S s) {
        if (s != null) {
            view.toggleLoading(s.isLoading());
            if (s.getState() != null)
                if (s.getState().equals(BaseState.ERROR))
                    onError(s.getError());
                else
                    view.renderState(s);
        }
    }
}
