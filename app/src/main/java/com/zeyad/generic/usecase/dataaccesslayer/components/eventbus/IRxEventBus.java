package com.zeyad.generic.usecase.dataaccesslayer.components.eventbus;

import android.support.annotation.NonNull;

import rx.Observable;

public interface IRxEventBus {
    void send(Object o);

    @NonNull
    Observable<Object> toObserverable();

    boolean hasObservers();
}
