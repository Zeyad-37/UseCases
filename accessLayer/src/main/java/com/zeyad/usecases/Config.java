package com.zeyad.usecases;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zeyad.usecases.data.repository.stores.DataStoreFactory;
import com.zeyad.usecases.data.utils.Utils;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

public class Config {
    private static Config sInstance;
    private static DataStoreFactory mDataStoreFactory;
    private static Gson mGson;
    private static String mBaseURL;
    private Context mContext;
    private boolean mUseApiWithCache;

    private Config(@NonNull Context context) {
        mContext = context;
        mGson = createGson();
    }

    private Config() {
        mGson = createGson();
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

//    private void setupRealm() {
//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
//                .name("library.realm")
//                .modules(new LibraryModule())
//                .build();
//    }

    public static String getBaseURL() {
        if (Utils.isNotEmpty(mBaseURL))
            return mBaseURL;
        else
            throw new IllegalArgumentException("Base Url is empty");
    }

    public static void setBaseURL(String baseURL) {
        mBaseURL = baseURL;
    }

    public static Gson getGson() {
        if (mGson == null)
            mGson = createGson();
        return mGson;
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

    public DataStoreFactory getDataStoreFactory() {
        return mDataStoreFactory;
    }

    public void setDataStoreFactory(DataStoreFactory dataStoreFactory) {
        mDataStoreFactory = dataStoreFactory;
    }
}
