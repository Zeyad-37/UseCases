package com.zeyad.usecases.app.components.mvvm;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseViewModel<S extends BaseState> {

    private BehaviorSubject<S> state = BehaviorSubject.create();

    public abstract S reduce(S previous, S changes);

    public abstract Observable.Transformer<?, S> applyStates();

    public BehaviorSubject<S> getState() {
        return state;
    }

    public S getViewState() {
        return getState().getValue();
    }
}
