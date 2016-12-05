package com.zeyad.usecases.data.utils;

import com.zeyad.usecases.data.mappers.EntityMapper;

public abstract class EntityMapperUtil implements IEntityMapperUtil {

    @Override
    public abstract EntityMapper getDataMapper(Class dataClass);
}
