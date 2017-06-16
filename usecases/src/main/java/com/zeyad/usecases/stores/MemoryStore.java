package com.zeyad.usecases.stores;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.Single;
import st.lowlevel.storo.Storo;

/**
 * @author by ZIaDo on 6/5/17.
 */
public class MemoryStore {
    private static final String TAG = "MemoryStore";
    private final Gson gson;
    private final Map<Class, Set<String>> mapOfIds;

    MemoryStore(Gson gson) {
        this.gson = gson;
        mapOfIds = new HashMap<>();
    }

    public <M> Single<M> getItem(String itemId, @NonNull Class dataClass) {
        String key = dataClass.getSimpleName() + itemId;
        return Single.defer(() -> {
            if (isValid(key)) {
                return Utils.getInstance().<M>toFlowable(Storo.get(key, dataClass).async())
                        .firstElement().toSingle();
            } else {
                return Single.error(new IllegalAccessException("Cache Miss!"));
            }
        });
    }

    public <M> Single<List<M>> getAllItems(@NonNull Class<M> dataClass) {
        final boolean[] missed = new boolean[1];
        Set<String> stringSet = mapOfIds.get(dataClass);
        if (stringSet == null)
            return Single.error(new IllegalAccessException("Cache Miss!"));
        List<M> result = Observable.fromIterable(stringSet)
                .filter((key) -> {
                    if (isValid(key))
                        return true;
                    else {
                        missed[0] = true;
                        return false;
                    }
                })
                .filter(s -> !missed[0])
                .map(key -> Storo.get(key, dataClass).execute())
                .toList(missed[0] ? 0 : stringSet.size())
                .blockingGet();
        return missed[0] ? Single.error(new IllegalAccessException("Cache Miss!")) : Single.just(result);
    }

    void cacheObject(String idColumnName, @NonNull JSONObject jsonObject, @NonNull Class dataClass) {
        String className = dataClass.getSimpleName();
        String key = className + jsonObject.optString(idColumnName);
        Storo.put(key, gson.fromJson(jsonObject.toString(), dataClass))
                .setExpiry(Config.getCacheAmount(), Config.getCacheTimeUnit())
                .execute();
        addKey(dataClass, key);
        Log.d(TAG, className + " cached!, id = " + key);
    }

    void cacheList(String idColumnName, @NonNull JSONArray jsonArray, @NonNull Class dataClass) {
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            cacheObject(idColumnName, jsonArray.optJSONObject(i), dataClass);
        }
    }

    void deleteList(@NonNull List<String> ids, @NonNull Class dataClass) {
        if (ids.isEmpty()) {
            Log.e(TAG, "deleteList", new IllegalArgumentException("List of ids is empty!"));
            return;
        }
        String className = dataClass.getSimpleName();
        Observable.fromIterable(ids)
                .map(id -> className + String.valueOf(id))
                .filter((key) -> {
                    if (isValid(key))
                        return true;
                    else {
                        removeKey(dataClass, key);
                        return false;
                    }
                })
                .doOnEach(stringNotification -> {
                    String key = stringNotification.getValue();
                    removeKey(dataClass, key);
                    Log.d(TAG, String.format("%s %s deleted!, id = %s", className,
                            (Storo.delete(key) ? "" : "not "), key));
                })
                .blockingSubscribe();
    }

    private void addKey(Class dataType, String key) {
        if (!mapOfIds.containsKey(dataType)) {
            mapOfIds.put(dataType, new HashSet<>(Collections.singleton(key)));
        } else {
            mapOfIds.get(dataType).add(key);
        }
    }

    private void removeKey(Class dataType, String key) {
        if (mapOfIds.containsKey(dataType)) {
            mapOfIds.get(dataType).remove(key);
        }
    }

    private boolean isValid(String key) {
        return Storo.contains(key) && !Storo.hasExpired(key).execute();
    }
}
