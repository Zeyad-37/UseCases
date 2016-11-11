package com.zeyad.genericusecase.data.requests;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.HashMap;

/**
 * @author zeyad on 7/29/16.
 */
public class FileIORequest {

    File mFile;
    String mUrl, mKey;
    boolean mOnWifi, mWhileCharging, mQueuable;
    Class mDataClass, mPresentationClass;
    HashMap<String, Object> mParameters;

    public FileIORequest() {
    }

    public FileIORequest(@NonNull FileIORequestBuilder uploadRequestBuilder) {
        mUrl = uploadRequestBuilder.getUrl();
        mOnWifi = uploadRequestBuilder.isOnWifi();
        mWhileCharging = uploadRequestBuilder.isWhileCharging();
        mQueuable = uploadRequestBuilder.isQueuable();
        mFile = uploadRequestBuilder.getFile();
        mKey = uploadRequestBuilder.getKey();
        mParameters = uploadRequestBuilder.getParameters();
        mDataClass = uploadRequestBuilder.getDataClass();
        mPresentationClass = uploadRequestBuilder.getPresentationClass();
    }

    public FileIORequest(String url, File file, String key, HashMap<String, Object> parameters, boolean onWifi, boolean whileCharging, Class presentationClass,
                         Class dataClass) {
        mOnWifi = onWifi;
        mWhileCharging = whileCharging;
        mPresentationClass = presentationClass;
        mDataClass = dataClass;
        mUrl = url;
        mKey = key;
        mParameters = parameters;
        mFile = file;
    }

    public String getUrl() {
        return mUrl;
    }

    public boolean onWifi() {
        return mOnWifi;
    }

    public boolean isWhileCharging() {
        return mWhileCharging;
    }

    public boolean isQueuable() {
        return mQueuable;
    }

    public Class getDataClass() {
        return mDataClass;
    }

    public Class getPresentationClass() {
        return mPresentationClass;
    }

    public File getFile() {
        return mFile;
    }

    public String getKey() {
        return mKey;
    }

    public HashMap<String, Object> getParameters() {
        return mParameters;
    }

    public static class FileIORequestBuilder {

        private File mFile;
        private String mUrl, mKey;
        private boolean mOnWifi, mWhileCharging, mQueuable;
        private Class mDataClass, mPresentationClass;
        private HashMap<String, Object> mParameters;

        public FileIORequestBuilder(String url, File file) {
            mUrl = url;
            mFile = file;
        }

        @NonNull
        public FileIORequestBuilder url(String url) {
            mUrl = url;
            return this;
        }

        @NonNull
        public FileIORequestBuilder presentationClass(Class presentationClass) {
            mPresentationClass = presentationClass;
            return this;
        }

        @NonNull
        public FileIORequestBuilder dataClass(Class dataClass) {
            mDataClass = dataClass;
            return this;
        }

        @NonNull
        public FileIORequestBuilder onWifi(boolean onWifi) {
            mOnWifi = onWifi;
            return this;
        }

        @NonNull
        public FileIORequestBuilder key(String key) {
            mKey = key;
            return this;
        }

        @NonNull
        public FileIORequestBuilder payLoad(HashMap<String, Object> parameters) {
            mParameters = parameters;
            return this;
        }

        @NonNull
        public FileIORequestBuilder queuable(boolean queuable) {
            mQueuable = queuable;
            return this;
        }

        @NonNull
        public FileIORequestBuilder whileCharging(boolean whileCharging) {
            mWhileCharging = whileCharging;
            return this;
        }

        @NonNull
        public FileIORequest build() {
            return new FileIORequest(this);
        }

        public File getFile() {
            return mFile;
        }

        public String getUrl() {
            return mUrl;
        }

        public boolean isOnWifi() {
            return mOnWifi;
        }

        public boolean isQueuable() {
            return mQueuable;
        }

        public boolean isWhileCharging() {
            return mWhileCharging;
        }

        public Class getDataClass() {
            return mDataClass;
        }

        public Class getPresentationClass() {
            return mPresentationClass;
        }

        public String getKey() {
            return mKey;
        }

        public HashMap<String, Object> getParameters() {
            return mParameters;
        }
    }
}