package com.zeyad.usecases.app.models.ui;

import com.google.gson.annotations.SerializedName;

/**
 * @author zeyad on 12/1/16.
 */
public class UserModel {

    public static final String ID = "id", COVER_URL = "cover_url", FULL_NAME = "full_name",
            DESCRIPTION = "description", FOLLOWERS = "followers", EMAIL = "email";

    @SerializedName(ID)
    private int id;
    @SerializedName(COVER_URL)
    private String cover_url;
    @SerializedName(FULL_NAME)
    private String full_name;
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

    public String getCover_url() {
        return this.cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getFull_name() {
        return this.full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
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
