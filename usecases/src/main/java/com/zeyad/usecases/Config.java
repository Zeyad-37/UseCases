package com.zeyad.usecases;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.stores.CloudStore;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

public final class Config {
    private static Config sInstance;
    private static Gson mGson;
    private static String mBaseURL;
    private static boolean withCache, withRealm;
    private static int cacheAmount;
    private static TimeUnit cacheTimeUnit;
    private static Scheduler backgroundThread;
    private static ApiConnection apiConnection;
    private static CloudStore cloudStore;
    private static boolean withSQLite;
    private Context mContext;
    private boolean mUseApiWithCache;

    private Config(@NonNull Context context) {
        mContext = context;
        setup();
    }

    private Config() {
        setup();
    }

    private static GsonBuilder createGson() {
        return new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(@NonNull FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class)
                        && f.getDeclaredClass().equals(RealmModel.class)
                        && f.getDeclaringClass().equals(RealmList.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
    }

    public static Config getInstance() {
        if (sInstance == null) {
            sInstance = new Config();
        }
        return sInstance;
    }

    public static void init(@NonNull Context context) {
        sInstance = new Config(context);
    }

    public static String getBaseURL() {
        return mBaseURL;
    }

    public static void setBaseURL(String baseURL) {
        mBaseURL = baseURL;
    }

    public static Gson getGson() {
        return mGson;
    }

    /**
     * @return withCache, whether DataUseCase is using caching or not.
     */
    public static boolean isWithCache() {
        return withCache;
    }

    public static void setWithCache(boolean withCache) {
        Config.withCache = withCache;
    }

    public static void setCacheExpiry(int cacheAmount, TimeUnit timeUnit) {
        Config.cacheAmount = cacheAmount;
        Config.cacheTimeUnit = timeUnit;
    }

    public static int getCacheAmount() {
        return cacheAmount;
    }

    public static TimeUnit getCacheTimeUnit() {
        return cacheTimeUnit;
    }

    public static void setHasRealm(boolean hasRealm) {
        Config.withRealm = hasRealm;
    }

    public static boolean isWithRealm() {
        return withRealm;
    }

    public static Scheduler getBackgroundThread() {
        return backgroundThread;
    }

    public static void setBackgroundThread(Scheduler backgroundThread) {
        Config.backgroundThread = backgroundThread;
    }

    public static ApiConnection getApiConnection() {
        return apiConnection;
    }

    public static void setApiConnection(ApiConnection apiConnection) {
        Config.apiConnection = apiConnection;
    }

    public static CloudStore getCloudStore() {
        return cloudStore;
    }

    public static void setCloudStore(CloudStore cloudStore) {
        Config.cloudStore = cloudStore;
    }

    public static boolean isWithSQLite() {
        return withSQLite;
    }

    public static void setWithSQLite(boolean withSQLite) {
        Config.withSQLite = withSQLite;
    }

    public static void setGson() {
        mGson = createGson().create();
    }

    private void setup() {
        mGson = createGson().create();
        setupRealm();
    }

    private void setupRealm() {
        //        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
        //                .name("library.realm")
        //                .modules(new LibraryModule())
        //                .rxFactory(new RealmObservableFactory())
        //                .deleteRealmIfMigrationNeeded()
        //                .build());
    }

    public Context getContext() {
        return mContext;
    }

    public boolean isUseApiWithCache() {
        return mUseApiWithCache;
    }

    public void setUseApiWithCache(boolean useApiWithCache) {
        mUseApiWithCache = useApiWithCache;
    }
}
