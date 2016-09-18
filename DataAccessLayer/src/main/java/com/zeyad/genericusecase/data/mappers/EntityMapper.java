package com.zeyad.genericusecase.data.mappers;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * @author Zeyad on 11/05/16.
 */
public interface EntityMapper<D, R> {

    R transformToRealm(D item, Class dataClass);

    List<R> transformAllToRealm(List<D> list, Class dataClass);

    @Nullable
    D transformToDomain(R tenderoRealmModel);

    @Nullable
    List<D> transformAllToDomain(List<R> tenderoRealmModels);

    @Nullable
    D transformToDomain(R userRealmModel, Class domainClass);

    List<D> transformAllToDomain(List<R> tenderoRealmModels, Class domainClass);
}