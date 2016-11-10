package com.zeyad.genericusecase.domain.interactor;

import rx.Observable;

public class ObjectObservable extends Observable<Object> {
    protected ObjectObservable(OnSubscribe<Object> f) {
        super(f);
    }

}
