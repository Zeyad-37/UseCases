package com.zeyad.usecases.app.components.redux;

import android.arch.lifecycle.ViewModel;

import com.zeyad.usecases.utils.ReplayingShare;

import rx.Observable;
import rx.Observable.Transformer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseViewModel<S> extends ViewModel {

    public SuccessStateAccumulator<S> successStateAccumulator;
    public S initialState;

    /**
     * @param successStateAccumulator a success State Accumulator.
     * @param initialState            Initial state to start with.
     */
    public abstract void init(SuccessStateAccumulator<S> successStateAccumulator, S initialState,
                              Object... otherDependencies);

    /**
     * A Transformer, given events returns UIModels by applying the redux pattern.
     *
     * @return {@link Transformer} the Redux pattern transformer.
     */
    Transformer<BaseEvent, UIModel<S>> uiModels() {
        return events -> events.observeOn(Schedulers.io())
                .flatMap(event -> Observable.just(event)
                        .flatMap(mapEventsToExecutables())
                        .map(Result::successResult)
                        .onErrorReturn(Result::errorResult)
                        .startWith(Result.loadingResult()))
                .scan(UIModel.idleState(initialState), (currentUIModel, result) -> {
                    S bundle = currentUIModel.getBundle();
                    if (result.isLoading())
                        currentUIModel = UIModel.loadingState(bundle);
                    else if (result.isSuccessful())
                        currentUIModel = UIModel.successState(successStateAccumulator
                                .accumulateSuccessStates(result, bundle));
                    else currentUIModel = UIModel.errorState(result.getError());
                    return currentUIModel;
                })
                .compose(ReplayingShare.instance())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * A Function that given an event maps it to the correct executable logic.
     *
     * @return a {@link Func1} the mapping function.
     */
    public abstract Func1<BaseEvent, Observable<?>> mapEventsToExecutables();
}
