package com.zeyad.usecases.domain.interactors.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.zeyad.usecases.data.mappers.EntityDataMapper;
import com.zeyad.usecases.data.mappers.EntityMapper;
import com.zeyad.usecases.data.mappers.EntityMapperUtil;
import com.zeyad.usecases.data.mappers.IEntityMapperUtil;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * @author by ZIaDo on 12/9/16.
 */

public class DataUseCaseConfig {

    private Context context;
    private IEntityMapperUtil entityMapper;
    private OkHttpClient.Builder okHttpBuilder;
    private Cache cache;
    private String baseUrl;
    private boolean withCache, withRealm;
    private int cacheSize;

    private DataUseCaseConfig(Builder dataUseCaseConfigBuilder) {
        context = dataUseCaseConfigBuilder.getContext();
        entityMapper = dataUseCaseConfigBuilder.getEntityMapper();
        okHttpBuilder = dataUseCaseConfigBuilder.getOkHttpBuilder();
        cache = dataUseCaseConfigBuilder.getCache();
        baseUrl = dataUseCaseConfigBuilder.getBaseUrl();
        withCache = dataUseCaseConfigBuilder.isWithCache();
        withRealm = dataUseCaseConfigBuilder.isWithRealm();
        cacheSize = dataUseCaseConfigBuilder.getCacheSize();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    IEntityMapperUtil getEntityMapper() {
        if (entityMapper == null) {
            return new EntityMapperUtil() {
                @NonNull
                @Override
                public EntityMapper getDataMapper(Class dataClass) {
                    return new EntityDataMapper();
                }
            };
        }
        return entityMapper;
    }

    OkHttpClient.Builder getOkHttpBuilder() {
        return okHttpBuilder;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
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
        return cacheSize == 0 ? 8192 : cacheSize;
    }

    public static class Builder {
        private Context context;
        private IEntityMapperUtil entityMapper;
        private OkHttpClient.Builder okHttpBuilder;
        private Cache cache;
        private String baseUrl;
        private boolean withCache, withRealm;
        private int cacheSize;

        public Builder(Context context) {
            this.context = context;
        }

        @NonNull
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        @NonNull
        public Builder entityMapper(IEntityMapperUtil entityMapper) {
            this.entityMapper = entityMapper;
            return this;
        }

        @NonNull
        public Builder okHttpBuilder(OkHttpClient.Builder okHttpBuilder) {
            this.okHttpBuilder = okHttpBuilder;
            return this;
        }

        @NonNull
        public Builder okhttpCache(Cache cache) {
            this.cache = cache;
            return this;
        }

        @NonNull
        public Builder withRealm(boolean withRealm) {
            this.withRealm = withRealm;
            return this;
        }

        @NonNull
        public Builder withCache(boolean withCache) {
            this.withCache = withCache;
            return this;
        }

        @NonNull
        public Builder cacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
            return this;
        }

        Context getContext() {
            return context;
        }

        void setContext(Context context) {
            this.context = context;
        }

        IEntityMapperUtil getEntityMapper() {
            return entityMapper;
        }

        OkHttpClient.Builder getOkHttpBuilder() {
            return okHttpBuilder;
        }

        Cache getCache() {
            return cache;
        }

        void setCache(Cache cache) {
            this.cache = cache;
        }

        String getBaseUrl() {
            return baseUrl;
        }

        boolean isWithRealm() {
            return withRealm;
        }

        boolean isWithCache() {
            return withCache;
        }

        int getCacheSize() {
            return cacheSize;
        }

        @NonNull
        public DataUseCaseConfig build() {
            return new DataUseCaseConfig(this);
        }
    }
}
