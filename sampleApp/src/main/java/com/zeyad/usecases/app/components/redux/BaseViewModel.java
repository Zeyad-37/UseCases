package com.zeyad.usecases.app.components.redux;

import rx.Observable;
import rx.Observable.Transformer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static com.zeyad.usecases.app.components.redux.UIModel.SUCCESS;

/**
 * @author zeyad on 11/28/16.
 */
public class BaseViewModel<S> {

    public Transformer<BaseEvent, UIModel<S>> uiModels(Func1<BaseEvent, Observable<?>> mapEventsToExecutables,
                                                       Func2<UIModel<S>, Result<?>, UIModel<S>> stateAccumulator,
                                                       UIModel<S> initialState) {
        return events -> events.observeOn(Schedulers.io())
                .flatMap(event -> Observable.just(event)
                        .flatMap(mapEventsToExecutables)
                        .map(Result::successResult)
                        .onErrorReturn(Result::errorResult)
                        .startWith(Result.loadingResult()))
                .distinctUntilChanged(result -> result.getState() + (result.getState().equals(SUCCESS) ?
                        (result.getBundle() != null ? result.getBundle().toString() : "") : ""))
                .scan(initialState, stateAccumulator)
                .distinctUntilChanged(uiModel -> uiModel.getState() + (uiModel.getState().equals(SUCCESS) ?
                        (uiModel.getBundle() != null ? uiModel.getBundle().toString() : "") : ""))
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Transformer<BaseEvent, UIModel<S>> uiModels(Func1<BaseEvent, Observable<?>> mapEventsToExecutables,
                                                       Func2<UIModel<S>, Result<?>, UIModel<S>> stateAccumulator) {
        return uiModels(mapEventsToExecutables, stateAccumulator, UIModel.idleState());
    }
}
