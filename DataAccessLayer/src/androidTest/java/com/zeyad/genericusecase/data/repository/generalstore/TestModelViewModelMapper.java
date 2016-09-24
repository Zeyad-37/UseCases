package com.zeyad.genericusecase.data.repository.generalstore;

import com.google.gson.Gson;
import com.zeyad.genericusecase.data.db.realm_test_models.TestModel;
import com.zeyad.genericusecase.data.mappers.EntityMapper;

import org.mockito.internal.invocation.AbstractAwareMethod;

import java.util.ArrayList;
import java.util.List;

public class TestModelViewModelMapper implements EntityMapper<Object, Object>, AbstractAwareMethod {

    @Override
    public Object transformToRealm(Object item, Class dataClass) {
        final Gson gson = new Gson();
        final String serializedData = gson.toJson(item);
        return gson.fromJson(serializedData, dataClass);
    }

    @Override
    public List<Object> transformAllToRealm(List<Object> list, Class dataClass) {
        List<Object> objects = new ArrayList<>();
        Object o;
        for (Object object : list) {
            o = transformToRealm(object, dataClass);
            if (o != null)
                objects.add(o);
        }
        return objects;
    }

    @Override
    public Object transformToDomain(Object tenderoRealmModel) {
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
    public List<Object> transformAllToDomain(List<Object> tenderoRealmModels) {
        List<Object> domainList = new ArrayList<>();
        for (Object object : tenderoRealmModels) {
            domainList.add(transformToDomain(object));
        }
        return domainList;
    }

    @Override
    public Object transformToDomain(Object userRealmModel, Class domainClass) {
        if (userRealmModel instanceof TestModel) {
            return transformToDomain(userRealmModel);
        }
        if (domainClass.isInstance(userRealmModel)) {
            return userRealmModel;
        }
        return null;
    }

    @Override
    public List<Object> transformAllToDomain(List<Object> tenderoRealmModels, Class domainClass) {
        List<Object> list = new ArrayList<>();
        for (Object object : tenderoRealmModels) {
            final Object domainObject = transformToDomain(object, domainClass);
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
