package com.zeyad.genericusecase.data.network;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RestApiImltest extends RestApiTest {
    @Override
    public RestApi getRestApiImplementation(IApiConnection mockedApiConnection) {
        return new RestApiImpl(mockedApiConnection);
    }
}
