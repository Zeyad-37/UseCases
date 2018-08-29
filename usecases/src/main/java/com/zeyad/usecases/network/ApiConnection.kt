package com.zeyad.usecases.network

import android.util.Log
import com.zeyad.usecases.BuildConfig
import com.zeyad.usecases.Config
import io.reactivex.Flowable
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

/**
 * Api Connection class used to retrieve data from the cloud. Implements [Callable] so when
 * executed asynchronously can return a value.
 */
class ApiConnection(internal val restApiWithoutCache: RestApi, internal val restApiWithCache: RestApi) {
    private val restApi: RestApi
        get() = if (Config.useApiWithCache) restApiWithCache else restApiWithoutCache

    fun dynamicDownload(url: String): Flowable<ResponseBody> {
        return restApi.dynamicDownload(url)
    }

    fun <M> dynamicGetObject(url: String): Flowable<M> {
        return restApi.dynamicGetObject(url) as Flowable<M>
    }

    fun <M> dynamicGetObject(url: String, shouldCache: Boolean): Flowable<M> {
        if (shouldCache && !Config.useApiWithCache) {
            logNoCache()
        }
        return restApi.dynamicGetObject(url) as Flowable<M>
    }

    fun <M> dynamicGetList(url: String): Flowable<List<M>> {
        return restApi.dynamicGetList(url) as Flowable<List<M>>
    }

    fun <M> dynamicGetList(url: String, shouldCache: Boolean): Flowable<List<M>> {
        if (shouldCache && !Config.useApiWithCache) {
            logNoCache()
        }
        return restApi.dynamicGetList(url) as Flowable<List<M>>
    }

    fun <M> dynamicPost(url: String, requestBody: RequestBody): Flowable<M> {
        return restApi.dynamicPost(url, requestBody) as Flowable<M>
    }

    fun <M> dynamicPut(url: String, requestBody: RequestBody): Flowable<M> {
        return restApi.dynamicPut(url, requestBody) as Flowable<M>
    }

    fun <M> dynamicUpload(
            url: String, partMap: Map<String, RequestBody>, files: List<MultipartBody.Part>): Flowable<M> {
        return restApi.dynamicUpload(url, partMap, files) as Flowable<M>
    }

    fun <M> dynamicDelete(url: String): Flowable<M> {
        return restApi.dynamicDelete(url) as Flowable<M>
    }

    fun <M> dynamicPatch(url: String, body: RequestBody): Flowable<M> {
        return restApi.dynamicPatch(url, body) as Flowable<M>
    }

    private fun logNoCache() {
        Log.e(javaClass.simpleName, CACHING_DISABLED)
    }


    //    private Interceptor provideGzipRequestInterceptor() {
    //        return chain -> {
    //            Request originalRequest = chain.request();
    //            return originalRequest.body() == null || originalRequest.header("Content-Encoding") != null ?
    //                    chain.proceed(originalRequest) :
    //                    chain.proceed(originalRequest.newBuilder().header("Content-Encoding", "gzip")
    //                            .method(originalRequest.method(), forceContentLength(gzip(originalRequest.body())))
    //                            .build());
    //        };
    //    }

    //    private CertificatePinner provideCertificatePinner() {
    //        return new CertificatePinner.Builder()
    //                //                .add("api.github.com", "sha256/6wJsqVDF8K19zxfLxV5DGRneLyzso9adVdUN/exDacw=")
    //                .build();
    //    }

    //    @SuppressWarnings("unused")
    //    private List<ConnectionSpec> provideConnectionSpecsList() {
    //        return Collections.singletonList(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
    //                .tlsVersions(TlsVersion.TLS_1_2)
    //                .build());
    //    }

    //    @NonNull
    //    private RequestBody forceContentLength(@NonNull final RequestBody requestBody) throws IOException {
    //        final Buffer buffer = new Buffer();
    //        requestBody.writeTo(buffer);
    //        return new RequestBody() {
    //            @Override
    //            public MediaType contentType() {
    //                return requestBody.contentType();
    //            }
    //
    //            @Override
    //            public long contentLength() {
    //                return buffer.size();
    //            }
    //
    //            @Override
    //            public void writeTo(@NonNull BufferedSink sink) throws IOException {
    //                sink.write(buffer.snapshot());
    //            }
    //        };
    //    }

    //    @NonNull
    //    private RequestBody gzip(@NonNull final RequestBody body) {
    //        return new RequestBody() {
    //            @Override
    //            public MediaType contentType() {
    //                return body.contentType();
    //            }
    //
    //            @Override
    //            public long contentLength() {
    //                return -1; // We don't know the compressed length in advance!
    //            }
    //
    //            @Override
    //            public void writeTo(@NonNull BufferedSink sink) throws IOException {
    //                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
    //                body.writeTo(gzipSink);
    //                gzipSink.close();
    //            }
    //        };
    //    }

    //    @Nullable
    //    private Cache provideCache() {
    //        Cache cache = null;
    //        try {
    //            cache = new Cache(new File(Config.getInstance().getContext().getCacheDir(), "http-cache"),
    //                    10 * 1024 * 1024); // 10 MB
    //        } catch (Exception e) {
    //            Log.e("ApiConnection", "", e);
    //        }
    //        return cache;
    //    }

    companion object {
        private val CACHING_DISABLED = "There would be no caching. Since caching module is disabled."//,
        //            CACHE_CONTROL = "Cache-Control";
        private val TIME_OUT = 15

        fun initWithCache(
                okHttpBuilder: OkHttpClient.Builder?, cache: Cache?): RestApi {

            return createRetrofitClient(provideOkHttpClient(okHttpBuilder
                    ?: builderForOkHttp, cache)).create(RestApi::class.java)
        }

        fun init(okHttpBuilder: OkHttpClient.Builder?): RestApi {
            return createRetrofitClient(provideOkHttpClient(okHttpBuilder
                    ?: builderForOkHttp, null)).create(RestApi::class.java)
        }

        internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor { message -> Log.d("NetworkInfo", message) }
                    .setLevel(if (BuildConfig.DEBUG)
                        HttpLoggingInterceptor.Level.BODY
                    else
                        HttpLoggingInterceptor.Level.NONE)
        }

        private val builderForOkHttp: OkHttpClient.Builder
            get() = OkHttpClient.Builder()
                    .addInterceptor(provideHttpLoggingInterceptor())
                    .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)

        private fun provideOkHttpClient(
                okHttpBuilder: OkHttpClient.Builder, cache: Cache?): OkHttpClient {
            val useApiWithCache = cache != null
            Config.useApiWithCache = useApiWithCache
            if (useApiWithCache) {
                okHttpBuilder.cache(cache)
            }
            return okHttpBuilder.build()
        }

        private fun createRetrofitClient(okHttpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(Config.baseURL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(Config.gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }
    }
}
