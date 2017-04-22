package com.zeyad.usecases.app.components.mvvm;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * @author zeyad on 11/28/16.
 */
public class BaseViewModel {

    public Observable.Transformer<BaseEvent, UIModel> uiModels(Func1<BaseEvent, BaseAction> mapEventsToActions,
                                                               Func1<BaseAction, Observable<?>> mapActionsToExecutables,
                                                               Func2<UIModel, BaseResult, UIModel> stateAccumulator,
                                                               UIModel initialState) {
        return events -> events.map(mapEventsToActions)
                .compose(actions -> actions.flatMap(action -> Observable.just(action)
                        .flatMap(mapActionsToExecutables)
                        .map(BaseResult::successResult)
                        .onErrorReturn(BaseResult::errorResult)
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith(BaseResult.IN_FLIGHT)))
                .scan(initialState, stateAccumulator);
    }

    public Observable.Transformer<BaseEvent, UIModel> uiModels(Func1<BaseEvent, BaseAction> mapEventsToActions,
                                                               Func1<BaseAction, Observable<?>> mapActionsToExecutables,
                                                               Func2<UIModel, BaseResult, UIModel> stateAccumulator) {
        return uiModels(mapEventsToActions, mapActionsToExecutables, stateAccumulator, UIModel.idleState);
    }
}
