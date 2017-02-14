package com.zeyad.usecases.data.repository.stores;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.domain.interactors.data.DataUseCase;

public class DataStoreFactory {
    private final static String DB_NOT_ENABLED = "Database not enabled!", DB_MANAGER_NULL = "DataBaseManager cannot be null!";
    @Nullable
    private DataBaseManager mDataBaseManager;
    private RestApiImpl mRestApi;

    public DataStoreFactory(RestApiImpl restApi) {
        mDataBaseManager = null;
        mRestApi = restApi;
    }

    public DataStoreFactory(@Nullable DataBaseManager dataBaseManager, RestApiImpl restApi) {
        if (dataBaseManager == null)
            throw new IllegalArgumentException(DB_MANAGER_NULL);
        mDataBaseManager = dataBaseManager;
        mRestApi = restApi;
    }

    /**
     * Create {@link DataStore} .
     */
    @NonNull
    public DataStore dynamically(@NonNull String url, IDAOMapper entityDataMapper) throws Exception {
        if (!url.isEmpty())
            return new CloudDataStore(mRestApi, mDataBaseManager, entityDataMapper);
        else if (mDataBaseManager == null)
            throw new IllegalAccessException(DB_NOT_ENABLED);
        else
            return new DiskDataStore(mDataBaseManager, entityDataMapper);
    }

    /**
     * Creates a disk {@link DataStore}.
     */
    @NonNull
    public DataStore disk(IDAOMapper entityDataMapper) throws IllegalAccessException {
        if (!DataUseCase.hasRealm() || mDataBaseManager == null) {
            throw new IllegalAccessException(DB_NOT_ENABLED);
        }
        return new DiskDataStore(mDataBaseManager, entityDataMapper);
    }

    /**
     * Creates a cloud {@link DataStore}.
     */
    @NonNull
    public DataStore cloud(IDAOMapper entityDataMapper) {
        return new CloudDataStore(mRestApi, mDataBaseManager, entityDataMapper);
    }
}
