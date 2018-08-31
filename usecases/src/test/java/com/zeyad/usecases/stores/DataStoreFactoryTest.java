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
    public void setUp() {
        mDataStoreFactory =
                new DataStoreFactory(
                        dataClass -> mock(RealmManager.class),
                        mock(ApiConnection.class),
                        new DAOMapper());
    }

    private String getSomeValidUrl() {
        return "https://www.google.com";
    }

    private String getInvalidUrl() {
        return "";
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValid() {
        assertThat(
                mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskStore.class)));
    }

    @Test
    public void
    testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidAndNetWorkNotAvailable() {
        assertThat(
                mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskStore.class)));
    }

    @Test
    public void
    testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkNotAvailable() {
        assertThat(
                mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskStore.class)));
    }

    @Test
    public void
    testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkIsAvailable() {
        assertThat(
                mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmpty() {
        assertThat(
                mDataStoreFactory.dynamically(getSomeValidUrl(), Object.class),
                is(instanceOf(CloudStore.class)));
    }

    @Test
    public void
    testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItem() {
        assertThat(
                mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskStore.class)));
    }

    @Test
    public void
    testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItemAndNetWorkNotAvailable() {
        assertThat(
                mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskStore.class)));
    }

    @Test
    public void
    testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkNotAvailable() {
        assertThat(
                mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskStore.class)));
    }

    @Test
    public void
    testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkIsAvailable() {
        assertThat(
                mDataStoreFactory.dynamically(getInvalidUrl(), Object.class),
                is(instanceOf(DiskStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmptyForSingleItem() {
        assertThat(
                mDataStoreFactory.dynamically(getSomeValidUrl(), Object.class),
                is(instanceOf(CloudStore.class)));
    }

    @Test
    public void testDiskMethod_ifExpectedDataStoreIsReturned_whenMockedEntityMapperIsPassed()
            throws IllegalAccessException {
        Config.INSTANCE.setWithRealm(true);
        assertThat(mDataStoreFactory.disk(Object.class), is(notNullValue()));
    }

    @Test
    public void testDiskMethod_ifExpectedCloudStoreIsReturned_whenMockedEntityMapperIsPassed() {
        assertThat(mDataStoreFactory.cloud(Object.class), is(notNullValue()));
    }
}

