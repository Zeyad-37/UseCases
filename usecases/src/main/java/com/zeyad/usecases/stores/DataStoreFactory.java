package com.zeyad.usecases.stores;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.mapper.DAOMapper;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.utils.DataBaseManagerUtil;

public class DataStoreFactory {
    private final static String DB_NOT_ENABLED = "Database not enabled!";
    @Nullable
    private final DataBaseManagerUtil mDataBaseManagerUtil;
    private final ApiConnection mApiConnection;
    private final DAOMapper mDAOMapper;

    public DataStoreFactory(@Nullable DataBaseManagerUtil dataBaseManagerUtil, ApiConnection restApi, DAOMapper daoMapper) {
        Config.setHasRealm(dataBaseManagerUtil != null);
        mDataBaseManagerUtil = dataBaseManagerUtil;
        mApiConnection = restApi;
        mDAOMapper = daoMapper;
    }

    /**
     * Create {@link DataStore} .
     */
    @NonNull
    public DataStore dynamically(@NonNull String url, Class dataClass) throws Exception {
        if (!url.isEmpty()) {
            return cloud(dataClass);
        } else if (mDataBaseManagerUtil == null) {
            throw new IllegalAccessException(DB_NOT_ENABLED);
        } else {
            return disk(dataClass);
        }
    }

    /**
     * Creates a disk {@link DataStore}.
     */
    @NonNull
    public DataStore disk(Class dataClass) throws IllegalAccessException {
        if (!Config.isWithRealm() || mDataBaseManagerUtil == null) {
            throw new IllegalAccessException(DB_NOT_ENABLED);
        }
        return new DiskDataStore(mDataBaseManagerUtil.getDataBaseManager(dataClass), mDAOMapper);
    }

    /**
     * Creates a cloud {@link DataStore}.
     */
    @NonNull
    public DataStore cloud(Class dataClass) {
        return new CloudDataStore(mApiConnection, mDataBaseManagerUtil.getDataBaseManager(dataClass),
                mDAOMapper, Config.getInstance().getContext());
    }
}
