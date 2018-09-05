package com.zeyad.usecases.stores;

import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.TestRealmModel;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;

import st.lowlevel.storo.StoroBuilder;

/**
 * @author by ZIaDo on 6/5/17.
 */
@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(constants = BuildConfig.class, sdk = 25)
public class MemoryStoreTest { // TODO: 6/15/17 test ids
    private MemoryStore memoryStore;
    private static boolean initialize = true;

    @Before
    public void setUp() {
        if (initialize) {
            StoroBuilder.configure(8129)
                    .setCacheDirectory(RuntimeEnvironment.application, StoroBuilder.Storage.INTERNAL)
                    .setDefaultCacheDirectory(RuntimeEnvironment.application)
                    .setGsonInstance(Config.INSTANCE.getGson())
                    .initialize();
            initialize = false;
        }
        memoryStore = new MemoryStore(Config.INSTANCE.getGson(), new HashMap<>());
    }

    @Test
    public void getAllItems() {
        memoryStore.getAllItems(TestRealmModel.class);
    }

    @Test
    public void getObject() {
        memoryStore.getItem("", TestRealmModel.class);
    }

    @Test
    public void cacheObject() {
        memoryStore.cacheObject("", new JSONObject(), TestRealmModel.class);
    }

//    @Test
//    public void deleteList() {
//        memoryStore.deleteList(Collections.singletonList("1"), TestRealmModel.class);
//    }
}
