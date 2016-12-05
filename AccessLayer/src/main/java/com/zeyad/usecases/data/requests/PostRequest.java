package com.zeyad.usecases.data.requests;

import android.support.annotation.NonNull;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.repository.DataRepository;
import com.zeyad.usecases.data.utils.ModelConverters;

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
        url = postRequestBuilder.url;
        dataClass = postRequestBuilder.dataClass;
        presentationClass = postRequestBuilder.presentationClass;
        persist = postRequestBuilder.persist;
        queuable = postRequestBuilder.queuable;
        subscriber = postRequestBuilder.subscriber;
        keyValuePairs = postRequestBuilder.keyValuePairs;
        jsonObject = postRequestBuilder.jsonObject;
        jsonArray = postRequestBuilder.jsonArray;
        idColumnName = postRequestBuilder.idColumnName;
        method = postRequestBuilder.method;
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
        return url != null ? url : "";
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public Class getDataClass() {
        return dataClass;
    }

    public Class getPresentationClass() {
        return presentationClass != null ? presentationClass : dataClass;
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
        return idColumnName != null ? idColumnName : DataRepository.DEFAULT_ID_KEY;
    }

    public String getMethod() {
        return method;
    }

    public static class PostRequestBuilder {
        JSONArray jsonArray;
        JSONObject jsonObject;
        HashMap<String, Object> keyValuePairs;
        String url, idColumnName, method;
        Subscriber subscriber;
        Class dataClass, presentationClass;
        boolean persist, queuable;

        public PostRequestBuilder(Class dataClass, boolean persist) {
            this.dataClass = dataClass;
            this.persist = persist;
        }

        @NonNull
        public PostRequestBuilder url(String url) {
            this.url = Config.getBaseURL() + url;
            return this;
        }

        @NonNull
        public PostRequestBuilder fullUrl(String url) {
            this.url = url;
            return this;
        }

        @NonNull
        public PostRequestBuilder queuable(boolean queuable) {
            this.queuable = queuable;
            return this;
        }

        @NonNull
        public PostRequestBuilder presentationClass(Class presentationClass) {
            this.presentationClass = presentationClass;
            return this;
        }

        @NonNull
        public PostRequestBuilder subscriber(Subscriber subscriber) {
            this.subscriber = subscriber;
            return this;
        }

        @NonNull
        public PostRequestBuilder idColumnName(String idColumnName) {
            this.idColumnName = idColumnName;
            return this;
        }

        @NonNull
        public PostRequestBuilder payLoad(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            return this;
        }

        @NonNull
        public PostRequestBuilder payLoad(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
            return this;
        }

        @NonNull
        public PostRequestBuilder payLoad(HashMap<String, Object> hashMap) {
            keyValuePairs = hashMap;
            return this;
        }

        @NonNull
        public PostRequestBuilder method(String method) {
            this.method = method;
            return this;
        }

        @NonNull
        public PostRequest build() {
            return new PostRequest(this);
        }
    }
}
