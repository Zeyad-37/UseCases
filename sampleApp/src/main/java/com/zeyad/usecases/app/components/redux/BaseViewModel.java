package com.zeyad.usecases.app.components.redux;

import android.arch.lifecycle.ViewModel;

import com.zeyad.usecases.utils.ReplayingShare;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseViewModel<S> extends ViewModel {

    private SuccessStateAccumulator<S> successStateAccumulator;
    private S initialState;

    /**
     * @param successStateAccumulator a success State Accumulator.
     * @param initialState            Initial state to start with.
     */
    public abstract void init(SuccessStateAccumulator<S> successStateAccumulator,
                              S initialState, Object... otherDependencies);

    /**
     * A Transformer, given events returns UIModels by applying the redux pattern.
     *
     * @return {@link FlowableTransformer} the Redux pattern transformer.
     */
    FlowableTransformer<BaseEvent, UIModel<S>> uiModels() {
        return events -> events.observeOn(Schedulers.io())
                .flatMap(event -> Flowable.just(event)
                        .flatMap(mapEventsToExecutables())
                        .map(result -> Result.successResult(new ResultBundle<>(event, result)))
                        .onErrorReturn(Result::errorResult)
                        .startWith(Result.loadingResult()))
                .scan(UIModel.idleState(initialState), (currentUIModel, result) -> {
                    S bundle = currentUIModel.getBundle();
                    if (result.isLoading()) {
                        currentUIModel = UIModel.loadingState(bundle);
                    } else if (result.isSuccessful()) {
                        currentUIModel =
                                UIModel.successState(successStateAccumulator.accumulateSuccessStates(
                                        result.getBundle(), result.getEvent(), bundle));
                    } else {
                        currentUIModel = UIModel.errorState(result.getError(), bundle);
                    }
                    return currentUIModel;
                })
                .compose(ReplayingShare.instance())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * A Function that given an event maps it to the correct executable logic.
     *
     * @return a {@link Function} the mapping function.
     */
    public abstract Function<BaseEvent, Flowable<?>> mapEventsToExecutables();

    public void setSuccessStateAccumulator(SuccessStateAccumulator<S> successStateAccumulator) {
        this.successStateAccumulator = successStateAccumulator;
    }

    public void setInitialState(S initialState) {
        this.initialState = initialState;
    }
}
