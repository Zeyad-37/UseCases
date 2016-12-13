package com.zeyad.usecases.data.db;

import android.support.annotation.Nullable;

import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;

import static com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory.REALM;

/**
 * DatabaseManager interface implementer.
 */
public class DatabaseManagerFactory {
    /**
     * @return DataBaseManager the implemented instance of the DatabaseManager.
     */
    @Nullable
    public static DataBaseManager getInstance() {
        if (DataUseCaseFactory.getDBType() == REALM)
            return RealmManager.getInstance();
        return null;
    }

    /**
     * Creates a RealmManager instance
     */
    public static void initRealm() {
        RealmManager.init();
    }
}
