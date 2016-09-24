package com.zeyad.genericusecase.domain.interactors;

import android.support.annotation.NonNull;

import java.io.File;

/**
 * @author zeyad on 7/29/16.
 */
public class FileIORequest {

    File mFile;
    String mUrl;
    boolean mOnWifi, mWhileCharging;
    Class mDataClass, mPresentationClass;

    public FileIORequest() {
    }

    public FileIORequest(@NonNull UploadRequestBuilder uploadRequestBuilder) {
        mUrl = uploadRequestBuilder.getUrl();
        mOnWifi = uploadRequestBuilder.isOnWifi();
        mWhileCharging = uploadRequestBuilder.isWhileCharging();
        mFile = uploadRequestBuilder.getFile();
        mDataClass = uploadRequestBuilder.getDataClass();
        mPresentationClass = uploadRequestBuilder.getPresentationClass();
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

    public Class getDataClass() {
        return mDataClass;
    }

    public Class getPresentationClass() {
        return mPresentationClass;
    }

    public File getFile() {
        return mFile;
    }

    public static class UploadRequestBuilder {

        private File mFile;
        private String mUrl;
        private boolean mOnWifi, mWhileCharging;
        private Class mDataClass, mPresentationClass;

        public UploadRequestBuilder(String url, File file) {
            mUrl = url;
            mFile = file;
        }

        @NonNull
        public UploadRequestBuilder url(String url) {
            mUrl = url;
            return this;
        }

        @NonNull
        public UploadRequestBuilder presentationClass(Class presentationClass) {
            mPresentationClass = presentationClass;
            return this;
        }

        @NonNull
        public UploadRequestBuilder dataClass(Class dataClass) {
            mDataClass = dataClass;
            return this;
        }

        @NonNull
        public UploadRequestBuilder onWifi(boolean onWifi) {
            mOnWifi = onWifi;
            return this;
        }

        @NonNull
        public UploadRequestBuilder whileCharging(boolean whileCharging) {
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