package com.zeyad.usecases.app.mapper;

import com.zeyad.usecases.app.view_models.AutoMap_RepoModel;
import com.zeyad.usecases.app.view_models.RepoModel;
import com.zeyad.usecases.data.mappers.DAOMapper;

/**
 * @author zeyad on 11/29/16.
 */
public class RepoMapper extends DAOMapper<RepoModel, AutoMap_RepoModel> {

    public RepoMapper() {
        super();
    }

    @Override
    public RepoModel mapToDomainManual(AutoMap_RepoModel repoRealm) {
        RepoModel repoModel = new RepoModel();
        repoModel.setName(repoRealm.getName());
        repoModel.setUrl(repoRealm.getUrl());
        return repoModel;
    }
}
