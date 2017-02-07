package com.zeyad.usecases.app.components.mvvm;

import rx.Observable;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseViewModel<S extends BaseState> {
    private S viewState;

    public abstract S reduce(S previous, S changes);

    public abstract Observable.Transformer<?, S> applyStates();

    public S getViewState() {
        return viewState;
    }

    public void setViewState(S viewState) {
        this.viewState = viewState;
    }
}
