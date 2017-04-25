package com.zeyad.usecases.data.requests;

import android.support.annotation.NonNull;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.db.RealmManager;
import com.zeyad.usecases.data.repository.DataRepository;

/**
 * @author zeyad on 7/29/16.
 */
public class GetRequest {

    private String url, idColumnName;
    private Class dataClass, presentationClass;
    private boolean persist, shouldCache;
    private int itemId;
    private RealmManager.RealmQueryProvider queryFactory;

    private GetRequest(@NonNull GetRequestBuilder getRequestBuilder) {
        url = getRequestBuilder.mUrl;
        dataClass = getRequestBuilder.mDataClass;
        presentationClass = getRequestBuilder.mPresentationClass;
        persist = getRequestBuilder.mPersist;
        idColumnName = getRequestBuilder.mIdColumnName;
        itemId = getRequestBuilder.mItemId;
        shouldCache = getRequestBuilder.mShouldCache;
        queryFactory = getRequestBuilder.mQueryFactory;
    }

    public String getUrl() {
        return url != null ? url : "";
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

    public boolean isShouldCache() {
        return shouldCache;
    }

    public String getIdColumnName() {
        return idColumnName != null ? idColumnName : DataRepository.DEFAULT_ID_KEY;
    }

    public RealmManager.RealmQueryProvider getQueryFactory() {
        return queryFactory;
    }

    public int getItemId() {
        return itemId;
    }

    public static class GetRequestBuilder {
        private int mItemId;
        private boolean mShouldCache, mPersist;
        private String mIdColumnName, mUrl;
        private Class mDataClass, mPresentationClass;
        private RealmManager.RealmQueryProvider mQueryFactory;

        public GetRequestBuilder(Class dataClass, boolean persist) {
            mDataClass = dataClass;
            mPersist = persist;
        }

        @NonNull
        public GetRequestBuilder url(String url) {
            mUrl = Config.getBaseURL() + url;
            return this;
        }

        @NonNull
        public GetRequestBuilder fullUrl(String url) {
            mUrl = url;
            return this;
        }

        @NonNull
        public GetRequestBuilder presentationClass(Class presentationClass) {
            mPresentationClass = presentationClass;
            return this;
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

        @NonNull
        public GetRequestBuilder queryFactory(RealmManager.RealmQueryProvider queryFactory) {
            mQueryFactory = queryFactory;
            return this;
        }

        @NonNull
        public GetRequest build() {
            return new GetRequest(this);
        }
    }
}