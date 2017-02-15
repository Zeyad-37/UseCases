package com.zeyad.usecases.data.repository.stores;

import com.zeyad.usecases.data.db.RealmManager;
import com.zeyad.usecases.data.mappers.DefaultDAOMapper;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.domain.interactors.data.DataUseCase;

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

    private IDAOMapper mIDAOMapper;
    private DataStoreFactory mDataStoreFactory; // class under test

    @Before
    public void setUp() throws Exception {
        mIDAOMapper = mock(DefaultDAOMapper.class);

        mDataStoreFactory = new DataStoreFactory(mock(RealmManager.class), mock(RestApiImpl.class));
    }

    private String getSomeValidUrl() {
        return "https://www.google.com";
    }

    private String getInvalidUrl() {
        return "";
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValid() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), mIDAOMapper),
                is(instanceOf(DiskDataStore.class)));
    }


    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidAndNetWorkNotAvailable() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), mIDAOMapper),
                is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkNotAvailable() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), mIDAOMapper),
                is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidAndNetWorkIsAvailable() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), mIDAOMapper),
                is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmpty() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getSomeValidUrl(), mIDAOMapper),
                is(instanceOf(CloudDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItem() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), mIDAOMapper),
                is(instanceOf(DiskDataStore.class)));
    }


    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemAreValidForSingleItemAndNetWorkNotAvailable() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), mIDAOMapper),
                is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkNotAvailable() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), mIDAOMapper),
                is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfDiskDataStoreIsReturned_whenUrlIsEmptyAndItemsAreNotValidForSingleItemAndNetWorkIsAvailable() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getInvalidUrl(), mIDAOMapper),
                is(instanceOf(DiskDataStore.class)));
    }

    @Test
    public void testDynamically_IfCloudDataStoreIsReturned_whenUrlIsNotEmptyForSingleItem() throws Exception {
        assertThat(mDataStoreFactory.dynamically(getSomeValidUrl(), mIDAOMapper),
                is(instanceOf(CloudDataStore.class)));
    }

    @Test
    public void testDiskMethod_ifExpectedDataStoreIsReturned_whenMockedEntityMapperIsPassed() throws IllegalAccessException {
        DataUseCase.setHasRealm(true);
        assertThat(mDataStoreFactory.disk(mIDAOMapper), is(notNullValue()));
    }

    @Test
    public void testDiskMethod_ifExpectedCloudStoreIsReturned_whenMockedEntityMapperIsPassed() {
        assertThat(mDataStoreFactory.cloud(mIDAOMapper), is(notNullValue()));
    }
}