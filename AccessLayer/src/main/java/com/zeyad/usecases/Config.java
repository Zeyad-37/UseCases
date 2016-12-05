package com.zeyad.usecases;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.usecases.data.repository.stores.DataStoreFactory;

public class Config {

    public static final int NONE = 0, REALM = 1;
    private static Config sInstance;
    private static DataStoreFactory mDataStoreFactory;
    private Context mContext;
    private static String mBaseURL;
    private boolean mUseApiWithCache;
    private int mDBType;

    private Config(@NonNull Context context) {
        mContext = context;
    }

    private Config() {
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

    public int getDBType() {
        return mDBType;
    }

    public void setDBType(int dBType) {
        mDBType = dBType;
    }

    public DataStoreFactory getDataStoreFactory() {
        return mDataStoreFactory;
    }

    public void setDataStoreFactory(DataStoreFactory dataStoreFactory) {
        mDataStoreFactory = dataStoreFactory;
    }

    public static String getBaseURL() {
        if (mBaseURL == null)
            throw new NullPointerException("Base Url is null");
        if (mBaseURL.isEmpty())
            throw new IllegalArgumentException("Base Url is empty");
        return mBaseURL;
    }

    public static void setBaseURL(String baseURL) {
        mBaseURL = baseURL;
    }
}
