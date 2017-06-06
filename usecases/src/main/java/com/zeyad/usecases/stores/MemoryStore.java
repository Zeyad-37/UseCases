package com.zeyad.usecases.stores;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Maybe;
import st.lowlevel.storo.Storo;

/**
 * @author by ZIaDo on 6/5/17.
 */
public class MemoryStore {
    final private Gson gson;

    MemoryStore(Gson gson) {
        this.gson = gson;
    }

    public <M> Maybe<M> getObject(Long itemIdL, String itemIdS, @NonNull Class dataClass) {
        String key = dataClass.getSimpleName() + (itemIdL == null ? itemIdS : String.valueOf(itemIdL));
        return Config.isWithCache() && Storo.contains(key) && !Storo.hasExpired(key).execute() ?
                Utils.getInstance().<M>toFlowable(Storo.get(key, dataClass).async()).firstElement() :
                Maybe.error(new IllegalAccessException("Cache Miss!"));
//                Maybe.empty();
    }

    void cacheObject(String idColumnName, @NonNull JSONObject jsonObject, @NonNull Class dataClass) {
        Storo.put(dataClass.getSimpleName() + jsonObject.optString(idColumnName),
                gson.fromJson(jsonObject.toString(), dataClass))
                .setExpiry(Config.getCacheAmount(), Config.getCacheTimeUnit())
                .execute();
    }

    void deleteList(JSONArray jsonArray, @NonNull Class dataClass) {
        List<Long> convertToListOfId = Utils.getInstance().convertToListOfId(jsonArray);
        int convertToListOfIdSize = convertToListOfId != null ? convertToListOfId.size() : 0;
        for (int i = 0; i < convertToListOfIdSize; i++) {
            Storo.delete(dataClass.getSimpleName() + convertToListOfId.get(i));
        }
    }

//    public void cacheList(String idColumnName, @NonNull JSONArray jsonArray, @NonNull Class dataClass) {
//        int size = jsonArray.length();
//        for (int i = 0; i < size; i++) {
//            cacheObject(idColumnName, jsonArray.optJSONObject(i), dataClass);
//        }
//    }
}
