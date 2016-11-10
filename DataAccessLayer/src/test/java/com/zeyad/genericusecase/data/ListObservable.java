package com.zeyad.genericusecase.data;

import java.util.List;

import rx.Observable;

public class ListObservable extends Observable<List> {
    protected ListObservable(OnSubscribe<List> f) {
        super(f);
    }
}
