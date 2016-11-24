package com.zeyad.genericusecase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Config {

    private static final String NO_CONTEXT_ERROR = "Config.init(context) must be called before or Config.setContext(context)";
    public static final int NONE = 0, REALM = 1;
    private static Config sInstance;
    private Context mContext;
    private String mPrefFileName;
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
        if (mContext == null)
            throw new NullPointerException(NO_CONTEXT_ERROR);
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
