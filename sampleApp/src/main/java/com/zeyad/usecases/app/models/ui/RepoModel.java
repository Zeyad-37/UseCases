package com.zeyad.usecases.app.models.ui;

import com.google.gson.annotations.SerializedName;

/**
 * @author zeyad on 11/29/16.
 */

public class RepoModel {

    private String name;
    @SerializedName("html_url")
    private String url;

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
}
