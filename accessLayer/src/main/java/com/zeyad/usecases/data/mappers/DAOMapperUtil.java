package com.zeyad.usecases.data.mappers;

public abstract class DAOMapperUtil implements IDAOMapperUtil {

    @Override
    public abstract IDAOMapper getDataMapper(Class dataClass);
}
