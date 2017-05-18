package com.zeyad.usecases.stores;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.TestRealmModel;
import com.zeyad.usecases.db.DataBaseManager;
import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.mapper.DAOMapper;

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

import rx.Completable;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DiskDataStoreTest {

    private DiskDataStore mDiskDataStore;
    private DataBaseManager dbManager;

    @Before
    public void setUp() throws Exception {
        dbManager = mock(DataBaseManager.class);
        DAOMapper mapper = mock(DAOMapper.class);
        when(mapper.mapAllTo(any(List.class), any(Class.class))).thenReturn(new ArrayList());
        Config.setWithCache(false);
        mDiskDataStore = new DiskDataStore(dbManager, mapper);
    }

    @Test
    public void testGetAll() {
        List<TestRealmModel> testRealmObjects = new ArrayList<>();
        testRealmObjects.add(new TestRealmModel());
        Observable<List<TestRealmModel>> observable = Observable.just(testRealmObjects);
        when(dbManager.<TestRealmModel>getAll(any(Class.class))).thenReturn(observable);

        mDiskDataStore.dynamicGetList("", Object.class, false, false);

        Mockito.verify(dbManager, times(1)).getAll(any(Class.class));
    }

    @Test
    public void testGetObject() {
        Observable observable = Observable.just(new TestRealmModel());
        when(dbManager.getById(anyString(), anyInt(), any(Class.class))).thenReturn(observable);

        mDiskDataStore.dynamicGetObject("", "", 0, Object.class, false, false);

        Mockito.verify(dbManager, times(1)).getById(anyString(), anyInt(), any(Class.class));
    }

    @Test
    public void testSearchDiskRealmQuery() {
        when(dbManager.getQuery(any(RealmQueryProvider.class))).thenReturn(any(Observable.class));

        mDiskDataStore.queryDisk(realm -> realm.where(TestRealmModel.class));

        Mockito.verify(dbManager, times(1)).getQuery(any(RealmQueryProvider.class));
    }

    @Test
    public void testDynamicDeleteAll() {
        when(dbManager.evictAll(any(Class.class))).thenReturn(Completable.complete());

        mDiskDataStore.dynamicDeleteAll(TestRealmModel.class);

        Mockito.verify(dbManager, times(1)).evictAll(any(Class.class));
    }

    @Test
    public void testDynamicDeleteCollection() {
        when(dbManager.evictCollection(anyString(), anyList(), any(Class.class))).thenReturn(Completable.complete());

        mDiskDataStore.dynamicDeleteCollection("", "", new JSONArray(), Object.class, false, false);

        Mockito.verify(dbManager, times(1)).evictCollection(anyString(), anyListOf(Long.class), any(Class.class));
    }

    @Test
    public void testDynamicPatchObject() throws Exception {
        when(dbManager.put(any(JSONObject.class), anyString(), any(Class.class))).thenReturn(Completable.complete());

        mDiskDataStore.dynamicPatchObject("", "", new JSONObject(), Object.class, false, false);

        Mockito.verify(dbManager, times(1)).put(any(JSONObject.class), anyString(), any(Class.class));
    }

    @Test
    public void testDynamicPostObject() throws Exception {
        when(dbManager.put(any(JSONObject.class), anyString(), any(Class.class))).thenReturn(Completable.complete());

        mDiskDataStore.dynamicPostObject("", "", new JSONObject(), Object.class, false, false);

        Mockito.verify(dbManager, times(1)).put(any(JSONObject.class), anyString(), any(Class.class));
    }

    @Test
    public void testDynamicPutObject() throws Exception {
        when(dbManager.put(any(JSONObject.class), anyString(), any(Class.class))).thenReturn(Completable.complete());

        mDiskDataStore.dynamicPutObject("", "", new JSONObject(), Object.class, false, false);

        Mockito.verify(dbManager, times(1)).put(any(JSONObject.class), anyString(), any(Class.class));
    }

    @Test
    public void testDynamicPostList() throws Exception {
        when(dbManager.putAll(any(JSONArray.class), anyString(), any(Class.class))).thenReturn(Completable.complete());

        mDiskDataStore.dynamicPostList("", "", new JSONArray(), Object.class, false, false);

        Mockito.verify(dbManager, times(1)).putAll(any(JSONArray.class), anyString(), any(Class.class));
    }

    @Test
    public void testDynamicPutList() throws Exception {
        when(dbManager.putAll(any(JSONArray.class), anyString(), any(Class.class))).thenReturn(Completable.complete());

        mDiskDataStore.dynamicPutList("", "", new JSONArray(), Object.class, false, false);

        Mockito.verify(dbManager, times(1)).putAll(any(JSONArray.class), anyString(), any(Class.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testDynamicDownloadFile() throws Exception {
        Observable observable = mDiskDataStore.dynamicDownloadFile("", new File(""), false, false, false);

        // Verify repository interactions
        verifyZeroInteractions(dbManager);

        // Assert return type
        assertEquals(new IllegalStateException("Can not IO file to local DB"), observable.toBlocking().first());
    }

    @Test(expected = IllegalStateException.class)
    public void testDynamicUploadFile() throws Exception {
        Observable observable = mDiskDataStore.dynamicUploadFile("", new File(""), "", new HashMap<>(),
                false, false, false, Object.class);

        // Verify repository interactions
        verifyZeroInteractions(dbManager);

        // Assert return type
        assertEquals(new IllegalStateException("Can not IO file to local DB"), observable.toBlocking().first());
    }

}