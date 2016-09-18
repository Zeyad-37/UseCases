package com.zeyad.genericusecase.domain.interactors;

import android.support.annotation.NonNull;

import rx.Subscriber;

/**
 * @author zeyad on 7/29/16.
 */
public class GetObjectRequest {

    private String mUrl;
    private Subscriber mSubscriber;
    private Class mDataClass, mPresentationClass, mDomainClass;
    private boolean mPersist;
    private String mIdColumnName;
    private int mItemId;
    private boolean mShouldCache;

    public GetObjectRequest(@NonNull GetObjectRequestBuilder getObjectRequestBuilder) {
        mUrl = getObjectRequestBuilder.getUrl();
        mDataClass = getObjectRequestBuilder.getDataClass();
        mPresentationClass = getObjectRequestBuilder.getPresentationClass();
        mDomainClass = getObjectRequestBuilder.getDomainClass();
        mPersist = getObjectRequestBuilder.isPersist();
        mSubscriber = getObjectRequestBuilder.getSubscriber();
        mIdColumnName = getObjectRequestBuilder.getIdColumnName();
        mItemId = getObjectRequestBuilder.getItemId();
        mShouldCache = getObjectRequestBuilder.isShouldCache();
    }

    public GetObjectRequest(@NonNull Subscriber subscriber, String url, String idColumnName,
                            int itemId, @NonNull Class presentationClass, Class domainClass,
                            Class dataClass, boolean persist, boolean shouldCache) {
        mSubscriber = subscriber;
        mUrl = url;
        mIdColumnName = idColumnName;
        mItemId = itemId;
        mPresentationClass = presentationClass;
        mDomainClass = domainClass;
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

    public Class getDomainClass() {
        return mDomainClass;
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

    public static class GetObjectRequestBuilder {
        private boolean mShouldCache;
        private String mIdColumnName;
        private int mItemId;
        private String mUrl;
        private Subscriber mSubscriber;
        private Class mDataClass, mPresentationClass, mDomainClass;
        private boolean mPersist;

        public GetObjectRequestBuilder(Class dataClass, boolean persist) {
            mDataClass = dataClass;
            mPersist = persist;
        }

        @NonNull
        public GetObjectRequestBuilder url(String url) {
            mUrl = url;
            return this;
        }

        @NonNull
        public GetObjectRequestBuilder presentationClass(Class presentationClass) {
            mPresentationClass = presentationClass;
            return this;
        }

        @NonNull
        public GetObjectRequestBuilder domainClass(Class domainClass) {
            mDomainClass = domainClass;
            return this;
        }

        @NonNull
        public GetObjectRequestBuilder subscriber(Subscriber subscriber) {
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

        public Class getDomainClass() {
            return mDomainClass;
        }

        public boolean isPersist() {
            return mPersist;
        }

        @NonNull
        public GetObjectRequest build() {
            return new GetObjectRequest(this);
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
        public GetObjectRequestBuilder shouldCache(boolean shouldCache) {
            mShouldCache = shouldCache;
            return this;
        }

        @NonNull
        public GetObjectRequestBuilder idColumnName(String idColumnName) {
            mIdColumnName = idColumnName;
            return this;
        }

        @NonNull
        public GetObjectRequestBuilder id(int id) {
            mItemId = id;
            return this;
        }
    }
}