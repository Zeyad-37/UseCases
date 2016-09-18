package com.zeyad.genericusecase.data.db;

import android.content.Context;

public class DatabaseManagerFactory {

    public static DataBaseManager getInstance(Context context) {
        return GenericRealmManager.getInstance(context);
    }

    public static DataBaseManager getInstance() {
        return GenericRealmManager.getInstance();
    }

    public static void init(Context context) {
        GenericRealmManager.init(context);
    }
}
