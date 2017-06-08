package com.zeyad.usecases.stores;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import st.lowlevel.storo.Storo;

/**
 * @author by ZIaDo on 6/5/17.
 */
public class MemoryStore {
    final private Gson gson;
//    final Map<Class, Map<Class, >> mapOfIds;

    MemoryStore(Gson gson) {
        this.gson = gson;
//        mapOfIds = new HashMap<>();
//        Map<Object, Class> objectClassMap = new HashMap<>();
//        objectClassMap.put()
//        mapOfIds.put(RequiredModel.class, );
    }

    public <M> Maybe<M> getObject(String itemId, @NonNull Class dataClass) {
        String key = dataClass.getSimpleName() + itemId;
        return Maybe.defer(() -> Storo.contains(key) && !Storo.hasExpired(key).execute() ?
                Utils.getInstance().<M>toFlowable(Storo.get(key, dataClass).async()).firstElement() :
                Maybe.error(new IllegalAccessException("Cache Miss!")));
    }

    void cacheObject(String idColumnName, @NonNull JSONObject jsonObject, @NonNull Class dataClass) {
        String className = dataClass.getSimpleName();
        String key = className + jsonObject.optString(idColumnName);
        Storo.put(key, gson.fromJson(jsonObject.toString(), dataClass))
                .setExpiry(Config.getCacheAmount(), Config.getCacheTimeUnit())
                .execute();
        Log.d("MemoryStore", className + " cached!, id = " + key);
    }

    void deleteList(List<Long> ids, @NonNull Class dataClass) {
        String className = dataClass.getSimpleName();
        Observable.fromIterable(ids)
                .map(id -> className + String.valueOf(id))
                .filter(Storo::contains)
                .doOnEach(stringNotification -> {
                    String key = stringNotification.getValue();
                    Log.d("MemoryStore", className + " " + (Storo.delete(key) ? "" : "not ") +
                            "deleted!, id = " + key);
                })
                .blockingSubscribe();
    }

    void cacheList(String idColumnName, @NonNull JSONArray jsonArray, @NonNull Class dataClass) {
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            cacheObject(idColumnName, jsonArray.optJSONObject(i), dataClass);
        }
    }
}
