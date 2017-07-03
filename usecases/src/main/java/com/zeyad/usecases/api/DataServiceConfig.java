package com.zeyad.usecases.api;

import android.content.Context;
import android.os.HandlerThread;
import android.support.annotation.NonNull;

import com.zeyad.usecases.mapper.DAOMapper;
import com.zeyad.usecases.utils.DataBaseManagerUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/** @author by ZIaDo on 12/9/16. */
public class DataServiceConfig {

    private final Context context;
    private final OkHttpClient.Builder okHttpBuilder;
    private final Cache okHttpCache;
    private final String baseUrl;
    private final boolean withCache, withRealm;
    private final int cacheSize, cacheAmount;
    private final TimeUnit timeUnit;
    private final Scheduler postExecutionThread;
    private final DataBaseManagerUtil dataBaseManagerUtil;

    private DataServiceConfig(@NonNull Builder dataUseCaseConfigBuilder) {
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
        dataBaseManagerUtil = dataUseCaseConfigBuilder.dataBaseManagerUtil;
    }

    public Context getContext() {
        return context;
    }

    @NonNull
    DAOMapper getEntityMapper() {
        return new DAOMapper();
    }

    Scheduler getPostExecutionThread() {
        return postExecutionThread;
    }

    OkHttpClient.Builder getOkHttpBuilder() {
        return okHttpBuilder;
    }

    Cache getOkHttpCache() {
        return okHttpCache;
    }

    @NonNull
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

    DataBaseManagerUtil getDataBaseManagerUtil() {
        return dataBaseManagerUtil;
    }

    @NonNull
    HandlerThread getHandlerThread() {
        return new HandlerThread("backgroundThread");
    }

    public static class Builder {
        private final Context context;
        private OkHttpClient.Builder okHttpBuilder;
        private Cache okHttpCache;
        private String baseUrl;
        private boolean withCache, withRealm;
        private int cacheSize, cacheAmount;
        private TimeUnit timeUnit;
        private Scheduler postExecutionThread;
        private DataBaseManagerUtil dataBaseManagerUtil;

        public Builder(Context context) {
            this.context = context;
        }

        @NonNull
        public Builder postExecutionThread(Scheduler postExecutionThread) {
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
            this.dataBaseManagerUtil = null;
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
        public Builder withSQLite(DataBaseManagerUtil dataBaseManagerUtil) {
            this.dataBaseManagerUtil = dataBaseManagerUtil;
            this.withRealm = false;
            return this;
        }

        @NonNull
        public DataServiceConfig build() {
            return new DataServiceConfig(this);
        }
    }
}
