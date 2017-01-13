package com.zeyad.usecases.domain.interactors.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.zeyad.usecases.data.executor.JobExecutor;
import com.zeyad.usecases.data.mappers.DAOMapperFactory;
import com.zeyad.usecases.data.mappers.DefaultDAOMapper;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.mappers.IDAOMapperFactory;
import com.zeyad.usecases.domain.executors.PostExecutionThread;
import com.zeyad.usecases.domain.executors.ThreadExecutor;
import com.zeyad.usecases.domain.executors.UIThread;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * @author by ZIaDo on 12/9/16.
 */

public class DataUseCaseConfig {

    private Context context;
    private IDAOMapperFactory entityMapper;
    private OkHttpClient.Builder okHttpBuilder;
    private Cache okHttpCache;
    private String baseUrl;
    private boolean withCache, withRealm;
    private int cacheSize;
    private ThreadExecutor threadExecutor;
    private PostExecutionThread postExecutionThread;

    private DataUseCaseConfig(Builder dataUseCaseConfigBuilder) {
        context = dataUseCaseConfigBuilder.getContext();
        entityMapper = dataUseCaseConfigBuilder.getEntityMapper();
        okHttpBuilder = dataUseCaseConfigBuilder.getOkHttpBuilder();
        okHttpCache = dataUseCaseConfigBuilder.getOkHttpCache();
        baseUrl = dataUseCaseConfigBuilder.getBaseUrl();
        withCache = dataUseCaseConfigBuilder.isWithCache();
        withRealm = dataUseCaseConfigBuilder.isWithRealm();
        cacheSize = dataUseCaseConfigBuilder.getCacheSize();
        threadExecutor = dataUseCaseConfigBuilder.getThreadExecutor();
        postExecutionThread = dataUseCaseConfigBuilder.getPostExecutionThread();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    IDAOMapperFactory getEntityMapper() {
        if (entityMapper == null) {
            return new DAOMapperFactory() {
                @NonNull
                @Override
                public IDAOMapper getDataMapper(Class dataClass) {
                    return new DefaultDAOMapper();
                }
            };
        }
        return entityMapper;
    }

    ThreadExecutor getThreadExecutor() {
        return threadExecutor == null ? new JobExecutor() : threadExecutor;
    }

    PostExecutionThread getPostExecutionThread() {
        return postExecutionThread == null ? new UIThread() : postExecutionThread;
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

    public static class Builder {
        private Context context;
        private IDAOMapperFactory entityMapper;
        private OkHttpClient.Builder okHttpBuilder;
        private Cache okHttpCache;
        private String baseUrl;
        private boolean withCache, withRealm;
        private int cacheSize;
        private ThreadExecutor threadExecutor;
        private PostExecutionThread postExecutionThread;

        public Builder(Context context) {
            this.context = context;
        }

        @NonNull
        public Builder threadExecutor(ThreadExecutor threadExecutor) {
            this.threadExecutor = threadExecutor;
            return this;
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
        public Builder entityMapper(IDAOMapperFactory entityMapper) {
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
            this.okHttpCache = cache;
            return this;
        }

        @NonNull
        public Builder withRealm() {
            this.withRealm = true;
            return this;
        }

        @NonNull
        public Builder withCache() {
            this.withCache = true;
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

        ThreadExecutor getThreadExecutor() {
            return threadExecutor;
        }

        PostExecutionThread getPostExecutionThread() {
            return postExecutionThread;
        }

        IDAOMapperFactory getEntityMapper() {
            return entityMapper;
        }

        OkHttpClient.Builder getOkHttpBuilder() {
            return okHttpBuilder;
        }

        Cache getOkHttpCache() {
            return okHttpCache;
        }

        void setOkHttpCache(Cache okHttpCache) {
            this.okHttpCache = okHttpCache;
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
