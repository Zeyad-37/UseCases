package com.zeyad.genericusecase.domain.interactors;

import android.support.annotation.Nullable;

import com.zeyad.genericusecase.data.services.realm_test_models.TestModel;
import com.zeyad.genericusecase.data.services.realm_test_models.TestViewModel;
import com.zeyad.genericusecase.domain.interactors.requests.GetListRequest;

import rx.Subscriber;
import rx.observers.TestSubscriber;

class GetListRequestTestRobot {


    static final Class DATA_CLASS = TestModel.class;
    static final boolean TO_PERSIST = false;
    static final Class PRESENTATION_CLASS = TestViewModel.class;
    static final Subscriber SUBSCRIBER = new TestSubscriber<>();
    static final String URL = "www.google.com";
    static final boolean SHOULD_CACHE = true;

    @Nullable
    static GetListRequest createGetListRequest() {
        return new GetListRequest.GetListRequestBuilder(DATA_CLASS, TO_PERSIST)
                .subscriber(SUBSCRIBER)
                .presentationClass(PRESENTATION_CLASS)
                .shouldCache(SHOULD_CACHE)
                .url(URL)
                .build();
    }
}
