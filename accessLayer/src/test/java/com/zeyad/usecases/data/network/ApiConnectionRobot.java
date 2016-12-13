package com.zeyad.usecases.data.network;

import org.mockito.Mockito;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

class ApiConnectionRobot {

    static RestApi createMockedRestApi() {
        return Mockito.mock(RestApiImpl.class);
    }

    static String getValidUrl() {
        return "http://www.google.com";
    }

    static RequestBody getMockedRequestBody() {
        return Mockito.mock(RequestBody.class);
    }

    static Map getPartMap() {
        return Mockito.mock(Map.class);
    }

    static MultipartBody.Part getValidMultipartBodyPart() {
        return MultipartBody.Part.create(getMockedRequestBody());
    }

    static RequestBody getValidRequestBody() {
        return RequestBody.create(MediaType.parse("multipart/form-data"), Mockito.mock(File.class));
    }
}
