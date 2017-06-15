package com.zeyad.usecases.stores;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

/**
 * @author by ZIaDo on 6/5/17.
 */
public class MemoryStoreTest { // TODO: 6/15/17 test ids
    private MemoryStore memoryStore;
    @Before
    public void setUp() throws Exception {
        memoryStore = new MemoryStore(new Gson());
    }

    @Test
    public void getAllItems() throws Exception {
//        memoryStore.getAllItems(TestRealmModel.class);
    }

    @Test
    public void getObject() throws Exception {
//        memoryStore.getItem("", TestRealmModel.class);
    }

    @Test
    public void cacheObject() throws Exception {
//        memoryStore.cacheObject("", new JSONObject(), TestRealmModel.class);
    }

    @Test
    public void deleteList() throws Exception {
//        memoryStore.deleteList(Collections.singletonList(1L), TestRealmModel.class);
    }
}
