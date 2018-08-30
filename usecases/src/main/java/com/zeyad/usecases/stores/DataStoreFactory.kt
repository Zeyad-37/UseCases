package com.zeyad.usecases.stores

import com.zeyad.usecases.Config
import com.zeyad.usecases.mapper.DAOMapper
import com.zeyad.usecases.network.ApiConnection
import com.zeyad.usecases.utils.DataBaseManagerUtil

class DataStoreFactory(private val dataBaseManagerUtil: DataBaseManagerUtil?,
                       private val restApi: ApiConnection,
                       private val daoMapper: DAOMapper) {

    private val withCache: Boolean = Config.withCache

    fun dynamically(url: String, dataClass: Class<*>): DataStore {
        return if (!url.isEmpty()) {
            cloud(dataClass)
        } else if (dataBaseManagerUtil == null) {
            throw IllegalAccessException(DB_NOT_ENABLED)
        } else {
            disk(dataClass)
        }
    }

    /**
     * Creates a disk [DataStore].
     */
    fun memory(): MemoryStore? {
        if (withCache && memoryStore == null) {
            memoryStore = MemoryStore(Config.gson)
        }
        return if (withCache) memoryStore else null
    }

    /**
     * Creates a disk [DataStore].
     */
    @Throws(IllegalAccessException::class)
    fun disk(dataClass: Class<*>): DataStore {
        if (dataBaseManagerUtil == null) {
            throw IllegalAccessException(DB_NOT_ENABLED)
        } else if (diskStore == null) {
            diskStore = DiskStore(dataBaseManagerUtil.getDataBaseManager(dataClass)!!, memory())
        }
        return diskStore as DiskStore
    }

    /**
     * Creates a cloud [DataStore].
     */
    fun cloud(dataClass: Class<*>): DataStore {
        if (cloudStore == null) {
            cloudStore = CloudStore(restApi,
                    dataBaseManagerUtil?.getDataBaseManager(dataClass)!!, daoMapper, memory())
        }
        return cloudStore as CloudStore
    }

    companion object {
        private var cloudStore: CloudStore? = null
        private var diskStore: DiskStore? = null
        private var memoryStore: MemoryStore? = null
        private const val DB_NOT_ENABLED = "Database not enabled!"
    }
}
