package com.zeyad.usecases.utils;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmModule;

/**
 * @author by ZIaDo on 2/13/17.
 */
@RealmModule
public class TestRealmModel implements RealmModel {
    @SerializedName("id")
    @PrimaryKey
    private int id;
    @SerializedName("value")
    private String value;

    public TestRealmModel(int id, String value) {
        this.id = id;
        this.value = value;
        if (id <= 0)
            throw new IllegalArgumentException("id should be greater than 0");
    }

    public TestRealmModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return "TestRealmObject{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestRealmObject)) return false;
        TestRealmObject testRealmObject = (TestRealmObject) o;
        return getId() == testRealmObject.getId() && getValue().equals(testRealmObject.getValue());
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getValue().hashCode();
        return result;
    }
}
