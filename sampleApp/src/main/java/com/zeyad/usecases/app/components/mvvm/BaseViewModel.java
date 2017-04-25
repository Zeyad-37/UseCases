package com.zeyad.usecases.app.components.mvvm;

import rx.Observable;
import rx.Observable.Transformer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * @author zeyad on 11/28/16.
 */
public class BaseViewModel<S> {

    public Transformer<BaseEvent, UIModel<S>> uiModels(Func1<BaseEvent, BaseAction> mapEventsToActions,
                                                       Func1<BaseAction, Observable<?>> mapActionsToExecutables,
                                                       Func2<UIModel<S>, Result<?>, UIModel<S>> stateAccumulator,
                                                       UIModel<S> initialState) {
        return events -> events.map(mapEventsToActions)
                .compose(actions -> actions.flatMap(action -> Observable.just(action)
                        .flatMap(mapActionsToExecutables)
                        .map(Result::successResult)
                        .onErrorReturn(Result::errorResult)
                        .startWith(Result.IN_FLIGHT)))
                .scan(initialState, stateAccumulator)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Transformer<BaseEvent, UIModel<S>> uiModels(Func1<BaseEvent, BaseAction> mapEventsToActions,
                                                       Func1<BaseAction, Observable<?>> mapActionsToExecutables,
                                                       Func2<UIModel<S>, Result<?>, UIModel<S>> stateAccumulator) {
        return uiModels(mapEventsToActions, mapActionsToExecutables, stateAccumulator, UIModel.idleState);
    }
}
