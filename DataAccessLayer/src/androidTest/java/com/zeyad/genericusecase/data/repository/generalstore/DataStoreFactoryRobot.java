package com.zeyad.genericusecase.data.repository.generalstore;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.db.GenericRealmManager;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.services.realm_test_models.TestModel2;
import com.zeyad.genericusecase.data.services.realm_test_models.TestModelViewModelMapper;

import org.mockito.Mockito;

class DataStoreFactoryRobot {


    static DataStoreFactory createDataStoreFactory(DataBaseManager dataBaseManager, Context mockedContext) {
        return new DataStoreFactory(dataBaseManager, mockedContext, Mockito.mock(GcmNetworkManager.class));
    }

    static DataBaseManager createMockedDataBaseManager() {
        final GenericRealmManager realmManager = Mockito.mock(GenericRealmManager.class);
        final Context mockedContext = CloudDataStoreTestRobot.getMockedContext();
        Mockito.when(realmManager.getContext()).thenReturn(mockedContext);
        return realmManager;
    }

    static DataBaseManager setDataBaseManagerForValidItems(DataBaseManager mockedDbManager) {
        Mockito.when(mockedDbManager.areItemsValid(Mockito.anyString())).thenReturn(Boolean.TRUE);
        return mockedDbManager;
    }

    static DataBaseManager setDataBaseManagerForInvalidItems(DataBaseManager mockedDbManager) {
        Mockito.when(mockedDbManager.areItemsValid(Mockito.any())).thenReturn(Boolean.FALSE);
        return mockedDbManager;
    }

    static DataBaseManager setDataBaseManagerForValidItem(DataBaseManager mockedDbManager) {
        Mockito.when(mockedDbManager.isItemValid(getValidColumnId(), getValidColumnName(), getDataClass()))
                .thenReturn(Boolean.TRUE);
        return mockedDbManager;
    }

    static DataBaseManager setDataBaseManagerForInvalidItem(DataBaseManager mockedDbManager) {
        Mockito.when(mockedDbManager.isItemValid(getValidColumnId(), getValidColumnName(), getDataClass()))
                .thenReturn(Boolean.FALSE);
        return mockedDbManager;
    }

    static EntityMapper<Object, Object> createMockedEntityMapper() {
        return Mockito.mock(TestModelViewModelMapper.class);
    }

    static String getSomeValidUrl() {
        return "https://www.google.com";
    }

    @NonNull
    static Class<TestModel2> getDataClass() {
        return TestModel2.class;
    }

    @NonNull
    static String getInvalidUrl() {
        return "";
    }

    public static String getValidColumnName() {
        return "";
    }

    public static int getValidColumnId() {
        return 1;
    }
}
