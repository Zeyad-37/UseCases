package com.zeyad.usecases.data.db;

import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;

import static com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory.REALM;

/**
 * DatabaseManager interface implementer.
 */
public class DatabaseManagerFactory {
    /**
     * @return {@link DataBaseManager} the implemented instance of the DatabaseManager.
     */
    public static DataBaseManager getInstance() {
        if (DataUseCaseFactory.getDBType() == REALM)
            return RealmManager.getInstance();
        throw new IllegalAccessError("Realm not initialized");
    }

    /**
     * Creates a RealmManager instance
     */
    public static void initRealm() {
        RealmManager.init();
    }
}
