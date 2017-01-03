package com.zeyad.usecases.app;

import com.zeyad.usecases.app.mapper.RepoMapper;
import com.zeyad.usecases.app.models.AutoMap_RepoModel;
import com.zeyad.usecases.data.mappers.DAOMapperUtil;
import com.zeyad.usecases.data.mappers.DefaultDAOMapper;
import com.zeyad.usecases.data.mappers.IDAOMapper;

/**
 * @author zeyad on 1/3/17.
 */
// TODO: 1/3/17 Delete!
public class AutoMap_DAOMapperUtil extends DAOMapperUtil {
    @Override
    public IDAOMapper getDataMapper(Class dataClass) {
        if (dataClass == AutoMap_RepoModel.class)
            return new RepoMapper();
        return new DefaultDAOMapper();
    }
}
