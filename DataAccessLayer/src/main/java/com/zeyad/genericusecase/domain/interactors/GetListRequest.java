package com.zeyad.genericusecase.domain.interactors;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Subscriber;

/**
 * @author Zeyad on 7/29/16.
 */
public class GetListRequest {

    private boolean mShouldCache, mPersist;
    private String mUrl;
    private Subscriber mSubscriber;
    private Class mDataClass, mPresentationClass, mDomainClass;

    public GetListRequest(@NonNull GetListRequestBuilder genericUseCaseRequestBuilder) {
        mUrl = genericUseCaseRequestBuilder.getUrl();
        mDataClass = genericUseCaseRequestBuilder.getDataClass();
        mPresentationClass = genericUseCaseRequestBuilder.getPresentationClass();
        mDomainClass = genericUseCaseRequestBuilder.getDomainClass();
        mPersist = genericUseCaseRequestBuilder.isPersist();
        mShouldCache = genericUseCaseRequestBuilder.isShouldCache();
        mSubscriber = genericUseCaseRequestBuilder.getSubscriber();
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

    public static class GetListRequestBuilder {

        private boolean mShouldCache;
        private String mUrl;
        private Class mDataClass, mPresentationClass, mDomainClass;
        private boolean mPersist;
        private Subscriber mSubscriber;

        public GetListRequestBuilder(Class dataClass, boolean persist) {
            mDataClass = dataClass;
            mPersist = persist;
        }

        @NonNull
        public GetListRequestBuilder url(String url) {
            mUrl = url;
            return this;
        }

        @NonNull
        public GetListRequestBuilder presentationClass(Class presentationClass) {
            mPresentationClass = presentationClass;
            return this;
        }

        @NonNull
        public GetListRequestBuilder domainClass(Class domainClass) {
            mDomainClass = domainClass;
            return this;
        }

        @NonNull
        public GetListRequestBuilder subscriber(Subscriber subscriber) {
            mSubscriber = subscriber;
            return this;
        }

        public String getUrl() {
            return mUrl;
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

        @Nullable
        public GetListRequest build() {
            if (mUrl != null && mDataClass != null && mPresentationClass != null && mDomainClass != null)
                return new GetListRequest(this);
            return null;
        }

        public boolean isShouldCache() {
            return mShouldCache;
        }

        @NonNull
        public GetListRequestBuilder shouldCache(boolean shouldCache) {
            mShouldCache = shouldCache;
            return this;
        }

        public Subscriber getSubscriber() {
            return mSubscriber;
        }
    }
}