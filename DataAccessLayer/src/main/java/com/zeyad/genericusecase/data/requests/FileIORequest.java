package com.zeyad.genericusecase.data.requests;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.HashMap;

/**
 * @author zeyad on 7/29/16.
 */
public class FileIORequest {

    File file;
    String url, key;
    boolean onWifi, whileCharging, queuable;
    Class dataClass, presentationClass;
    HashMap<String, Object> parameters;

    public FileIORequest() {
    }

    public FileIORequest(@NonNull FileIORequestBuilder uploadRequestBuilder) {
        url = uploadRequestBuilder.getUrl();
        onWifi = uploadRequestBuilder.isOnWifi();
        whileCharging = uploadRequestBuilder.isWhileCharging();
        queuable = uploadRequestBuilder.isQueuable();
        file = uploadRequestBuilder.getFile();
        key = uploadRequestBuilder.getKey();
        parameters = uploadRequestBuilder.getParameters();
        dataClass = uploadRequestBuilder.getDataClass();
        presentationClass = uploadRequestBuilder.getPresentationClass();
    }

    public FileIORequest(String url, File file, String key, HashMap<String, Object> parameters, boolean onWifi, boolean whileCharging, Class presentationClass,
                         Class dataClass) {
        this.onWifi = onWifi;
        this.whileCharging = whileCharging;
        this.presentationClass = presentationClass;
        this.dataClass = dataClass;
        this.url = url;
        this.key = key;
        this.parameters = parameters;
        this.file = file;
    }

    public String getUrl() {
        return url;
    }

    public boolean onWifi() {
        return onWifi;
    }

    public boolean isWhileCharging() {
        return whileCharging;
    }

    public boolean isQueuable() {
        return queuable;
    }

    public Class getDataClass() {
        return dataClass;
    }

    public Class getPresentationClass() {
        if (presentationClass == null) {
            return dataClass;
        }
        return presentationClass;
    }

    public File getFile() {
        return file;
    }

    public String getKey() {
        if (key == null) {
            return "";
        }
        return key;
    }

    public HashMap<String, Object> getParameters() {
        if (parameters == null) {
            return new HashMap<>();
        }
        return parameters;
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