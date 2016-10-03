package com.zeyad.genericusecase.data.db;

import android.content.Context;

public class DatabaseManagerFactory {

    public static DataBaseManager getInstance(Context context) {
        return RealmManager.getInstance(context);
    }

    public static DataBaseManager getInstance() {
        return RealmManager.getInstance();
    }

    public static void init(Context context) {
        RealmManager.init(context);
    }
}
