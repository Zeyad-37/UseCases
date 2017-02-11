package com.zeyad.usecases.data.repository;

import android.support.annotation.NonNull;
import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.mappers.IDAOMapperFactory;
import com.zeyad.usecases.data.repository.stores.DataStore;
import com.zeyad.usecases.data.repository.stores.DataStoreFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.Observable;

import static com.zeyad.usecases.data.repository.DataRepositoryJUnitRobot.createMockedDataStoreFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(constants = BuildConfig.class, sdk = 21)
public class DataRepositoryJUnitTest {

    private boolean mIsDiskType;
    private boolean mToCache;
    private DataStore mockDataStore;
    private DataRepository mDataRepository; // class under test
    private DataStoreFactory mockDataStoreFactory;

    @NonNull
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        final ArrayList<Object[]> objects = new ArrayList<>(2);
        //boolean isDiskType, boolean toCache
        objects.add(new Object[]{true, true});
        objects.add(new Object[]{true, false});
        objects.add(new Object[]{false, true});
        objects.add(new Object[]{false, false});
        return objects;
    }

    @Before
    public void setUp() throws Exception {
        // init mocks
        mockDataStore = mIsDiskType ? DataRepositoryJUnitRobot.createMockedDiskStore()
                : DataRepositoryJUnitRobot.createMockedCloudStore();
        mockDataStoreFactory = createMockedDataStoreFactory(mockDataStore);
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
        mDataRepository.getListDynamically(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepositoryJUnitRobot.getValidPresentationClass()
                , DataRepositoryJUnitRobot.getValidDataClass()
                , false
                , mToCache);
        // verify interactions
        verify(mockDataStoreFactory, times(1)).dynamically(DataRepositoryJUnitRobot.getValidUrl()
                , null);
        verify(mockDataStore, times(1)).dynamicGetList(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepositoryJUnitRobot.getValidPresentationClass()
                , DataRepositoryJUnitRobot.getValidDataClass()
                , false
                , mToCache);
        // assert values
        assertEquals(ArrayList.class.getSimpleName(), observable.toBlocking().first().getClass()
                .getSimpleName());
    }

    @Test
    public void testGetListDynamicallyCacheVersion_ifExpectedObservableIsReturned_whenArgumentsArePassed() {
        final Observable<List> mockedObservable = mock(Observable.class);
        DataRepositoryJUnitRobot.mockDataStoreForDynamicGetList(mockDataStore, false, mToCache)
                .thenReturn(mockedObservable);
        Observable<List> observable = mDataRepository
                .getListDynamically(DataRepositoryJUnitRobot.getValidUrl()
                        , DataRepositoryJUnitRobot.getValidPresentationClass()
                        , DataRepositoryJUnitRobot.getValidDataClass()
                        , false
                        , mToCache);
        assertThat(observable, is(equalTo(mockedObservable)));
    }

    @Test
    public void testGetObjectDynamicallyByIdCacheVersion_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() throws Exception {
        // dependency behaviour

        Observable<List> observable = Observable.just(new ArrayList());
        when(mockDataStore.dynamicGetList(anyString(), any(Class.class), any(Class.class), anyBoolean(),
                anyBoolean())).thenReturn(observable);
        // invoke method under test
        mDataRepository.getObjectDynamicallyById(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepositoryJUnitRobot.getColumnName()
                , DataRepositoryJUnitRobot.getColumnId()
                , DataRepositoryJUnitRobot.getValidPresentationClass()
                , DataRepositoryJUnitRobot.getValidDataClass()
                , false
                , mToCache);
        // verify interactions
        verify(mockDataStoreFactory, times(1)).dynamically(DataRepositoryJUnitRobot.getValidUrl(), null);
        verify(mockDataStore, times(1))
                .dynamicGetObject(DataRepositoryJUnitRobot.getValidUrl()
                        , DataRepositoryJUnitRobot.getColumnName()
                        , DataRepositoryJUnitRobot.getColumnId()
                        , DataRepositoryJUnitRobot.getValidPresentationClass()
                        , DataRepositoryJUnitRobot.getValidDataClass()
                        , false
                        , mToCache);
        // assert values
        assertEquals(ArrayList.class.getSimpleName(), observable.toBlocking().first().getClass()
                .getSimpleName());
    }

    @Test
    public void testPostObjectDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenJsonObjectIsPassed() {
        mDataRepository.postObjectDynamically(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepository.DEFAULT_ID_KEY
                , DataRepositoryJUnitRobot.getValidJSONObject()
                , DataRepositoryJUnitRobot.getValidPresentationClass()
                , DataRepositoryJUnitRobot.getValidDataClass()
                , true, true);
        verify(mockDataStore, times(1))
                .dynamicPostObject(DataRepositoryJUnitRobot.getValidUrl()
                        , DataRepository.DEFAULT_ID_KEY, DataRepositoryJUnitRobot.getValidJSONObject()
                        , DataRepositoryJUnitRobot.getValidPresentationClass()
                        , DataRepositoryJUnitRobot.getValidDataClass()
                        , true, true);
    }

    @Test
    public void testPostListDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.postListDynamically(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepository.DEFAULT_ID_KEY, DataRepositoryJUnitRobot.getValidJSONArray()
                , DataRepositoryJUnitRobot.getValidPresentationClass()
                , DataRepositoryJUnitRobot.getValidDataClass()
                , true, true);
        verify(mockDataStore, times(1))
                .dynamicPostList(DataRepositoryJUnitRobot.getValidUrl()
                        , DataRepository.DEFAULT_ID_KEY, DataRepositoryJUnitRobot.getValidJSONArray()
                        , DataRepositoryJUnitRobot.getValidPresentationClass()
                        , DataRepositoryJUnitRobot.getValidDataClass()
                        , true, true);
    }

    @Test
    public void testDeleteListDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.deleteListDynamically(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepositoryJUnitRobot.getValidJSONArray()
                , DataRepositoryJUnitRobot.getValidPresentationClass()
                , DataRepositoryJUnitRobot.getValidDataClass()
                , true, true);
        verify(mockDataStore, times(1))
                .dynamicDeleteCollection(DataRepositoryJUnitRobot.getValidUrl()
                        , DataRepository.DEFAULT_ID_KEY
                        , DataRepositoryJUnitRobot.getValidJSONArray()
                        , DataRepositoryJUnitRobot.getValidDataClass()
                        , true, true);
    }

    @Test
    public void testSearchDisk_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenRealmQueryIsPassed() throws IllegalAccessException {
        mockDataStore = DataRepositoryJUnitRobot.createMockedDiskStore();
        when(mockDataStoreFactory.disk(any(IDAOMapper.class))).thenReturn(mockDataStore);

        mDataRepository.queryDisk(DataRepositoryJUnitRobot.getValidRealmQuery()
                , DataRepositoryJUnitRobot.getValidPresentationClass());
        verify(mockDataStore, times(1))
                .queryDisk(DataRepositoryJUnitRobot.getValidRealmQuery()
                        , DataRepositoryJUnitRobot.getValidPresentationClass());
    }

