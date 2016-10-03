package com.zeyad.genericusecase.data.repository.generalstore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.network.RestApiImpl;
import com.zeyad.genericusecase.data.utils.Utils;


public class DataStoreFactory {

    private final Context mContext;
    private final GcmNetworkManager mGCMNetworkManager;
    @Nullable
    private DataBaseManager mDataBaseManager;

    public DataStoreFactory(@Nullable DataBaseManager dataBaseManager, Context context) {
        if (dataBaseManager == null)
            throw new IllegalArgumentException("Constructor parameters cannot be null!");
        mContext = context;
        mDataBaseManager = dataBaseManager;
        mGCMNetworkManager = GcmNetworkManager.getInstance(mContext);
    }

    DataStoreFactory(@Nullable DataBaseManager dataBaseManager, Context context, GcmNetworkManager gcmNetworkManager) {
        if (dataBaseManager == null)
            throw new IllegalArgumentException("Constructor parameters cannot be null!");
        mContext = context;
        mDataBaseManager = dataBaseManager;
        mGCMNetworkManager = gcmNetworkManager;
    }

    /**
     * Create {@link DataStore} .
     */
    @NonNull
    public DataStore dynamically(@NonNull String url, EntityMapper entityDataMapper, @NonNull Class dataClass) {
        if (url.isEmpty() && (mDataBaseManager.areItemsValid(DataBaseManager.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE
                + dataClass.getSimpleName()) || !Utils.isNetworkAvailable(mContext)))
            return new DiskDataStore(mDataBaseManager, entityDataMapper);
        else if (!url.isEmpty())
            return new CloudDataStore(new RestApiImpl(), mDataBaseManager, entityDataMapper, mGCMNetworkManager);
        else return new DiskDataStore(mDataBaseManager, entityDataMapper);
    }

    /**
     * Create {@link DataStore} from an id.
     */
    @NonNull
    public DataStore dynamically(@NonNull String url, String idColumnName, int id, EntityMapper entityDataMapper,
                                 Class dataClass) {
        if (url.isEmpty() && (mDataBaseManager.isItemValid(id, idColumnName, dataClass) || !Utils.isNetworkAvailable(mContext)))
            return new DiskDataStore(mDataBaseManager, entityDataMapper);
        else if (!url.isEmpty())
            return new CloudDataStore(new RestApiImpl(), mDataBaseManager, entityDataMapper, mGCMNetworkManager);
        else return new DiskDataStore(mDataBaseManager, entityDataMapper);
    }

    /**
     * Creates a disk {@link DataStore}.
     */
    @NonNull
    public DataStore disk(EntityMapper entityDataMapper) {
        return new DiskDataStore(mDataBaseManager, entityDataMapper);
    }

    /**
     * Creates a cloud {@link DataStore}.
     */
    @NonNull
    public DataStore cloud(EntityMapper entityDataMapper) {
        return new CloudDataStore(new RestApiImpl(), mDataBaseManager, entityDataMapper, mGCMNetworkManager);
    }
}