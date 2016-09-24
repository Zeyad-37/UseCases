package com.zeyad.genericusecase.data.repository;

import com.zeyad.genericusecase.data.TestUtility;
import com.zeyad.genericusecase.data.mockable.ListObservable;
import com.zeyad.genericusecase.data.repository.generalstore.DataStore;
import com.zeyad.genericusecase.data.repository.generalstore.DataStoreFactory;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.Observable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assume.assumeThat;

@RunWith(Parameterized.class)
public class OrderHistoryRepositoryTest {

    private final boolean mIsDiskType;
    private DataStore mDataStore;
    private final boolean mToCache;
    private DataRepository mDataRepository;

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

    public OrderHistoryRepositoryTest(boolean isDiskType, boolean toCache) {
        mIsDiskType = isDiskType;
        mToCache = toCache;
    }

    @Before
    public void setUp() throws Exception {
        mDataStore = mIsDiskType
                ? DataRepositoryRobot.createMockedDiskStore()
                : DataRepositoryRobot.createMockedCloudStore();
        DataStoreFactory dataStoreFactory = DataRepositoryRobot.createMockedDataStoreFactory(mDataStore);
        mDataRepository = new DataRepository(dataStoreFactory, TestUtility.createEntityMapper());
        DataRepositoryRobot.mockDataStore(mIsDiskType, mDataStore, dataStoreFactory);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetListDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository
                .getListDynamically(DataRepositoryRobot.getValidUrl()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass()
                        , false, false);
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicGetList(DataRepositoryRobot.getValidUrl()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass()
                        , false
                        , DataRepository.DEFAULT_TO_CACHE);
    }

    @Test
    public void testGetListDynamically_ifExpectedObservableIsReturned_whenArgumentsArePassed() {
        final ListObservable mockedObservable = Mockito.mock(ListObservable.class);
        DataRepositoryRobot.mockDataStoreForDynamicGetList(mDataStore, false, DataRepository.DEFAULT_TO_CACHE)
                .thenReturn(mockedObservable);
        Observable<List> observable = mDataRepository
                .getListDynamically(DataRepositoryRobot.getValidUrl()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass()
                        , false, false);
        assertThat(observable, is(equalTo(mockedObservable)));
    }

