package com.zeyad.genericusecase.data.repository;

import android.support.annotation.NonNull;
import android.support.test.rule.BuildConfig;

import com.zeyad.genericusecase.data.repository.stores.DataStore;
import com.zeyad.genericusecase.data.repository.stores.DataStoreFactory;
import com.zeyad.genericusecase.utils.TestUtility2;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricGradleTestRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.Realm;
import rx.Observable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assume.assumeThat;

@RunWith(RobolectricGradleTestRunner.class)
@org.robolectric.annotation.Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Realm.class})
public class DataRepositoryJUnitTest {

    private final boolean mIsDiskType;
    private final boolean mToCache;
    private DataStore mDataStore;
    private DataRepository mDataRepository;

    public DataRepositoryJUnitTest(boolean isDiskType, boolean toCache) {
        mIsDiskType = isDiskType;
        mToCache = toCache;
    }

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
        TestUtility2.performInitialSetupOfDb();
        mDataStore = mIsDiskType ? DataRepositoryJUnitRobot.createMockedDiskStore()
                : DataRepositoryJUnitRobot.createMockedCloudStore();
        DataStoreFactory dataStoreFactory = DataRepositoryJUnitRobot.createMockedDataStoreFactory(mDataStore);
        mDataRepository = new DataRepository(dataStoreFactory, TestUtility2.createEntityMapper());
        DataRepositoryJUnitRobot.mockDataStore(mIsDiskType, mDataStore, dataStoreFactory);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetListDynamicallyCacheVersion_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.getListDynamically(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepositoryJUnitRobot.getValidPresentationClass()
                , DataRepositoryJUnitRobot.getValidDataClass()
                , false
                , mToCache);
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicGetList(DataRepositoryJUnitRobot.getValidUrl()
                        , DataRepositoryJUnitRobot.getValidPresentationClass()
                        , DataRepositoryJUnitRobot.getValidDataClass()
                        , false
                        , mToCache);
    }

    @Test
    public void testGetListDynamicallyCacheVersion_ifExpectedObservableIsReturned_whenArgumentsArePassed() {
        final Observable<List> mockedObservable = Mockito.mock(Observable.class);
        DataRepositoryJUnitRobot.mockDataStoreForDynamicGetList(mDataStore, false, mToCache)
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
    public void testGetObjectDynamicallyByIdCacheVersion_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository
                .getObjectDynamicallyById(DataRepositoryJUnitRobot.getValidUrl()
                        , DataRepositoryJUnitRobot.getColumnName()
                        , DataRepositoryJUnitRobot.getColumnId()
                        , DataRepositoryJUnitRobot.getValidPresentationClass()
                        , DataRepositoryJUnitRobot.getValidDataClass()
                        , false
                        , mToCache);
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicGetObject(DataRepositoryJUnitRobot.getValidUrl()
                        , DataRepositoryJUnitRobot.getColumnName()
                        , DataRepositoryJUnitRobot.getColumnId()
                        , DataRepositoryJUnitRobot.getValidPresentationClass()
                        , DataRepositoryJUnitRobot.getValidDataClass()
                        , false
                        , mToCache);
    }

    @Test
    public void testPostObjectDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenJsonObjectIsPassed() {
        mDataRepository.postObjectDynamically(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepository.DEFAULT_ID_KEY
                , DataRepositoryJUnitRobot.getValidJSONObject()
                , DataRepositoryJUnitRobot.getValidPresentationClass()
                , DataRepositoryJUnitRobot.getValidDataClass()
                , true, true);
        Mockito.verify(mDataStore, Mockito.times(1))
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
        Mockito.verify(mDataStore, Mockito.times(1))
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
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicDeleteCollection(DataRepositoryJUnitRobot.getValidUrl()
                        , DataRepository.DEFAULT_ID_KEY
                        , DataRepositoryJUnitRobot.getValidJSONArray()
                        , DataRepositoryJUnitRobot.getValidDataClass()
                        , true, true);
    }

    @Test
    public void testSearchDisk_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenColumnNameAndValueArePassed() {
        assumeThat(mIsDiskType, Matchers.is(true));
        mDataRepository.searchDisk(DataRepositoryJUnitRobot.getValidQuery()
                , DataRepositoryJUnitRobot.getColumnName()
                , DataRepositoryJUnitRobot.getValidPresentationClass()
                , DataRepositoryJUnitRobot.getValidDataClass());
        Mockito.verify(mDataStore, Mockito.times(1))
                .searchDisk(DataRepositoryJUnitRobot.getValidQuery()
                        , DataRepositoryJUnitRobot.getColumnName()
                        , DataRepositoryJUnitRobot.getValidPresentationClass()
                        , DataRepositoryJUnitRobot.getValidDataClass());
    }

    @Test
    public void testSearchDisk_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenRealmQueryIsPassed() {
        assumeThat(mIsDiskType, Matchers.is(true));
        mDataRepository.searchDisk(DataRepositoryJUnitRobot.getValidRealmQuery()
                , DataRepositoryJUnitRobot.getValidPresentationClass());
        Mockito.verify(mDataStore, Mockito.times(1))
                .searchDisk(DataRepositoryJUnitRobot.getValidRealmQuery()
                        , DataRepositoryJUnitRobot.getValidPresentationClass());
    }

    @Test
    public void testUploadFile_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        assumeThat(mIsDiskType, Matchers.is(false));
        mDataRepository.uploadFileDynamically(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepositoryJUnitRobot.getValidFile(), DataRepositoryJUnitRobot.getKey(),
                DataRepositoryJUnitRobot.getValidMap(), DataRepositoryJUnitRobot.ON_WIFI,
                DataRepositoryJUnitRobot.WHILE_CHARGING, DataRepositoryJUnitRobot.QUEUABLE,
                DataRepositoryJUnitRobot.getValidPresentationClass(), DataRepositoryJUnitRobot.getValidDataClass());
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicUploadFile(DataRepositoryJUnitRobot.getValidUrl(), DataRepositoryJUnitRobot.getValidFile(),
                        DataRepositoryJUnitRobot.getKey(), DataRepositoryJUnitRobot.getValidMap(),
                        DataRepositoryJUnitRobot.ON_WIFI, DataRepositoryJUnitRobot.WHILE_CHARGING,
                        DataRepositoryJUnitRobot.QUEUABLE, DataRepositoryJUnitRobot.getValidPresentationClass());
    }

    @Test
    public void testPutObjectDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.putObjectDynamically(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepository.DEFAULT_ID_KEY
                , DataRepositoryJUnitRobot.getValidJSONObject()
                , DataRepositoryJUnitRobot.getValidPresentationClass()
                , DataRepositoryJUnitRobot.getValidDataClass(), true, true);
        Mockito.verify(mDataStore, Mockito.times(1))
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
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicPutList(DataRepositoryJUnitRobot.getValidUrl()
                        , DataRepository.DEFAULT_ID_KEY
                        , DataRepositoryJUnitRobot.getValidJSONArray()
                        , DataRepositoryJUnitRobot.getValidPresentationClass()
                        , DataRepositoryJUnitRobot.getValidDataClass(), true, true);
    }

    @Test
    public void testDeleteALlDynamically_ifDataStoreGetMethodIsCalledWithExpectedParameters_whenArgumentsArePassed() {
        mDataRepository.deleteAllDynamically(DataRepositoryJUnitRobot.getValidUrl()
                , DataRepositoryJUnitRobot.getValidDataClass(), true);
        Mockito.verify(mDataStore, Mockito.times(1))
                .dynamicDeleteAll(DataRepositoryJUnitRobot.getValidUrl()
                        , DataRepositoryJUnitRobot.getValidDataClass(), true);
    }
}