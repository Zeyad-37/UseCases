package com.zeyad.usecases.data.mappers;

public abstract class DaoMapperUtil implements IDaoMapperUtil {

    @Override
    public abstract IDaoMapper getDataMapper(Class dataClass);
}
