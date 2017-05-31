package com.zeyad.usecases.mapper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.zeyad.usecases.Config;

import java.util.ArrayList;
import java.util.List;

public final class DAOMapper {
    private static DAOMapper sDAOMapper;
    private final Gson gson;

    private DAOMapper() {
        gson = Config.getGson();
    }

    public static DAOMapper getInstance() {
        if (sDAOMapper == null) {
            sDAOMapper = new DAOMapper();
        }
        return sDAOMapper;
    }

    @NonNull
    public <M> M mapTo(@Nullable Object object, @NonNull Class domainClass) {
        return (M) gson.fromJson(gson.toJson(object), domainClass);
    }

    /**
     * Transform a {Entity}ies into an {Model}s.
     *
     * @param list Objects to be transformed.
     * @return {Model} if valid {Entity} otherwise empty Object.
     */
    @NonNull
    public <M> M mapAllTo(@NonNull List list, @NonNull Class domainClass) {
        int size = list.size();
        List<Object> objects = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            objects.add(mapTo(list.get(i), domainClass));
        }
        return (M) objects;
    }
}
