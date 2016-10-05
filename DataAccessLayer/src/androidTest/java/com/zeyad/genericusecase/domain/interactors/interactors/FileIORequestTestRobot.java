package com.zeyad.genericusecase.domain.interactors.interactors;

import com.zeyad.genericusecase.data.services.realm_test_models.TestModel;
import com.zeyad.genericusecase.domain.interactors.requests.FileIORequest;

import org.mockito.Mockito;

import java.io.File;

public class FileIORequestTestRobot {

    static final Class DATA_CLASS = TestModel.class;
    static final boolean ON_WIFI = false;
    static final boolean WHILE_CHARGING = false;
    static final String URL = "www.google.com";
    static final File FILE = Mockito.mock(File.class);

    static FileIORequest createUploadRequest() {
        return new FileIORequest.UploadRequestBuilder(URL, FILE)
                .onWifi(ON_WIFI)
                .whileCharging(WHILE_CHARGING)
                .build();
    }
}
