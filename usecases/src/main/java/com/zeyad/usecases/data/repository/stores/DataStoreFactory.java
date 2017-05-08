package com.zeyad.usecases.data.repository.stores;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.mapper.DAOMapper;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.domain.interactors.DataUseCase;

public class DataStoreFactory {
    private final static String DB_NOT_ENABLED = "Database not enabled!", DB_MANAGER_NULL = "DataBaseManager cannot be null!";
    @Nullable
    private DataBaseManager mDataBaseManager;
    private RestApiImpl mRestApi;
    private DAOMapper mDAOMapper;

    public DataStoreFactory(RestApiImpl restApi, DAOMapper daoMapper) {
        mDataBaseManager = null;
        mRestApi = restApi;
        mDAOMapper = daoMapper;
    }

    public DataStoreFactory(DataBaseManager dataBaseManager, RestApiImpl restApi, DAOMapper daoMapper) {
        if (dataBaseManager == null)
            throw new IllegalArgumentException(DB_MANAGER_NULL);
        DataUseCase.setHasRealm(true);
        mDataBaseManager = dataBaseManager;
        mRestApi = restApi;
        mDAOMapper = daoMapper;
    }

    /**
     * Create {@link DataStore} .
     */
    @NonNull
    public DataStore dynamically(@NonNull String url) throws Exception {
        if (!url.isEmpty())
            return cloud();
        else if (mDataBaseManager == null)
            throw new IllegalAccessException(DB_NOT_ENABLED);
        else
            return disk();
    }

    /**
     * Creates a disk {@link DataStore}.
     */
    @NonNull
    public DataStore disk() throws IllegalAccessException {
        if (!DataUseCase.hasRealm() || mDataBaseManager == null) {
            throw new IllegalAccessException(DB_NOT_ENABLED);
        }
        return new DiskDataStore(mDataBaseManager, mDAOMapper);
    }

    /**
     * Creates a cloud {@link DataStore}.
     */
    @NonNull
    public DataStore cloud() {
        return new CloudDataStore(mRestApi, mDataBaseManager, mDAOMapper);
    }
}
