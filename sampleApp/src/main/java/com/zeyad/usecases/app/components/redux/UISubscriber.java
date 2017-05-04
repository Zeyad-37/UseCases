package com.zeyad.usecases.app.components.redux;

import android.util.Log;

import com.zeyad.usecases.app.components.exceptions.ErrorMessageFactory;

import rx.Subscriber;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * @author zeyad on 11/28/16.
 */
public class UISubscriber<V extends LoadDataView<S>, S> extends Subscriber<UIModel<S>> {
    private V view;

    public UISubscriber(V view) {
        this.view = view;
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable throwable) {
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
                Exception throwable = (Exception) uiModel.getError();
                throwable.printStackTrace();
                String errorMsg = ErrorMessageFactory.create(throwable);
                view.showError(errorMsg);
            }
        }
    }
}
