package com.zeyad.usecases.stores;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.TestRealmModel;
import com.zeyad.usecases.db.DataBaseManager;
import com.zeyad.usecases.db.RealmQueryProvider;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DiskStoreTest { // TODO: 6/5/17 add cache verifications

    private DiskStore mDiskStore;
    private DataBaseManager dbManager;

    @Before
    public void setUp() throws Exception {
        dbManager = mock(DataBaseManager.class);
        Config.setWithCache(false);
        Config.setGson();
        mDiskStore = new DiskStore(dbManager, new MemoryStore(Config.getGson()));
    }

    @Test
    public void testGetAll() {
        List<TestRealmModel> testRealmObjects = new ArrayList<>();
        testRealmObjects.add(new TestRealmModel());
        Flowable<List<TestRealmModel>> observable = Flowable.just(testRealmObjects);
        when(dbManager.<TestRealmModel>getAll(any(Class.class))).thenReturn(observable);

        mDiskStore.dynamicGetList("", Object.class, false, false);

        Mockito.verify(dbManager, times(1)).getAll(any(Class.class));
    }

    @Test
    public void testGetObject() {
        Flowable observable = Flowable.just(new TestRealmModel());
        when(dbManager.getById(anyString(), any(), any(Class.class), any(Class.class)))
                .thenReturn(observable);

        mDiskStore.dynamicGetObject("", "", 0L, long.class, Object.class, false, false);

        Mockito.verify(dbManager, times(1))
                .getById(anyString(), any(), any(Class.class), any(Class.class));
    }

    @Test
    public void testSearchDiskRealmQuery() {
        when(dbManager.getQuery(any(RealmQueryProvider.class))).thenReturn(any(Flowable.class));

        mDiskStore.queryDisk(realm -> realm.where(TestRealmModel.class));

        Mockito.verify(dbManager, times(1)).getQuery(any(RealmQueryProvider.class));
    }

    @Test
    public void testDynamicDeleteAll() {
        when(dbManager.evictAll(any(Class.class))).thenReturn(Single.just(true));

        mDiskStore.dynamicDeleteAll(TestRealmModel.class);

        Mockito.verify(dbManager, times(1)).evictAll(any(Class.class));
    }

    @Test
    public void testDynamicDeleteCollection() {
        when(dbManager.evictCollection(anyString(), anyList(), any(Class.class)))
                .thenReturn(Single.just(true));

        mDiskStore.dynamicDeleteCollection(
                "", "", new JSONArray(), Object.class, Object.class, false, false, false);

        Mockito.verify(dbManager, times(1))
                .evictCollection(anyString(), anyListOf(Long.class), any(Class.class));
    }

    @Test
    public void testDynamicPatchObject() throws Exception {
        when(dbManager.put(any(JSONObject.class), anyString(), any(Class.class)))
                .thenReturn(Single.just(true));

        mDiskStore.dynamicPatchObject(
                "", "", new JSONObject(), Object.class, Object.class, false, false, false);

        Mockito.verify(dbManager, times(1))
                .put(any(JSONObject.class), anyString(), any(Class.class));
    }

    @Test
    public void testDynamicPostObject() throws Exception {
        when(dbManager.put(any(JSONObject.class), anyString(), any(Class.class)))
                .thenReturn(Single.just(true));

        mDiskStore.dynamicPostObject(
                "", "", new JSONObject(), Object.class, Object.class, false, false, false);

        Mockito.verify(dbManager, times(1))
                .put(any(JSONObject.class), anyString(), any(Class.class));
    }

    @Test
    public void testDynamicPutObject() throws Exception {
        when(dbManager.put(any(JSONObject.class), anyString(), any(Class.class)))
                .thenReturn(Single.just(true));

        mDiskStore.dynamicPutObject(
                "", "", new JSONObject(), Object.class, Object.class, false, false, false);

        Mockito.verify(dbManager, times(1))
                .put(any(JSONObject.class), anyString(), any(Class.class));
    }

    @Test
    public void testDynamicPostList() throws Exception {
        when(dbManager.putAll(any(JSONArray.class), anyString(), any(Class.class)))
                .thenReturn(Single.just(true));

        mDiskStore.dynamicPostList(
                "", "", new JSONArray(), Object.class, Object.class, false, false, false);

        Mockito.verify(dbManager, times(1))
                .putAll(any(JSONArray.class), anyString(), any(Class.class));
    }

    @Test
    public void testDynamicPutList() throws Exception {
        when(dbManager.putAll(any(JSONArray.class), anyString(), any(Class.class)))
                .thenReturn(Single.just(true));

        mDiskStore.dynamicPutList(
                "", "", new JSONArray(), Object.class, Object.class, false, false, false);

        Mockito.verify(dbManager, times(1))
                .putAll(any(JSONArray.class), anyString(), any(Class.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testDynamicDownloadFile() throws Exception {
        Flowable observable =
                mDiskStore.dynamicDownloadFile("", new File(""), false, false, false);

        // Verify repository interactions
        verifyZeroInteractions(dbManager);

        // Assert return type
        IllegalStateException expected = new IllegalStateException("Can not IO file to local DB");
        assertEquals(
                expected.getMessage(),
                ((IllegalStateException) observable.first(expected).blockingGet()).getMessage());
    }

    @Test(expected = IllegalStateException.class)
    public void testDynamicUploadFile() throws Exception {
        Flowable observable =
                mDiskStore.dynamicUploadFile(
                        "", new File(""), "", new HashMap<>(), false, false, false, Object.class);

        // Verify repository interactions
        verifyZeroInteractions(dbManager);

        // Assert return type
        IllegalStateException expected = new IllegalStateException("Can not IO file to local DB");
        assertEquals(
                expected.getMessage(),
                ((IllegalStateException) observable.first(expected).blockingGet()).getMessage());
    }
}

