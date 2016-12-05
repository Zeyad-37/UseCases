package com.zeyad.usecases.data.repository.stores;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.TestUtility;
import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.mappers.EntityMapper;
import com.zeyad.usecases.data.network.ApiConnectionFactory;
import com.zeyad.usecases.data.utils.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class))
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
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }


    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidAndNetWorkNotAvailable() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForValidItems(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkNotAvailable() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForInvalidItems(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkIsAvailable() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForInvalidItems(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, true);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmpty() throws IllegalAccessException {
        Mockito.when(Utils.isNetworkAvailable(InstrumentationRegistry.getContext())).thenReturn(true);
        DataStoreFactoryRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryRobot.getSomeValidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(CloudDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItem() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }


    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItemAndNetWorkNotAvailable() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkNotAvailable() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkIsAvailable() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
        TestUtility.changeStateOfNetwork(mMockedContext, true);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmptyForSingleItem() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryRobot.getSomeValidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(CloudDataStore.class)));
    }

    @Test
    public void testDiskMethod_ifExpectedDataStoreIsReturned_whenMockedEntityMapperIsPassed() throws IllegalAccessException {
        DataStoreFactoryRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
        assertThat(mDataStoreFactory.disk(mEntityMapper), is(notNullValue()));
    }

    @Test
    public void testDiskMethod_ifExpectedCloudStoreIsReturned_whenMockedEntityMapperIsPassed() {
        assertThat(mDataStoreFactory.cloud(mEntityMapper), is(notNullValue()));
    }
}