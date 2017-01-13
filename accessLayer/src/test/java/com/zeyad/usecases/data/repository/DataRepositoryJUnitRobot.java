package com.zeyad.usecases.data.repository;

import android.support.annotation.NonNull;

import com.zeyad.usecases.data.repository.stores.CloudDataStore;
import com.zeyad.usecases.data.repository.stores.DataStore;
import com.zeyad.usecases.data.repository.stores.DataStoreFactory;
import com.zeyad.usecases.data.repository.stores.DiskDataStore;
import com.zeyad.usecases.utils.TestModel;
import com.zeyad.usecases.utils.TestViewModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;

public class DataRepositoryJUnitRobot {

    //    public static final RealmQuery REALM_QUERY = Realm.getDefaultInstance().where(getValidDataClass());
    //    public static final HashMap<String, Object> OBJECT_HASH_MAP = new HashMap<>();
    public static final JSONObject JSON_OBJECT = new JSONObject();
    public static final JSONArray JSON_ARRAY = new JSONArray();
    public static final File MOCKED_FILE = Mockito.mock(File.class);
    public static final HashMap MOCKED_MAP = Mockito.mock(HashMap.class);
    public static final boolean ON_WIFI = true;
    public static final boolean WHILE_CHARGING = true;
    public static final boolean QUEUABLE = true;


    static DataStoreFactory createMockedDataStoreFactory(DataStore dataStore) {
        final DataStoreFactory dataStoreFactory = Mockito.mock(DataStoreFactory.class);
        Mockito.when(dataStoreFactory.cloud(Mockito.any())).thenReturn(dataStore);
        return dataStoreFactory;
    }

    static void addMockForDiskStore(@NonNull DataStoreFactory mockedDataStoreFactory, DataStore mockedDataStore) throws Exception {
        Mockito.when(mockedDataStoreFactory.dynamically(Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
                .thenReturn(mockedDataStore);
        Mockito.when(mockedDataStoreFactory.dynamically(Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
                .thenReturn(mockedDataStore);
        Mockito.when(mockedDataStoreFactory.disk(Mockito.any())).thenReturn(mockedDataStore);
    }

    static void addMockForCloudStore(@NonNull DataStoreFactory mockedDataStoreFactory, DataStore mockedCloudStore) throws Exception {
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
    static Class getValidPresentationClass() {
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

    static void mockDataStore(boolean isDiskType, DataStore dataStore, @NonNull DataStoreFactory dataStoreFactory) throws Exception {
        if (isDiskType) {
            DataRepositoryJUnitRobot.addMockForDiskStore(dataStoreFactory, dataStore);
        } else {
            DataRepositoryJUnitRobot.addMockForCloudStore(dataStoreFactory, dataStore);
        }
    }

    static OngoingStubbing<Observable<List>> mockDataStoreForDynamicGetList(@NonNull DataStore dataStore, boolean persist, boolean toCache) {
        return Mockito.when(dataStore.dynamicGetList(getValidUrl()
                , getValidPresentationClass()
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
//        return REALM_QUERY;
        return null;
    }

    public static File getValidFile() {
        return MOCKED_FILE;
    }

    public static HashMap getValidMap() {
        return MOCKED_MAP;
    }
}
