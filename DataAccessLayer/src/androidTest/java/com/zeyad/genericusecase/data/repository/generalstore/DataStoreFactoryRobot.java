package com.zeyad.genericusecase.data.repository.generalstore;

import android.content.Context;
import android.support.annotation.NonNull;

import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.db.RealmManager;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.services.realm_test_models.TestModel;
import com.zeyad.genericusecase.data.services.realm_test_models.TestModelViewModelMapper;

import org.mockito.Mockito;

class DataStoreFactoryRobot {


    @NonNull
    static DataStoreFactory createDataStoreFactory(DataBaseManager dataBaseManager, Context mockedContext) {
        return new DataStoreFactory(dataBaseManager, mockedContext);
    }

    static DataBaseManager createMockedDataBaseManager() {
        final RealmManager realmManager = Mockito.mock(RealmManager.class);
        final Context mockedContext = CloudDataStoreTestRobot.getMockedContext();
        Mockito.when(realmManager.getContext()).thenReturn(mockedContext);
        return realmManager;
    }

    @NonNull
    static DataBaseManager setDataBaseManagerForValidItems(@NonNull DataBaseManager mockedDbManager) {
        Mockito.when(mockedDbManager.areItemsValid(Mockito.anyString())).thenReturn(Boolean.TRUE);
        return mockedDbManager;
    }

    @NonNull
    static DataBaseManager setDataBaseManagerForInvalidItems(@NonNull DataBaseManager mockedDbManager) {
        Mockito.when(mockedDbManager.areItemsValid(Mockito.any())).thenReturn(Boolean.FALSE);
        return mockedDbManager;
    }

    @NonNull
    static DataBaseManager setDataBaseManagerForValidItem(@NonNull DataBaseManager mockedDbManager) {
        Mockito.when(mockedDbManager.isItemValid(getValidColumnId(), getValidColumnName(), getDataClass()))
                .thenReturn(Boolean.TRUE);
        return mockedDbManager;
    }

    @NonNull
    static DataBaseManager setDataBaseManagerForInvalidItem(@NonNull DataBaseManager mockedDbManager) {
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
    static Class<TestModel> getDataClass() {
        return TestModel.class;
    }

    @NonNull
    static String getInvalidUrl() {
        return "";
    }

    static String getValidColumnName() {
        return "";
    }

    static int getValidColumnId() {
        return 1;
    }
}
