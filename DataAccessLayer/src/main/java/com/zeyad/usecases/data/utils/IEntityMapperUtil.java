package com.zeyad.usecases.data.utils;

import com.zeyad.usecases.data.mappers.EntityMapper;

public interface IEntityMapperUtil {
    EntityMapper getDataMapper(Class dataClass);
}
