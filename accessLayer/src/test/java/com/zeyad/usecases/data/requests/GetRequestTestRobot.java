package com.zeyad.usecases.data.requests;

import com.zeyad.usecases.utils.TestRealmObject;

class GetRequestTestRobot {

    static final Class DATA_CLASS = TestRealmObject.class;
    static final boolean TO_PERSIST = false;
    static final String ID_COLUMN_NAME = "id";
    static final Class PRESENTATION_CLASS = Object.class;
    static final String URL = "www.google.com";
    static final boolean SHOULD_CACHE = true;
    static final Integer ID_COLUMN_ID = 1;

    static GetRequest createGetObjectRequest() {
        return new GetRequest.GetRequestBuilder(DATA_CLASS, TO_PERSIST)
                .fullUrl(URL)
                .shouldCache(SHOULD_CACHE)
                .presentationClass(PRESENTATION_CLASS)
                .idColumnName(ID_COLUMN_NAME)
                .id(ID_COLUMN_ID)
                .build();


    }
}
