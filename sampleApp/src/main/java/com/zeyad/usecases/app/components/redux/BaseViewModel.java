package com.zeyad.usecases.app.components.redux;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Observable.Transformer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * @author zeyad on 11/28/16.
 */
public class BaseViewModel<S> {

    public Transformer<BaseEvent, UIModel<S>> uiModels(Func1<BaseEvent, Observable<?>> mapEventsToExecutables,
                                                       Func2<UIModel<S>, Result, UIModel<S>> stateAccumulator,
                                                       UIModel<S> initialState, Class... classes) {
        return events -> events.compose(unmergedEvents -> unmergedEvents.observeOn(Schedulers.io())
                .publish(shared -> {
                    List<Class> classList = Arrays.asList(classes);
                    for (int i = 0, size = classList.size(); i < size; i++)
                        shared = shared.mergeWith(shared.ofType(classList.get(i)));
                    return shared;
                }))
                .compose(eventsObservable -> eventsObservable.flatMap(event -> Observable.just(event)
                        .flatMap(mapEventsToExecutables)
                        .map(Result::successResult)
                        .onErrorReturn(Result::errorResult)
                        .startWith(Result.IN_FLIGHT))
                        .distinctUntilChanged(new Func1<Result, String>() {
                            @Override
                            public String call(Result uiModel) {
                                return uiModel.getState();
                            }
                        })
                )
                .scan(initialState, stateAccumulator)
                .distinctUntilChanged(new Func1<UIModel<S>, String>() {
                    @Override
                    public String call(UIModel uiModel) {
                        return uiModel.getState();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Transformer<BaseEvent, UIModel<S>> uiModels(Func1<BaseEvent, Observable<?>> mapEventsToExecutables,
                                                       Func2<UIModel<S>, Result, UIModel<S>> stateAccumulator,
                                                       Class... classes) {
        return uiModels(mapEventsToExecutables, stateAccumulator, UIModel.idleState,
                classes);
    }

    //------------------------------------------------------------------------------------------------//
    private Transformer<BaseEvent, Result> eventsToResults(Func1<BaseEvent, Observable<?>> mapEventsToExecutables,
                                                           Class... classes) {
        return events -> events.observeOn(Schedulers.io())
                .publish(shared -> {
                    List<Class> classList = Arrays.asList(classes);
                    Observable<Result> results = Observable.empty();
                    for (int i = 0, size = classList.size(); i < size; i++)
                        results = shared.mergeWith(shared.ofType(classList.get(i)))
                                .compose(new Transformer<BaseEvent, Result>() {
                                    @Override
                                    public Observable<Result> call(Observable<BaseEvent> events) {
                                        return events.flatMap(event -> Observable.just(event)
                                                .flatMap(mapEventsToExecutables)
                                                .map(Result::successResult)
                                                .onErrorReturn(Result::errorResult)
                                                .startWith(Result.IN_FLIGHT));
                                    }
                                });
                    return results;
                });
    }

    public Transformer<BaseEvent, UIModel<S>> uiModels2(Func1<BaseEvent, Observable<?>> mapEventsToExecutables,
                                                        Func2<UIModel<S>, Result, UIModel<S>> stateAccumulator,
                                                        UIModel<S> initialState, Class... classes) {
        return events -> events.compose(eventsToResults(mapEventsToExecutables, classes))
                .compose(results -> results.scan(initialState, stateAccumulator))
                .distinctUntilChanged(UIModel::getState)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Transformer<BaseEvent, UIModel<S>> uiModels2(Func1<BaseEvent, Observable<?>> mapEventsToExecutables,
                                                        Func2<UIModel<S>, Result, UIModel<S>> stateAccumulator,
                                                        Class... classes) {
        return uiModels2(mapEventsToExecutables, stateAccumulator, UIModel.idleState, classes);
    }
}
