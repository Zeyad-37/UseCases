package com.zeyad.usecases.app.components.mvvm;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseViewModel<S extends BaseState> {
    private S viewState;

    private BehaviorSubject<S> state = BehaviorSubject.create(getViewState());

    public Observable<S> getState(Observable<S> input) {
        if (state == null)
            state = BehaviorSubject.create(getViewState());
        return state.concatMap(o -> input);
    }

    public abstract S reduce(S previous, S changes);

    public abstract Observable.Transformer<?, S> applyStates();

    public S getViewState() {
        return viewState;
    }

    public void setViewState(S viewState) {
        this.viewState = viewState;
    }

    public BehaviorSubject<S> getState() {
        return state;
    }
}
