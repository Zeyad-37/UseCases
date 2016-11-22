package com.zeyad.genericusecase.data.requests;

import android.support.annotation.NonNull;

import rx.Subscriber;

/**
 * @author zeyad on 7/29/16.
 */
public class GetRequest {

    private String mUrl;
    private Subscriber mSubscriber;
    private Class mDataClass, mPresentationClass;
    private boolean mPersist;
    private String mIdColumnName;
    private int mItemId;
    private boolean mShouldCache;

    public GetRequest(@NonNull GetRequestBuilder getRequestBuilder) {
        mUrl = getRequestBuilder.getUrl();
        mDataClass = getRequestBuilder.getDataClass();
        mPresentationClass = getRequestBuilder.getPresentationClass();
        mPersist = getRequestBuilder.isPersist();
        mSubscriber = getRequestBuilder.getSubscriber();
        mIdColumnName = getRequestBuilder.getIdColumnName();
        mItemId = getRequestBuilder.getItemId();
        mShouldCache = getRequestBuilder.isShouldCache();
    }

    public GetRequest(@NonNull Subscriber subscriber, String url, String idColumnName,
                      int itemId, @NonNull Class presentationClass, Class dataClass, boolean persist,
                      boolean shouldCache) {
        mSubscriber = subscriber;
        mUrl = url;
        mIdColumnName = idColumnName;
        mItemId = itemId;
        mPresentationClass = presentationClass;
        mDataClass = dataClass;
        mPersist = persist;
        mShouldCache = shouldCache;
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

    public boolean isShouldCache() {
        return mShouldCache;
    }

    public String getIdColumnName() {
        return mIdColumnName;
    }

    public int getItemId() {
        return mItemId;
    }

    public static class GetRequestBuilder {
        private boolean mShouldCache;
        private String mIdColumnName;
        private int mItemId;
        private String mUrl;
        private Subscriber mSubscriber;
        private Class mDataClass, mPresentationClass;
        private boolean mPersist;

        public GetRequestBuilder(Class dataClass, boolean persist) {
            mDataClass = dataClass;
            mPersist = persist;
        }

        @NonNull
        public GetRequestBuilder url(String url) {
            mUrl = url;
            return this;
        }

        @NonNull
        public GetRequestBuilder presentationClass(Class presentationClass) {
            mPresentationClass = presentationClass;
            return this;
        }

        @NonNull
        public GetRequestBuilder subscriber(Subscriber subscriber) {
            mSubscriber = subscriber;
            return this;
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

        @NonNull
        public GetRequest build() {
            return new GetRequest(this);
        }

        public boolean isShouldCache() {
            return mShouldCache;
        }

        public String getIdColumnName() {
            return mIdColumnName;
        }

        public int getItemId() {
            return mItemId;
        }

        @NonNull
        public GetRequestBuilder shouldCache(boolean shouldCache) {
            mShouldCache = shouldCache;
            return this;
        }

        @NonNull
        public GetRequestBuilder idColumnName(String idColumnName) {
            mIdColumnName = idColumnName;
            return this;
        }

        @NonNull
        public GetRequestBuilder id(int id) {
            mItemId = id;
            return this;
        }
    }
}