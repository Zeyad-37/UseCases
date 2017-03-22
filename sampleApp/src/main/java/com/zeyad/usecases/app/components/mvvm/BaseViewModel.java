package com.zeyad.usecases.app.components.mvvm;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseViewModel<S extends BaseState> {

    private BehaviorSubject<S> state = BehaviorSubject.create();

    public Observable.Transformer<Object, S> applyStates() {
        return listObservable -> listObservable
                .onErrorReturn(throwable -> BaseState.error(throwable))
                .startWith(BaseState.loading())
                .flatMap(state -> {
                    if (state != null)
                        getState().onNext((S) ((S) state).reduce(getViewState()));
                    return getState();
                });
    }

    public BehaviorSubject<S> getState() {
        return state;
    }

    public S getViewState() {
        return getState().getValue();
    }
}
