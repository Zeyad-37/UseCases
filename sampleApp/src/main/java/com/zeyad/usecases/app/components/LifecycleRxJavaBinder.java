package com.zeyad.usecases.app.components;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.MaybeTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.annotations.NonNull;

/**
 * @author by ZIaDo on 6/14/17.
 */
public class LifecycleRxJavaBinder {

    private LifecycleRxJavaBinder() {
    }

    public static <T> FlowableTransformer<T, T> applyFlowable(@NonNull final LifecycleOwner lifecycleOwner) {
        return flowable -> {
            LiveData<T> liveData = LiveDataReactiveStreams.fromPublisher(flowable);
            return Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, liveData));
        };
    }

    public static <T> ObservableTransformer<T, T> applyObservable(@NonNull final LifecycleOwner lifecycleOwner,
            final BackpressureStrategy strategy) {
        return observable -> {
            LiveData<T> liveData = LiveDataReactiveStreams.fromPublisher(observable.toFlowable(strategy));
            return Flowable.fromPublisher(LiveDataReactiveStreams
                    .toPublisher(lifecycleOwner, liveData)).toObservable();
        };
    }

    public static <T> ObservableTransformer<T, T> applyObservable(@NonNull final LifecycleOwner lifecycleOwner) {
        return observable -> {
            LiveData<T> liveData = LiveDataReactiveStreams.fromPublisher(observable
                    .toFlowable(BackpressureStrategy.BUFFER));
            return Flowable.fromPublisher(LiveDataReactiveStreams
                    .toPublisher(lifecycleOwner, liveData)).toObservable();
        };
    }

    public static <T> SingleTransformer<T, T> applySingle(@NonNull final LifecycleOwner lifecycleOwner) {
        return single -> {
            LiveData<T> liveData = LiveDataReactiveStreams.fromPublisher(single.toFlowable());
            return Flowable.fromPublisher(LiveDataReactiveStreams
                    .toPublisher(lifecycleOwner, liveData)).singleOrError();
        };
    }

    public static <T> MaybeTransformer<T, T> applyMaybe(@NonNull final LifecycleOwner lifecycleOwner) {
        return maybe -> {
            LiveData<T> liveData = LiveDataReactiveStreams.fromPublisher(maybe.toFlowable());
            return Flowable.fromPublisher(LiveDataReactiveStreams
                    .toPublisher(lifecycleOwner, liveData)).firstElement();
        };
    }
}
