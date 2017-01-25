package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.components.mvvm.BaseModel;

import org.parceler.Parcel;

import java.util.List;

/**
 * @author zeyad on 1/24/17.
 */
@Parcel
public class UserListModel extends BaseModel {

    List<UserRealm> users;
    int yScroll;

    public UserListModel() {
        super(false, null);
        users = null;
    }

    UserListModel(List<UserRealm> users, boolean isLoading, Throwable error) {
        super(isLoading, error);
        this.users = users;
    }

    public static UserListModel onNext(List<UserRealm> users) {
        return new UserListModel(users, false, null);
    }

    public static UserListModel error(Throwable error) {
        return new UserListModel(null, false, error);
    }

    public static UserListModel loading() {
        return new UserListModel(null, true, null);
    }

    List<UserRealm> getUsers() {
        return users;
    }

    public void setUsers(List<UserRealm> users) {
        this.users = users;
    }

    public int getyScroll() {
        return yScroll;
    }

    public void setyScroll(int yScroll) {
        this.yScroll = yScroll;
    }
}
