package com.zeyad.genericusecase.data.mappers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

public class EntityDataMapper implements EntityMapper<Object, Object> {
    private Gson gson;

    public EntityDataMapper() {
        gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(@NonNull FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
    }

    @Override
    public Object transformToRealm(Object item, @NonNull Class dataClass) {
        return gson.fromJson(gson.toJson(item), dataClass);
    }

    @NonNull
    @Override
    public List transformAllToRealm(@NonNull List list, @NonNull Class dataClass) {
        List<Object> objects = new ArrayList<>();
        Object object;
        for (Object item : list) {
            object = transformToRealm(item, dataClass);
            if (object != null)
                objects.add(object);
        }
        return objects;
    }

    @Nullable
    @Override
    public Object transformToDomain(@NonNull Object tenderoRealmModel) {
        return gson.fromJson(gson.toJson(tenderoRealmModel), tenderoRealmModel.getClass());
    }

    @Nullable
    @Override
    public List<Object> transformAllToDomain(@NonNull List<Object> objectList) {
        List<Object> list = new ArrayList<>();
        for (Object realmObject : objectList)
            list.add(transformToDomain(realmObject));
        for (int i = 0; i < list.size(); i++)
            if (list.get(i) == null)
                list.remove(i);
        return list;
    }

    @Nullable
    @Override
    public Object transformToDomain(@Nullable Object object, @NonNull Class domainClass) {
        if (object != null)
            return domainClass.cast(gson.fromJson(gson.toJson(object), domainClass));
        return null;
    }

    /**
     * Transform a {Entity} into an {Model}.
     *
     * @param list Objects to be transformed.
     * @return {Model} if valid {Entity} otherwise null.
     */
    @NonNull
    @Override
    public List<Object> transformAllToDomain(@NonNull List list, @NonNull Class domainClass) {
        List domainObjects = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
            domainObjects.add(transformToDomain(list.get(i), domainClass));
        return domainObjects;
    }
}
