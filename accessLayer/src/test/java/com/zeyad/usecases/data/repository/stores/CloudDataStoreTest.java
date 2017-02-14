package com.zeyad.usecases.data.repository.stores;

import android.content.Context;
import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.network.RestApi;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.utils.TestRealmObject;
import com.zeyad.usecases.utils.TestUtility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author by ZIaDo on 2/14/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CloudDataStoreTest {
    private CloudDataStore cloudDataStore;
    private Context mockContext;
    private RestApi mockRestApi;
    private DataBaseManager mockDataBaseManager;
    private IDAOMapper mockEntityDataMapper;
    private Observable observable;

    @Before
    public void setUp() throws Exception {
        observable = Observable.just(true);
        mockContext = mock(Context.class);
        mockRestApi = mock(RestApiImpl.class);
        mockDataBaseManager = mock(DataBaseManager.class);
        mockEntityDataMapper = mock(IDAOMapper.class);
        TestUtility.changeStateOfNetwork(mockContext, true);
        cloudDataStore = new CloudDataStore(mockRestApi, mockDataBaseManager, mockEntityDataMapper, mockContext);
    }

    @Test
    public void dynamicGetObject() throws Exception {
        when(mockRestApi.dynamicGetObject(anyString(), anyBoolean())).thenReturn(observable);

        cloudDataStore.dynamicGetObject("", "", 0, Object.class, Object.class, false, false);

        verify(mockRestApi, times(1)).dynamicGetObject(anyString(), anyBoolean());
    }

    @Test
    public void dynamicGetList() throws Exception {
        List<TestRealmObject> testRealmObjects = new ArrayList<>();
        testRealmObjects.add(new TestRealmObject());
        Observable<List> observable = Observable.just(testRealmObjects);
        when(mockRestApi.dynamicGetList(anyString(), anyBoolean())).thenReturn(observable);

        cloudDataStore.dynamicGetList("", Object.class, Object.class, false, false);

        verify(mockRestApi, times(1)).dynamicGetList(anyString(), anyBoolean());
    }

    @Test
    public void dynamicPatchObject() throws Exception {
        when(mockRestApi.dynamicPatch(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPatchObject("", "", new JSONObject(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPatch(anyString(), any(RequestBody.class));
    }

    @Test
    public void dynamicPostObject() throws Exception {
        when(mockRestApi.dynamicPost(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPostObject("", "", new JSONObject(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPost(anyString(), any(RequestBody.class));
    }

    @Test
    public void dynamicPostList() throws Exception {
        when(mockRestApi.dynamicPost(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPostList("", "", new JSONArray(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPost(anyString(), any(RequestBody.class));
    }

    @Test
    public void dynamicPutObject() throws Exception {
        when(mockRestApi.dynamicPut(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPutObject("", "", new JSONObject(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPut(anyString(), any(RequestBody.class));
    }

    @Test
    public void dynamicPutList() throws Exception {
        when(mockRestApi.dynamicPut(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPutList("", "", new JSONArray(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPut(anyString(), any(RequestBody.class));
    }

    @Test
    public void dynamicDeleteCollection() throws Exception {
        when(mockRestApi.dynamicDelete(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicDeleteCollection("", "", new JSONArray(), Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicDelete(anyString(), any(RequestBody.class));
    }

    @Test(expected = IllegalStateException.class)
    public void dynamicDeleteAll() throws Exception {
        Observable observable = cloudDataStore.dynamicDeleteAll(Object.class);

        // Verify repository interactions
        verifyZeroInteractions(mockRestApi);

        // Assert return type
        assertEquals(new IllegalStateException("Can not IO file to local DB"), observable.toBlocking().first());
    }

    @Test
    public void dynamicUploadFile() throws Exception {
        when(mockRestApi.dynamicUpload(anyString(), anyMap(), any(MultipartBody.Part.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicUploadFile("", new File(""), "", new HashMap(), false, false, false, Object.class)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicUpload(anyString(), anyMap(), any(MultipartBody.Part.class));
    }

    @Test
    public void dynamicDownloadFile() throws Exception {
        when(mockRestApi.dynamicDownload(anyString())).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicDownloadFile("", new File(""), false, false, false)
                .subscribe(testSubscriber);

//        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicDownload(anyString());
    }

    @Test(expected = RuntimeException.class)
    public void queryDisk() throws Exception {
        Observable observable = cloudDataStore.queryDisk(realm -> realm.where(TestRealmObject.class),
                Object.class);

        // Verify repository interactions
        verifyZeroInteractions(mockRestApi);

        // Assert return type
        assertEquals(new RuntimeException(), observable.toBlocking().first());
    }
}
