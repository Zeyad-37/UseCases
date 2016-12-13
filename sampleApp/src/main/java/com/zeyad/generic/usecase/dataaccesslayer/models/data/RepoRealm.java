package com.zeyad.generic.usecase.dataaccesslayer.models.data;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author zeyad on 11/29/16.
 */

public class RepoRealm extends RealmObject {

    @PrimaryKey
    @SerializedName("html_url")
    private String url;
    private String name;

    public RepoRealm() {
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
