package com.zeyad.genericusecase.domain.interactors.requests;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import rx.Subscriber;

/**
 * @author zeyad on 7/29/16.
 */
public class PostRequest {
    public static final String POST = "post", DELETE = "delete", PUT = "put";
    public String mUrl, mIdColumnName, mMethod;
    public Subscriber mSubscriber;
    public Class mDataClass, mPresentationClass;
    public boolean mPersist;
    public JSONObject mJsonObject;
    public JSONArray mJsonArray;
    public HashMap<String, Object> mKeyValuePairs;

    public PostRequest(@NonNull PostRequestBuilder postRequestBuilder) {
        mUrl = postRequestBuilder.getUrl();
        mDataClass = postRequestBuilder.getDataClass();
        mPresentationClass = postRequestBuilder.getPresentationClass();
        mPersist = postRequestBuilder.isPersist();
        mSubscriber = postRequestBuilder.getSubscriber();
        mKeyValuePairs = postRequestBuilder.getKeyValuePairs();
        mJsonObject = postRequestBuilder.getJsonObject();
        mJsonArray = postRequestBuilder.getJsonArray();
        mIdColumnName = postRequestBuilder.getIdColumnName();
        mMethod = postRequestBuilder.getMethod();
    }

    public PostRequest(Subscriber subscriber, String idColumnName, String url, JSONObject keyValuePairs,
                       Class presentationClass, Class dataClass, boolean persist) {
        mIdColumnName = idColumnName;
        mJsonObject = keyValuePairs;
        mPersist = persist;
        mPresentationClass = presentationClass;
        mDataClass = dataClass;
        mSubscriber = subscriber;
        mUrl = url;
    }

    public PostRequest(Subscriber subscriber, String idColumnName, String url, JSONArray keyValuePairs,
                       Class presentationClass, Class dataClass, boolean persist) {
        mIdColumnName = idColumnName;
        mJsonArray = keyValuePairs;
        mPersist = persist;
        mPresentationClass = presentationClass;
        mDataClass = dataClass;
        mSubscriber = subscriber;
        mUrl = url;
    }

    public PostRequest(Subscriber subscriber, String idColumnName, String url, HashMap<String, Object> keyValuePairs,
                       Class presentationClass, Class dataClass, boolean persist) {
        mIdColumnName = idColumnName;
        mKeyValuePairs = keyValuePairs;
        mPersist = persist;
        mPresentationClass = presentationClass;
        mDataClass = dataClass;
        mSubscriber = subscriber;
        mUrl = url;
    }

    @Nullable
    public JSONObject getObjectBundle() {
        if (mJsonObject != null)
            return mJsonObject;
        else if (mKeyValuePairs != null)
            return new JSONObject(mKeyValuePairs);
        else return null;
    }

    public String getUrl() {
        return mUrl;
    }

    public Subscriber getSubscriber() {
        return mSubscriber;
    }

    public Class getDataClass() {
        return mDataClass;
    }

    public Class getPresentationClass() {
        return mPresentationClass;
    }

    public boolean isPersist() {
        return mPersist;
    }

    public JSONObject getJsonObject() {
        return mJsonObject;
    }

    public JSONArray getJsonArray() {
        return mJsonArray;
    }

    public HashMap<String, Object> getKeyValuePairs() {
        return mKeyValuePairs;
    }

    public String getIdColumnName() {
        return mIdColumnName;
    }

    public String getMethod() {
        return mMethod;
    }

    public static class PostRequestBuilder {

        private JSONArray mJsonArray;
        private JSONObject mJsonObject;
        private HashMap<String, Object> mKeyValuePairs;
        private String mUrl, mIdColumnName, mMethod;
        private Subscriber mSubscriber;
        private Class mDataClass, mPresentationClass;
        private boolean mPersist;

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
        public PostRequestBuilder jsonObject(JSONObject jsonObject) {
            mJsonObject = jsonObject;
            return this;
        }

        @NonNull
        public PostRequestBuilder method(String method) {
            mMethod = method;
            return this;
        }

        @NonNull
        public PostRequestBuilder jsonArray(JSONArray jsonArray) {
            mJsonArray = jsonArray;
            return this;
        }

        @NonNull
        public PostRequestBuilder hashMap(HashMap<String, Object> bundle) {
            mKeyValuePairs = bundle;
            return this;
        }

        public String getMethod() {
            return mMethod;
        }

        public String getUrl() {
            return mUrl;
        }

        protected Subscriber getSubscriber() {
            return mSubscriber;
        }

        protected Class getDataClass() {
            return mDataClass;
        }

        protected Class getPresentationClass() {
            return mPresentationClass;
        }

        protected boolean isPersist() {
            return mPersist;
        }

        protected String getIdColumnName() {
            return mIdColumnName;
        }

        public JSONObject getJsonObject() {
            return mJsonObject;
        }

        public JSONArray getJsonArray() {
            return mJsonArray;
        }

        public HashMap<String, Object> getKeyValuePairs() {
            return mKeyValuePairs;
        }

        @NonNull
        public PostRequest build() {
            return new PostRequest(this);
        }
    }
}
