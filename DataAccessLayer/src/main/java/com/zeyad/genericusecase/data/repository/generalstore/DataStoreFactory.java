package com.zeyad.genericusecase.data.repository.generalstore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.R;
import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.network.RestApiImpl;
import com.zeyad.genericusecase.data.utils.Utils;

import static com.zeyad.genericusecase.Config.NONE;
import static com.zeyad.genericusecase.Config.getInstance;


public class DataStoreFactory {

    private final Context mContext;
    @Nullable
    private DataBaseManager mDataBaseManager;

    public DataStoreFactory(Context context) {
        mContext = context;
        mDataBaseManager = null;
        Config.getInstance().setDBType(NONE);
    }

    public DataStoreFactory(@Nullable DataBaseManager dataBaseManager, Context context) {
        if (dataBaseManager == null)
            throw new IllegalArgumentException(context.getString(R.string.dbmanager_null_error));
        mContext = context;
        mDataBaseManager = dataBaseManager;
    }

    /**
     * Create {@link DataStore} .
     */
    @NonNull
    public DataStore dynamically(@NonNull String url, EntityMapper entityDataMapper,
                                 @NonNull Class dataClass) throws IllegalAccessException {
        if (!url.isEmpty())
            return new CloudDataStore(new RestApiImpl(), mDataBaseManager, entityDataMapper);
        else if (mDataBaseManager != null && url.isEmpty() && (mDataBaseManager
                .areItemsValid(DataBaseManager.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE
                        + dataClass.getSimpleName()) || !Utils.isNetworkAvailable(mContext)))
            return new DiskDataStore(mDataBaseManager, entityDataMapper);
        else
            throw new IllegalAccessException(getInstance().getContext().getString(R.string.no_db));
    }

    /**
     * Create {@link DataStore} from an id.
     */
    @NonNull
    public DataStore dynamically(@NonNull String url, String idColumnName, int id,
                                 EntityMapper entityDataMapper, Class dataClass) throws IllegalAccessException {
        if (!url.isEmpty())
            return new CloudDataStore(new RestApiImpl(), mDataBaseManager, entityDataMapper);
        else if (mDataBaseManager != null && url.isEmpty() && (mDataBaseManager.isItemValid(id,
                idColumnName, dataClass) || !Utils.isNetworkAvailable(mContext)))
            return new DiskDataStore(mDataBaseManager, entityDataMapper);
        else
            throw new IllegalAccessException(getInstance().getContext().getString(R.string.no_db));
    }

    /**
     * Creates a disk {@link DataStore}.
     */
    @NonNull
    public DataStore disk(EntityMapper entityDataMapper) throws IllegalAccessException {
        if (Config.getInstance().getDBType() == NONE || mDataBaseManager == null)
            throw new IllegalAccessException(getInstance().getContext().getString(R.string.no_db));
        return new DiskDataStore(mDataBaseManager, entityDataMapper);
    }

    /**
     * Creates a cloud {@link DataStore}.
     */
    @NonNull
    public DataStore cloud(EntityMapper entityDataMapper) {
        return new CloudDataStore(new RestApiImpl(), mDataBaseManager, entityDataMapper);
    }
}