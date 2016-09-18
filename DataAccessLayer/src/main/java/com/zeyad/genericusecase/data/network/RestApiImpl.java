package com.zeyad.genericusecase.data.network;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Part;
import retrofit2.http.Url;
import rx.Observable;

/**
 * {@link RestApi} implementation for retrieving data from the network.
 */
public class RestApiImpl implements RestApi {

    private final IApiConnection mApiConnection;

    public RestApiImpl() {
        mApiConnection = ApiConnectionFactory.getInstance();
    }

    RestApiImpl(IApiConnection apiConnection) {
        mApiConnection = apiConnection;
    }

    @Override
    public Observable<ResponseBody> dynamicDownload(@Url String url) {
        return mApiConnection.dynamicDownload(url);
    }

    @Override
    public Observable<Object> upload(@Url String url,
                                     @Part(value = "image", encoding = "binary") RequestBody requestBody) {
        return mApiConnection.upload(url, requestBody);
    }

    @Override
    public Observable<ResponseBody> upload(@Url String url, @Part MultipartBody.Part file) {
        return mApiConnection.upload(url, file);
    }

    @Override
    public Observable<Object> dynamicGetObject(@Url String url) {
        return mApiConnection.dynamicGetObject(url);
    }

    @Override
    public Observable<Object> dynamicGetObject(@Url String url, boolean shouldCache) {
        return mApiConnection.dynamicGetObject(url, shouldCache);
    }

    @Override
    public Observable<List> dynamicGetList(@Url String url) {
        return mApiConnection.dynamicGetList(url);
    }

    @Override
    public Observable<List> dynamicGetList(@Url String url, boolean shouldCache) {
        return mApiConnection.dynamicGetList(url, shouldCache);
    }

    @Override
    public Observable<Object> dynamicPostObject(@Url String url, RequestBody body) {
        return mApiConnection.dynamicPostObject(url, body);
    }

    @Override
    public Observable<List> dynamicPostList(@Url String url, @Body RequestBody body) {
        return mApiConnection.dynamicPostList(url, body);
    }

    @Override
    public Observable<Object> dynamicPutObject(@Url String url, @Body RequestBody body) {
        return mApiConnection.dynamicPutObject(url, body);
    }

    @Override
    public Observable<List> dynamicPutList(@Url String url, @Body RequestBody body) {
        return mApiConnection.dynamicPutList(url, body);
    }

    @Override
    public Observable<Object> dynamicDeleteObject(@Url String url, @Body RequestBody body) {
        return mApiConnection.dynamicDeleteObject(url, body);
    }

    @Override
    public Observable<List> dynamicDeleteList(@Url String url, @Body RequestBody body) {
        return mApiConnection.dynamicDeleteList(url, body);
    }
}
