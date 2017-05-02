package com.zeyad.usecases.app.components.redux;

import rx.Observable;
import rx.Observable.Transformer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseViewModel<S> {

    public Transformer<BaseEvent, UIModel<S>> uiModels(Func2<UIModel<S>, Result<?>, UIModel<S>> stateAccumulator,
                                                       UIModel<S> initialState) {
        return events -> events.observeOn(Schedulers.io())
                .flatMap(event -> Observable.just(event)
                        .flatMap(mapEventsToExecutables())
                        .map(Result::successResult)
                        .onErrorReturn(Result::errorResult)
                        .startWith(Result.loadingResult()))
                .scan(initialState, stateAccumulator)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Transformer<BaseEvent, UIModel<S>> uiModels(Func2<UIModel<S>, Result<?>, UIModel<S>> stateAccumulator) {
        return uiModels(stateAccumulator, UIModel.idleState());
    }

    public abstract Func1<BaseEvent, Observable<?>> mapEventsToExecutables();
}
