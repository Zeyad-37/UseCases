package com.zeyad.usecases.app.components.mvvm;

import rx.Observable;
import rx.Subscriber;
import rx.subjects.BehaviorSubject;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseViewModel<S extends BaseState> {
    private S viewState;

    private BehaviorSubject<S> state = BehaviorSubject.create(getViewState());

    private Subscriber<S> bvmSubscriber = new Subscriber<S>() {
        @Override
        public void onCompleted() {
            unsubscribe();
        }

        @Override
        public void onError(Throwable e) {
            unsubscribe();
            e.printStackTrace();
        }

        @Override
        public void onNext(S s) {
            getState().onNext(s);
        }
    };

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

    public Subscriber<S> getSubscriber() {
        return bvmSubscriber;
    }
}
