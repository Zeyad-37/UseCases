package com.zeyad.genericusecase.data.network;

import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ApiConnectionImplementationTest extends IApiConnectionTest {

    @Override
    protected RestApi getCurrentSetRestApiWithoutCache(IApiConnection apiConnection) {
        return ((ApiConnection) apiConnection).getRestApiWithoutCache();
    }

    @Override
    protected RestApi getCurrentSetRestApiWithCache(IApiConnection apiConnection) {
        return ((ApiConnection) apiConnection).getRestApiWithCache();
    }

    @Override
    protected IApiConnection getApiImplementation(RestApi restApiWithoutCache, RestApi restApiWithCache) {
        ApiConnection.init(restApiWithoutCache, restApiWithCache);
        return ApiConnection.getInstance();
    }
}
