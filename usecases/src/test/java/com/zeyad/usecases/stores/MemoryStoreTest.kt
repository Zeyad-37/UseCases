package com.zeyad.usecases.stores

import android.support.test.rule.BuildConfig
import com.zeyad.usecases.Config
import com.zeyad.usecases.TestRealmModel
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import st.lowlevel.storo.StoroBuilder
import java.util.*

/**
 * @author by ZIaDo on 6/5/17.
 */
@RunWith(RobolectricTestRunner::class)
@org.robolectric.annotation.Config(constants = BuildConfig::class, sdk = intArrayOf(25))
class MemoryStoreTest { // TODO: 6/15/17 test ids
    private var memoryStore: MemoryStore? = null

    @Before
    fun setUp() {
        if (initialize) {
            StoroBuilder.configure(8129)
                    .setCacheDirectory(RuntimeEnvironment.application, StoroBuilder.Storage.INTERNAL)
                    .setDefaultCacheDirectory(RuntimeEnvironment.application)
                    .setGsonInstance(Config.gson)
                    .initialize()
            initialize = false
        }
        memoryStore = MemoryStore(Config.gson, HashMap())
    }

    @Test
    fun getAllItems() {
        memoryStore!!.getAllItems(TestRealmModel::class.java)
    }

    @Test
    fun getObject() {
        memoryStore!!.getItem<Any>("", TestRealmModel::class.java)
    }

    @Test
    fun cacheObject() {
        memoryStore!!.cacheObject("", JSONObject(), TestRealmModel::class.java)
    }

    companion object {
        private var initialize = true
    }

    //    @Test
    //    public void deleteListById() {
    //        memoryStore.deleteListById(Collections.singletonList("1"), TestRealmModel.class);
    //    }
}
