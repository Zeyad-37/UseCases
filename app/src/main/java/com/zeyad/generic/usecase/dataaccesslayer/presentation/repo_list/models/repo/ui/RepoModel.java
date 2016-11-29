package com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_list.models.repo.ui;

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

    public void setUrl(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
