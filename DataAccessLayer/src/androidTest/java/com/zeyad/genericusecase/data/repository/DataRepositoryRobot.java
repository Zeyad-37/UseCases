package com.zeyad.genericusecase.data.repository;

import android.support.annotation.NonNull;

import com.zeyad.genericusecase.data.repository.stores.CloudDataStore;
import com.zeyad.genericusecase.data.repository.stores.DataStore;
import com.zeyad.genericusecase.data.repository.stores.DataStoreFactory;
import com.zeyad.genericusecase.data.repository.stores.DiskDataStore;
import com.zeyad.genericusecase.data.services.realm_test_models.TestModel;
import com.zeyad.genericusecase.data.services.realm_test_models.TestViewModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.io.File;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import rx.Observable;

class DataRepositoryRobot {

    public static final RealmQuery REALM_QUERY = Realm.getDefaultInstance().where(getValidDataClass());
    //    public static final HashMap<String, Object> OBJECT_HASH_MAP = new HashMap<>();
    public static final JSONObject JSON_OBJECT = new JSONObject();
    public static final JSONArray JSON_ARRAY = new JSONArray();
    public static final File MOCKED_FILE = Mockito.mock(File.class);
    public static final boolean ON_WIFI = true;
    public static final boolean WHILE_CHARGING = true;
    public static final boolean QUEUABLE = true;


    static DataStoreFactory createMockedDataStoreFactory(DataStore dataStore) {
        final DataStoreFactory dataStoreFactory = Mockito.mock(DataStoreFactory.class);
        Mockito.when(dataStoreFactory.cloud(Mockito.any())).thenReturn(dataStore);
        return dataStoreFactory;
    }

    static void addMockForDiskStore(@NonNull DataStoreFactory mockedDataStoreFactory, DataStore mockedDataStore) throws IllegalAccessException {
        Mockito.when(mockedDataStoreFactory.dynamically(Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
                .thenReturn(mockedDataStore);
        Mockito.when(mockedDataStoreFactory.dynamically(Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
                .thenReturn(mockedDataStore);
        Mockito.when(mockedDataStoreFactory.disk(Mockito.any())).thenReturn(mockedDataStore);
    }

    static void addMockForCloudStore(@NonNull DataStoreFactory mockedDataStoreFactory, DataStore mockedCloudStore) throws IllegalAccessException {
        Mockito.when(mockedDataStoreFactory.dynamically(Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
                .thenReturn(mockedCloudStore);
        Mockito.when(mockedDataStoreFactory.dynamically(Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
                .thenReturn(mockedCloudStore);
        Mockito.when(mockedDataStoreFactory.cloud(Mockito.any())).thenReturn(mockedCloudStore);
    }

    static DiskDataStore createMockedDiskStore() {
        return Mockito.mock(DiskDataStore.class);
    }

    static CloudDataStore createMockedCloudStore() {
        return Mockito.mock(CloudDataStore.class);
    }

    static String getValidUrl() {
        return "http://www.google.com";
    }

    @NonNull
    static Class getValidDomainClass() {
        return TestViewModel.class;
    }

    @NonNull
    static Class getValidDataClass() {
        return TestModel.class;
    }

    static String getColumnName() {
        return "id";
    }

    static String getKey() {
        return "image";
    }

    static int getColumnId() {
        return 1;
    }

    static void mockDataStore(boolean isDiskType, DataStore dataStore, @NonNull DataStoreFactory dataStoreFactory) throws IllegalAccessException {
        if (isDiskType) {
            DataRepositoryRobot.addMockForDiskStore(dataStoreFactory, dataStore);
        } else {
            DataRepositoryRobot.addMockForCloudStore(dataStoreFactory, dataStore);
        }
    }

    static OngoingStubbing<Observable<List>> mockDataStoreForDynamicGetList(@NonNull DataStore dataStore, boolean persist, boolean toCache) {
        return Mockito.when(dataStore.dynamicGetList(getValidUrl()
                , getValidDomainClass()
                , getValidDataClass()
                , persist
                , toCache));
    }

//    public static HashMap<String, Object> getValidKeyValuePairs() {
//        return OBJECT_HASH_MAP;
//    }

    @NonNull
    public static JSONObject getValidJSONObject() {
        return JSON_OBJECT;
    }

    @NonNull
    public static JSONArray getValidJSONArray() {
        return JSON_ARRAY;
    }

    public static String getValidQuery() {
        return "1";
    }

    @NonNull
    public static RealmQuery getValidRealmQuery() {
        return REALM_QUERY;

    }

    public static File getValidFile() {
        return MOCKED_FILE;
    }
}
