package com.zeyad.usecases.data.mappers;

public abstract class EntityMapperUtil implements IEntityMapperUtil {

    @Override
    public abstract EntityMapper getDataMapper(Class dataClass);
}
