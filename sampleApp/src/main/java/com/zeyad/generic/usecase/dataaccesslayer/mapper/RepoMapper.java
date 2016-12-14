package com.zeyad.generic.usecase.dataaccesslayer.mapper;

import com.zeyad.generic.usecase.dataaccesslayer.models.data.RepoRealm;
import com.zeyad.generic.usecase.dataaccesslayer.models.ui.RepoModel;
import com.zeyad.usecases.data.mappers.DAOMapper;

/**
 * @author zeyad on 11/29/16.
 */
public class RepoMapper extends DAOMapper<RepoModel, RepoRealm> {

    public RepoMapper() {
        super();
    }

    @Override
    public RepoModel mapToDomainManual(RepoRealm repoRealm) {
        RepoModel repoModel = new RepoModel();
        repoModel.setName(repoRealm.getName());
        repoModel.setUrl(repoRealm.getUrl());
        return repoModel;
    }
}
