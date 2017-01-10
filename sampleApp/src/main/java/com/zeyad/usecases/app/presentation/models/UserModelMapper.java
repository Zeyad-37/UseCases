package com.zeyad.usecases.app.presentation.models;

import com.zeyad.usecases.data.mappers.DAOMapper;

public class UserModelMapper extends DAOMapper<UserModel, UserRealm> {
    private static UserModelMapper sAutoMap_UserModelMapper;

    public UserModelMapper() {
        super();
    }

    public static UserModel staticMapToDomainManual(UserRealm autoMap_Usermodel) {
        if (UserRealm.isEmpty(autoMap_Usermodel)) {
            return new UserModel();
        }
        UserModel userModel = new UserModel();
        userModel.setLogin(autoMap_Usermodel.getLogin());
        userModel.setId(autoMap_Usermodel.getId());
        userModel.setAvatarUrl(autoMap_Usermodel.getAvatarUrl());
        userModel.setGravatarId(autoMap_Usermodel.getGravatarId());
        userModel.setUrl(autoMap_Usermodel.getUrl());
        userModel.setHtmlUrl(autoMap_Usermodel.getHtmlUrl());
        userModel.setFollowersUrl(autoMap_Usermodel.getFollowersUrl());
        userModel.setFollowingUrl(autoMap_Usermodel.getFollowingUrl());
        userModel.setGistsUrl(autoMap_Usermodel.getGistsUrl());
        userModel.setStarredUrl(autoMap_Usermodel.getStarredUrl());
        userModel.setSubscriptionsUrl(autoMap_Usermodel.getSubscriptionsUrl());
        userModel.setOrganizationsUrl(autoMap_Usermodel.getOrganizationsUrl());
        userModel.setReposUrl(autoMap_Usermodel.getReposUrl());
        userModel.setEventsUrl(autoMap_Usermodel.getEventsUrl());
        userModel.setReceivedEventsUrl(autoMap_Usermodel.getReceivedEventsUrl());
        userModel.setType(autoMap_Usermodel.getType());
//    userModel.setSiteAdmin(autoMap_Usermodel.getSiteAdmin());
        return userModel;
    }

    public static UserModelMapper getInstance() {
        if (sAutoMap_UserModelMapper == null) {
            sAutoMap_UserModelMapper = new UserModelMapper();
        }
        return sAutoMap_UserModelMapper;
    }

    @Override
    public UserModel mapToDomainManual(UserRealm autoMap_Usermodel) {
        return staticMapToDomainManual(autoMap_Usermodel);
    }
}
