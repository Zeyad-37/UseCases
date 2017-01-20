package com.zeyad.usecases.data.mappers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.zeyad.usecases.Config;

import java.util.ArrayList;
import java.util.List;

public abstract class DAOMapper<VM, DM> implements IDAOMapper {
    public Gson gson;

    public DAOMapper() {
        gson = Config.getGson();
    }

    public abstract VM mapToDomainManual(DM object);

    @Override
    public Object mapToRealm(Object item, @NonNull Class dataClass) {
        if (item != null)
            if (item instanceof List)
                return mapAllToRealm((List) item, dataClass);
            else try {
                return gson.fromJson(gson.toJson(item), dataClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return new Object();
    }

    /**
     * @param list Objects to be transformed.
     * @return {@link java.util.List } if valid {.} otherwise empty List.
     */
    @NonNull
    @Override
    public List mapAllToRealm(@NonNull List list, @NonNull Class dataClass) {
        List<Object> objects = new ArrayList<>(list.size());
        for (int i = 0, listSize = list.size(); i < listSize; i++)
            objects.add(mapToRealm(list.get(i), dataClass));
        return objects;
    }

    @Nullable
    @Override
    public Object mapToDomain(@Nullable Object object, @NonNull Class domainClass) {
        if (object != null)
            if (object instanceof List)
                return mapAllToDomain((List) object, domainClass);
            else return mapToDomainHelper(object, domainClass);
        return new Object();
    }

    /**
     * Transform a {Entity}ies into an {Model}s.
     *
     * @param list Objects to be transformed.
     * @return {Model} if valid {Entity} otherwise empty Object.
     */
    @NonNull
    @Override
    public List mapAllToDomain(@NonNull List list, @NonNull Class domainClass) {
        List<Object> objects = new ArrayList<>(list.size());
        for (int i = 0, size = list.size(); i < size; i++)
            objects.add(mapToDomain(list.get(i), domainClass));
        return objects;
    }

    @Nullable
    private Object mapToDomainHelper(@Nullable Object object, @NonNull Class domainClass) {
        try {
            return gson.fromJson(gson.toJson(object), domainClass);
        } catch (Exception e1) {
            e1.printStackTrace();
            try {
                return mapToDomainManual((DM) object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Object();
    }
}
