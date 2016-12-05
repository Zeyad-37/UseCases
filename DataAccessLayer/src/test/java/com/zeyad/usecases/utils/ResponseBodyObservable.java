package com.zeyad.usecases.utils;

import okhttp3.ResponseBody;
import rx.Observable;

public class ResponseBodyObservable extends Observable<ResponseBody> {
    protected ResponseBodyObservable(OnSubscribe<ResponseBody> f) {
        super(f);
    }
}
