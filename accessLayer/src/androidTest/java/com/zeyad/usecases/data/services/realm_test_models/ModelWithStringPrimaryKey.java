package com.zeyad.usecases.data.services.realm_test_models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ModelWithStringPrimaryKey extends RealmObject {

    @PrimaryKey
    String studentId;
    String studentName;

    public ModelWithStringPrimaryKey(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public ModelWithStringPrimaryKey() {
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
