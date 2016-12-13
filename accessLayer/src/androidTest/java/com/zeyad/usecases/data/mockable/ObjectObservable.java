package com.zeyad.usecases.data.mockable;

import rx.Observable;

public class ObjectObservable extends Observable<Object> {
    protected ObjectObservable(OnSubscribe<Object> f) {
        super(f);
    }

}
