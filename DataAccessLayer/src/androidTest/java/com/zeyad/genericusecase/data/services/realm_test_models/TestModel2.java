package com.zeyad.genericusecase.data.services.realm_test_models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TestModel2 extends RealmObject {

    @SerializedName("id")
    @PrimaryKey
    private int id;
    @SerializedName("value")
    private String value;

    public TestModel2(int id, String value) {
        this.id = id;
        this.value = value;
        if (id <= 0)
            throw new IllegalArgumentException("id should be greater than 0");
    }

    public TestModel2() {
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
        return "TestModel2{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestModel2)) return false;
        TestModel2 testModel2 = (TestModel2) o;
        return getId() == testModel2.getId() && getValue().equals(testModel2.getValue());
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getValue().hashCode();
        return result;
    }
}
