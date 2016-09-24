package com.zeyad.genericusecase.data.db;

import android.content.Context;

public class DatabaseManagerFactory {

    public static DataBaseManager getInstance(Context context) {
        return com.zeyad.genericusecase.data.db.GenericRealmManager.getInstance(context);
    }

    public static DataBaseManager getInstance() {
        return com.zeyad.genericusecase.data.db.GenericRealmManager.getInstance();
    }

    public static void init(Context context) {
        GenericRealmManager.init(context);
    }
}
