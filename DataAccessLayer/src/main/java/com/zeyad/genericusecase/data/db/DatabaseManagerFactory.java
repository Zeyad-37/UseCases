package com.zeyad.genericusecase.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.zeyad.genericusecase.Config;

public class DatabaseManagerFactory {

    public static DataBaseManager getInstance() {
        return RealmManager.getInstance();
    }

    public static void initRealm(Context context) {
        RealmManager.init(context);
        Config.getInstance().setDBType(Config.REALM);
    }

    public static void initSQLBrite(SQLiteOpenHelper sqLiteOpenHelper) {
        SQLBriteManager.init(sqLiteOpenHelper);
        Config.getInstance().setDBType(Config.SQLBRITE);
    }
}
