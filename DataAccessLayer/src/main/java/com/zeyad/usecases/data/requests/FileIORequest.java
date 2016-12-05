package com.zeyad.usecases.data.requests;

import android.support.annotation.NonNull;

import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;

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

    public FileIORequest(String url, File file, String key, HashMap<String, Object> parameters,
                         boolean onWifi, boolean whileCharging, Class presentationClass,
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
        return presentationClass != null ? presentationClass : dataClass;
    }

    public File getFile() {
        return file;
    }

    public String getKey() {
        return key != null ? key : "";
    }

    public HashMap<String, Object> getParameters() {
        return parameters != null ? parameters : new HashMap<>();
    }

    public static class FileIORequestBuilder {

        private File file;
        private String url, key;
        private boolean onWifi, whileCharging, queuable;
        private Class dataClass, presentationClass;
        private HashMap<String, Object> parameters;

        public FileIORequestBuilder(String url, File file) {
            this.url = url;
            this.file = file;
        }

        @NonNull
        public FileIORequestBuilder url(String url) {
            this.url = DataUseCaseFactory.getBaseURL() + url;
            return this;
        }

        @NonNull
        public FileIORequestBuilder fullUrl(String url) {
            this.url = url;
            return this;
        }

        @NonNull
        public FileIORequestBuilder presentationClass(Class presentationClass) {
            this.presentationClass = presentationClass;
            return this;
        }

        @NonNull
        public FileIORequestBuilder dataClass(Class dataClass) {
            this.dataClass = dataClass;
            return this;
        }

        @NonNull
        public FileIORequestBuilder onWifi(boolean onWifi) {
            this.onWifi = onWifi;
            return this;
        }

        @NonNull
        public FileIORequestBuilder key(String key) {
            this.key = key;
            return this;
        }

        @NonNull
        public FileIORequestBuilder payLoad(HashMap<String, Object> parameters) {
            this.parameters = parameters;
            return this;
        }

        @NonNull
        public FileIORequestBuilder queuable(boolean queuable) {
            this.queuable = queuable;
            return this;
        }

        @NonNull
        public FileIORequestBuilder whileCharging(boolean whileCharging) {
            this.whileCharging = whileCharging;
            return this;
        }

        @NonNull
        public FileIORequest build() {
            return new FileIORequest(this);
        }

        public File getFile() {
            return file;
        }

        public String getUrl() {
            return url;
        }

        public boolean isOnWifi() {
            return onWifi;
        }

        public boolean isQueuable() {
            return queuable;
        }

        public boolean isWhileCharging() {
            return whileCharging;
        }

        public Class getDataClass() {
            return dataClass;
        }

        public Class getPresentationClass() {
            return presentationClass;
        }

        public String getKey() {
            return key;
        }

        public HashMap<String, Object> getParameters() {
            return parameters;
        }
    }
}