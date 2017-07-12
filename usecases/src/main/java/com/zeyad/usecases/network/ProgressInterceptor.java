package com.zeyad.usecases.network;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author by ZIaDo on 7/11/17.
 */

public abstract class ProgressInterceptor implements Interceptor {

    private ProgressListener progressListener;

    protected ProgressInterceptor(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        Response.Builder responseBuilder = originalResponse.newBuilder();
        if (isUpDownload(originalResponse)) {
            responseBuilder.body(new ProgressResponseBody(originalResponse.body(), progressListener));
        } else {
            responseBuilder.body(originalResponse.body());
        }
        return responseBuilder.build();
    }

    public abstract boolean isUpDownload(Response originalResponse);

}
