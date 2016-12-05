package com.zeyad.usecases.utils;

import rx.Observable;

public class ObjectObservable extends Observable<Object> {
    protected ObjectObservable(OnSubscribe<Object> f) {
        super(f);
    }

}
