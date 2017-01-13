package com.zeyad.usecases.data.mappers;

public interface IDAOMapperFactory {
    IDAOMapper getDataMapper(Class dataClass);
}
