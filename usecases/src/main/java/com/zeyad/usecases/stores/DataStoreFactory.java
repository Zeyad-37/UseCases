package com.zeyad.usecases.stores;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.mapper.DAOMapper;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.utils.DataBaseManagerUtil;
import com.zeyad.usecases.utils.Utils;

public class DataStoreFactory {
    private final static String DB_NOT_ENABLED = "Database not enabled!";
    private static CloudStore mCloudStore;
    private static DiskStore mDiskStore;
    private static MemoryStore mMemoryStore;
    private final DataBaseManagerUtil mDataBaseManagerUtil;
    private final ApiConnection mApiConnection;
    private final DAOMapper mDAOMapper;
    private final boolean withCache;

    public DataStoreFactory(@Nullable DataBaseManagerUtil dataBaseManagerUtil, ApiConnection restApi,
                            DAOMapper daoMapper) {
//        Config.INSTANCE.setHasRealm(dataBaseManagerUtil != null);
//        Config.INSTANCE.setWithSQLite(dataBaseManagerUtil != null);
        mDataBaseManagerUtil = dataBaseManagerUtil;
        mApiConnection = restApi;
        mDAOMapper = daoMapper;
        withCache = Config.INSTANCE.getWithCache();
    }

    /**
     * Create {@link DataStore} .
     */
    @NonNull
    public DataStore dynamically(@NonNull String url, Class dataClass) throws IllegalAccessException {
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
    public MemoryStore memory() {
        if (withCache && mMemoryStore == null) {
            mMemoryStore = new MemoryStore(Config.INSTANCE.getGson());
        }
        return withCache ? mMemoryStore : null;
    }

    /**
     * Creates a disk {@link DataStore}.
     */
    @NonNull
    public DataStore disk(Class dataClass) throws IllegalAccessException {
        if (!Config.INSTANCE.getWithRealm() || mDataBaseManagerUtil == null) {
            throw new IllegalAccessException(DB_NOT_ENABLED);
        } else if (mDiskStore == null) {
            mDiskStore = new DiskStore(mDataBaseManagerUtil.getDataBaseManager(dataClass), memory());
        }
        return mDiskStore;
    }

    /**
     * Creates a cloud {@link DataStore}.
     */
    @NonNull
    public DataStore cloud(Class dataClass) {
        if (mCloudStore == null) {
            mCloudStore = new CloudStore(mApiConnection,
                    mDataBaseManagerUtil.getDataBaseManager(dataClass), mDAOMapper, memory(),
                    Utils.getInstance());
        }
        return mCloudStore;
    }
}
