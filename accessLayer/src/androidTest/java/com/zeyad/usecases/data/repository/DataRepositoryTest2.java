package com.zeyad.usecases.data.repository;

import android.support.test.runner.AndroidJUnit4;

import com.zeyad.usecases.data.mappers.DAOMapper;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.mappers.IDAOMapperFactory;
import com.zeyad.usecases.data.realm_test_models.TestModel;
import com.zeyad.usecases.data.repository.stores.DataStore;
import com.zeyad.usecases.data.repository.stores.DataStoreFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Collections;

import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * @author zeyad on 11/25/16.
 */
@RunWith(AndroidJUnit4.class)
public class DataRepositoryTest2 {
    private static final int FAKE_ITEM_ID = 123;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private DataRepository dataRepository;
    private DataStoreFactory mockDataStoreFactory;
    private DataStore mockDataStore;
    private IDAOMapper mockIDAOMapper;

    @Before
    public void setUp() {
        mockDataStoreFactory = Mockito.mock(DataStoreFactory.class);
        IDAOMapperFactory mockEntityDataMapper = Mockito.mock(IDAOMapperFactory.class);
        mockDataStore = Mockito.mock(DataStore.class);
        mockIDAOMapper = Mockito.mock(DAOMapper.class);
        dataRepository = new DataRepository(mockDataStoreFactory, mockEntityDataMapper);
//        try {
//            given(mockDataStoreFactory.dynamically(anyString(), true, mockIDAOMapper))
//                    .willReturn(mockDataStore);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        given(mockDataStoreFactory.cloud(mockIDAOMapper)).willReturn(mockDataStore);
    }

    @Test
    public void testGetList() {
        given(mockDataStore.dynamicGetList(anyString(), any(), any(), anyBoolean(), anyBoolean()))
                .willReturn(Observable.just(Collections.singletonList(new TestModel())));

        dataRepository.getListDynamically(anyString(), any(), any(), anyBoolean(), anyBoolean());

//        verify(mockDataStoreFactory).cloud(eq(mockIDAOMapper));
//        verify(mockDataStore).dynamicGetList(eq(anyString()), eq(any()), eq(any()), eq(anyBoolean()),
//                eq(anyBoolean()));

        Mockito.verify(mockDataStore, Mockito.times(1))
                .dynamicGetList(DataRepositoryRobot.getValidUrl()
                        , DataRepositoryRobot.getValidPresentationClass()
                        , DataRepositoryRobot.getValidDataClass()
                        , false
                        , false);
    }

    @Test
    public void testGetItemHappyCase() throws Exception {
        Observable observable = Observable.just(new TestModel());
        given(mockDataStore.dynamicGetObject(anyString(), anyString(), anyInt(), any(), any(),
                anyBoolean(), anyBoolean())).willReturn(observable);

        dataRepository.getObjectDynamicallyById(anyString(), anyString(), anyInt(), any(), any(),
                anyBoolean(), anyBoolean());

        verify(mockDataStoreFactory).dynamically(eq(anyString()), eq(anyBoolean()), eq(mockIDAOMapper));
        verify(mockDataStore).dynamicGetObject(eq(anyString()), eq(anyString()), eq(anyInt()), eq(any()),
                eq(any()), eq(anyBoolean()), eq(anyBoolean()));
    }
}
