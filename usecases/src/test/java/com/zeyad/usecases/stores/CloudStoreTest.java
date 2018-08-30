package com.zeyad.usecases.stores;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.TestRealmModel;
import com.zeyad.usecases.db.DataBaseManager;
import com.zeyad.usecases.db.RealmManager;
import com.zeyad.usecases.mapper.DAOMapper;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.utils.Utils;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subscribers.TestSubscriber;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
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
public class CloudStoreTest { // TODO: 6/5/17 add error assertions, disk and cache verifications
    private CloudStore cloudStore;
    private Context mockContext;
    private ApiConnection mockApiConnection;
    private DataBaseManager mockDataBaseManager;
    private Flowable<Object> observable;
    private Flowable<ResponseBody> fileFlowable;

    @Before
    public void setUp() {
        observable = Flowable.just(new Object());
        fileFlowable = Flowable.just(ResponseBody.create(null, ""));
        mockContext = mock(Context.class);
        mockApiConnection = mock(ApiConnection.class);
        mockDataBaseManager = mock(RealmManager.class);
        Utils utils = mock(Utils.class);
        changeStateOfNetwork(mockContext, true);
        when(mockDataBaseManager.put(any(JSONObject.class), anyString(), any(Class.class), any(Class.class)))
                .thenReturn(Single.just(true));
        when(mockDataBaseManager.putAll(any(JSONArray.class), anyString(), any(Class.class), any(Class.class)))
                .thenReturn(Single.just(true));
        when(mockDataBaseManager.putAll(anyList(), any(Class.class)))
                .thenReturn(Single.just(true));
        when(utils.isNetworkAvailable(any(Context.class))).thenReturn(true);
        when(utils.withDisk(true)).thenReturn(true);
        cloudStore = new CloudStore(mockApiConnection, mockDataBaseManager, new DAOMapper(),
                new MemoryStore(com.zeyad.usecases.Config.INSTANCE.getGson()), utils);
        HandlerThread backgroundThread = new HandlerThread("backgroundThread");
        backgroundThread.start();
        com.zeyad.usecases.Config.INSTANCE.setWithCache(false);
        com.zeyad.usecases.Config.INSTANCE.setWithSQLite(true);
        com.zeyad.usecases.Config.INSTANCE
                .setBackgroundThread(AndroidSchedulers.from(backgroundThread.getLooper()));
    }

