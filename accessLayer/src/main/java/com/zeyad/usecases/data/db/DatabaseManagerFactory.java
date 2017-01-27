package com.zeyad.usecases.data.db;

import com.zeyad.usecases.domain.interactors.data.DataUseCase;

import static com.zeyad.usecases.domain.interactors.data.DataUseCase.REALM;

/**
 * DatabaseManager interface implementer.
 */
public class DatabaseManagerFactory {
    /**
     * @return {@link DataBaseManager} the implemented instance of the DatabaseManager.
     */
    public static DataBaseManager getInstance() {
        if (DataUseCase.getDBType() == REALM)
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
