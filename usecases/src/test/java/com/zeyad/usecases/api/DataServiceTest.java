package com.zeyad.usecases.api;

import com.zeyad.usecases.TestRealmModel;
import com.zeyad.usecases.db.RealmQueryProvider;
import com.zeyad.usecases.requests.FileIORequest;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;
import com.zeyad.usecases.stores.CloudStore;
import com.zeyad.usecases.stores.DataStoreFactory;
import com.zeyad.usecases.stores.DiskStore;
import com.zeyad.usecases.stores.MemoryStore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author by ZIaDo on 5/9/17.
 */
public class DataServiceTest {

    private DataService dataService;
    private DataStoreFactory dataStoreFactory;
    private GetRequest getRequest;
    private PostRequest postRequest;
    private Flowable flowable;

    @Before
    public void setUp() throws Exception {
        flowable = Flowable.just(true);
        postRequest = new PostRequest.Builder(Object.class, false).build();
        getRequest = new GetRequest.Builder(TestRealmModel.class, false)
                .url("")
                .id(37, "id", int.class)
                .cache("id")
                .build();
        dataStoreFactory = mock(DataStoreFactory.class);
        when(dataStoreFactory.dynamically(anyString(), any(Class.class)))
                .thenReturn(mock(CloudStore.class));
        when(dataStoreFactory.disk(any())).thenReturn(mock(DiskStore.class));
        when(dataStoreFactory.cloud(any())).thenReturn(mock(CloudStore.class));
        when(dataStoreFactory.memory()).thenReturn(mock(MemoryStore.class));
        dataService =
                new DataService(
                        dataStoreFactory, AndroidSchedulers.mainThread(), mock(Scheduler.class));
        com.zeyad.usecases.Config.setWithCache(false);
        com.zeyad.usecases.Config.setWithSQLite(true);
    }

    @Test
    public void getList() throws Exception {
        when(dataStoreFactory
                .dynamically(anyString(), any(Class.class))
                .dynamicGetList(anyString(), anyString(), any(Class.class), anyBoolean(), anyBoolean()))
                .thenReturn(Flowable.just(Collections.EMPTY_LIST));

        dataService.getList(getRequest);

        verify(dataStoreFactory.dynamically(anyString(), any(Class.class)), times(1))
                .dynamicGetList(anyString(), anyString(), any(Class.class), anyBoolean(), anyBoolean());
    }

