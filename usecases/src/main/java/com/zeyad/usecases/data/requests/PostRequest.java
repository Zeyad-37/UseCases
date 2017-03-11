package com.zeyad.usecases.data.requests;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.repository.DataRepository;

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
        @Override
        public PostRequest createFromParcel(Parcel source) {
            return new PostRequest(source);
        }

        @Override
        public PostRequest[] newArray(int size) {
            return new PostRequest[size];
        }
    };
    private String url, idColumnName, method;
    private Class dataClass, presentationClass;
    private boolean onWifi, whileCharging, persist, queuable;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private HashMap<String, Object> keyValuePairs;
    private Object object;

    public PostRequest(@NonNull PostRequestBuilder postRequestBuilder) {
        url = postRequestBuilder.url;
        dataClass = postRequestBuilder.dataClass;
        presentationClass = postRequestBuilder.presentationClass;
        persist = postRequestBuilder.persist;
        onWifi = postRequestBuilder.onWifi;
        whileCharging = postRequestBuilder.whileCharging;
        queuable = postRequestBuilder.queuable;
        keyValuePairs = postRequestBuilder.keyValuePairs;
        jsonObject = postRequestBuilder.jsonObject;
        jsonArray = postRequestBuilder.jsonArray;
        idColumnName = postRequestBuilder.idColumnName;
        method = postRequestBuilder.method;
        object = postRequestBuilder.object;
    }

    public PostRequest(String idColumnName, String url, JSONObject keyValuePairs,
                       Class presentationClass, Class dataClass, boolean persist) {
        this.idColumnName = idColumnName;
        jsonObject = keyValuePairs;
        this.persist = persist;
        this.presentationClass = presentationClass;
        this.dataClass = dataClass;
        this.url = url;
    }

    // for test
    public PostRequest(String idColumnName, String url, JSONArray keyValuePairs,
                       Class presentationClass, Class dataClass, boolean persist) {
        this.idColumnName = idColumnName;
        jsonArray = keyValuePairs;
        this.persist = persist;
        this.presentationClass = presentationClass;
        this.dataClass = dataClass;
        this.url = url;
    }

    public PostRequest(String idColumnName, String url, HashMap<String, Object> keyValuePairs,
                       Class presentationClass, Class dataClass, boolean persist) {
        this.idColumnName = idColumnName;
        this.keyValuePairs = keyValuePairs;
        this.persist = persist;
        this.presentationClass = presentationClass;
        this.dataClass = dataClass;
        this.url = url;
    }

    protected PostRequest(Parcel in) {
        this.url = in.readString();
        this.idColumnName = in.readString();
        this.method = in.readString();
        this.dataClass = (Class) in.readSerializable();
        this.presentationClass = (Class) in.readSerializable();
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

    public String getUrl() {
        return url != null ? url : "";
    }

    public Class getDataClass() {
        return dataClass;
    }

    public Class getPresentationClass() {
        return presentationClass != null ? presentationClass : dataClass;
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

    public String getIdColumnName() {
        return idColumnName != null ? idColumnName : DataRepository.DEFAULT_ID_KEY;
    }

    public String getMethod() {
        return method;
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
        dest.writeSerializable(this.dataClass);
        dest.writeSerializable(this.presentationClass);
        dest.writeByte(this.persist ? (byte) 1 : (byte) 0);
        dest.writeByte(this.whileCharging ? (byte) 1 : (byte) 0);
        dest.writeByte(this.onWifi ? (byte) 1 : (byte) 0);
        dest.writeByte(this.queuable ? (byte) 1 : (byte) 0);
        dest.writeParcelable((Parcelable) this.jsonObject, flags);
        dest.writeParcelable((Parcelable) this.jsonArray, flags);
        dest.writeSerializable(this.keyValuePairs);
        dest.writeParcelable((Parcelable) this.object, flags);
    }

    public static class PostRequestBuilder {
        Object object;
        JSONArray jsonArray;
        JSONObject jsonObject;
        HashMap<String, Object> keyValuePairs;
        String url, idColumnName, method;
        Class dataClass, presentationClass;
        boolean persist, queuable, onWifi, whileCharging;

        public PostRequestBuilder(Class dataClass, boolean persist) {
            this.dataClass = dataClass;
            this.persist = persist;
        }

        @NonNull
        public PostRequestBuilder url(String url) {
            this.url = Config.getBaseURL() + url;
            return this;
        }

        @NonNull
        public PostRequestBuilder fullUrl(String url) {
            this.url = url;
            return this;
        }

        @NonNull
        public PostRequestBuilder queuable() {
            queuable = true;
            return this;
        }

        @NonNull
        public PostRequestBuilder onWifi() {
            onWifi = true;
            return this;
        }

        @NonNull
        public PostRequestBuilder whileCharging() {
            whileCharging = true;
            return this;
        }

        @NonNull
        public PostRequestBuilder presentationClass(Class presentationClass) {
            this.presentationClass = presentationClass;
            return this;
        }

        @NonNull
        public PostRequestBuilder idColumnName(String idColumnName) {
            this.idColumnName = idColumnName;
            return this;
        }

        @NonNull
        public PostRequestBuilder payLoad(Object object) {
            this.object = object;
            return this;
        }

        @NonNull
        public PostRequestBuilder payLoad(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            return this;
        }

        @NonNull
        public PostRequestBuilder payLoad(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
            return this;
        }

        @NonNull
        public PostRequestBuilder payLoad(HashMap<String, Object> hashMap) {
            keyValuePairs = hashMap;
            return this;
        }

        @NonNull
        public PostRequestBuilder method(String method) {
            this.method = method;
            return this;
        }

        @NonNull
        public PostRequest build() {
            return new PostRequest(this);
        }
    }
}
