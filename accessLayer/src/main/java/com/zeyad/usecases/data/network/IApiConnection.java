package com.zeyad.usecases.data.network;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.Observable;

interface IApiConnection {

    @NonNull
    HttpLoggingInterceptor provideHttpLoggingInterceptor();

    Observable<ResponseBody> dynamicDownload(String url);

    Observable<Object> dynamicGetObject(String url);

    Observable<Object> dynamicGetObject(String url, boolean shouldCache);

    Observable<List> dynamicGetList(String url);

    Observable<List> dynamicGetList(String url, boolean shouldCache);

    Observable<Object> dynamicPost(String url, RequestBody requestBody);

    Observable<Object> dynamicPut(String url, RequestBody requestBody);

    Observable<Object> upload(String url, Map<String, RequestBody> partMap, MultipartBody.Part file);

    Observable<Object> dynamicDelete(String url, RequestBody body);
}
