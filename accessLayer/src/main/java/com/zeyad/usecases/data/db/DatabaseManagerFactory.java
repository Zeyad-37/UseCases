package com.zeyad.usecases.data.db;

import com.zeyad.usecases.domain.interactors.data.DataUseCase;

/**
 * DatabaseManager interface implementer.
 */
public class DatabaseManagerFactory {
    /**
     * @return {@link DataBaseManager} the implemented instance of the DatabaseManager.
     */
    public static DataBaseManager getInstance() {
        if (DataUseCase.hasRealm())
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
