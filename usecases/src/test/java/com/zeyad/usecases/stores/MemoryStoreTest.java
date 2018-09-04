package com.zeyad.usecases.stores;

import com.google.gson.Gson;
import com.zeyad.usecases.TestRealmModel;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

/**
 * @author by ZIaDo on 6/5/17.
 */
public class MemoryStoreTest { // TODO: 6/15/17 test ids
    private MemoryStore memoryStore;

    @Before
    public void setUp() {
        memoryStore = new MemoryStore(new Gson(), new HashMap<>());
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

    @Test
    public void deleteList() {
        memoryStore.deleteList(Collections.singletonList("1"), TestRealmModel.class);
    }
}
