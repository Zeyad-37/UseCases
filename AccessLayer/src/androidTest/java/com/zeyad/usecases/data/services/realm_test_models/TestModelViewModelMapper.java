package com.zeyad.usecases.data.services.realm_test_models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.zeyad.usecases.data.mappers.IDaoMapper;

import org.mockito.internal.invocation.AbstractAwareMethod;

import java.util.ArrayList;
import java.util.List;

public class TestModelViewModelMapper implements IDaoMapper, AbstractAwareMethod {

    @Override
    public Object mapToRealm(Object item, @NonNull Class dataClass) {
        final Gson gson = new Gson();
        final String serializedData = gson.toJson(item);
        return gson.fromJson(serializedData, dataClass);
    }

    @NonNull
    @Override
    public List<Object> mapAllToRealm(@NonNull List<Object> list, @NonNull Class dataClass) {
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
    public Object mapToDomain(@Nullable Object tenderoRealmModel) {
        if (tenderoRealmModel == null) {
            throw new IllegalArgumentException("object to convert is null");
        }
        if (!(tenderoRealmModel instanceof TestModel)) {
            throw new IllegalArgumentException("only test realm models can be converted to domain, trying to convert:" + tenderoRealmModel);
        }
        TestModel testModel = (TestModel) tenderoRealmModel;
        TestViewModel testViewModel = new TestViewModel();
        testViewModel.setTestInfo(testModel.getId() + ":" + testModel.getValue());
        return testViewModel;
    }

    @Override
    public List<Object> mapAllToDomain(@NonNull List<Object> tenderoRealmModels) {
        List<Object> domainList = new ArrayList<>();
        for (Object object : tenderoRealmModels) {
            domainList.add(mapToDomain(object));
        }
        return domainList;
    }

    @Override
    public Object mapToDomain(Object userRealmModel, @NonNull Class domainClass) {
        if (userRealmModel instanceof TestModel) {
            return mapToDomain(userRealmModel);
        }
        if (domainClass.isInstance(userRealmModel)) {
            return userRealmModel;
        }
        return null;
    }

    @NonNull
    @Override
    public List<Object> mapAllToDomain(@NonNull List<Object> tenderoRealmModels, @NonNull Class domainClass) {
        List<Object> list = new ArrayList<>();
        for (Object object : tenderoRealmModels) {
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
