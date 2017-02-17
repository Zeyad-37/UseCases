package com.zeyad.usecases.data.repository.stores;

import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.utils.TestRealmModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(constants = BuildConfig.class, sdk = 21)
public class DiskDataStoreJUnitTest {

    private DiskDataStore mDiskDataStore;
    private DataBaseManager dbManager;
    private IDAOMapper mapper;

    @Before
    public void setUp() throws Exception {
        dbManager = mock(DataBaseManager.class);
        mapper = mock(IDAOMapper.class);
        when(mapper.mapAllToDomain(any(List.class), any(Class.class))).thenReturn(new ArrayList());
        Config.setWithCache(false);
        mDiskDataStore = new DiskDataStore(dbManager, mapper);
    }

    @Test
    public void testGetAll() {
        List<TestRealmModel> testRealmObjects = new ArrayList<>();
        testRealmObjects.add(new TestRealmModel());
        Observable<List> observable = Observable.just(testRealmObjects);
        when(dbManager.getAll(any(Class.class))).thenReturn(observable);

        mDiskDataStore.dynamicGetList("", Object.class, Object.class, false, false);

        Mockito.verify(dbManager, times(1)).getAll(any(Class.class));
    }

    @Test
    public void testGetObject() {
        Observable observable = Observable.just(new TestRealmModel());
        when(dbManager.getById(anyString(), anyInt(), any(Class.class))).thenReturn(observable);

        mDiskDataStore.dynamicGetObject("", "", 0, Object.class, Object.class, false, false);

        Mockito.verify(dbManager, times(1)).getById(anyString(), anyInt(), any(Class.class));
    }

//    @Test
//    public void testSearchDiskRealmQuery() {
//        when(dbManager.getQuery(any(RealmManager.RealmQueryProvider.class))).thenReturn(any(Observable.class));
//
//        mDiskDataStore.queryDisk(realm -> realm.where(TestRealmModel.class), TestRealmModel.class);
//
//        Mockito.verify(dbManager, times(1)).getQuery(any(RealmManager.RealmQueryProvider.class));
//    }

    @Test
    public void testDynamicDeleteAll() {
        Observable<Boolean> observable = Observable.just(true);
        when(dbManager.evictAll(any(Class.class))).thenReturn(observable);

        mDiskDataStore.dynamicDeleteAll(TestRealmModel.class);

        Mockito.verify(dbManager, times(1)).evictAll(any(Class.class));
    }

    @Test
    public void testDynamicDeleteCollection() {
        Observable<Boolean> observable = Observable.just(true);
        when(dbManager.evictCollection(anyString(), anyList(), any(Class.class))).thenReturn(observable);

        mDiskDataStore.dynamicDeleteCollection("", "", new JSONArray(), Object.class, false, false);

        Mockito.verify(dbManager, times(1)).evictCollection(anyString(), anyListOf(Long.class), any(Class.class));
    }

    @Test
    public void testDynamicPatchObject() throws Exception {
        Observable observable = Observable.just(true);
        when(dbManager.put(any(JSONObject.class), anyString(), any(Class.class))).thenReturn(observable);

        mDiskDataStore.dynamicPatchObject("", "", new JSONObject(), Object.class, Object.class, false, false);

        Mockito.verify(dbManager, times(1)).put(any(JSONObject.class), anyString(), any(Class.class));
    }

    @Test
    public void testDynamicPostObject() throws Exception {
        Observable observable = Observable.just(true);
        when(dbManager.put(any(JSONObject.class), anyString(), any(Class.class))).thenReturn(observable);

        mDiskDataStore.dynamicPostObject("", "", new JSONObject(), Object.class, Object.class, false, false);

        Mockito.verify(dbManager, times(1)).put(any(JSONObject.class), anyString(), any(Class.class));
    }

    @Test
    public void testDynamicPutObject() throws Exception {
        Observable observable = Observable.just(true);
        when(dbManager.put(any(JSONObject.class), anyString(), any(Class.class))).thenReturn(observable);

        mDiskDataStore.dynamicPutObject("", "", new JSONObject(), Object.class, Object.class, false, false);

        Mockito.verify(dbManager, times(1)).put(any(JSONObject.class), anyString(), any(Class.class));
    }

    @Test
    public void testDynamicPostList() throws Exception {
        Observable observable = Observable.just(true);
        when(dbManager.putAll(any(JSONArray.class), anyString(), any(Class.class))).thenReturn(observable);

        mDiskDataStore.dynamicPostList("", "", new JSONArray(), Object.class, Object.class, false, false);

        Mockito.verify(dbManager, times(1)).putAll(any(JSONArray.class), anyString(), any(Class.class));
    }

    @Test
    public void testDynamicPutList() throws Exception {
        Observable observable = Observable.just(true);
        when(dbManager.putAll(any(JSONArray.class), anyString(), any(Class.class))).thenReturn(observable);

        mDiskDataStore.dynamicPutList("", "", new JSONArray(), Object.class, Object.class, false, false);

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