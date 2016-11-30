package com.zeyad.genericusecase.data.repository.stores;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.BuildConfig;

import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.network.ApiConnectionFactory;
import com.zeyad.genericusecase.data.utils.Utils;
import com.zeyad.genericusecase.utils.TestUtility2;

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
    private EntityMapper<Object, Object> mEntityMapper;
    private DataStoreFactory mDataStoreFactory;

    @Before
    public void setUp() throws Exception {
        Config.init(InstrumentationRegistry.getContext());
        ApiConnectionFactory.init();
        mDataBaseManager = DataStoreFactoryJUnitRobot.createMockedDataBaseManager();
        mMockedContext = CloudDataStoreTestJUnitRobot.getMockedContext();
        mEntityMapper = DataStoreFactoryJUnitRobot.createMockedEntityMapper();
        mDataStoreFactory = DataStoreFactoryJUnitRobot.createDataStoreFactory(mDataBaseManager, mMockedContext);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValid() throws IllegalAccessException {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForValidItems(mDataBaseManager);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }


    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidAndNetWorkNotAvailable() throws IllegalAccessException {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForValidItems(mDataBaseManager);
        TestUtility2.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkNotAvailable() throws IllegalAccessException {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForInvalidItems(mDataBaseManager);
        TestUtility2.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkIsAvailable() throws IllegalAccessException {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForInvalidItems(mDataBaseManager);
        TestUtility2.changeStateOfNetwork(mMockedContext, true);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmpty() throws IllegalAccessException {
        Mockito.when(Utils.isNetworkAvailable(InstrumentationRegistry.getContext())).thenReturn(true);
        DataStoreFactoryJUnitRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getSomeValidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(CloudDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItem() throws IllegalAccessException {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }


    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItemAndNetWorkNotAvailable() throws IllegalAccessException {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        TestUtility2.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkNotAvailable() throws IllegalAccessException {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
        TestUtility2.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkIsAvailable() throws IllegalAccessException {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
        TestUtility2.changeStateOfNetwork(mMockedContext, true);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmptyForSingleItem() throws IllegalAccessException {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForValidItem(mDataBaseManager);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getSomeValidUrl(), Mockito.anyBoolean(),
                mEntityMapper), is(instanceOf(CloudDataStore.class)));
    }

    @Test
    public void testDiskMethod_ifExpectedDataStoreIsReturned_whenMockedEntityMapperIsPassed() throws IllegalAccessException {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
        assertThat(mDataStoreFactory.disk(mEntityMapper), is(notNullValue()));
    }

    @Test
    public void testDiskMethod_ifExpectedCloudStoreIsReturned_whenMockedEntityMapperIsPassed() {
        assertThat(mDataStoreFactory.cloud(mEntityMapper), is(notNullValue()));
    }
}