package com.zeyad.usecases.domain.interactors;

import android.content.Context;
import android.os.HandlerThread;
import android.support.annotation.NonNull;

import com.zeyad.usecases.data.mapper.DAOMapper;
import com.zeyad.usecases.domain.executors.PostExecutionThread;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * @author by ZIaDo on 12/9/16.
 */
public class DataUseCaseConfig {

    private Context context;
    private OkHttpClient.Builder okHttpBuilder;
    private Cache okHttpCache;
    private String baseUrl;
    private boolean withCache, withRealm;
    private int cacheSize, cacheAmount;
    private TimeUnit timeUnit;
    private PostExecutionThread postExecutionThread;

    private DataUseCaseConfig(Builder dataUseCaseConfigBuilder) {
        context = dataUseCaseConfigBuilder.context;
        okHttpBuilder = dataUseCaseConfigBuilder.okHttpBuilder;
        okHttpCache = dataUseCaseConfigBuilder.okHttpCache;
        baseUrl = dataUseCaseConfigBuilder.baseUrl;
        withCache = dataUseCaseConfigBuilder.withCache;
        withRealm = dataUseCaseConfigBuilder.withRealm;
        cacheSize = dataUseCaseConfigBuilder.cacheSize;
        cacheAmount = dataUseCaseConfigBuilder.cacheAmount;
        timeUnit = dataUseCaseConfigBuilder.timeUnit;
        postExecutionThread = dataUseCaseConfigBuilder.postExecutionThread;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    DAOMapper getEntityMapper() {
        return new DAOMapper();
    }

    PostExecutionThread getPostExecutionThread() {
        return postExecutionThread;
    }

    OkHttpClient.Builder getOkHttpBuilder() {
        return okHttpBuilder;
    }

    Cache getOkHttpCache() {
        return okHttpCache;
    }

    String getBaseUrl() {
        return baseUrl != null ? baseUrl : "";
    }

    boolean isWithRealm() {
        return withRealm;
    }

    boolean isWithCache() {
        return withCache;
    }

    int getCacheSize() {
        return cacheSize == 0 || cacheSize > 8192 ? 8192 : cacheSize;
    }

    int getCacheAmount() {
        return cacheAmount;
    }

    TimeUnit getTimeUnit() {
        return timeUnit;
    }

    HandlerThread getHandlerThread() {
        return new HandlerThread("backgroundThread");
    }

    public static class Builder {
        private Context context;
        private OkHttpClient.Builder okHttpBuilder;
        private Cache okHttpCache;
        private String baseUrl;
        private boolean withCache, withRealm;
        private int cacheSize, cacheAmount;
        private TimeUnit timeUnit;
        private PostExecutionThread postExecutionThread;

        public Builder(Context context) {
            this.context = context;
        }

        @NonNull
        public Builder postExecutionThread(PostExecutionThread postExecutionThread) {
            this.postExecutionThread = postExecutionThread;
            return this;
        }

        @NonNull
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        @NonNull
        public Builder okHttpBuilder(OkHttpClient.Builder okHttpBuilder) {
            this.okHttpBuilder = okHttpBuilder;
            return this;
        }

        @NonNull
        public Builder okhttpCache(Cache cache) {
            this.okHttpCache = cache;
            return this;
        }

        @NonNull
        public Builder withRealm() {
            this.withRealm = true;
            return this;
        }

        @NonNull
        public Builder withCache(int expiryAmount, TimeUnit timeUnit) {
            this.withCache = true;
            this.cacheAmount = expiryAmount;
            this.timeUnit = timeUnit;
            return this;
        }

        @NonNull
        public Builder cacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
            return this;
        }

        @NonNull
        public DataUseCaseConfig build() {
            return new DataUseCaseConfig(this);
        }
    }
}
