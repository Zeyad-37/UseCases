package com.zeyad.usecases.requests;

import android.support.annotation.NonNull;

import com.zeyad.usecases.Config;

/** @author zeyad on 7/29/16. */
public class GetRequest {

    private static final String DEFAULT_ID_KEY = "id";
    private final String url, idColumnName;
    private final Class dataClass, idType;
    private final boolean persist, shouldCache;
    private final Object itemId;

    private GetRequest(@NonNull Builder builder) {
        url = builder.mUrl;
        dataClass = builder.mDataClass;
        idType = builder.idType;
        persist = builder.mPersist;
        idColumnName = builder.mIdColumnName;
        itemId = builder.mItemId;
        shouldCache = builder.mShouldCache;
    }

    @NonNull
    public String getUrl() {
        return url != null ? url : "";
    }

    public <M> Class<M> getDataClass() {
        return dataClass;
    }

    public boolean isPersist() {
        return persist;
    }

    public boolean isShouldCache() {
        return shouldCache;
    }

    @NonNull
    public String getIdColumnName() {
        return idColumnName != null ? idColumnName : DEFAULT_ID_KEY;
    }

    public Class getIdType() {
        return idType;
    }

    public Object getItemId() {
        return itemId;
    }

    public static class Builder {
        private final boolean mPersist;
        private final Class mDataClass;
        private Object mItemId;
        private boolean mShouldCache;
        private String mIdColumnName, mUrl;
        private Class idType;

        public Builder(Class dataClass, boolean persist) {
            mDataClass = dataClass;
            mPersist = persist;
        }

        @NonNull
        public Builder url(String url) {
            mUrl = Config.getBaseURL() + url;
            return this;
        }

        @NonNull
        public Builder fullUrl(String url) {
            mUrl = url;
            return this;
        }

        @NonNull
        public Builder cache(String idColumnName) {
            mShouldCache = true;
            mIdColumnName = idColumnName;
            return this;
        }

        @NonNull
        public Builder id(Object id, String idColumnName, Class type) {
            mItemId = id;
            idType = type;
            mIdColumnName = idColumnName;
            return this;
        }

        @NonNull
        public GetRequest build() {
            return new GetRequest(this);
        }
    }
}
