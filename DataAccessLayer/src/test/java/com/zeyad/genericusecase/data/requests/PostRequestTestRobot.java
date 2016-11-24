package com.zeyad.genericusecase.data.requests;

import com.zeyad.genericusecase.realm_test_models.TestModel;
import com.zeyad.genericusecase.realm_test_models.TestViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import rx.Subscriber;
import rx.observers.TestSubscriber;

class PostRequestTestRobot {


    static final Class DATA_CLASS = TestModel.class;
    static final boolean TO_PERSIST = false;
    static final HashMap<String, Object> HASH_MAP = new HashMap<>();
    static final String ID_COLUMN_NAME = "id";
    static final JSONArray JSON_ARRAY = new JSONArray();
    static final JSONObject JSON_OBJECT = new JSONObject();
    static final Class PRESENTATION_CLASS = TestViewModel.class;
    static final Subscriber SUBSCRIBER = new TestSubscriber<>();
    static final String URL = "www.google.com";

    public static PostRequest buildPostRequest() {
        return new PostRequest.PostRequestBuilder(DATA_CLASS, TO_PERSIST)
                .payLoad(HASH_MAP)
                .idColumnName(ID_COLUMN_NAME)
                .payLoad(JSON_ARRAY)
                .payLoad(JSON_OBJECT)
                .presentationClass(PRESENTATION_CLASS)
                .subscriber(SUBSCRIBER)
                .url(URL)
                .build();
    }
}
