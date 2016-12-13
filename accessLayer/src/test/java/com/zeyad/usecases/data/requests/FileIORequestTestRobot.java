package com.zeyad.usecases.data.requests;

import com.zeyad.usecases.utils.TestModel;

import org.mockito.Mockito;

import java.io.File;

public class FileIORequestTestRobot {

    static final Class DATA_CLASS = TestModel.class;
    static final boolean ON_WIFI = false;
    static final boolean WHILE_CHARGING = false;
    static final String URL = "www.google.com";
    static final File FILE = Mockito.mock(File.class);

    static FileIORequest createUploadRequest() {
        return new FileIORequest.FileIORequestBuilder(URL, FILE)
                .onWifi(ON_WIFI)
                .whileCharging(WHILE_CHARGING)
                .dataClass(DATA_CLASS)
                .build();
    }
}