    @Test
    public void getObject() throws Exception {
        when(dataStoreFactory
                .dynamically(anyString(), any(Class.class))
                .dynamicGetObject(
                        anyString(),
                        anyString(),
                        any(),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable);

        dataService.getObject(getRequest);

        verify(dataStoreFactory.dynamically(anyString(), any(Class.class)), times(1))
                .dynamicGetObject(
                        anyString(),
                        anyString(),
                        any(),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean());
    }

    @Test
    public void getListOffLineFirst() throws Exception {
        when(dataStoreFactory
                .cloud(Object.class)
                .dynamicGetList(anyString(), anyString(), any(Class.class), anyBoolean(), anyBoolean()))
                .thenReturn(Flowable.just(Collections.EMPTY_LIST));
        when(dataStoreFactory
                .disk(Object.class)
                .dynamicGetList(anyString(), anyString(), any(Class.class), anyBoolean(), anyBoolean()))
                .thenReturn(Flowable.just(Collections.EMPTY_LIST));
        when(dataStoreFactory
                .memory()
                .getAllItems(any(Class.class)))
                .thenReturn(Single.just(Collections.singletonList(true)));

        dataService.getListOffLineFirst(getRequest);

        verify(dataStoreFactory.cloud(Object.class), times(1))
                .dynamicGetList(anyString(), anyString(), any(Class.class), anyBoolean(), anyBoolean());
        verify(dataStoreFactory.disk(Object.class), times(1))
                .dynamicGetList(anyString(), anyString(), any(Class.class), anyBoolean(), anyBoolean());
    }

    @Test
    public void getObjectOffLineFirst() throws Exception {
        when(dataStoreFactory.cloud(Object.class)
                .dynamicGetObject(
                        anyString(),
                        anyString(),
                        any(),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable);
        when(dataStoreFactory
                .disk(Object.class)
                .dynamicGetObject(
                        anyString(),
                        anyString(),
                        any(),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable);

        when(dataStoreFactory
                .memory()
                .getItem(anyString(), any(Class.class)))
                .thenReturn(Single.just(true));

        dataService.getObjectOffLineFirst(getRequest);

        verify(dataStoreFactory.cloud(Object.class), times(1))
                .dynamicGetObject(
                        anyString(),
                        anyString(),
                        any(),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean());
        verify(dataStoreFactory.disk(Object.class), times(1))
                .dynamicGetObject(
                        anyString(),
                        anyString(),
                        any(),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean());
    }

    @Test
    public void patchObject() throws Exception {
        when(dataStoreFactory.dynamically(anyString(), any(Class.class))
                .dynamicPatchObject(
                        anyString(),
                        anyString(),
                        any(Class.class),
                        any(JSONObject.class),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable);

        dataService.patchObject(postRequest);

        verify(dataStoreFactory.dynamically(anyString(), any(Class.class)), times(1))
                .dynamicPatchObject(
                        anyString(),
                        anyString(),
                        any(Class.class),
                        any(JSONObject.class),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean());
    }

    @Test
    public void postObject() throws Exception {
        when(dataStoreFactory
                .dynamically(anyString(), any(Class.class))
                .dynamicPostObject(
                        anyString(),
                        anyString(),
                        any(Class.class),
                        any(JSONObject.class),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable);

        dataService.postObject(postRequest);

        verify(dataStoreFactory.dynamically(anyString(), any(Class.class)), times(1))
                .dynamicPostObject(
                        anyString(),
                        anyString(),
                        any(Class.class),
                        any(JSONObject.class),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean());
    }

    @Test
    public void postList() throws Exception {
        when(dataStoreFactory
                .dynamically(anyString(), any(Class.class))
                .dynamicPostList(
                        anyString(),
                        anyString(),
                        any(Class.class),
                        any(JSONArray.class),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable);

        dataService.postList(postRequest);

        verify(dataStoreFactory.dynamically(anyString(), any(Class.class)), times(1))
                .dynamicPostList(
                        anyString(),
                        anyString(),
                        any(Class.class),
                        any(JSONArray.class),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean());
    }

    @Test
    public void putObject() throws Exception {
        when(dataStoreFactory
                .dynamically(anyString(), any(Class.class))
                .dynamicPutObject(
                        anyString(),
                        anyString(),
                        any(Class.class),
                        any(JSONObject.class),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable);

        dataService.putObject(postRequest);

        verify(dataStoreFactory.dynamically(anyString(), any(Class.class)), times(1))
                .dynamicPutObject(
                        anyString(),
                        anyString(),
                        any(Class.class),
                        any(JSONObject.class),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean());
    }

    @Test
    public void putList() throws Exception {
        when(dataStoreFactory
                .dynamically(anyString(), any(Class.class))
                .dynamicPutList(
                        anyString(),
                        anyString(),
                        any(Class.class),
                        any(JSONArray.class),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable);

        dataService.putList(postRequest);

        verify(dataStoreFactory.dynamically(anyString(), any(Class.class)), times(1))
                .dynamicPutList(
                        anyString(),
                        anyString(),
                        any(Class.class),
                        any(JSONArray.class),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean());
    }

    @Test
    public void deleteItemById() throws Exception {
        when(dataStoreFactory
                .dynamically(anyString(), any(Class.class))
                .dynamicDeleteCollection(
                        anyString(),
                        anyString(),
                        any(Class.class),
                        any(JSONArray.class),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable);

        dataService.deleteItemById(postRequest);

        verify(dataStoreFactory.dynamically(anyString(), any(Class.class)), times(1))
                .dynamicDeleteCollection(
                        anyString(),
                        anyString(),
                        any(Class.class),
                        any(JSONArray.class),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean());
    }

    @Test
    public void deleteCollection() throws Exception {
        when(dataStoreFactory
                .dynamically(anyString(), any(Class.class))
                .dynamicDeleteCollection(
                        anyString(),
                        anyString(),
                        any(Class.class),
                        any(JSONArray.class),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable);

        dataService.deleteCollectionByIds(postRequest);

        verify(dataStoreFactory.dynamically(anyString(), any(Class.class)), times(1))
                .dynamicDeleteCollection(
                        anyString(),
                        anyString(),
                        any(Class.class),
                        any(JSONArray.class),
                        any(Class.class),
                        any(Class.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean());
    }

    @Test
    public void deleteAll() throws Exception {
        when(dataStoreFactory.disk(Object.class).dynamicDeleteAll(any(Class.class)))
                .thenReturn(Single.just(true));

        dataService.deleteAll(postRequest);

        verify(dataStoreFactory.disk(Object.class), times(1)).dynamicDeleteAll(any(Class.class));
    }

    @Test
    public void queryDisk() throws Exception {
        when(dataStoreFactory.disk(Object.class).queryDisk(any(RealmQueryProvider.class)))
                .thenReturn(flowable);

        dataService.queryDisk(realm -> realm.where(TestRealmModel.class));

        verify(dataStoreFactory.disk(Object.class), times(1))
                .queryDisk(any(RealmQueryProvider.class));
    }

    @Test
    public void uploadFile() throws Exception {
        when(dataStoreFactory
                .cloud(Object.class)
                .dynamicUploadFile(
                        anyString(),
                        any(File.class),
                        anyString(),
                        (HashMap<String, Object>) anyMap(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean(),
                        any(Class.class)))
                .thenReturn(flowable);

        dataService.uploadFile(
                new FileIORequest.Builder("", new File("")).dataClass(Object.class).build());

        verify(dataStoreFactory.cloud(Object.class), times(1))
                .dynamicUploadFile(
                        anyString(),
                        any(File.class),
                        anyString(),
                        (HashMap<String, Object>) anyMap(),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean(),
                        any(Class.class));
    }

    @Test
    public void downloadFile() throws Exception {
        when(dataStoreFactory
                .cloud(Object.class)
                .dynamicDownloadFile(
                        anyString(),
                        any(File.class),
                        anyBoolean(),
                        anyBoolean(),
                        anyBoolean()))
                .thenReturn(flowable);

        dataService.downloadFile(
                new FileIORequest.Builder("", new File("")).dataClass(Object.class).build());

        verify(dataStoreFactory.cloud(Object.class), times(1))
                .dynamicDownloadFile(
                        anyString(), any(File.class), anyBoolean(), anyBoolean(), anyBoolean());
    }
}

