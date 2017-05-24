package com.zeyad.usecases.app.screens.user_list;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * @author zeyad on 1/10/17.
 */
//@Parcel
@Entity(tableName = "User")
public class User {

    static final String LOGIN = "login";
    private static final String ID = "id", AVATAR_URL = "avatar_url";
    @PrimaryKey
    @SerializedName(LOGIN)
    @ColumnInfo(name = LOGIN)
    String login;

    @SerializedName(ID)
    @ColumnInfo(name = ID)
    int id;

    @SerializedName(AVATAR_URL)
    @ColumnInfo(name = AVATAR_URL)
    String avatarUrl;

    public User() {
    }

    public User(String login, int id, String avatarUrl) {
        this.login = login;
        this.id = id;
        this.avatarUrl = avatarUrl;
    }

    public static boolean isEmpty(User user) {
        return user == null || (user.login == null && user.avatarUrl == null);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id && (login != null ? login.equals(user.login) : user.login == null
                && (avatarUrl != null ? avatarUrl.equals(user.avatarUrl) : user.avatarUrl == null));
    }

    @Override
    public int hashCode() {
        int result = login != null ? login.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + (avatarUrl != null ? avatarUrl.hashCode() : 0);
        return result;
    }
}
