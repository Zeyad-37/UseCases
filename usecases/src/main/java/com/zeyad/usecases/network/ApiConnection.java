package com.zeyad.usecases.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zeyad.usecases.BuildConfig;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Api Connection class used to retrieve data from the cloud. Implements {@link Callable} so when
 * executed asynchronously can return a value.
 */
public class ApiConnection {
    private static final String
            CACHING_DISABLED = "There would be no caching. Since caching module is disabled.",
            CACHE_CONTROL = "Cache-Control";
    private static final int TIME_OUT = 15;
    private final RestApi mRestApiWithoutCache, mRestApiWithCache;

    public ApiConnection(RestApi restApiWithoutCache, RestApi restApiWithCache) {
        mRestApiWithoutCache = restApiWithoutCache;
        mRestApiWithCache = restApiWithCache;
    }

    public static RestApi initWithCache(
            @Nullable OkHttpClient.Builder okHttpBuilder, @Nullable Cache cache) {
        if (okHttpBuilder == null) {
            okHttpBuilder = getBuilderForOkHttp();
        }
        return createRetro2Client(provideOkHttpClient(okHttpBuilder, cache)).create(RestApi.class);
    }

    public static RestApi init(@Nullable OkHttpClient.Builder okHttpBuilder) {
        if (okHttpBuilder == null) {
            okHttpBuilder = getBuilderForOkHttp();
        }
        return createRetro2Client(provideOkHttpClient(okHttpBuilder, null)).create(RestApi.class);
    }

    @NonNull
    static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor(message -> Log.d("NetworkInfo", message))
                .setLevel(
                        BuildConfig.DEBUG
                                ? HttpLoggingInterceptor.Level.BODY
                                : HttpLoggingInterceptor.Level.NONE);
    }

    private static Interceptor provideCacheInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());
            // re-write response header to force use of cache
            CacheControl cacheControl =
                    new CacheControl.Builder().maxAge(2, TimeUnit.MINUTES).build();
            return response.newBuilder().header(CACHE_CONTROL, cacheControl.toString()).build();
        };
    }

    private static Interceptor provideOfflineCacheInterceptor() {
        return chain -> {
            Request request = chain.request();
            if (!Utils.getInstance().isNetworkAvailable(Config.getInstance().getContext())) {
                request = request.newBuilder()
                        .cacheControl(new CacheControl.Builder()
                                .maxStale(1, TimeUnit.DAYS)
                                .build())
                        .build();
            }
            return chain.proceed(request);
        };
    }

    @NonNull
    private static OkHttpClient.Builder getBuilderForOkHttp() {
        return new OkHttpClient.Builder()
                .addInterceptor(provideHttpLoggingInterceptor())
                //                .addInterceptor(provideOfflineCacheInterceptor())
                //                .addNetworkInterceptor(provideCacheInterceptor())
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS);
    }

    private static OkHttpClient provideOkHttpClient(
            @NonNull OkHttpClient.Builder okHttpBuilder, @Nullable Cache cache) {
        boolean useApiWithCache = cache != null;
        Config.getInstance().setUseApiWithCache(useApiWithCache);
        if (useApiWithCache) {
            okHttpBuilder.cache(cache);
        }
        return okHttpBuilder.build();
    }

    private static Retrofit createRetro2Client(@NonNull OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(Config.getBaseURL())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(Config.getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @NonNull
    public Flowable<ResponseBody> dynamicDownload(String url) {
        return getRestApi().dynamicDownload(url);
    }

    private RestApi getRestApi() {
        return Config.getInstance().isUseApiWithCache() ? mRestApiWithCache : mRestApiWithoutCache;
    }

    @NonNull
    public <M> Flowable<M> dynamicGetObject(String url) {
        return (Flowable<M>) getRestApi().dynamicGetObject(url);
    }

    @NonNull
    public <M> Flowable<M> dynamicGetObject(String url, boolean shouldCache) {
        if (shouldCache && !Config.getInstance().isUseApiWithCache()) {
            logNoCache();
        }
        return (Flowable<M>) getRestApi().dynamicGetObject(url);
    }

    @NonNull
    public Flowable<List> dynamicGetList(String url) {
        return getRestApi().dynamicGetList(url);
    }

    @NonNull
    public Flowable<List> dynamicGetList(String url, boolean shouldCache) {
        if (shouldCache && !Config.getInstance().isUseApiWithCache()) {
            logNoCache();
        }
        return getRestApi().dynamicGetList(url);
    }

    @NonNull
    public <M> Flowable<M> dynamicPost(String url, RequestBody requestBody) {
        return (Flowable<M>) getRestApi().dynamicPost(url, requestBody);
    }

    @NonNull
    public <M> Flowable<M> dynamicPut(String url, RequestBody requestBody) {
        return (Flowable<M>) getRestApi().dynamicPut(url, requestBody);
    }

    @NonNull
    public <M> Flowable<M> dynamicUpload(
            String url, Map<String, RequestBody> partMap, MultipartBody.Part file) {
        return (Flowable<M>) getRestApi().dynamicUpload(url, partMap, file);
    }

    @NonNull
    public <M> Flowable<M> dynamicDelete(String url, RequestBody body) {
        return (Flowable<M>) getRestApi().dynamicDelete(url, body);
    }

    @NonNull
    public <M> Flowable<M> dynamicPatch(String url, RequestBody body) {
        return (Flowable<M>) getRestApi().dynamicPatch(url, body);
    }

    RestApi getRestApiWithoutCache() {
        return mRestApiWithoutCache;
    }

    RestApi getRestApiWithCache() {
        return mRestApiWithCache;
    }

    @SuppressWarnings("unused")
    private Interceptor provideGzipRequestInterceptor() {
        return chain -> {
            Request originalRequest = chain.request();
            return originalRequest.body() == null || originalRequest.header("Content-Encoding") != null ?
                    chain.proceed(originalRequest) :
                    chain.proceed(originalRequest.newBuilder().header("Content-Encoding", "gzip")
                            .method(originalRequest.method(), forceContentLength(gzip(originalRequest.body())))
                            .build());
        };
    }

    @SuppressWarnings("unused")
    private CertificatePinner provideCertificatePinner() {
        return new CertificatePinner.Builder()
                //                .add("api.github.com", "sha256/6wJsqVDF8K19zxfLxV5DGRneLyzso9adVdUN/exDacw=")
                .build();
    }

    @SuppressWarnings("unused")
    private List<ConnectionSpec> provideConnectionSpecsList() {
        return Collections.singletonList(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .build());
    }

    @NonNull
    private RequestBody forceContentLength(@NonNull final RequestBody requestBody) throws IOException {
        final Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return requestBody.contentType();
            }

            @Override
            public long contentLength() {
                return buffer.size();
            }

            @Override
            public void writeTo(@NonNull BufferedSink sink) throws IOException {
                sink.write(buffer.snapshot());
            }
        };
    }

    @NonNull
    private RequestBody gzip(@NonNull final RequestBody body) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public long contentLength() {
                return -1; // We don't know the compressed length in advance!
            }

            @Override
            public void writeTo(@NonNull BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                body.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }

    @SuppressWarnings("unused")
    @Nullable
    private Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(Config.getInstance().getContext().getCacheDir(), "http-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            Log.e("ApiConnection", "", e);
        }
        return cache;
    }

    private void logNoCache() {
        Log.e(getClass().getSimpleName(), CACHING_DISABLED);
    }
}
