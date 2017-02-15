package com.zeyad.usecases.data.requests;

import com.zeyad.usecases.utils.TestRealmObject;

import org.mockito.Mockito;

import java.io.File;

class FileIORequestTestRobot {

    static final boolean ON_WIFI = false;
    static final boolean WHILE_CHARGING = false;
    static final String URL = "www.google.com";
    static final File FILE = Mockito.mock(File.class);
    private static final Class DATA_CLASS = TestRealmObject.class;

    static FileIORequest createUploadRequest() {
        return new FileIORequest.FileIORequestBuilder(URL, FILE)
                .onWifi(ON_WIFI)
                .whileCharging(WHILE_CHARGING)
                .dataClass(DATA_CLASS)
                .build();
    }
}
