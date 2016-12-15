package com.zeyad.usecases.app.components.eventbus;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Small wrapper on top of the EventBus to allow consumption of events as
 * Rx streams.
 *
 * @author Zeyad
 */
class RxEventBus implements IRxEventBus {

    private static IRxEventBus mInstance;
    private final SerializedSubject<Object, Object> rxBus;

    private RxEventBus() {
        rxBus = new SerializedSubject<>(PublishSubject.create());
    }

    static IRxEventBus getInstance() {
        if (mInstance == null)
            mInstance = new RxEventBus();
        return mInstance;
    }

    @Override
    public void send(Object o) {
        rxBus.onNext(o);
    }

    @Override
    @NonNull
    public Observable<Object> toObserverable() {
        return rxBus;
    }

    @Override
    public boolean hasObservers() {
        return rxBus.hasObservers();
    }
}