package com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_list.models.repo.mapper;

import android.support.annotation.NonNull;

import com.zeyad.genericusecase.data.mappers.EntityDataMapper;
import com.zeyad.genericusecase.data.mappers.EntityMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zeyad on 11/29/16.
 */

public class RepoMapper extends EntityDataMapper implements EntityMapper<Object, Object> {

    public RepoMapper() {
        super();
    }

    @Override
    public Object transformToDomain(@NonNull Object o) {
        if (!(o instanceof Boolean))
            try {
//                return transformToDomainHelper((RepoRealm) o);
            } catch (Exception e) {
                e.printStackTrace();
                return gson.fromJson(gson.toJson(o), o.getClass());
            }
        return o;
    }

    @Override
    public Object transformToDomain(Object object, @NonNull Class domainClass) {
        if (object != null)
            try {
//                return transformToDomainHelper(new Gson().fromJson(new Gson().toJson((object)), RepoRealm.class));
            } catch (Exception e) {
                return super.transformToDomain(object, domainClass);
            }
        return null;
    }

    @Override
    public List<Object> transformAllToDomain(@NonNull List<Object> objectList) {
        List<Object> list = new ArrayList<>(objectList.size());
        for (int i = 0, objectListSize = objectList.size(); i < objectListSize; i++)
            list.add(transformToDomain(objectList.get(i)));
        for (int i = 0, size = list.size(); i < size; i++)
            if (list.get(i) == null)
                list.remove(i);
        return list;
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
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
            objects.add(transformToDomain(list.get(i), domainClass));
        for (int i = 0; i < objects.size(); i++)
            if (objects.get(i) == null)
                objects.remove(i);
        return objects;
    }
}
