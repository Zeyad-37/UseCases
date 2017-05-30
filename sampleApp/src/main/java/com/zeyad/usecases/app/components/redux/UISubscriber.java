package com.zeyad.usecases.app.components.redux;

import android.util.Log;

import io.reactivex.subscribers.DisposableSubscriber;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * @author zeyad on 11/28/16.
 */
public class UISubscriber<V extends LoadDataView<S>, S> extends DisposableSubscriber<UIModel<S>> {
    private final ErrorMessageFactory errorMessageFactory;
    private final V view;

    public UISubscriber(V view, ErrorMessageFactory errorMessageFactory) {
        this.view = view;
        this.errorMessageFactory = errorMessageFactory;
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
        throw new OnErrorNotImplementedException(throwable);
    }

    @Override
    public void onNext(UIModel<S> uiModel) {
        Log.d("onNext", "UIModel: " + uiModel.toString());
        view.toggleViews(uiModel.isLoading());
        if (!uiModel.isLoading()) {
            if (uiModel.isSuccessful())
                view.renderState(uiModel.getBundle());
            else if (uiModel.getError() != null) {
                Throwable throwable = uiModel.getError();
                Log.e("UISubscriber", throwable.getMessage(), throwable);
                throwable.printStackTrace();
                view.showError(errorMessageFactory.getErrorMessage(throwable));
            }
        }
    }
}
