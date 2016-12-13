package com.zeyad.usecases.data.mappers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.zeyad.usecases.Config;

import java.util.ArrayList;
import java.util.List;

public abstract class DaoMapper implements IDaoMapper {
    public Gson gson;
    private Class dataClass;

    public DaoMapper() {
        gson = Config.getGson();
    }

    public abstract Object mapToDomainManual(Object object);

    // TODO: 12/13/16 Try both Options!
    @Override
    public Object mapToRealm(Object item, @NonNull Class dataClass) {
        this.dataClass = dataClass;
        if (item != null)
            if (item instanceof List)
                return mapAllToRealm((List) item, dataClass);
            else try {
                return dataClass.cast(gson.fromJson(gson.toJson(item), dataClass));
//                return gson.fromJson(gson.toJson(item), dataClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    /**
     * @param list Objects to be transformed.
     * @return {@link java.util.List } if valid {.} otherwise null.
     */
    @NonNull
    @Override
    public List<Object> mapAllToRealm(@NonNull List list, @NonNull Class dataClass) {
        List<Object> objects = new ArrayList<>(list.size());
        Object object;
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            object = mapToRealm(list.get(i), dataClass);
            if (object != null)
                objects.add(object);
        }
        return objects;
    }

    @Nullable
    @Override
    public Object mapToDomain(@NonNull Object item) {
        if (item instanceof List)
            return mapAllToDomain((List) item);
        else return mapToDomainHelper(item, item.getClass());
    }

    @Nullable
    @Override
    public List<Object> mapAllToDomain(@NonNull List<Object> objectList) {
        List<Object> list = new ArrayList<>(objectList.size());
        Object object;
        for (int i = 0, objectListSize = objectList.size(); i < objectListSize; i++) {
            object = mapToDomain(objectList.get(i));
            if (object != null)
                list.add(object);
        }
        return list;
    }

    @Nullable
    @Override
    public Object mapToDomain(@Nullable Object object, @NonNull Class domainClass) {
        if (object != null)
            if (object instanceof List)
                return mapAllToDomain((List) object, domainClass);
            else return mapToDomainHelper(object, domainClass);
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
    public List<Object> mapAllToDomain(@NonNull List list, @NonNull Class domainClass) {
        List<Object> objects = new ArrayList<>(list.size());
        Object object;
        for (int i = 0, size = list.size(); i < size; i++) {
            object = mapToDomain(list.get(i), domainClass);
            if (object != null)
                objects.add(object);
        }
        return objects;
    }

    // TODO: 12/13/16 Try both Options!
    @Nullable
    private Object mapToDomainHelper(@Nullable Object object, @NonNull Class domainClass) {
        try {
//            if (dataClass == null || domainClass == dataClass)// both Pojos
            return domainClass.cast(gson.fromJson(gson.toJson(object), domainClass));
//            else return mapToDomainManual(object);
        } catch (Exception e1) {
            e1.printStackTrace();
            try {
                return mapToDomainManual(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
