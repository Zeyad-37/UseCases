package com.zeyad.usecases.app.components.mvvm;

import android.view.View;

import com.zeyad.usecases.app.components.exceptions.ErrorMessageFactory;

import rx.Subscriber;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * @author zeyad on 11/28/16.
 */

public class BaseSubscriber<V extends LoadDataView> extends Subscriber<UIModel> {
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
    }

    @Override
    public void onError(Throwable throwable) {
        throw new OnErrorNotImplementedException(throwable);
    }

    @Override
    public void onNext(UIModel uiModel) {
        view.toggleLoading(uiModel.isLoading());
        View toggleableView = view.getViewToToggleEnabling();
        if (toggleableView != null)
            toggleableView.setEnabled(!uiModel.isLoading());
        if (!uiModel.isLoading()) {
            if (uiModel.isSuccessful())
                view.renderState(uiModel.getBundle());
            else if (uiModel.getError() != null) {
                Exception throwable = (Exception) uiModel.getError();
                throwable.printStackTrace();
                String errorMsg = ErrorMessageFactory.create(throwable);
                if (errorPolicy == ERROR)
                    view.showError(errorMsg);
                else if (errorPolicy == ERROR_WITH_RETRY)
                    view.showErrorWithRetry(errorMsg);
            }
        }
    }
}
