package com.zeyad.usecases.utils;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.zeyad.usecases.data.mappers.IDAOMapper;

import org.mockito.internal.invocation.AbstractAwareMethod;

import java.util.ArrayList;
import java.util.List;

public class TestModelViewModelMapper implements IDAOMapper, AbstractAwareMethod {

    @Override
    public Object mapToRealm(Object item, @NonNull Class dataClass) {
        final Gson gson = new Gson();
        final String serializedData = gson.toJson(item);
        return gson.fromJson(serializedData, dataClass);
    }

    @NonNull
    @Override
    public List mapAllToRealm(@NonNull List list, @NonNull Class dataClass) {
        List<Object> objects = new ArrayList<>();
        Object o;
        for (Object object : list) {
            o = mapToRealm(object, dataClass);
            if (o != null)
                objects.add(o);
        }
        return objects;
    }

    @Override
    public Object mapToDomain(Object realmModels, @NonNull Class domainClass) {
        if (realmModels instanceof TestModel) {
            return mapToDomain(realmModels, domainClass);
        }
        if (domainClass.isInstance(realmModels)) {
            return realmModels;
        }
        return null;
    }

    @NonNull
    @Override
    public List mapAllToDomain(@NonNull List realmModels, @NonNull Class domainClass) {
        List<Object> list = new ArrayList<>();
        for (Object object : realmModels) {
            final Object domainObject = mapToDomain(object, domainClass);
            if (domainObject != null) {
                list.add(domainObject);
            }
        }
        return list;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }
}
