package com.zeyad.genericusecase.data.utils;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.sql.Date;
import java.text.SimpleDateFormat;
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
                //json array can not be converted into array of id ints.
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
    public static <T> JSONArray convertToJsonArray(@NonNull List<T> hashMap) {
        final JSONArray jsonArray = new JSONArray();
        for (int i = 0, hashMapSize = hashMap.size(); i < hashMapSize; i++)
            jsonArray.put(hashMap.get(i));
        return jsonArray;
    }

    public static ContentValues objectToContentValues(Object o) throws IllegalAccessException {
        ContentValues cv = new ContentValues();
        Field[] fields = o.getClass().getFields();
        for (int i = 0, fieldsLength = fields.length; i < fieldsLength; i++) {
            Field field = fields[i];
            Object value = field.get(o);
            //check if compatible with contentvalues
            if (value instanceof Double || value instanceof Integer || value instanceof String
                    || value instanceof Boolean || value instanceof Long || value instanceof Float
                    || value instanceof Short) {
                cv.put(field.getName(), value.toString());
                Log.d("CVLOOP", field.getName() + ":" + value.toString());
            } else if (value instanceof Date)
                cv.put(field.getName(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) value));
        }
        return cv;
    }

    public static JSONObject contentValueToJSONObject(ContentValues contentValues) {
        HashMap<String, Object> set = new HashMap<>(contentValues.size());
        for (Map.Entry<String, Object> stringObjectEntry : contentValues.valueSet())
            set.put(stringObjectEntry.getKey(), stringObjectEntry.getValue());
        return new JSONObject(set);
    }

    public static JSONArray contentValuesToJSONArray(ContentValues[] contentValues) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0, contentValuesLength = contentValues.length; i < contentValuesLength; i++)
            jsonArray.put(contentValueToJSONObject(contentValues[i]));
        return jsonArray;
    }
}
