package com.zeyad.usecases.data.repository.stores;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.db.RealmManager;
import com.zeyad.usecases.data.exceptions.NetworkConnectionException;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.network.RestApi;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.utils.Utils;
import com.zeyad.usecases.utils.TestRealmObject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmModel;
import io.realm.RealmObject;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
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
        observable = Observable.just(new Object());
        mockContext = mock(Context.class);
        mockRestApi = mock(RestApiImpl.class);
        mockDataBaseManager = mock(RealmManager.class);
        mockEntityDataMapper = mock(IDAOMapper.class);
        changeStateOfNetwork(mockContext, true);
        when(mockDataBaseManager.put(any(JSONObject.class), anyString(), any(Class.class))).thenReturn(observable);
        when(mockDataBaseManager.putAll(any(JSONArray.class), anyString(), any(Class.class))).thenReturn(observable);
        when(mockDataBaseManager.putAll(anyList(), any(Class.class))).thenReturn(observable);
        cloudDataStore = new CloudDataStore(mockRestApi, mockDataBaseManager, mockEntityDataMapper, mockContext);
    }

    @Test
    public void dynamicGetObject() throws Exception {
        when(mockRestApi.dynamicGetObject(anyString(), anyBoolean())).thenReturn(observable);

        cloudDataStore.dynamicGetObject("", "", 0, Object.class, Object.class, false, false);

        verify(mockRestApi, times(1)).dynamicGetObject(anyString(), anyBoolean());
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicGetObjectCanWillPersist() throws Exception {
        cloudDataStore.mCanPersist = true;
        when(mockRestApi.dynamicGetObject(anyString(), anyBoolean())).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicGetObject("", "", 0, Object.class, Object.class, true, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicGetObject(anyString(), anyBoolean());
        verifyDBInteractions(0, 0, 1, 0, 0, 0);
    }

    @Test
    public void dynamicGetList() throws Exception {
        List<TestRealmObject> testRealmObjects = new ArrayList<>();
        testRealmObjects.add(new TestRealmObject());
        Observable<List> observable = Observable.just(testRealmObjects);
        when(mockRestApi.dynamicGetList(anyString(), anyBoolean())).thenReturn(observable);

        cloudDataStore.dynamicGetList("", Object.class, Object.class, false, false);

        verify(mockRestApi, times(1)).dynamicGetList(anyString(), anyBoolean());
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicGetListCanWillPersist() throws Exception {
        cloudDataStore.mCanPersist = true;
        Observable observable = Observable.from(new ArrayList<>());
        when(mockRestApi.dynamicGetList(anyString(), anyBoolean())).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicGetList("", Object.class, Object.class, true, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicGetList(anyString(), anyBoolean());
//        verifyDBInteractions(0, 1, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPatchObject() throws Exception {
        when(mockRestApi.dynamicPatch(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPatchObject("", "", new JSONObject(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPatch(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPatchObjectCanWillPersist() throws Exception {
        cloudDataStore.mCanPersist = true;
        when(mockRestApi.dynamicPatch(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPatchObject("", "", new JSONObject(), Object.class, Object.class, true, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPatch(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 1, 0, 0, 0);
    }

    @Test
    public void dynamicPatchObjectNoNetwork() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPatchObject("", "", new JSONObject(), Object.class, Object.class, false, true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPatchObjectNoNetworkNoQueue() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPatchObject("", "", new JSONObject(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPostObject() throws Exception {
        when(mockRestApi.dynamicPost(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPostObject("", "", new JSONObject(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPost(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPostObjectCanWillPersist() throws Exception {
        cloudDataStore.mCanPersist = true;
        when(mockRestApi.dynamicPost(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPostObject("", "", new JSONObject(), Object.class, Object.class, true, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPost(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 1, 0, 0, 0);
    }

    @Test
    public void dynamicPostObjectNoNetwork() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPostObject("", "", new JSONObject(), Object.class, Object.class, false, true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPostObjectNoNetworkNoQueue() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPostObject("", "", new JSONObject(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPostList() throws Exception {
        when(mockRestApi.dynamicPost(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPostList("", "", new JSONArray(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPost(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPostListCanWillPersist() throws Exception {
        cloudDataStore.mCanPersist = true;
        when(mockRestApi.dynamicPost(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPostList("", "", new JSONArray(), Object.class, Object.class, true, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPost(anyString(), any(RequestBody.class));
        verifyDBInteractions(1, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPostListNoNetwork() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPostList("", "", new JSONArray(), Object.class, Object.class, false, true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPostListNoNetworkNoQueue() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPostList("", "", new JSONArray(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPutObject() throws Exception {
        when(mockRestApi.dynamicPut(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPutObject("", "", new JSONObject(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPut(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPutObjectCanWillPersist() throws Exception {
        cloudDataStore.mCanPersist = true;
        when(mockRestApi.dynamicPut(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPutObject("", "", new JSONObject(), Object.class, Object.class, true, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPut(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 1, 0, 0, 0);
    }

    @Test
    public void dynamicPutObjectNoNetwork() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPutObject("", "", new JSONObject(), Object.class, Object.class, false, true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPutObjectNoNetworkNoQueue() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPutObject("", "", new JSONObject(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPutList() throws Exception {
        when(mockRestApi.dynamicPut(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPutList("", "", new JSONArray(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPut(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPutListCanWillPersist() throws Exception {
        cloudDataStore.mCanPersist = true;
        when(mockRestApi.dynamicPut(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPutList("", "", new JSONArray(), Object.class, Object.class, true, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicPut(anyString(), any(RequestBody.class));
        verifyDBInteractions(1, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPutListNoNetwork() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPostList("", "", new JSONArray(), Object.class, Object.class, false, true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPutListNoNetworkNoQueue() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicPutList("", "", new JSONArray(), Object.class, Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicDeleteCollection() throws Exception {
        when(mockRestApi.dynamicDelete(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicDeleteCollection("", "", new JSONArray(), Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicDelete(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicDeleteCollectionCanWillPersist() throws Exception {
        cloudDataStore.mCanPersist = true;
        when(mockRestApi.dynamicDelete(anyString(), any(RequestBody.class))).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicDeleteCollection("", "", new JSONArray(), Object.class, true, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicDelete(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicDeleteCollectionNoNetwork() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicDeleteCollection("", "", new JSONArray(), Object.class, false, true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicDeleteCollectionNoNetworkNoQueue() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicDeleteCollection("", "", new JSONArray(), Object.class, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void dynamicDeleteAll() throws Exception {
        Observable observable = cloudDataStore.dynamicDeleteAll(Object.class);

        // Verify repository interactions
        verifyZeroInteractions(mockRestApi);
        verifyZeroInteractions(mockDataBaseManager);

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
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicUploadFileNoNetwork() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicUploadFile("", new File(""), "", new HashMap(), false, false, true, Object.class)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicUploadFileNoNetworkNoQueue() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicUploadFile("", new File(""), "", new HashMap(), false, false, false, Object.class)
                .subscribe(testSubscriber);

        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicDownloadFile() throws Exception {
        when(mockRestApi.dynamicDownload(anyString())).thenReturn(observable);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicDownloadFile("", new File(""), false, false, false)
                .subscribe(testSubscriber);

//        testSubscriber.assertNoErrors();

        verify(mockRestApi, times(1)).dynamicDownload(anyString());
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicDownloadFileNoNetwork() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicDownloadFile("", new File(""), false, false, true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void dynamicDownloadFileNoNetworkNoQueue() throws Exception {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber testSubscriber = new TestSubscriber();
        cloudDataStore.dynamicDownloadFile("", new File(""), false, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0, 0, 0);
    }

    @Test(expected = RuntimeException.class)
    public void queryDisk() throws Exception {
        Observable observable = cloudDataStore.queryDisk(realm -> realm.where(TestRealmObject.class),
                Object.class);

        // Verify repository interactions
        verifyZeroInteractions(mockRestApi);
        verifyZeroInteractions(mockDataBaseManager);

        // Assert return type
        assertEquals(new RuntimeException(), observable.toBlocking().first());
    }

    private void verifyDBInteractions(int putAllJ, int putAllL, int putJ, int putO, int putM, int evict) {
        verify(mockDataBaseManager, times(putAllJ)).putAll(any(JSONArray.class), anyString(), any(Class.class));
        verify(mockDataBaseManager, times(putAllL)).putAll(anyList(), any(Class.class));
        verify(mockDataBaseManager, times(putJ)).put(any(JSONObject.class), anyString(), any(Class.class));
        verify(mockDataBaseManager, times(putO)).put(any(RealmObject.class), any(Class.class));
        verify(mockDataBaseManager, times(putM)).put(any(RealmModel.class), any(Class.class));
        verify(mockDataBaseManager, atLeast(evict)).evictById(any(Class.class), anyString(), anyLong());
    }

    private Context changeStateOfNetwork(@NonNull Context mockedContext, boolean toEnable) {
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        Mockito.when(mockedContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        if (Utils.getInstance().hasLollipop()) {
            Network network = Mockito.mock(Network.class);
            Network[] networks = new Network[]{network};
            Mockito.when(connectivityManager.getAllNetworks()).thenReturn(networks);
            NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
            Mockito.when(connectivityManager.getNetworkInfo(network)).thenReturn(networkInfo);
            Mockito.when(networkInfo.getState()).thenReturn(toEnable ? NetworkInfo.State.CONNECTED : NetworkInfo.State.DISCONNECTED);
        } else {
            NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
            Mockito.when(connectivityManager.getAllNetworkInfo()).thenReturn(new NetworkInfo[]{networkInfo});
            Mockito.when(networkInfo.getState()).thenReturn(toEnable ? NetworkInfo.State.CONNECTED : NetworkInfo.State.DISCONNECTED);
        }
        return mockedContext;
    }
}
