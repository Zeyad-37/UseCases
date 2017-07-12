package com.zeyad.usecases.requests;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.zeyad.usecases.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * @author zeyad on 7/29/16.
 */
public class PostRequest implements Parcelable {

    public static final String POST = "post", DELETE = "delete", PUT = "put", PATCH = "patch";
    public static final Parcelable.Creator<PostRequest> CREATOR = new Parcelable.Creator<PostRequest>() {
        @Override
        public PostRequest createFromParcel(Parcel source) {
            return new PostRequest(source);
        }

        @Override
        public PostRequest[] newArray(int size) {
            return new PostRequest[size];
        }
    };
    private static final String DEFAULT_ID_KEY = "id";
    private final String url, idColumnName, method, payload;
    private final Class requestType, responseType, idType;
    private final boolean onWifi, whileCharging, persist, queuable, cache;
    private Object object;

    public PostRequest(@NonNull Builder builder) {
        url = builder.url;
        requestType = builder.requestType;
        responseType = builder.responseType;
        persist = builder.persist;
        onWifi = builder.onWifi;
        whileCharging = builder.whileCharging;
        queuable = builder.queuable;
        idColumnName = builder.idColumnName;
        idType = builder.idType;
        method = builder.method;
        object = builder.object;
        cache = builder.cache;
        String objectString = getObjectBundle(builder.jsonObject, builder.keyValuePairs).toString();
        String arrayString = getArrayBundle(builder.jsonArray, builder.keyValuePairs).toString();
        String objectBundle = objectString == null ? "" : objectString;
        String arrayBundle = arrayString == null ? "" : arrayString;
        payload = objectBundle.replaceAll("\\{", "").replaceAll("\\}", "").isEmpty() ?
                  arrayBundle : objectBundle;
    }

    protected PostRequest(Parcel in) {
        this.url = in.readString();
        this.idColumnName = in.readString();
        this.method = in.readString();
        this.requestType = (Class) in.readSerializable();
        this.responseType = (Class) in.readSerializable();
        this.idType = (Class) in.readSerializable();
        this.onWifi = in.readByte() != 0;
        this.whileCharging = in.readByte() != 0;
        this.persist = in.readByte() != 0;
        this.queuable = in.readByte() != 0;
        this.cache = in.readByte() != 0;
        this.payload = in.readString();
    }

    public JSONObject getObjectBundle() throws JSONException {
        return new JSONObject(payload);
    }

    public JSONArray getArrayBundle() throws JSONException {
        return new JSONArray(payload);
    }

    private JSONObject getObjectBundle(JSONObject jsonObject, HashMap<String, Object> keyValuePairs) {
        JSONObject result = new JSONObject();
        if (object != null) {
            try {
                return new JSONObject(Config.getGson().toJson(object));
            } catch (JSONException e) {
                //                Log.e("PostRequest", "", e);
                return result;
            }
        } else if (jsonObject != null) {
            result = jsonObject;
        } else if (keyValuePairs != null) {
            result = new JSONObject(keyValuePairs);
        }
        return result;
    }

    private JSONArray getArrayBundle(JSONArray jsonArray, HashMap<String, Object> keyValuePairs) {
        if (jsonArray != null) {
            return jsonArray;
        } else if (keyValuePairs != null) {
            final JSONArray result = new JSONArray();
            for (Object item : keyValuePairs.values()) {
                result.put(item);
            }
            return result;
        } else if (object instanceof List) {
            final JSONArray result = new JSONArray();
            List ids = (List) object;
            for (Object item : ids) {
                result.put(item);
            }
            return result;
        } else if (object instanceof Object[]) {
            final JSONArray result = new JSONArray();
            Object[] ids = (Object[]) object;
            for (Object item : ids) {
                result.put(item);
            }
            return result;
        } else {
            return new JSONArray();
        }
    }

    @NonNull
    public String getUrl() {
        return url == null ? "" : url;
    }

    public Class getRequestType() {
        return requestType;
    }

    public boolean isPersist() {
        return persist;
    }

    public boolean isCache() {
        return cache;
    }

    public boolean isQueuable() {
        return queuable;
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

    public boolean isOnWifi() {
        return onWifi;
    }

    public boolean isWhileCharging() {
        return whileCharging;
    }

    public Class getIdType() {
        return idType;
    }

    @NonNull
    public Class getResponseType() {
        return responseType == null ? requestType : responseType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.idColumnName);
        dest.writeString(this.method);
        dest.writeSerializable(this.requestType);
        dest.writeSerializable(this.responseType);
        dest.writeSerializable(this.idType);
        dest.writeByte(this.onWifi ? (byte) 1 : (byte) 0);
        dest.writeByte(this.whileCharging ? (byte) 1 : (byte) 0);
        dest.writeByte(this.persist ? (byte) 1 : (byte) 0);
        dest.writeByte(this.queuable ? (byte) 1 : (byte) 0);
        dest.writeByte(this.cache ? (byte) 1 : (byte) 0);
        dest.writeString(this.payload);
    }

    public static class Builder {
        Object object;
        JSONArray jsonArray;
        JSONObject jsonObject;
        HashMap<String, Object> keyValuePairs;
        String url, idColumnName, method;
        Class requestType, responseType, idType;
        boolean persist, queuable, cache, onWifi, whileCharging;

        public Builder(Class requestType, boolean persist) {
            this.requestType = requestType;
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
        public Builder responseType(Class responseType) {
            this.responseType = responseType;
            return this;
        }

        @NonNull
        public Builder queuable(boolean onWifi, boolean whileCharging) {
            queuable = true;
            this.onWifi = onWifi;
            this.whileCharging = whileCharging;
            return this;
        }

        @NonNull
        public Builder cache() {
            cache = true;
            return this;
        }

        @NonNull
        public Builder idColumnName(String idColumnName, Class type) {
            this.idColumnName = idColumnName;
            idType = type;
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