    @Test
    public void dynamicGetObject() {
        when(mockApiConnection.dynamicGetObject(anyString(), anyBoolean())).thenReturn(observable);

        cloudStore.dynamicGetObject("", "", 0L, long.class, Object.class, false, false);

        verify(mockApiConnection, times(1)).dynamicGetObject(anyString(), anyBoolean());
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicGetObjectCanWillPersist() {
        when(mockApiConnection.dynamicGetObject(anyString(), anyBoolean())).thenReturn(observable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicGetObject("", "", 0L, long.class, Object.class, true, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicGetObject(anyString(), anyBoolean());
        verifyDBInteractions(0, 0, 1, 0);
    }

    @Test
    public void dynamicGetList() {
        List<TestRealmModel> testRealmObjects = new ArrayList<>();
        testRealmObjects.add(new TestRealmModel());
        Flowable<List<TestRealmModel>> observable = Flowable.just(testRealmObjects);
        when(mockApiConnection.<TestRealmModel>dynamicGetList(anyString(), anyBoolean())).thenReturn(observable);

        cloudStore.dynamicGetList("", "", Object.class, false, false);

        verify(mockApiConnection, times(1)).dynamicGetList(anyString(), anyBoolean());
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicGetListCanWillPersist() {
        Flowable<List<TestRealmModel>> observable = Flowable.just(Collections.singletonList(new TestRealmModel()));
        when(mockApiConnection.<TestRealmModel>dynamicGetList(anyString(), anyBoolean())).thenReturn(observable);

        TestSubscriber<List> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicGetList("", "", Object.class, true, false).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicGetList(anyString(), anyBoolean());
        //        verifyDBInteractions(0, 1, 0, 0, 0, 0);
    }

    @Test
    public void dynamicPatchObject() {
        when(mockApiConnection.dynamicPatch(anyString(), any(RequestBody.class)))
                .thenReturn(observable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPatchObject(
                "", "", int.class, new JSONObject(), Object.class, Object.class, false, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicPatch(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicPatchObjectCanWillPersist() {
        com.zeyad.usecases.Config.INSTANCE.setWithSQLite(true);
        when(mockApiConnection.dynamicPatch(anyString(), any(RequestBody.class)))
                .thenReturn(observable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPatchObject(
                "", "", int.class, new JSONObject(), Object.class, Object.class, true, true, true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicPatch(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 1, 0);
    }

    @Test
    public void dynamicPatchObjectNoNetwork() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPatchObject(
                "", "", int.class, new JSONObject(), Object.class, Object.class, false, false, true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicPatchObjectNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPatchObject(
                "", "", int.class, new JSONObject(), Object.class, Object.class, false, false, false)
                .subscribe(testSubscriber);

//        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicPostObject() {
        when(mockApiConnection.dynamicPost(anyString(), any(RequestBody.class)))
                .thenReturn(observable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPostObject(
                "", "", int.class, new JSONObject(), Object.class, Object.class, false, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicPost(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicPostObjectCanWillPersist() {
        when(mockApiConnection.dynamicPost(anyString(), any(RequestBody.class)))
                .thenReturn(observable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPostObject(
                "", "", int.class, new JSONObject(), Object.class, Object.class, true, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicPost(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 1, 0);
    }

    @Test
    public void dynamicPostObjectNoNetwork() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPostObject(
                "", "", int.class, new JSONObject(), Object.class, Object.class, false, false, true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicPostObjectNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPostObject(
                "", "", int.class, new JSONObject(), Object.class, Object.class, false, false, false)
                .subscribe(testSubscriber);

//        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicPostList() {
        when(mockApiConnection.dynamicPost(anyString(), any(RequestBody.class)))
                .thenReturn(observable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPostList("", "", int.class, new JSONArray(), Object.class, Object.class, false,
                false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicPost(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicPostListCanWillPersist() {
        when(mockApiConnection.dynamicPost(anyString(), any(RequestBody.class)))
                .thenReturn(observable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPostList("", "", int.class, new JSONArray(), Object.class, Object.class,
                true, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicPost(anyString(), any(RequestBody.class));
        verifyDBInteractions(1, 0, 0, 0);
    }

    @Test
    public void dynamicPostListNoNetwork() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPostList("", "", int.class, new JSONArray(), Object.class, Object.class, false, false,
                true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicPostListNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPostList("", "", int.class, new JSONArray(), Object.class, Object.class, false, false,
                false)
                .subscribe(testSubscriber);

//        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicPutObject() {
        when(mockApiConnection.dynamicPut(anyString(), any(RequestBody.class)))
                .thenReturn(observable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPutObject(
                "", "", int.class, new JSONObject(), Object.class, Object.class, false, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicPut(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicPutObjectCanWillPersist() {
        when(mockApiConnection.dynamicPut(anyString(), any(RequestBody.class)))
                .thenReturn(observable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPutObject(
                "", "", int.class, new JSONObject(), Object.class, Object.class, true, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicPut(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 1, 0);
    }

    @Test
    public void dynamicPutObjectNoNetwork() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPutObject(
                "", "", int.class, new JSONObject(), Object.class, Object.class, false, false, true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicPutObjectNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPutObject(
                "", "", int.class, new JSONObject(), Object.class, Object.class, false, false, false)
                .subscribe(testSubscriber);

//        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicPutList() {
        when(mockApiConnection.dynamicPut(anyString(), any(RequestBody.class)))
                .thenReturn(observable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPutList("", "", int.class, new JSONArray(), Object.class, Object.class, false, false,
                false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicPut(anyString(), any(RequestBody.class));
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicPutListCanWillPersist() {
        when(mockApiConnection.dynamicPut(anyString(), any(RequestBody.class)))
                .thenReturn(observable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPutList("", "", int.class, new JSONArray(), Object.class, Object.class, true, false,
                false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicPut(anyString(), any(RequestBody.class));
        verifyDBInteractions(1, 0, 0, 0);
    }

    @Test
    public void dynamicPutListNoNetwork() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPostList("", "", int.class, new JSONArray(), Object.class, Object.class, false, false, true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicPutListNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicPutList("", "", int.class, new JSONArray(), Object.class, Object.class, false, false,
                false)
                .subscribe(testSubscriber);

//        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicDeleteCollection() {
        when(mockApiConnection.dynamicDelete(anyString()))
                .thenReturn(observable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicDeleteCollection(
                "", "", String.class, new JSONArray(), Object.class, Object.class, false, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicDelete(anyString());
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicDeleteCollectionCanWillPersist() {
        when(mockApiConnection.dynamicDelete(anyString()))
                .thenReturn(observable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicDeleteCollection(
                "", "", String.class, new JSONArray(), Object.class, Object.class, true, false, false)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicDelete(anyString());
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicDeleteCollectionNoNetwork() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicDeleteCollection(
                "", "", String.class, new JSONArray(), Object.class, Object.class, false, false, true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicDeleteCollectionNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicDeleteCollection(
                "", "", String.class, new JSONArray(), Object.class, Object.class, false, false, false)
                .subscribe(testSubscriber);

//        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void dynamicDeleteAll() {
        Single completable = cloudStore.dynamicDeleteAll(Object.class);

        // Verify repository interactions
        verifyZeroInteractions(mockApiConnection);
        verifyZeroInteractions(mockDataBaseManager);

        // Assert return type
        assertEquals(IllegalStateException.class, completable.blockingGet().getClass());
    }

    @Test
    public void dynamicUploadFile() {
        when(mockApiConnection.dynamicUpload(anyString(), anyMap(), anyListOf(MultipartBody.Part.class)))
                .thenReturn(observable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicUploadFile(
                "", new HashMap<>(), new HashMap<>(), false, false, false, Object.class)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1))
                .dynamicUpload(anyString(), anyMap(), anyListOf(MultipartBody.Part.class));
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicUploadFileNoNetwork() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicUploadFile(
                "", new HashMap<>(), new HashMap<>(), false, false, true, Object.class)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicUploadFileNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicUploadFile(
                "", new HashMap<>(), new HashMap<>(), false, false, false, Object.class)
                .subscribe(testSubscriber);

//        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicDownloadFile() {
        when(mockApiConnection.dynamicDownload(anyString())).thenReturn(fileFlowable);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicDownloadFile("", new File(""), false, false, false)
                .subscribe(testSubscriber);

        //        testSubscriber.assertNoErrors();

        verify(mockApiConnection, times(1)).dynamicDownload(anyString());
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicDownloadFileNoNetwork() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicDownloadFile("", new File(""), false, false, true)
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test
    public void dynamicDownloadFileNoNetworkNoQueue() {
        changeStateOfNetwork(mockContext, false);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        cloudStore.dynamicDownloadFile("", new File(""), false, false, false)
                .subscribe(testSubscriber);

//        testSubscriber.assertError(NetworkConnectionException.class);
        verifyDBInteractions(0, 0, 0, 0);
    }

    @Test(expected = RuntimeException.class)
    public void queryDisk() {
//        Flowable observable = cloudStore.queryDisk(realm -> realm.where(TestRealmModel.class));

        // Verify repository interactions
        verifyZeroInteractions(mockApiConnection);
        verifyZeroInteractions(mockDataBaseManager);

        // Assert return type
        RuntimeException expected = new RuntimeException();
        assertEquals(expected.getClass(), observable.first(expected).blockingGet().getClass());
    }

    private void verifyDBInteractions(
            int putAllJ, int putAllL, int putJ, int evict) {
        verify(mockDataBaseManager, times(putAllJ))
                .putAll(any(JSONArray.class), anyString(), any(Class.class), any(Class.class));
        verify(mockDataBaseManager, times(putAllL)).putAll(anyList(), any(Class.class));
        verify(mockDataBaseManager, times(putJ))
                .put(any(JSONObject.class), anyString(), any(Class.class), any(Class.class));
        verify(mockDataBaseManager, atLeast(evict)).evictById(any(Class.class), anyString(), any(),
                any(Class.class));
    }

    @NonNull
    private Context changeStateOfNetwork(@NonNull Context mockedContext, boolean toEnable) {
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        Mockito.when(mockedContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(connectivityManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network network = Mockito.mock(Network.class);
            Network[] networks = new Network[]{network};
            Mockito.when(connectivityManager.getAllNetworks()).thenReturn(networks);
            NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
            Mockito.when(connectivityManager.getNetworkInfo(network)).thenReturn(networkInfo);
            Mockito.when(networkInfo.getState())
                    .thenReturn(
                            toEnable
                                    ? NetworkInfo.State.CONNECTED
                                    : NetworkInfo.State.DISCONNECTED);
        } else {
            NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
            Mockito.when(connectivityManager.getAllNetworkInfo())
                    .thenReturn(new NetworkInfo[]{networkInfo});
            Mockito.when(networkInfo.getState())
                    .thenReturn(
                            toEnable
                                    ? NetworkInfo.State.CONNECTED
                                    : NetworkInfo.State.DISCONNECTED);
        }
        return mockedContext;
    }
}
