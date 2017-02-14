package com.zeyad.usecases.data.repository.stores;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.domain.interactors.data.DataUseCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class DataStoreFactoryJUnitTest {

    private DataBaseManager mDataBaseManager;
    private IDAOMapper mIDAOMapper;
    private DataStoreFactory mDataStoreFactory; // class under test

    @Before
    public void setUp() throws Exception {
//        PowerMockito.mockStatic(Utils.class);
        Config.setBaseURL("www.google.com");
//        ApiConnectionFactory.init();
        mDataBaseManager = DataStoreFactoryJUnitRobot.createMockedDataBaseManager();
        mIDAOMapper = DataStoreFactoryJUnitRobot.createMockedEntityMapper();

        mDataStoreFactory = DataStoreFactoryJUnitRobot.createDataStoreFactory(mDataBaseManager,
                mock(RestApiImpl.class));
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
//        TestUtility2.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(),
                mIDAOMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkNotAvailable() throws Exception {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForInvalidItems(mDataBaseManager);
//        TestUtility2.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(),
                mIDAOMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkIsAvailable() throws Exception {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForInvalidItems(mDataBaseManager);
//        TestUtility2.changeStateOfNetwork(mMockedContext, true);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(),
                mIDAOMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmpty() throws Exception {
//        when(Utils.isNetworkAvailable(mMockedContext)).thenReturn(true);
//
//        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
//        when(mMockedContext.getSystemService(Context.CONNECTIVITY_SERVICE))
//                .thenReturn(mock(ConnectivityManager.class));
//        when(connectivityManager.getAllNetworks()).thenReturn(new Network[]{});
//        when(connectivityManager.getAllNetworkInfo()).thenReturn(new NetworkInfo[0]);

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
//        TestUtility2.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(),
                mIDAOMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkNotAvailable() throws Exception {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
//        TestUtility2.changeStateOfNetwork(mMockedContext, false);
        assertThat(mDataStoreFactory.dynamically(DataStoreFactoryJUnitRobot.getInvalidUrl(),
                mIDAOMapper), is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkIsAvailable() throws Exception {
        DataStoreFactoryJUnitRobot.setDataBaseManagerForInvalidItem(mDataBaseManager);
//        TestUtility2.changeStateOfNetwork(mMockedContext, true);
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
        DataUseCase.setHasRealm(true);
        assertThat(mDataStoreFactory.disk(mIDAOMapper), is(notNullValue()));
    }

    @Test
    public void testDiskMethod_ifExpectedCloudStoreIsReturned_whenMockedEntityMapperIsPassed() {
        assertThat(mDataStoreFactory.cloud(mIDAOMapper), is(notNullValue()));
    }
}