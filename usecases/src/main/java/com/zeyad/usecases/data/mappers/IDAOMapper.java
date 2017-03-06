package com.zeyad.usecases.data.mappers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * @author Zeyad on 11/05/16.
 */
public interface IDAOMapper {
    /**
     * Transform item to its realm counter part.
     *
     * @param item      item to be converted.
     * @param dataClass Realm Class to be converted to.
     * @return
     */
    Object mapToRealm(Object item, Class dataClass);

    /**
     * Transform a list items to its realm counter part.
     *
     * @param list      list of items to be converted.
     * @param dataClass Realm Class to be converted to.
     * @return
     */
    @NonNull
    List mapAllToRealm(List list, Class dataClass);

    /**
     * Transforms realmInstance to its domain counter part.
     *
     * @param realmInstance object to be converted.
     * @param domainClass   Domain class to be converted to.
     * @return
     */
    @Nullable
    Object mapToDomain(Object realmInstance, Class domainClass);

    /**
     * Transforms realmInstances to its domain counter part.
     *
     * @param realmInstances objects to be converted.
     * @param domainClass    Domain class to be converted to.
     * @return
     */
    @NonNull
    List mapAllToDomain(List realmInstances, Class domainClass);
}