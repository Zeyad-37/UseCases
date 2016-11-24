package com.zeyad.genericusecase.realm_test_models;

import rx.Observable;

public class ObjectObservable extends Observable<Object> {
    protected ObjectObservable(OnSubscribe<Object> f) {
        super(f);
    }

}
