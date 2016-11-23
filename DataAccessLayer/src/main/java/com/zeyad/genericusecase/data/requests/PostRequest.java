package com.zeyad.genericusecase.data.requests;

import android.support.annotation.NonNull;

import com.zeyad.genericusecase.data.repository.DataRepository;
import com.zeyad.genericusecase.data.utils.ModelConverters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import rx.Subscriber;

/**
 * @author zeyad on 7/29/16.
 */
public class PostRequest {
    public static final String POST = "post", DELETE = "delete", PUT = "put";
    private String url, idColumnName, method;
    private Subscriber subscriber;
    private Class dataClass, presentationClass;
    private boolean persist, queuable;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private HashMap<String, Object> keyValuePairs;

    public PostRequest(@NonNull PostRequestBuilder postRequestBuilder) {
        url = postRequestBuilder.mUrl;
        dataClass = postRequestBuilder.mDataClass;
        presentationClass = postRequestBuilder.mPresentationClass;
        persist = postRequestBuilder.mPersist;
        queuable = postRequestBuilder.mQueuable;
        subscriber = postRequestBuilder.mSubscriber;
        keyValuePairs = postRequestBuilder.mKeyValuePairs;
        jsonObject = postRequestBuilder.mJsonObject;
        jsonArray = postRequestBuilder.mJsonArray;
        idColumnName = postRequestBuilder.mIdColumnName;
        method = postRequestBuilder.mMethod;
    }

    public PostRequest(Subscriber subscriber, String idColumnName, String url, JSONObject keyValuePairs,
                       Class presentationClass, Class dataClass, boolean persist) {
        this.idColumnName = idColumnName;
        jsonObject = keyValuePairs;
        this.persist = persist;
        this.presentationClass = presentationClass;
        this.dataClass = dataClass;
        this.subscriber = subscriber;
        this.url = url;
    }

    // for test
    public PostRequest(Subscriber subscriber, String idColumnName, String url, JSONArray keyValuePairs,
                       Class presentationClass, Class dataClass, boolean persist) {
        this.idColumnName = idColumnName;
        jsonArray = keyValuePairs;
        this.persist = persist;
        this.presentationClass = presentationClass;
        this.dataClass = dataClass;
        this.subscriber = subscriber;
        this.url = url;
    }

    public PostRequest(Subscriber subscriber, String idColumnName, String url, HashMap<String, Object> keyValuePairs,
                       Class presentationClass, Class dataClass, boolean persist) {
        this.idColumnName = idColumnName;
        this.keyValuePairs = keyValuePairs;
        this.persist = persist;
        this.presentationClass = presentationClass;
        this.dataClass = dataClass;
        this.subscriber = subscriber;
        this.url = url;
    }

    public JSONObject getObjectBundle() {
        if (jsonObject != null)
            return jsonObject;
        else if (keyValuePairs != null)
            return new JSONObject(keyValuePairs);
        else return new JSONObject();
    }

    public JSONArray getArrayBundle() {
        if (jsonArray != null)
            return jsonArray;
        else if (keyValuePairs != null)
            return ModelConverters.convertToJsonArray(keyValuePairs);
        else return new JSONArray();
    }

    public String getUrl() {
        if (url == null) {
            return "";
        }
        return url;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public Class getDataClass() {
        return dataClass;
    }

    public Class getPresentationClass() {
        if (presentationClass == null) {
            return dataClass;
        }
        return presentationClass;
    }

    public boolean isPersist() {
        return persist;
    }

    public boolean isQueuable() {
        return queuable;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public HashMap<String, Object> getKeyValuePairs() {
        return keyValuePairs;
    }

    public String getIdColumnName() {
        if (idColumnName == null) {
            return DataRepository.DEFAULT_ID_KEY;
        }
        return idColumnName;
    }

    public String getMethod() {
        return method;
    }

    public static class PostRequestBuilder {
        JSONArray mJsonArray;
        JSONObject mJsonObject;
        HashMap<String, Object> mKeyValuePairs;
        String mUrl, mIdColumnName, mMethod;
        Subscriber mSubscriber;
        Class mDataClass, mPresentationClass;
        boolean mPersist, mQueuable;

        public PostRequestBuilder(Class dataClass, boolean persist) {
            mDataClass = dataClass;
            mPersist = persist;
        }

        @NonNull
        public PostRequestBuilder url(String url) {
            mUrl = url;
            return this;
        }

        @NonNull
        public PostRequestBuilder queuable(boolean queuable) {
            mQueuable = queuable;
            return this;
        }

        @NonNull
        public PostRequestBuilder presentationClass(Class presentationClass) {
            mPresentationClass = presentationClass;
            return this;
        }

        @NonNull
        public PostRequestBuilder subscriber(Subscriber subscriber) {
            mSubscriber = subscriber;
            return this;
        }

        @NonNull
        public PostRequestBuilder idColumnName(String idColumnName) {
            mIdColumnName = idColumnName;
            return this;
        }

        @NonNull
        public PostRequestBuilder payLoad(JSONObject jsonObject) {
            mJsonObject = jsonObject;
            return this;
        }

        @NonNull
        public PostRequestBuilder payLoad(JSONArray jsonArray) {
            mJsonArray = jsonArray;
            return this;
        }

        @NonNull
        public PostRequestBuilder payLoad(HashMap hashMap) {
            mKeyValuePairs = hashMap;
            return this;
        }

        @NonNull
        public PostRequestBuilder method(String method) {
            mMethod = method;
            return this;
        }

        @NonNull
        public PostRequest build() {
            return new PostRequest(this);
        }
    }
}
