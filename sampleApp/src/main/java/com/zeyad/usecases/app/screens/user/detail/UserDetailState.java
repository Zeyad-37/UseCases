package com.zeyad.usecases.app.screens.user.detail;

import android.os.Parcel;
import android.os.Parcelable;

import com.zeyad.usecases.app.screens.user.list.User;
import com.zeyad.usecases.app.utils.Utils;

import org.parceler.Transient;

import java.util.List;

/**
 * @author zeyad on 1/25/17.
 */
public class UserDetailState implements Parcelable {
    public static final Creator<UserDetailState> CREATOR = new Creator<UserDetailState>() {
        @Override
        public UserDetailState createFromParcel(Parcel in) {
            return new UserDetailState(in);
        }

        @Override
        public UserDetailState[] newArray(int size) {
            return new UserDetailState[size];
        }
    };
    boolean isTwoPane;
    String userLogin;
    @Transient
    List<Repository> repos;

    UserDetailState() {
        userLogin = null;
        repos = null;
        isTwoPane = false;
    }

    private UserDetailState(Builder builder) {
        isTwoPane = builder.isTwoPane;
        userLogin = builder.user;
        repos = builder.repos;
    }

    protected UserDetailState(Parcel in) {
        isTwoPane = in.readByte() != 0;
        userLogin = in.readString();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isTwoPane ? 1 : 0));
        dest.writeString(userLogin);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    boolean isTwoPane() {
        return isTwoPane;
    }

    String getUserLogin() {
        return userLogin;
    }

    List<Repository> getRepos() {
        return repos;
    }

    User getOwner() {
        if (Utils.isNotEmpty(repos))
            return repos.get(0).getOwner();
        else
            throw new IllegalAccessError("Repo list is empty");
    }

    @Override
    public int hashCode() {
        int result = (isTwoPane ? 1 : 0);
        result = 31 * result + (userLogin != null ? userLogin.hashCode() : 0);
        result = 31 * result + (repos != null ? repos.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserDetailState))
            return false;
        UserDetailState that = (UserDetailState) o;
        return isTwoPane == that.isTwoPane && (userLogin != null ? userLogin.equals(that.userLogin)
                : that.userLogin == null && (repos != null ? repos.equals(that.repos) : that.repos == null));
    }

    public static class Builder {
        List<Repository> repos;
        String user;
        boolean isTwoPane;

        Builder() {
        }

        public Builder setRepos(List<Repository> value) {
            repos = value;
            return this;
        }

        public Builder setIsTwoPane(boolean value) {
            isTwoPane = value;
            return this;
        }

        public Builder setUser(String value) {
            user = value;
            return this;
        }

        public UserDetailState build() {
            return new UserDetailState(this);
        }
    }
}
