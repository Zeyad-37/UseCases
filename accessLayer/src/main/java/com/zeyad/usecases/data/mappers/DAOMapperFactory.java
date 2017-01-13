package com.zeyad.usecases.data.mappers;

public abstract class DAOMapperFactory implements IDAOMapperFactory {

    @Override
    public abstract IDAOMapper getDataMapper(Class dataClass);
}
