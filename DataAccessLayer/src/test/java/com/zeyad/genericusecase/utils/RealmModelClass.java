package com.zeyad.genericusecase.utils;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class RealmModelClass implements RealmModel {

    @PrimaryKey
    private int id;
    private String value;

    public RealmModelClass() {
    }

    public RealmModelClass(int id, String value) {
        this.id = id;
        this.value = value;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealmModelClass)) return false;

        RealmModelClass that = (RealmModelClass) o;

        if (getId() != that.getId()) return false;
        return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        return result;
    }
}
