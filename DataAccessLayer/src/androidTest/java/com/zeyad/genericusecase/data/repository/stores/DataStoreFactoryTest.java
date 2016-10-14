package com.zeyad.genericusecase.data.repository.stores;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.data.TestUtility;
import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.network.ApiConnectionFactory;
import com.zeyad.genericusecase.data.utils.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(AndroidJUnit4.class)
public class DataStoreFactoryTest {

    private DataBaseManager mDataBaseManager;
    private Context mMockedContext;
    private EntityMapper<Object, Object> mEntityMapper;
    private DataStoreFactory mDataStoreFactory;

    @Before
    public void setUp() throws Exception {
        Config.init(InstrumentationRegistry.getContext());
        ApiConnectionFactory.init();
        mDataBaseManager = DataStoreFactoryRobot.createMockedDataBaseManager();
        mMockedContext = CloudDataStoreTestRobot.getMockedContext();
        mEntityMapper = DataStoreFactoryRobot.createMockedEntityMapper();
        mDataStoreFactory = DataStoreFactoryRobot.createDataStoreFactory(mDataBaseManager, mMockedContext);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValid() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForValidItems(mDataBaseManager);
        final DataStoreFactory dataStoreFactory = mDataStoreFactory;
        DataStore dataStore = dataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), mEntityMapper);
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }


    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidAndNetWorkNotAvailable() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForValidItems(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, false);
        DataStore dataStore = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), mEntityMapper);
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkNotAvailable() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForInvalidItems(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, false);
        DataStore dataStore = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), mEntityMapper);
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkIsAvailable() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForInvalidItems(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, true);
        DataStore dataStore = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), mEntityMapper);
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmpty() throws IllegalAccessException {
        Mockito.when(Utils.isNetworkAvailable(InstrumentationRegistry.getContext())).thenReturn(true);
        DataStoreFactoryRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        DataStore dataStore = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getSomeValidUrl(), mEntityMapper);
        assertThat(dataStore, is(instanceOf(CloudDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItem() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        final DataStoreFactory dataStoreFactory = mDataStoreFactory;
        DataStore dataStore = dataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), mEntityMapper);
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }


    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItemAndNetWorkNotAvailable() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, false);
        DataStore dataStore = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), mEntityMapper);
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkNotAvailable() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, false);
        DataStore dataStore = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), mEntityMapper);
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkIsAvailable() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, true);
        DataStore dataStore = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), mEntityMapper);
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmptyForSingleItem() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        DataStore dataStore = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getSomeValidUrl(), mEntityMapper);
        assertThat(dataStore, is(instanceOf(CloudDataStore.class)));
    }

    @Test
    public void testDiskMethod_ifExpectedDataStoreIsReturned_whenMockedEntityMapperIsPassed() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
        DataStore dataStore = mDataStoreFactory.disk(mEntityMapper);
        assertThat(dataStore, is(notNullValue()));
    }

    @Test
    public void testDiskMethod_ifExpectedCloudStoreIsReturned_whenMockedEntityMapperIsPassed() {
        DataStore cloudStore = mDataStoreFactory.cloud(mEntityMapper);
        assertThat(cloudStore, is(notNullValue()));
    }
}