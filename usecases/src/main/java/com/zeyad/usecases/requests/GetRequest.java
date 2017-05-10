package com.zeyad.usecases.requests;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.zeyad.usecases.Config;

/**
 * @author zeyad on 7/29/16.
 */
public class GetRequest implements Parcelable {
    private static final String DEFAULT_ID_KEY = "id";
    public static final Creator<GetRequest> CREATOR = new Creator<GetRequest>() {
        @Override
        public GetRequest createFromParcel(Parcel source) {
            return new GetRequest(source);
        }

        @Override
        public GetRequest[] newArray(int size) {
            return new GetRequest[size];
        }
    };
    private String url, idColumnName;
    private Class dataClass;
    private boolean persist, shouldCache;
    private int itemId;

    private GetRequest(@NonNull GetRequestBuilder getRequestBuilder) {
        url = getRequestBuilder.mUrl;
        dataClass = getRequestBuilder.mDataClass;
        persist = getRequestBuilder.mPersist;
        idColumnName = getRequestBuilder.mIdColumnName;
        itemId = getRequestBuilder.mItemId;
        shouldCache = getRequestBuilder.mShouldCache;
    }

    protected GetRequest(Parcel in) {
        this.url = in.readString();
        this.idColumnName = in.readString();
        this.dataClass = (Class) in.readSerializable();
        this.persist = in.readByte() != 0;
        this.shouldCache = in.readByte() != 0;
        this.itemId = in.readInt();
    }

    public String getUrl() {
        return url != null ? url : "";
    }

    public Class getDataClass() {
        return dataClass;
    }

    public boolean isPersist() {
        return persist;
    }

    public boolean isShouldCache() {
        return shouldCache;
    }

    public String getIdColumnName() {
        return idColumnName != null ? idColumnName : DEFAULT_ID_KEY;
    }

    public int getItemId() {
        return itemId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.idColumnName);
        dest.writeSerializable(this.dataClass);
        dest.writeByte(this.persist ? (byte) 1 : (byte) 0);
        dest.writeByte(this.shouldCache ? (byte) 1 : (byte) 0);
        dest.writeInt(this.itemId);
    }

    public static class GetRequestBuilder {
        private int mItemId;
        private boolean mShouldCache, mPersist;
        private String mIdColumnName, mUrl;
        private Class mDataClass;

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
        public GetRequest build() {
            return new GetRequest(this);
        }
    }
}