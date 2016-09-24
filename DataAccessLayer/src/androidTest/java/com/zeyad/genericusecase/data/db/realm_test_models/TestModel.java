package com.zeyad.genericusecase.data.db.realm_test_models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TestModel extends RealmObject {

    @SerializedName("id")
    @PrimaryKey
    private int id;
    @SerializedName("value")
    private String value;

    public TestModel(int id, String value) {
        this.id = id;
        this.value = value;
        if (id <= 0) {
            throw new IllegalArgumentException("id should be greater than 0");
        }
    }

    public TestModel() {
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
    public String toString() {
        return "TestModel2{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestModel)) return false;
        TestModel testModel = (TestModel) o;
        if (getId() != testModel.getId()) return false;
        return getValue().equals(testModel.getValue());
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getValue().hashCode();
        return result;
    }
}
