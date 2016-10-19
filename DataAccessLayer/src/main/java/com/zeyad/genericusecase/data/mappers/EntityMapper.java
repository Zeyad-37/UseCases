package com.zeyad.genericusecase.data.mappers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * @author Zeyad on 11/05/16.
 */
// TODO: 10/18/16 convert to abstract!
public interface EntityMapper<D, R> {
    /**
     * Transform item to its realm counter part.
     *
     * @param item      item to be converted.
     * @param dataClass Realm Class to be converted to.
     * @return
     */
    R transformToRealm(D item, Class dataClass);

    /**
     * Transform a list items to its realm counter part.
     *
     * @param list      list of items to be converted.
     * @param dataClass Realm Class to be converted to.
     * @return
     */
    @NonNull
    List transformAllToRealm(List<D> list, Class dataClass);

    /**
     * Transforms entity to its domain counter part.
     *
     * @param entity entity to be converted.
     * @return
     */
    @Nullable
    D transformToDomain(R entity);

    /**
     * Transforms entities to its domain counter part.
     *
     * @param entities entities to be converted.
     * @return
     */
    @Nullable
    List transformAllToDomain(List<R> entities);

    /**
     * Transforms realmInstance to its domain counter part.
     *
     * @param realmInstance object to be converted.
     * @param domainClass   Domain class to be converted to.
     * @return
     */
    @Nullable
    D transformToDomain(R realmInstance, Class domainClass);

    /**
     * Transforms realmInstances to its domain counter part.
     *
     * @param realmInstances objects to be converted.
     * @param domainClass    Domain class to be converted to.
     * @return
     */
    @NonNull
    List transformAllToDomain(List<R> realmInstances, Class domainClass);
}