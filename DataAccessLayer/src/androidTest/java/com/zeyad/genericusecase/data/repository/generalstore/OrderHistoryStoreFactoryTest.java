package com.zeyad.genericusecase.data.repository.generalstore;

import android.content.Context;

import com.zeyad.genericusecase.data.TestUtility;
import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.mappers.EntityMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(JUnit4.class)
public class OrderHistoryStoreFactoryTest {

    private DataBaseManager mDataBaseManager;
    private Context mMockedContext;
    private EntityMapper<Object, Object> mEntityMapper;
    private DataStoreFactory mDataStoreFactory;

    @Before
    public void setUp() throws Exception {

        mDataBaseManager = DataStoreFactoryRobot.createMockedDataBaseManager();
        mMockedContext = CloudDataStoreTestRobot.getMockedContext();
        mEntityMapper = DataStoreFactoryRobot.createMockedEntityMapper();
        mDataStoreFactory = DataStoreFactoryRobot.createDataStoreFactory(mDataBaseManager, mMockedContext);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValid() {
        DataStoreFactoryRobot.setDataBaseManagerForValidItems(mDataBaseManager);
        final DataStoreFactory dataStoreFactory = mDataStoreFactory;
        DataStore dataStore = dataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), mEntityMapper, DataStoreFactoryRobot.getDataClass());
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }


    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidAndNetWorkNotAvailable() {
        DataStoreFactoryRobot.setDataBaseManagerForValidItems(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, false);
        DataStore dataStore = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), mEntityMapper, DataStoreFactoryRobot.getDataClass());
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkNotAvailable() {
        DataStoreFactoryRobot.setDataBaseManagerForInvalidItems(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, false);
        DataStore dataStore = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), mEntityMapper, DataStoreFactoryRobot.getDataClass());
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkIsAvailable() {
        DataStoreFactoryRobot.setDataBaseManagerForInvalidItems(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, true);
        DataStore dataStore = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), mEntityMapper, DataStoreFactoryRobot.getDataClass());
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmpty() {
        DataStore dataStore
                = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getSomeValidUrl(), mEntityMapper, DataStoreFactoryRobot.getDataClass());
        assertThat(dataStore, is(instanceOf(CloudDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItem() {
        DataStoreFactoryRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        final DataStoreFactory dataStoreFactory = mDataStoreFactory;
        DataStore dataStore = dataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl()
                , DataStoreFactoryRobot.getValidColumnName()
                , DataStoreFactoryRobot.getValidColumnId()
                , mEntityMapper, DataStoreFactoryRobot.getDataClass());
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }


    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItemAndNetWorkNotAvailable() {
        DataStoreFactoryRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, false);
        DataStore dataStore = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl()
                , DataStoreFactoryRobot.getValidColumnName()
                , DataStoreFactoryRobot.getValidColumnId(), mEntityMapper, DataStoreFactoryRobot.getDataClass());
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkNotAvailable() {
        DataStoreFactoryRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, false);
        DataStore dataStore = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl()
                , DataStoreFactoryRobot.getValidColumnName()
                , DataStoreFactoryRobot.getValidColumnId(), mEntityMapper, DataStoreFactoryRobot.getDataClass());
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkIsAvailable() {
        DataStoreFactoryRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, true);
        DataStore dataStore = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl()
                , DataStoreFactoryRobot.getValidColumnName()
                , DataStoreFactoryRobot.getValidColumnId(), mEntityMapper, DataStoreFactoryRobot.getDataClass());
        assertThat(dataStore, is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmptyForSingleItem() {
        DataStore dataStore
                = mDataStoreFactory.dynamically(DataStoreFactoryRobot.getSomeValidUrl()
                , DataStoreFactoryRobot.getValidColumnName()
                , DataStoreFactoryRobot.getValidColumnId(), mEntityMapper, DataStoreFactoryRobot.getDataClass());
        assertThat(dataStore, is(instanceOf(CloudDataStore.class)));
    }

    @Test
    public void testDiskMethod_ifExpectedDataStoreIsReturned_whenMockedEntityMapperIsPassed() {
        DataStore dataStore = mDataStoreFactory.disk(mEntityMapper);
        assertThat(dataStore, is(notNullValue()));
    }

    @Test
    public void testDiskMethod_ifExpectedCloudStoreIsReturned_whenMockedEntityMapperIsPassed() {
        DataStore cloudStore = mDataStoreFactory.cloud(mEntityMapper);
        assertThat(cloudStore, is(notNullValue()));
    }
}