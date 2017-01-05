package com.zeyad.usecases.app.models;

import com.google.gson.annotations.SerializedName;
import com.zeyad.usecases.annotations.AutoMap;

import org.parceler.Parcel;

import io.realm.annotations.PrimaryKey;

/**
 * @author zeyad on 12/1/16.
 */
@Parcel
@AutoMap
public class UserModel {

    public static final String LOGIN = "login";
    public static final String ID = "id";
    public static final String AVATAR_URL = "avatarUrl";
    public static final String GRAVATAR_ID = "gravatarId";
    public static final String URL = "url";
    public static final String HTML_URL = "htmlUrl";
    public static final String FOLLOWERS_URL = "followersUrl";
    public static final String FOLLOWING_URL = "following_url";
    public static final String GISTS_URL = "gists_url";
    public static final String STARRED_URL = "starred_url";
    public static final String SUBSCRIPTIONS_URL = "subscriptions_url";
    public static final String ORGANIZATIONS_URL = "organizations_url";
    public static final String REPOS_URL = "repos_url";
    public static final String EVENTS_URL = "events_url";
    public static final String RECEIVED_EVENTS_URL = "received_events_url";
    public static final String TYPE = "type";
    public static final String SITE_ADMIN = "site_admin";
    @PrimaryKey
    @SerializedName(LOGIN)
    String login;
    @SerializedName(ID)
    int id;
    @SerializedName(AVATAR_URL)
    String avatarUrl;
    @SerializedName(GRAVATAR_ID)
    String gravatarId;
    @SerializedName(URL)
    String url;
    @SerializedName(HTML_URL)
    String htmlUrl;
    @SerializedName(FOLLOWERS_URL)
    String followersUrl;
    @SerializedName(FOLLOWING_URL)
    String followingUrl;
    @SerializedName(GISTS_URL)
    String gistsUrl;
    @SerializedName(STARRED_URL)
    String starredUrl;
    @SerializedName(SUBSCRIPTIONS_URL)
    String subscriptionsUrl;
    @SerializedName(ORGANIZATIONS_URL)
    String organizationsUrl;
    @SerializedName(REPOS_URL)
    String reposUrl;
    @SerializedName(EVENTS_URL)
    String eventsUrl;
    @SerializedName(RECEIVED_EVENTS_URL)
    String receivedEventsUrl;
    @SerializedName(TYPE)
    String type;
    @SerializedName(SITE_ADMIN)
    boolean siteAdmin;

    public UserModel() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getGravatarId() {
        return gravatarId;
    }

    public void setGravatarId(String gravatarId) {
        this.gravatarId = gravatarId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getFollowersUrl() {
        return followersUrl;
    }

    public void setFollowersUrl(String followersUrl) {
        this.followersUrl = followersUrl;
    }

    public String getFollowingUrl() {
        return followingUrl;
    }

    public void setFollowingUrl(String followingUrl) {
        this.followingUrl = followingUrl;
    }

    public String getGistsUrl() {
        return gistsUrl;
    }

    public void setGistsUrl(String gistsUrl) {
        this.gistsUrl = gistsUrl;
    }

    public String getStarredUrl() {
        return starredUrl;
    }

    public void setStarredUrl(String starredUrl) {
        this.starredUrl = starredUrl;
    }

    public String getSubscriptionsUrl() {
        return subscriptionsUrl;
    }

    public void setSubscriptionsUrl(String subscriptionsUrl) {
        this.subscriptionsUrl = subscriptionsUrl;
    }

    public String getOrganizationsUrl() {
        return organizationsUrl;
    }

    public void setOrganizationsUrl(String organizationsUrl) {
        this.organizationsUrl = organizationsUrl;
    }

    public String getReposUrl() {
        return reposUrl;
    }

    public void setReposUrl(String reposUrl) {
        this.reposUrl = reposUrl;
    }

    public String getEventsUrl() {
        return eventsUrl;
    }

    public void setEventsUrl(String eventsUrl) {
        this.eventsUrl = eventsUrl;
    }

    public String getReceivedEventsUrl() {
        return receivedEventsUrl;
    }

    public void setReceivedEventsUrl(String receivedEventsUrl) {
        this.receivedEventsUrl = receivedEventsUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSiteAdmin() {
        return siteAdmin;
    }

    public void setSiteAdmin(boolean siteAdmin) {
        this.siteAdmin = siteAdmin;
    }
}
