package com.zeyad.genericusecase;

import android.content.Context;
import android.support.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Config {

    private static Config sInstance;
    private Context mContext;
    private String mPrefFileName;

    private Config(@NonNull Context context) {
        mContext = context;
        setupRealm(context);
    }

    private void setupRealm(@NonNull Context context) {
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(context)
                .name("library.realm")
//                .modules(new LibraryModule())
                .build());
    }

    public static Config getInstance() {
        if (sInstance == null)
            throw new NullPointerException("init must be called before");
        return sInstance;
    }

    public static void init(@NonNull Context context) {
        sInstance = new Config(context);
    }

    public Context getContext() {
        if (mContext == null)
            throw new NullPointerException("set app context needs to be called first");
        return mContext;
    }

    public String getPrefFileName() {
        return mPrefFileName;
    }

    public void setPrefFileName(String prefFileName) {
        mPrefFileName = prefFileName;
    }
}
