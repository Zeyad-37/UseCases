package com.zeyad.usecases;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.stores.CloudDataStore;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

public class Config {
    private static Config sInstance;
    private static Gson mGson;
    private static String mBaseURL;
    private static boolean withCache, withRealm;
    private static int cacheAmount;
    private static TimeUnit cacheTimeUnit;
    private static Scheduler backgroundThread;
    private static ApiConnection apiConnection;
    private static CloudDataStore cloudDataStore;
    //    private static GenericDatabase appDatabase;
    private Context mContext;
    private boolean mUseApiWithCache;

    private Config(@NonNull Context context) {
        mContext = context;
        mGson = createGson();
        setupRealm();
    }

    private Config() {
        mGson = createGson();
        setupRealm();
    }

    private static Gson createGson() {
        mGson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
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
        }).create();
        return mGson;
    }

    public static Config getInstance() {
        if (sInstance == null)
            init();
        return sInstance;
    }

    public static void init(@NonNull Context context) {
        sInstance = new Config(context);
    }

    public static void init() {
        sInstance = new Config();
    }

    public static String getBaseURL() {
        return mBaseURL;
    }

    public static void setBaseURL(String baseURL) {
        mBaseURL = baseURL;
    }

    public static Gson getGson() {
        if (mGson == null)
            mGson = createGson();
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

    public static CloudDataStore getCloudDataStore() {
        return cloudDataStore;
    }

    public static void setCloudDataStore(CloudDataStore cloudDataStore) {
        Config.cloudDataStore = cloudDataStore;
    }

//    public static GenericDatabase getAppDatabase() {
//        return appDatabase;
//    }
//
//    public static void setAppDatabase(GenericDatabase appDatabase) {
//        Config.appDatabase = appDatabase;
//    }

    private void setupRealm() {
//        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
//                .name("library.realm")
//                .modules(new LibraryModule())
//                .rxFactory(new RealmObservableFactory())
//                .deleteRealmIfMigrationNeeded()
//                .build());
    }

    @Nullable
    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public boolean isUseApiWithCache() {
        return mUseApiWithCache;
    }

    public void setUseApiWithCache(boolean useApiWithCache) {
        mUseApiWithCache = useApiWithCache;
    }
}
