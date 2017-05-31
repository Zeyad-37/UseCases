package com.zeyad.usecases.stores;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.mapper.DAOMapper;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.utils.DataBaseManagerUtil;

public class DataStoreFactory {
    private static final String DB_NOT_ENABLED = "Database not enabled!",
            DB_MANAGER_NULL = "DataBaseManager cannot be null!";
    @Nullable private final DataBaseManagerUtil mDataBaseManager;
    private final ApiConnection mApiConnection;
    private final DAOMapper mDAOMapper;

    public DataStoreFactory(ApiConnection restApi, DAOMapper daoMapper) {
        mDataBaseManager = null;
        mApiConnection = restApi;
        mDAOMapper = daoMapper;
    }

    public DataStoreFactory(
            @Nullable DataBaseManagerUtil dataBaseManager,
            ApiConnection restApi,
            DAOMapper daoMapper) {
        if (dataBaseManager == null) {
            throw new IllegalArgumentException(DB_MANAGER_NULL);
        }
        Config.setHasRealm(true);
        mDataBaseManager = dataBaseManager;
        mApiConnection = restApi;
        mDAOMapper = daoMapper;
    }

    /** Create {@link DataStore} . */
    @NonNull
    public DataStore dynamically(@NonNull String url, Class dataClass) throws Exception {
        if (!url.isEmpty()) {
            return cloud(dataClass);
        } else if (mDataBaseManager == null) {
            throw new IllegalAccessException(DB_NOT_ENABLED);
        } else {
            return disk(dataClass);
        }
    }

    /** Creates a disk {@link DataStore}. */
    @NonNull
    public DataStore disk(Class dataClass) throws IllegalAccessException {
        if (!Config.isWithRealm() || mDataBaseManager == null) {
            throw new IllegalAccessException(DB_NOT_ENABLED);
        }
        return new DiskDataStore(mDataBaseManager.getDataBaseManager(dataClass), mDAOMapper);
    }

    /** Creates a cloud {@link DataStore}. */
    @NonNull
    public DataStore cloud(Class dataClass) {
        return new CloudDataStore(
                mApiConnection,
                mDataBaseManager.getDataBaseManager(dataClass),
                mDAOMapper,
                Config.getInstance().getContext());
    }
}
