package com.zeyad.genericusecase.data.utils;

import com.zeyad.genericusecase.data.mappers.EntityMapper;

public abstract class EntityMapperUtil implements IEntityMapperUtil {

    @Override
    public abstract EntityMapper getDataMapper(Class dataClass);
}
