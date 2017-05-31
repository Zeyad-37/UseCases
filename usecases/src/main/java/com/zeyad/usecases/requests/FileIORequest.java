package com.zeyad.usecases.requests;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.zeyad.usecases.Config;

import java.io.File;
import java.util.HashMap;

/** @author zeyad on 7/29/16. */
public class FileIORequest implements Parcelable {

    public static final Parcelable.Creator<FileIORequest> CREATOR =
            new Parcelable.Creator<FileIORequest>() {
                @NonNull
                @Override
                public FileIORequest createFromParcel(@NonNull Parcel source) {
                    return new FileIORequest(source);
                }

                @NonNull
                @Override
                public FileIORequest[] newArray(int size) {
                    return new FileIORequest[size];
                }
            };
    private File file;
    private String url, key;
    private boolean onWifi, whileCharging, queuable;
    private Class dataClass;
    private HashMap<String, Object> parameters;

    public FileIORequest() {}

    private FileIORequest(@NonNull Builder uploadRequestBuilder) {
        url = uploadRequestBuilder.url;
        onWifi = uploadRequestBuilder.onWifi;
        whileCharging = uploadRequestBuilder.whileCharging;
        queuable = uploadRequestBuilder.queuable;
        file = uploadRequestBuilder.file;
        key = uploadRequestBuilder.key;
        parameters = uploadRequestBuilder.parameters;
        dataClass = uploadRequestBuilder.dataClass;
    }

    private FileIORequest(@NonNull Parcel in) {
        this.file = (File) in.readSerializable();
        this.url = in.readString();
        this.key = in.readString();
        this.onWifi = in.readByte() != 0;
        this.whileCharging = in.readByte() != 0;
        this.queuable = in.readByte() != 0;
        this.dataClass = (Class) in.readSerializable();
        this.parameters = (HashMap<String, Object>) in.readSerializable();
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

    public File getFile() {
        return file;
    }

    @NonNull
    public String getKey() {
        return key != null ? key : "";
    }

    @NonNull
    public HashMap<String, Object> getParameters() {
        return parameters != null ? parameters : new HashMap<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeSerializable(this.file);
        dest.writeString(this.url);
        dest.writeString(this.key);
        dest.writeByte(this.onWifi ? (byte) 1 : (byte) 0);
        dest.writeByte(this.whileCharging ? (byte) 1 : (byte) 0);
        dest.writeByte(this.queuable ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.dataClass);
        dest.writeSerializable(this.parameters);
    }

    public static class Builder {

        private File file;
        private String url, key;
        private boolean onWifi, whileCharging, queuable;
        private Class dataClass;
        private HashMap<String, Object> parameters;

        public Builder(String url, File file) {
            this.url = url;
            this.file = file;
        }

        @NonNull
        public Builder url(String url) {
            this.url = Config.getBaseURL() + url;
            return this;
        }

        @NonNull
        public Builder fullUrl(String url) {
            this.url = url;
            return this;
        }

        @NonNull
        public Builder dataClass(Class dataClass) {
            this.dataClass = dataClass;
            return this;
        }

        @NonNull
        public Builder onWifi(boolean onWifi) {
            this.onWifi = onWifi;
            return this;
        }

        @NonNull
        public Builder key(String key) {
            this.key = key;
            return this;
        }

        @NonNull
        public Builder payLoad(HashMap<String, Object> parameters) {
            this.parameters = parameters;
            return this;
        }

        @NonNull
        public Builder queuable(boolean queuable) {
            this.queuable = queuable;
            return this;
        }

        @NonNull
        public Builder whileCharging(boolean whileCharging) {
            this.whileCharging = whileCharging;
            return this;
        }

        @NonNull
        public FileIORequest build() {
            return new FileIORequest(this);
        }
    }
}

