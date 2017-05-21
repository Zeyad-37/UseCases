package com.zeyad.usecases.requests;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.zeyad.usecases.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author zeyad on 7/29/16.
 */
public class PostRequest implements Parcelable {
    public static final String POST = "post", DELETE = "delete", PUT = "put", PATCH = "patch";
    public static final Parcelable.Creator<PostRequest> CREATOR = new Parcelable.Creator<PostRequest>() {
        @NonNull
        @Override
        public PostRequest createFromParcel(@NonNull Parcel source) {
            return new PostRequest(source);
        }

        @NonNull
        @Override
        public PostRequest[] newArray(int size) {
            return new PostRequest[size];
        }
    };
    private static final String DEFAULT_ID_KEY = "id";
    private String url, idColumnName, method;
    private Class dataClass;
    private boolean onWifi, whileCharging, persist, queuable;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private HashMap<String, Object> keyValuePairs;
    private Object object;

    public PostRequest(@NonNull Builder builder) {
        url = builder.url;
        dataClass = builder.dataClass;
        persist = builder.persist;
        onWifi = builder.onWifi;
        whileCharging = builder.whileCharging;
        queuable = builder.queuable;
        keyValuePairs = builder.keyValuePairs;
        jsonObject = builder.jsonObject;
        jsonArray = builder.jsonArray;
        idColumnName = builder.idColumnName;
        method = builder.method;
        object = builder.object;
    }

    protected PostRequest(@NonNull Parcel in) {
        this.url = in.readString();
        this.idColumnName = in.readString();
        this.method = in.readString();
        this.dataClass = (Class) in.readSerializable();
        this.persist = in.readByte() != 0;
        this.whileCharging = in.readByte() != 0;
        this.onWifi = in.readByte() != 0;
        this.queuable = in.readByte() != 0;
        this.jsonObject = in.readParcelable(JSONObject.class.getClassLoader());
        this.jsonArray = in.readParcelable(JSONArray.class.getClassLoader());
        this.keyValuePairs = (HashMap<String, Object>) in.readSerializable();
        this.object = in.readParcelable(Object.class.getClassLoader());
    }

    public JSONObject getObjectBundle() {
        JSONObject jsonObject = new JSONObject();
        if (object != null)
            try {
                return new JSONObject(Config.getGson().toJson(object));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        else if (this.jsonObject != null)
            jsonObject = this.jsonObject;
        else if (keyValuePairs != null)
            jsonObject = new JSONObject(keyValuePairs);
        return jsonObject;
    }

    public JSONArray getArrayBundle() {
        if (jsonArray != null)
            return jsonArray;
        else if (keyValuePairs != null) {
            final JSONArray jsonArray = new JSONArray();
            for (Object object : keyValuePairs.values())
                jsonArray.put(object);
            return jsonArray;
        } else return new JSONArray();
    }

    @NonNull
    public String getUrl() {
        return url != null ? url : "";
    }

    public Class getDataClass() {
        return dataClass;
    }

    public boolean isPersist() {
        return persist;
    }

    public boolean isQueuable() {
        return queuable;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public HashMap<String, Object> getKeyValuePairs() {
        return keyValuePairs;
    }

    public Object getObject() {
        return object;
    }

    @NonNull
    public String getIdColumnName() {
        return idColumnName != null ? idColumnName : DEFAULT_ID_KEY;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.idColumnName);
        dest.writeString(this.method);
        dest.writeSerializable(this.dataClass);
        dest.writeByte(this.persist ? (byte) 1 : (byte) 0);
        dest.writeByte(this.whileCharging ? (byte) 1 : (byte) 0);
        dest.writeByte(this.onWifi ? (byte) 1 : (byte) 0);
        dest.writeByte(this.queuable ? (byte) 1 : (byte) 0);
        dest.writeParcelable((Parcelable) this.jsonObject, flags);
        dest.writeParcelable((Parcelable) this.jsonArray, flags);
        dest.writeSerializable(this.keyValuePairs);
        dest.writeParcelable((Parcelable) this.object, flags);
    }

    public static class Builder {
        Object object;
        JSONArray jsonArray;
        JSONObject jsonObject;
        HashMap<String, Object> keyValuePairs;
        String url, idColumnName, method;
        Class dataClass;
        boolean persist, queuable, onWifi, whileCharging;

        public Builder(Class dataClass, boolean persist) {
            this.dataClass = dataClass;
            this.persist = persist;
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
        public Builder queuable() {
            queuable = true;
            return this;
        }

        @NonNull
        public Builder onWifi() {
            onWifi = true;
            return this;
        }

        @NonNull
        public Builder whileCharging() {
            whileCharging = true;
            return this;
        }

        @NonNull
        public Builder idColumnName(String idColumnName) {
            this.idColumnName = idColumnName;
            return this;
        }

        @NonNull
        public Builder payLoad(Object object) {
            this.object = object;
            return this;
        }

        @NonNull
        public Builder payLoad(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            return this;
        }

        @NonNull
        public Builder payLoad(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
            return this;
        }

        @NonNull
        public Builder payLoad(HashMap<String, Object> hashMap) {
            keyValuePairs = hashMap;
            return this;
        }

        @NonNull
        public Builder method(String method) {
            this.method = method;
            return this;
        }

        @NonNull
        public PostRequest build() {
            return new PostRequest(this);
        }
    }
}
