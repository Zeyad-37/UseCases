package com.zeyad.usecases.app.models;

import com.google.gson.annotations.SerializedName;
import com.zeyad.usecases.annotations.AutoMap;
import com.zeyad.usecases.annotations.FindMapped;

import io.realm.annotations.PrimaryKey;

/**
 * @author zeyad on 11/29/16.
 */
@AutoMap
public class RepoModel {

    private String name;
    @PrimaryKey
    @SerializedName("html_url")
    private String url;
    @FindMapped
    @SerializedName("user")
    private UserModel userModel;

    public RepoModel() {
        this.name = "";
        this.url = "";
    }

    public RepoModel(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