    @Test
    public void testGetListDynamicallyCacheVersion_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository
                .getListDynamically(DataRepositoryRobot.getValidUrl()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass()
                        , false
                        , mToCache);
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicGetList(DataRepositoryRobot.getValidUrl()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass()
                        , false
                        , mToCache);
    }

    @Test
    public void testGetListDynamicallyCacheVersion_ifExpectedObservableIsReturned_whenArgumentsArePassed() {
        final ListObservable mockedObservable = Mockito.mock(ListObservable.class);
        DataRepositoryRobot.mockDataStoreForDynamicGetList(mDataStore, false, mToCache)
                .thenReturn(mockedObservable);
        Observable<List> observable = mDataRepository
                .getListDynamically(DataRepositoryRobot.getValidUrl()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass()
                        , false
                        , mToCache);
        assertThat(observable, is(equalTo(mockedObservable)));
    }

    @Test
    public void testGetObjectDynamicallyById_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository
                .getObjectDynamicallyById(DataRepositoryRobot.getValidUrl()
                        , DataRepositoryRobot.getColumnName()
                        , DataRepositoryRobot.getColumnId()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass()
                        , false, false);
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicGetObject(DataRepositoryRobot.getValidUrl()
                        , DataRepositoryRobot.getColumnName()
                        , DataRepositoryRobot.getColumnId()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass()
                        , false
                        , DataRepository.DEFAULT_TO_CACHE);
    }

    @Test
    public void testGetObjectDynamicallyByIdCacheVersion_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository
                .getObjectDynamicallyById(DataRepositoryRobot.getValidUrl()
                        , DataRepositoryRobot.getColumnName()
                        , DataRepositoryRobot.getColumnId()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass()
                        , false
                        , mToCache);
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicGetObject(DataRepositoryRobot.getValidUrl()
                        , DataRepositoryRobot.getColumnName()
                        , DataRepositoryRobot.getColumnId()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass()
                        , false
                        , mToCache);
    }

    @Test
    public void testPostObjectDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenKeyValuesArePassed() {
        mDataRepository.postObjectDynamically(DataRepositoryRobot.getValidUrl()
                , DataRepository.DEFAULT_ID_TO_BE_REPLACED, DataRepositoryRobot.getValidJSONObject()
                , DataRepositoryRobot.getValidDomainClass()
                , DataRepositoryRobot.getValidDataClass()
                , true);
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicPostObject(DataRepositoryRobot.getValidUrl()
                        , DataRepository.DEFAULT_ID_TO_BE_REPLACED, DataRepositoryRobot.getValidJSONObject()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass()
                        , true);
    }

    @Test
    public void testPostObjectDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenJsonObjectIsPassed() {
        mDataRepository.postObjectDynamically(DataRepositoryRobot.getValidUrl()
                , DataRepository.DEFAULT_ID_TO_BE_REPLACED, DataRepositoryRobot.getValidJSONObject()
                , DataRepositoryRobot.getValidDomainClass()
                , DataRepositoryRobot.getValidDataClass()
                , true);
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicPostObject(DataRepositoryRobot.getValidUrl()
                        , DataRepository.DEFAULT_ID_TO_BE_REPLACED, DataRepositoryRobot.getValidJSONObject()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass()
                        , true);
    }

    @Test
    public void testPostListDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.postListDynamically(DataRepositoryRobot.getValidUrl()
                , DataRepository.DEFAULT_ID_TO_BE_REPLACED, DataRepositoryRobot.getValidJSONArray()
                , DataRepositoryRobot.getValidDomainClass()
                , DataRepositoryRobot.getValidDataClass()
                , true);
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicPostList(DataRepositoryRobot.getValidUrl()
                        , DataRepository.DEFAULT_ID_TO_BE_REPLACED, DataRepositoryRobot.getValidJSONArray()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass()
                        , true);
    }

    @Test
    public void testDeleteListDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.deleteListDynamically(DataRepositoryRobot.getValidUrl()
                , DataRepositoryRobot.getValidJSONArray()
                , DataRepositoryRobot.getValidDomainClass()
                , DataRepositoryRobot.getValidDataClass()
                , true);
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicDeleteCollection(DataRepositoryRobot.getValidUrl()
                        , DataRepository.DEFAULT_ID_TO_BE_REPLACED, DataRepositoryRobot.getValidJSONArray()
                        , DataRepositoryRobot.getValidDataClass()
                        , true);
    }

    @Test
    public void testSearchDisk_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenColumnNameAndValueArePassed() {
        assumeThat(mIsDiskType, Matchers.is(true));
        mDataRepository.searchDisk(DataRepositoryRobot.getValidQuery()
                , DataRepositoryRobot.getColumnName()
                , DataRepositoryRobot.getValidDomainClass()
                , DataRepositoryRobot.getValidDataClass());
        Mockito.verify(mDataStore, Mockito.times(1))
                .searchDisk(DataRepositoryRobot.getValidQuery()
                        , DataRepositoryRobot.getColumnName()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass());
    }

    @Test
    public void testSearchDisk_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenRealmQueryIsPassed() {
        assumeThat(mIsDiskType, Matchers.is(true));
        mDataRepository.searchDisk(DataRepositoryRobot.getValidRealmQuery()
                , DataRepositoryRobot.getValidDomainClass());
        Mockito.verify(mDataStore, Mockito.times(1))
                .searchDisk(DataRepositoryRobot.getValidRealmQuery()
                        , DataRepositoryRobot.getValidDomainClass());
    }

    @Test
    public void testUploadFile_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.uploadFileDynamically(DataRepositoryRobot.getValidUrl(),
                DataRepositoryRobot.getValidFile(),
                false, false,
                DataRepositoryRobot.getValidDomainClass(),
                DataRepositoryRobot.getValidDataClass());
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicUploadFile(DataRepositoryRobot.getValidUrl(), DataRepositoryRobot.getValidFile(),
                        false, false, DataRepositoryRobot.getValidDataClass());
    }

    @Test
    public void testPutObjectDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.putObjectDynamically(DataRepositoryRobot.getValidUrl()
                , DataRepository.DEFAULT_ID_TO_BE_REPLACED, DataRepositoryRobot.getValidJSONObject()
                , DataRepositoryRobot.getValidDomainClass()
                , DataRepositoryRobot.getValidDataClass(), true);
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicPutObject(DataRepositoryRobot.getValidUrl()
                        , DataRepository.DEFAULT_ID_TO_BE_REPLACED, DataRepositoryRobot.getValidJSONObject()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass(), true);
    }

    @Test
    public void testPutListDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.putListDynamically(DataRepositoryRobot.getValidUrl()
                , DataRepository.DEFAULT_ID_TO_BE_REPLACED, DataRepositoryRobot.getValidJSONArray()
                , DataRepositoryRobot.getValidDomainClass()
                , DataRepositoryRobot.getValidDataClass(), true);
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicPutList(DataRepositoryRobot.getValidUrl()
                        , DataRepository.DEFAULT_ID_TO_BE_REPLACED, DataRepositoryRobot.getValidJSONArray()
                        , DataRepositoryRobot.getValidDomainClass()
                        , DataRepositoryRobot.getValidDataClass(), true);
    }

    @Test
    public void testDeleteALlDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.deleteAllDynamically(DataRepositoryRobot.getValidUrl()
                , DataRepositoryRobot.getValidDataClass(), true);
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicDeleteAll(DataRepositoryRobot.getValidUrl()
                        , DataRepositoryRobot.getValidDataClass(), true);
    }
}