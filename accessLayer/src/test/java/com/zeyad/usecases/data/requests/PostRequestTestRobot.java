package com.zeyad.usecases.data.requests;

import com.zeyad.usecases.utils.TestRealmObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

class PostRequestTestRobot {


    static final boolean TO_PERSIST = false;
    static final Class DATA_CLASS = TestRealmObject.class;
    static final Class PRESENTATION_CLASS = Object.class;
    static final String ID_COLUMN_NAME = "id";
    static final String URL = "www.google.com";
    private static final JSONArray JSON_ARRAY = new JSONArray();
    private static final JSONObject JSON_OBJECT = new JSONObject();
    private static final HashMap<String, Object> HASH_MAP = new HashMap<>();

    static PostRequest buildPostRequest() {
        return new PostRequest.PostRequestBuilder(DATA_CLASS, TO_PERSIST)
                .payLoad(HASH_MAP)
                .idColumnName(ID_COLUMN_NAME)
                .payLoad(JSON_ARRAY)
                .payLoad(JSON_OBJECT)
                .presentationClass(PRESENTATION_CLASS)
                .fullUrl(URL)
                .build();
    }
}
