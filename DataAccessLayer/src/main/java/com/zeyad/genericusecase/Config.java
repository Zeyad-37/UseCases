package com.zeyad.genericusecase;

import android.content.Context;
import android.support.annotation.NonNull;

public class Config {

    public static final int NONE = 0, REALM = 1, SQLBRITE = 2;
    private static Config sInstance;
    private Context mContext;
    private String mPrefFileName;
    private boolean mUseApiWithCache;
    private int mDBType;

    private Config(@NonNull Context context) {
        mContext = context;
        setupRealm(context);
    }

    public static Config getInstance() {
        if (sInstance == null)
            throw new NullPointerException("Config.init(context) must be called before");
        return sInstance;
    }

    public static void init(@NonNull Context context) {
        sInstance = new Config(context);
    }

    private void setupRealm(@NonNull Context context) {
//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context, context.getCacheDir())
//                .name("library.realm")
//                .modules(new TestLibraryModule())
//                .build();
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

    public String getPrefFileName() {
        return mPrefFileName;
    }

    public void setPrefFileName(String prefFileName) {
        mPrefFileName = prefFileName;
    }

    public int getDBType() {
        return mDBType;
    }

    public void setDBType(int dBType) {
        mDBType = dBType;
    }
}
