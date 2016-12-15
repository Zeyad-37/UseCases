package com.zeyad.usecases.app.models.ui;

import com.google.gson.annotations.SerializedName;

/**
 * @author zeyad on 12/1/16.
 */
public class UserModel {

    public static final String ID = "id", COVER_URL = "coverUrl", FULL_NAME = "fullName",
            DESCRIPTION = "description", FOLLOWERS = "followers", EMAIL = "email";

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
    @SerializedName(EMAIL)
    private String email;

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
}
