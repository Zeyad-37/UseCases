package com.zeyad.genericusecase.data.repository.generalstore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.network.RestApiImpl;


public class DataStoreFactory {

    @Nullable
    private DataBaseManager mRealmManager;
    private final Context mContext;
    private final GcmNetworkManager mGCMNetworkManager;

    public DataStoreFactory(@Nullable DataBaseManager realmManager, Context context) {
        if (realmManager == null)
            throw new IllegalArgumentException("Constructor parameters cannot be null!");
        mContext = context;
        mRealmManager = realmManager;
        mGCMNetworkManager = GcmNetworkManager.getInstance(mContext);
    }

    DataStoreFactory(@Nullable DataBaseManager realmManager, Context context, GcmNetworkManager gcmNetworkManager) {
        if (realmManager == null)
            throw new IllegalArgumentException("Constructor parameters cannot be null!");
        mContext = context;
        mRealmManager = realmManager;
        mGCMNetworkManager = gcmNetworkManager;
    }

    /**
     * Create {@link DataStore} .
     */
    @NonNull
    public DataStore dynamically(@NonNull String url, EntityMapper entityDataMapper, @NonNull Class dataClass) {
        if (url.isEmpty() && (mRealmManager.areItemsValid(com.zeyad.genericusecase.data.utils.Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE
                + dataClass.getSimpleName()) || !com.zeyad.genericusecase.data.utils.Utils.isNetworkAvailable(mContext)))
            return new DiskDataStore(mRealmManager, entityDataMapper);
        else if (!url.isEmpty())
            return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper, mGCMNetworkManager);
        else return new DiskDataStore(mRealmManager, entityDataMapper);
    }

    /**
     * Create {@link DataStore} from an id.
     */
    @NonNull
    public DataStore dynamically(@NonNull String url, String idColumnName, int id, EntityMapper entityDataMapper,
                                 Class dataClass) {
        if (url.isEmpty() && (mRealmManager.isItemValid(id, idColumnName, dataClass) || !com.zeyad.genericusecase.data.utils.Utils.isNetworkAvailable(mContext)))
            return new DiskDataStore(mRealmManager, entityDataMapper);
        else if (!url.isEmpty())
            return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper, mGCMNetworkManager);
        else return new DiskDataStore(mRealmManager, entityDataMapper);
    }

    /**
     * Creates a disk {@link DataStore}.
     */
    @NonNull
    public DataStore disk(EntityMapper entityDataMapper) {
        return new DiskDataStore(mRealmManager, entityDataMapper);
    }

    /**
     * Creates a cloud {@link DataStore}.
     */
    @NonNull
    public DataStore cloud(EntityMapper entityDataMapper) {
        return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper, mGCMNetworkManager);
    }
}