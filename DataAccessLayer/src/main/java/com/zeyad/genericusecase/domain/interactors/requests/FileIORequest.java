package com.zeyad.genericusecase.domain.interactors.requests;

import android.support.annotation.NonNull;

import java.io.File;

/**
 * @author zeyad on 7/29/16.
 */
public class FileIORequest {

    private File mFile;
    private String mUrl;
    private boolean mOnWifi, mWhileCharging, mQueuable;
    private Class mDataClass, mPresentationClass;

    public FileIORequest() {
    }

    public FileIORequest(@NonNull FileIORequestBuilder fileIORequestBuilder) {
        mUrl = fileIORequestBuilder.getUrl();
        mOnWifi = fileIORequestBuilder.isOnWifi();
        mWhileCharging = fileIORequestBuilder.isWhileCharging();
        mQueuable = fileIORequestBuilder.isQueuable();
        mFile = fileIORequestBuilder.getFile();
        mDataClass = fileIORequestBuilder.getDataClass();
        mPresentationClass = fileIORequestBuilder.getPresentationClass();
    }

    public FileIORequest(String url, File file, boolean onWifi, boolean whileCharging, Class presentationClass,
                         Class dataClass) {
        mOnWifi = onWifi;
        mWhileCharging = whileCharging;
        mPresentationClass = presentationClass;
        mDataClass = dataClass;
        mUrl = url;
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

    public static class FileIORequestBuilder {

        private File mFile;
        private String mUrl;
        private boolean mOnWifi, mWhileCharging, mQueuable;
        private Class mDataClass, mPresentationClass;

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
    }
}