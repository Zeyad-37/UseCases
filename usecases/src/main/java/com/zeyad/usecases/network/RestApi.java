package com.zeyad.usecases.network;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * RestApi for retrieving data from the network.
 */
public interface RestApi {

    @NonNull
    @GET
    Flowable<Object> dynamicGetObject(@Url String url);

    @NonNull
    @GET
    Flowable<Object> dynamicGetObject(@Url String url, boolean shouldCache);

    @NonNull
    @GET
    Flowable<List> dynamicGetList(@Url String url);

    @NonNull
    @GET
    Flowable<List> dynamicGetList(@Url String url, boolean shouldCache);

    @NonNull
    @POST
    Flowable<Object> dynamicPost(@Url String url, @Body RequestBody body);

    @NonNull
    @PUT
    Flowable<Object> dynamicPut(@Url String url, @Body RequestBody body);

    @NonNull
    @PATCH
    Flowable<Object> dynamicPatch(@Url String url, @Body RequestBody requestBody);

    @NonNull
    @DELETE
    Flowable<Object> dynamicDelete(@Url String url);

    @NonNull
    @Streaming
    @GET
    Flowable<ResponseBody> dynamicDownload(@Url String fileUrl);

    @NonNull
    @Multipart
    @POST
    Flowable<Object> dynamicUpload(@Url String url, @PartMap() Map<String, RequestBody> partMap,
            @Part List<MultipartBody.Part> file);
}
