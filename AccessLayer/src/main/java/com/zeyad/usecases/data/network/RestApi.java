package com.zeyad.usecases.data.network;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * RestApi for retrieving data from the network.
 */
public interface RestApi {

    @GET
    Observable<Object> dynamicGetObject(@Url String url);

    @GET
    Observable<Object> dynamicGetObject(@Url String url, boolean shouldCache);

    @GET
    Observable<List> dynamicGetList(@Url String url);

    @GET
    Observable<List> dynamicGetList(@Url String url, boolean shouldCache);

    @POST
    Observable<Object> dynamicPost(@Url String url, @Body RequestBody body);

    @PUT
    Observable<Object> dynamicPut(@Url String url, @Body RequestBody body);

    @DELETE
    Observable<Object> dynamicDelete(@Url String url, @Body RequestBody body);

    @Streaming
    @GET
    Observable<ResponseBody> dynamicDownload(@Url String fileUrl);

    @Multipart
    @POST
    Observable<Object> upload(@Url String url, @PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);
}
