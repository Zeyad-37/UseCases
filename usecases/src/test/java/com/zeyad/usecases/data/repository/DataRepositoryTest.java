package com.zeyad.usecases.data.repository;

import com.zeyad.usecases.TestRealmModel;
import com.zeyad.usecases.data.db.RealmManager;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.mappers.IDAOMapperFactory;
import com.zeyad.usecases.data.repository.stores.DataStore;
import com.zeyad.usecases.data.repository.stores.DataStoreFactory;
import com.zeyad.usecases.data.repository.stores.DiskDataStore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataRepositoryTest {

    private DataStore mockDataStore;
    private DataRepository mDataRepository; // class under test
    private DataStoreFactory mockDataStoreFactory;
    private String validUrl = "http://www.google.com";

    @Before
    public void setUp() throws Exception {
        // init mocks
        mockDataStore = mock(DiskDataStore.class);

        mockDataStoreFactory = mock(DataStoreFactory.class);
        when(mockDataStoreFactory.cloud(Mockito.any())).thenReturn(mockDataStore);

        // init class under test
        mDataRepository = new DataRepository(mockDataStoreFactory, mock(IDAOMapperFactory.class));
        // global stub
        when(mockDataStoreFactory.dynamically(anyString(), any(IDAOMapper.class))).thenReturn(mockDataStore);
    }

    @Test
    public void testGetListDynamicallyCacheVersion_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() throws Exception {
        // dependency behaviour
        Observable<List> observable = Observable.just(new ArrayList());
        when(mockDataStore.dynamicGetList(anyString(), any(Class.class), any(Class.class), anyBoolean(),
                anyBoolean())).thenReturn(observable);
        // invoke method under test
        mDataRepository.getListDynamically(validUrl, Object.class, Object.class, false,
                false);
        // verify interactions
        verify(mockDataStoreFactory, times(1)).dynamically(anyString(), any(IDAOMapper.class));
        verify(mockDataStore, times(1)).dynamicGetList(anyString(), any(Class.class), any(Class.class),
                anyBoolean(), anyBoolean());
        // assert values
        assertEquals(ArrayList.class.getSimpleName(), observable.toBlocking().first().getClass().getSimpleName());
    }

    @Test
    public void testGetListDynamicallyCacheVersion_ifExpectedObservableIsReturned_whenArgumentsArePassed() {
        final Observable<List> mockedObservable = mock(Observable.class);
        when(mockDataStore.dynamicGetList(anyString(), any(Class.class), any(Class.class), anyBoolean(),
                anyBoolean())).thenReturn(mockedObservable);
        Observable<List> observable = mDataRepository.getListDynamically(validUrl, Object.class,
                Object.class, false, false);
        assertThat(observable, is(equalTo(mockedObservable)));
    }

    @Test
    public void testGetObjectDynamicallyByIdCacheVersion_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() throws Exception {
        // dependency behaviour
        Observable<List> observable = Observable.just(new ArrayList());
        when(mockDataStore.dynamicGetList(anyString(), any(Class.class), any(Class.class), anyBoolean(),
                anyBoolean())).thenReturn(observable);
        // invoke method under test
        mDataRepository.getObjectDynamicallyById(validUrl, "", 0, Object.class, Object.class, false, false);
        // verify interactions
        verify(mockDataStoreFactory, times(1)).dynamically(anyString(), any(IDAOMapper.class));
        verify(mockDataStore, times(1)).dynamicGetObject(anyString(), anyString(), anyInt(), any(),
                any(Class.class), anyBoolean(), anyBoolean());
        // assert values
        assertEquals(ArrayList.class.getSimpleName(), observable.toBlocking().first().getClass()
                .getSimpleName());
    }

    @Test
    public void testPostObjectDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenJsonObjectIsPassed() {
        mDataRepository.postObjectDynamically(validUrl, DataRepository.DEFAULT_ID_KEY,
                any(JSONObject.class), Object.class, TestRealmModel.class, true, true);
        verify(mockDataStore, times(1)).dynamicPostObject(anyString(), anyString(), any(JSONObject.class),
                any(Class.class), any(Class.class), anyBoolean(), anyBoolean());
    }

    @Test
    public void testPostListDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.postListDynamically(validUrl, DataRepository.DEFAULT_ID_KEY, any(JSONArray.class),
                Object.class, Object.class, true, true);
        verify(mockDataStore, times(1)).dynamicPostList(anyString(), anyString(), any(JSONArray.class),
                any(Class.class), any(Class.class), anyBoolean(), anyBoolean());
    }

    @Test
    public void testDeleteListDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.deleteListDynamically(validUrl, any(JSONArray.class), Object.class,
                TestRealmModel.class, true, true);
        verify(mockDataStore, times(1)).dynamicDeleteCollection(anyString(), anyString(), any(JSONArray.class),
                any(Class.class), anyBoolean(), anyBoolean());
    }

    @Test
    public void testSearchDisk_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenRealmQueryIsPassed() throws IllegalAccessException {
        mockDataStore = mock(DiskDataStore.class);
        when(mockDataStoreFactory.disk(any(IDAOMapper.class))).thenReturn(mockDataStore);

        mDataRepository.queryDisk(any(RealmManager.RealmQueryProvider.class), Object.class);
        verify(mockDataStore, times(1)).queryDisk(any(RealmManager.RealmQueryProvider.class),
                any(Class.class));
    }

    @Test
    public void testPutObjectDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.putObjectDynamically(validUrl, DataRepository.DEFAULT_ID_KEY, any(JSONObject.class),
                Object.class, Object.class, true, true);
        verify(mockDataStore, times(1)).dynamicPutObject(anyString(), anyString(), any(JSONObject.class),
                any(Class.class), any(Class.class), anyBoolean(), anyBoolean());
    }

    @Test
    public void testPutListDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.putListDynamically(validUrl, DataRepository.DEFAULT_ID_KEY, any(JSONArray.class),
                Object.class, Object.class, true, true);
        verify(mockDataStore, times(1)).dynamicPutList(anyString(), anyString(), any(JSONArray.class),
                any(Class.class), any(Class.class), anyBoolean(), anyBoolean());
    }

    @Test
    public void testDeleteALlDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() throws Exception {
        mockDataStore = mock(DiskDataStore.class);
        when(mockDataStoreFactory.disk(any(IDAOMapper.class))).thenReturn(mockDataStore);

        mDataRepository.deleteAllDynamically(validUrl, TestRealmModel.class, true);
        verify(mockDataStore, times(1)).dynamicDeleteAll(any(Class.class));
    }
}