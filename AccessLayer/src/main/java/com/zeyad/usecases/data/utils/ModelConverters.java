package com.zeyad.usecases.data.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelConverters {

    @NonNull
    public static JSONObject convertToJsonObject(Map hashMap) {
        return new JSONObject(hashMap);
    }

    @NonNull
    public static JSONArray convertToJsonArray(@NonNull HashMap<String, Object> hashMap) {
        final JSONArray jsonArray = new JSONArray();
        for (Object object : hashMap.values())
            jsonArray.put(object);
        return jsonArray;
    }

    public static String convertToString(@NonNull JSONArray jsonArray) {
        return jsonArray.toString();
    }

    public static String convertToString(@NonNull JSONObject jsonObject) {
        return jsonObject.toString();
    }

    public static String convertToString(Map hashMap) {
        return convertToString(convertToJsonObject(hashMap));
    }

    @Nullable
    public static List<Long> convertToListOfId(@Nullable JSONArray jsonArray) {
        List<Long> idList = null;
        if (jsonArray != null && jsonArray.length() > 0) {
            try {
                jsonArray.getLong(0);
            } catch (JSONException e) {
                return null;
            }
            idList = new ArrayList<>(jsonArray.length());
            for (int i = 0, length = jsonArray.length(); i < length; i++)
                try {
                    idList.add(jsonArray.getLong(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
        return idList;
    }

    @NonNull
    public static JSONArray convertToJsonArray(@NonNull List hashMap) {
        final JSONArray jsonArray = new JSONArray();
        for (int i = 0, hashMapSize = hashMap.size(); i < hashMapSize; i++)
            jsonArray.put(hashMap.get(i));
        return jsonArray;
    }
}
