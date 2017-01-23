package com.zeyad.usecases.data.repository.stores;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.network.ApiConnectionFactory;
import com.zeyad.usecases.data.utils.Utils;
import com.zeyad.usecases.utils.TestUtility2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricGradleTestRunner;

import io.realm.Realm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(RobolectricGradleTestRunner.class)
@org.robolectric.annotation.Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Realm.class})
public class DataStoreFactoryJUnitTest {

    private DataBaseManager mDataBaseManager;
    private Context mMockedContext;
    private IDAOMapper mIDAOMapper;
    private DataStoreFactory mDataStoreFactory;

    @Before
    public void setUp() throws Exception {
        Config.init(InstrumentationRegistry.getContext());
        ApiConnectionFactory.init();
        mDataBaseManager = DataStoreFactoryJUnitRobot.createMockedDataBaseManager();
        mMockedContext = CloudDataStoreTestJUnitRobot.getMockedContext();
        mIDAOMapper = DataStoreFactoryJUnitRobot.createMockedEntityMapper();
        mDataStoreFactory = DataStoreFactoryJUnitRobot.createDataStoreFactory(mDataBaseManager, mMockedContext);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValid() throws Exception {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForValidItems(mDataBaseManager);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(),
                mIDAOMapper), is(instanceOf(DiskDataStore.class)));
    }


    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidAndNetWorkNotAvailable() throws Exception {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForValidItems(mDataBaseManager);
        TestUtility2.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(),
                mIDAOMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkNotAvailable() throws Exception {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForInvalidItems(mDataBaseManager);
        TestUtility2.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(),
                mIDAOMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkIsAvailable() throws Exception {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForInvalidItems(mDataBaseManager);
        TestUtility2.changeStateOfNetwork(mMockedContext, true);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(),
                mIDAOMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmpty() throws Exception {
        Mockito.when(Utils.isNetworkAvailable(InstrumentationRegistry.getContext())).thenReturn(true);
        DataStoreFactoryJUnitRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getSomeValidUrl(),
                mIDAOMapper), is(instanceOf(CloudDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItem() throws Exception {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(),
                mIDAOMapper), is(instanceOf(DiskDataStore.class)));
    }


    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItemAndNetWorkNotAvailable() throws Exception {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        TestUtility2.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(),
                mIDAOMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkNotAvailable() throws Exception {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
        TestUtility2.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(),
                mIDAOMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkIsAvailable() throws Exception {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
        TestUtility2.changeStateOfNetwork(mMockedContext, true);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(),
                mIDAOMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmptyForSingleItem() throws Exception {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getSomeValidUrl(),
                mIDAOMapper), is(instanceOf(CloudDataStore.class)));
    }

    @Test
    public void testDiskMethod_ifExpectedDataStoreIsReturned_whenMockedEntityMapperIsPassed() throws IllegalAccessException {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
        assertThat(mDataStoreFactory.disk(mIDAOMapper), is(notNullValue()));
    }

    @Test
    public void testDiskMethod_ifExpectedCloudStoreIsReturned_whenMockedEntityMapperIsPassed() {
        assertThat(mDataStoreFactory.cloud(mIDAOMapper), is(notNullValue()));
    }
}