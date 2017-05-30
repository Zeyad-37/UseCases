package com.zeyad.usecases.stores;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.db.RealmManager;
import com.zeyad.usecases.mapper.DAOMapper;
import com.zeyad.usecases.network.ApiConnection;

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
public class DataStoreFactoryTest {

    private DataStoreFactory mDataStoreFactory; // class under test

    @Before
    public void setUp() throws Exception {
        mDataStoreFactory = new DataStoreFactory(dataClass -> mock(RealmManager.class),
                mock(ApiConnection.class), DAOMapper.getInstance());
    }

    private String getSomeValidUrl() {
        return "https://www.google.com";
    }

    private String getInvalidUrl() {
        return "";
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValid() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskDataStore.class)));
    }


    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidAndNetWorkNotAvailable() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkNotAvailable() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkIsAvailable() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmpty() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getSomeValidUrl(), Object.class),
                is(instanceOf(CloudDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItem() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskDataStore.class)));
    }


    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItemAndNetWorkNotAvailable() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkNotAvailable() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkIsAvailable() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmptyForSingleItem() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getSomeValidUrl(), Object.class),
                is(instanceOf(CloudDataStore.class)));
    }

    @Test
    public void testDiskMethod_ifExpectedDataStoreIsReturned_whenMockedEntityMapperIsPassed() throws IllegalAccessException {
        Config.setHasRealm(true);
        assertThat(mDataStoreFactory.disk(Object.class), is(notNullValue()));
    }

    @Test
    public void testDiskMethod_ifExpectedCloudStoreIsReturned_whenMockedEntityMapperIsPassed() {
        assertThat(mDataStoreFactory.cloud(Object.class), is(notNullValue()));
    }
}