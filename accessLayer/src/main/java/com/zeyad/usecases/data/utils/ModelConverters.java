package com.zeyad.usecases.data.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ModelConverters {

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
    public static JSONArray convertToJsonArray(@NonNull List list) {
        final JSONArray jsonArray = new JSONArray();
        for (int i = 0, hashMapSize = list.size(); i < hashMapSize; i++)
            jsonArray.put(list.get(i));
        return jsonArray;
    }
}
