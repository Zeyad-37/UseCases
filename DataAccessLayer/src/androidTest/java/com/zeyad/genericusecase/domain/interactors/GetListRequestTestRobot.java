package com.zeyad.genericusecase.domain.interactors;

import com.zeyad.genericusecase.data.services.realm_test_models.TestModel2;

import junit.framework.Test;

import rx.Subscriber;
import rx.observers.TestSubscriber;

class GetListRequestTestRobot {


    static final Class DATA_CLASS = TestModel2.class;
    static final boolean TO_PERSIST = false;
    static final Class PRESENTATION_CLASS = Test.class;
    static final Subscriber SUBSCRIBER = new TestSubscriber<>();
    static final String URL = "www.google.com";
    static final boolean SHOULD_CACHE = true;

    static GetListRequest createGetListRequest() {
        return new GetListRequest.GetListRequestBuilder(DATA_CLASS, TO_PERSIST)
                .subscriber(SUBSCRIBER)
                .presentationClass(PRESENTATION_CLASS)
                .shouldCache(SHOULD_CACHE)
                .url(URL)
                .build();
    }
}
