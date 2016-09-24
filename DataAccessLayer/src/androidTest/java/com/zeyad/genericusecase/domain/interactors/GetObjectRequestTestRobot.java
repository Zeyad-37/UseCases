package com.zeyad.genericusecase.domain.interactors;


import com.zeyad.genericusecase.data.services.realm_test_models.TestModel2;

import junit.framework.Test;

import rx.Subscriber;
import rx.observers.TestSubscriber;

class GetObjectRequestTestRobot {


    static final Class DATA_CLASS = TestModel2.class;
    static final boolean TO_PERSIST = false;
    static final String ID_COLUMN_NAME = "id";
    static final Class PRESENTATION_CLASS = Test.class;
    static final Subscriber SUBSCRIBER = new TestSubscriber<>();
    static final String URL = "www.google.com";
    static final boolean SHOULD_CACHE = true;
    static final Integer ID_COLUMN_ID = 1;

    static GetObjectRequest createGetObjectRequest() {
        return new GetObjectRequest.GetObjectRequestBuilder(DATA_CLASS, TO_PERSIST)
                .url(URL)
                .shouldCache(SHOULD_CACHE)
                .presentationClass(PRESENTATION_CLASS)
                .idColumnName(ID_COLUMN_NAME)
                .subscriber(SUBSCRIBER)
                .id(ID_COLUMN_ID)
                .build();


    }
}
