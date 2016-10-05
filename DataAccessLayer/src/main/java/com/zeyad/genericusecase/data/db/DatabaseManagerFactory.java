package com.zeyad.genericusecase.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.zeyad.genericusecase.Config;

import static com.zeyad.genericusecase.Config.REALM;
import static com.zeyad.genericusecase.Config.SQLBRITE;

/**
 * DatabaseManager interface implementer.
 */
public class DatabaseManagerFactory {
    /**
     * @return DataBaseManager the implemented instance of the DatabaseManager.
     */
    @Nullable
    public static DataBaseManager getInstance() {
        if (Config.getInstance().getDBType() == REALM)
            return RealmManager.getInstance();
        if (Config.getInstance().getDBType() == SQLBRITE)
            return SQLBriteManager.getInstance();
        return null;
    }

    /**
     * Creates a RealmManager instance
     *
     * @param context Application context to initialize the RealmManager;
     */
    public static void initRealm(Context context) {
        RealmManager.init(context);
        Config.getInstance().setDBType(REALM);
    }

    /**
     * Creates a SQLBriteManager instance
     *
     * @param sqLiteOpenHelper rules to apply on create and onUpgrade SQLite DB.;
     */
    public static void initSQLBrite(SQLiteOpenHelper sqLiteOpenHelper) {
        SQLBriteManager.init(sqLiteOpenHelper);
        Config.getInstance().setDBType(SQLBRITE);
    }
}
