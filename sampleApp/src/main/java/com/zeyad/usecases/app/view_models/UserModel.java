package com.zeyad.usecases.app.view_models;

import com.google.gson.annotations.SerializedName;
import com.zeyad.usecases.annotations.AutoMap;
import com.zeyad.usecases.annotations.FindMapped;

import java.util.List;

import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * @author zeyad on 12/1/16.
 */
@AutoMap
public class UserModel {

    public static final String ID = "id", COVER_URL = "coverUrl", FULL_NAME = "fullName",
            DESCRIPTION = "description", FOLLOWERS = "followers", EMAIL = "email", REPOS = "repos";
    @PrimaryKey
    @SerializedName(ID)
    private int id;
    @SerializedName(COVER_URL)
    private String coverUrl;
    @SerializedName(FULL_NAME)
    private String fullName;
    @SerializedName(DESCRIPTION)
    private String description;
    @SerializedName(FOLLOWERS)
    private int followers;
    @Ignore
    @SerializedName(EMAIL)
    private String email;
    @FindMapped
    @SerializedName(REPOS)
    private List<RepoModel> repos;

    public UserModel() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoverUrl() {
        return this.coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFollowers() {
        return this.followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<RepoModel> getRepos() {
        return repos;
    }

    public void setRepos(List<RepoModel> repos) {
        this.repos = repos;
    }
}
