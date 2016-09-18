package com.zeyad.genericusecase.domain.mapper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModelDataMapper {
    private Gson gson;

    public ModelDataMapper(Gson gson) {
        this.gson = gson;
    }

    /**
     * Transform an Object into another Object.
     *
     * @param object Object to be transformed.
     * @return Object if valid, otherwise null.
     */
    @Nullable
    public Object transformToPresentation(@Nullable Object object, @NonNull Class presentationClass) {
        if (object != null)
            if (!(object instanceof Boolean))
                if (object instanceof Collection)
                    return transformAllToPresentation((List) object, presentationClass);
                else
                    return presentationClass.cast(gson.fromJson(gson.toJson(object), presentationClass));
        return object;
    }

    /**
     * Transform a List into a List
     *
     * @param list Objects to be transformed.
     * @return {@link List} if valid {@link List} otherwise null.
     */
    @NonNull
    public List<Object> transformAllToPresentation(@Nullable List<Object> list, @NonNull Class presentationClass) {
        List<Object> transformedList = new ArrayList<>();
        if (list != null)
            for (int i = 0; i < list.size(); i++)
                transformedList.add(transformToPresentation(list.get(i), presentationClass));
        return transformedList;
    }
}
