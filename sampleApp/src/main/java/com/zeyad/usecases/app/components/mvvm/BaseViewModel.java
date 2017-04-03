package com.zeyad.usecases.app.components.mvvm;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseViewModel<B> {

    private BehaviorSubject<ViewState> state = BehaviorSubject.create();

    public Observable.Transformer<Object, ViewState> stateTransformer() {
        return listObservable -> listObservable
                .startWith(ViewState.loadingState(getViewStateBundle()))
                .onErrorReturn(throwable -> ViewState.errorState(throwable, getViewStateBundle()))
                .flatMap(nextState -> {
                    state.onNext((ViewState) nextState);
                    return state;
                });
    }

    public B getViewStateBundle() {
        if (state.getValue() != null)
            return (B) state.getValue().getBundle();
        else return null;
    }
}
