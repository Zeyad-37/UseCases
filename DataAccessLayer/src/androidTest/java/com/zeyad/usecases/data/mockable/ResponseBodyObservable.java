package com.zeyad.usecases.data.mockable;

import okhttp3.ResponseBody;
import rx.Observable;

public class ResponseBodyObservable extends Observable<ResponseBody> {
    protected ResponseBodyObservable(OnSubscribe<ResponseBody> f) {
        super(f);
    }
}
