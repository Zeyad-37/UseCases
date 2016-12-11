package com.zeyad.usecases.data.mappers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.zeyad.usecases.Config;

import java.util.ArrayList;
import java.util.List;

import io.realm.internal.OutOfMemoryError;

public class EntityDataMapper implements EntityMapper<Object, Object> {
    public Gson gson;

    public EntityDataMapper() {
        gson = Config.getGson();
    }

    @Override
    public Object transformToRealm(Object item, @NonNull Class dataClass) {
        if (item != null)
            if (item instanceof List)
                return transformAllToRealm((List) item, dataClass);
            else
                try {
                    return dataClass.cast(gson.fromJson(gson.toJson(item), dataClass));
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    return null;
                }
        return null;
    }

    /**
     * @param list Objects to be transformed.
     * @return {@link java.util.List } if valid {.} otherwise null.
     */
    @NonNull
    @Override
    public List<Object> transformAllToRealm(@NonNull List list, @NonNull Class dataClass) {
        List<Object> objects = new ArrayList<>(list.size());
        Object object;
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            object = transformToRealm(list.get(i), dataClass);
            if (object != null)
                objects.add(object);
        }
        return objects;
    }

    @Nullable
    @Override
    public Object transformToDomain(@NonNull Object item) {
        if (item instanceof List)
            return transformAllToDomain((List) item);
        else
            try {
                return item.getClass().cast(gson.fromJson(gson.toJson(item), item.getClass()));
//                return gson.fromJson(gson.toJson(item), item.getClass());
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return null;
            }
    }

    @Nullable
    @Override
    public List<Object> transformAllToDomain(@NonNull List<Object> objectList) {
        List<Object> list = new ArrayList<>(objectList.size());
        Object object;
        for (int i = 0, objectListSize = objectList.size(); i < objectListSize; i++) {
            object = transformToDomain(objectList.get(i));
            if (object != null)
                list.add(object);
        }
        return list;
    }

    @Nullable
    @Override
    public Object transformToDomain(@Nullable Object object, @NonNull Class domainClass) {
        if (object != null)
            if (object instanceof List)
                return transformAllToDomain((List) object, domainClass);
            else
                try {
                    return domainClass.cast(gson.fromJson(gson.toJson(object), domainClass));
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    return null;
                }
        return null;
    }

    /**
     * Transform a {Entity}ies into an {Model}s.
     *
     * @param list Objects to be transformed.
     * @return {Model} if valid {Entity} otherwise null.
     */
    @NonNull
    @Override
    public List<Object> transformAllToDomain(@NonNull List list, @NonNull Class domainClass) {
        List<Object> domainObjects = new ArrayList<>();
        Object object;
        for (int i = 0, size = list.size(); i < size; i++) {
            object = transformToDomain(list.get(i), domainClass);
            if (object != null)
                domainObjects.add(object);
        }
        return domainObjects;
    }
}
