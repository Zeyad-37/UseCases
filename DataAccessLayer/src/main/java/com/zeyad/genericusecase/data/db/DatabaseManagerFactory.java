package com.zeyad.genericusecase.data.db;

import android.content.Context;
import android.support.annotation.Nullable;

import com.zeyad.genericusecase.Config;

import static com.zeyad.genericusecase.Config.REALM;

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
}
