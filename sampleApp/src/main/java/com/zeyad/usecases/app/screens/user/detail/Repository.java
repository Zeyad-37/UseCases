package com.zeyad.usecases.app.screens.user.detail;

import org.parceler.Parcel;

import com.google.gson.annotations.SerializedName;
import com.zeyad.usecases.app.screens.user.list.User;

import io.realm.RealmObject;

/**
 * @author zeyad on 1/25/17.
 */
@Parcel
public class Repository extends RealmObject {
    @SerializedName("id")
    int id;
    @SerializedName("name")
    String name;
    @SerializedName("owner")
    User owner;

    public Repository() {
    }

    public static boolean isEmpty(Repository repository) {
        return repository == null || repository.name == null && repository.owner == null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Repository))
            return false;
        Repository that = (Repository) o;
        return id == that.id && (name != null ? name.equals(that.name)
                : that.name == null && (owner != null ? owner.equals(that.owner) : that.owner == null));
    }
}
