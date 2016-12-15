package com.zeyad.usecases.app.components.eventbus;

import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.Relay;

import rx.Observable;

/**
 * @author zeyad on 11/11/16.
 */

public class Store<T> {

    private final Relay<T, T> storeSubject;

    public Store() {
        storeSubject = BehaviorRelay.<T>create().toSerialized();
    }

    public Store(T defaultValue) {
        storeSubject = BehaviorRelay.create(defaultValue).toSerialized();
    }

    public Observable<T> observe() {
        return storeSubject.asObservable()
                .distinctUntilChanged();
    }

    public void publish(T value) {
        storeSubject.call(value);
    }
}