//    @Test
//    public void testUploadFile_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
//        mDataRepository.uploadFileDynamically(DataRepositoryJUnitRobot.getValidUrl()
//                , DataRepositoryJUnitRobot.getValidFile(), DataRepositoryJUnitRobot.getKey(),
//                DataRepositoryJUnitRobot.getValidMap(), DataRepositoryJUnitRobot.ON_WIFI,
//                DataRepositoryJUnitRobot.WHILE_CHARGING, DataRepositoryJUnitRobot.QUEUABLE,
//                DataRepositoryJUnitRobot.getValidPresentationClass(), DataRepositoryJUnitRobot.getValidDataClass());
//        Mockito.verify(mockDataStore, Mockito.times(1))
//                .dynamicUploadFile(DataRepositoryJUnitRobot.getValidUrl(), DataRepositoryJUnitRobot.getValidFile(),
//                        DataRepositoryJUnitRobot.getKey(), DataRepositoryJUnitRobot.getValidMap(),
//                        DataRepositoryJUnitRobot.ON_WIFI, DataRepositoryJUnitRobot.WHILE_CHARGING,
//                        DataRepositoryJUnitRobot.QUEUABLE, DataRepositoryJUnitRobot.getValidPresentationClass());
//    }

    @Test
    public void testPutObjectDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.putObjectDynamically(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepository.DEFAULT_ID_KEY
                , DataRepositoryJUnitRobot.getValidJSONObject()
                , DataRepositoryJUnitRobot.getValidPresentationClass()
                , DataRepositoryJUnitRobot.getValidDataClass(), true, true);
        verify(mockDataStore, times(1))
                .dynamicPutObject(DataRepositoryJUnitRobot.getValidUrl()
                        , DataRepository.DEFAULT_ID_KEY
                        , DataRepositoryJUnitRobot.getValidJSONObject()
                        , DataRepositoryJUnitRobot.getValidPresentationClass()
                        , DataRepositoryJUnitRobot.getValidDataClass(), true, true);
    }

    @Test
    public void testPutListDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.putListDynamically(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepository.DEFAULT_ID_KEY
                , DataRepositoryJUnitRobot.getValidJSONArray()
                , DataRepositoryJUnitRobot.getValidPresentationClass()
                , DataRepositoryJUnitRobot.getValidDataClass(), true, true);
        verify(mockDataStore, times(1))
                .dynamicPutList(DataRepositoryJUnitRobot.getValidUrl()
                        , DataRepository.DEFAULT_ID_KEY
                        , DataRepositoryJUnitRobot.getValidJSONArray()
                        , DataRepositoryJUnitRobot.getValidPresentationClass()
                        , DataRepositoryJUnitRobot.getValidDataClass(), true, true);
    }

    @Test
    public void testDeleteALlDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() throws Exception {
        mockDataStore = DataRepositoryJUnitRobot.createMockedDiskStore();
        when(mockDataStoreFactory.disk(any(IDAOMapper.class))).thenReturn(mockDataStore);

        mDataRepository.deleteAllDynamically(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepositoryJUnitRobot.getValidDataClass(), true);
        verify(mockDataStore, times(1)).dynamicDeleteAll(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepositoryJUnitRobot.getValidDataClass(), true);
    }
}