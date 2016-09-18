package com.zeyad.genericusecase.data.utils;

import com.zeyad.genericusecase.data.mappers.EntityMapper;

public interface IEntityMapperUtil {
    EntityMapper getDataMapper(Class dataClass);
}
