package com.zeyad.usecases.app.presentation.screens.user_list;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author zeyad on 1/10/17.
 */
@Parcel
public class UserRealm extends RealmObject {

    public static final String LOGIN = "login", ID = "id", AVATAR_URL = "avatar_url", GRAVATAR_ID = "gravatar_id",
            URL = "url", HTML_URL = "html_url", FOLLOWERS_URL = "followers_url", FOLLOWING_URL = "following_url",
            GISTS_URL = "gists_url", STARRED_URL = "starred_url", SUBSCRIPTIONS_URL = "subscriptions_url",
            ORGANIZATIONS_URL = "organizations_url", REPOS_URL = "repos_url", EVENTS_URL = "events_url",
            RECEIVED_EVENTS_URL = "received_events_url", TYPE = "type", SITE_ADMIN = "site_admin";
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

    public UserRealm() {
    }

    public static boolean isEmpty(UserRealm automapUsermodel) {
        return automapUsermodel == null ||
                (automapUsermodel.login == null &&
                        automapUsermodel.avatarUrl == null &&
                        automapUsermodel.gravatarId == null &&
                        automapUsermodel.url == null &&
                        automapUsermodel.htmlUrl == null &&
                        automapUsermodel.followersUrl == null &&
                        automapUsermodel.followingUrl == null &&
                        automapUsermodel.gistsUrl == null &&
                        automapUsermodel.starredUrl == null &&
                        automapUsermodel.subscriptionsUrl == null &&
                        automapUsermodel.organizationsUrl == null &&
                        automapUsermodel.reposUrl == null &&
                        automapUsermodel.eventsUrl == null &&
                        automapUsermodel.receivedEventsUrl == null &&
                        automapUsermodel.type == null);
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
