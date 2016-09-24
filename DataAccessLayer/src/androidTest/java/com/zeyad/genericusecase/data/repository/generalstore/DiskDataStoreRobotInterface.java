package com.zeyad.genericusecase.data.repository.generalstore;

import android.support.annotation.NonNull;

import com.zeyad.genericusecase.data.services.realm_test_models.TestModel2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;
import rx.observers.TestSubscriber;

public interface DiskDataStoreRobotInterface {

    void tearDown();

    DataStore createDiskDataStore();

    void insertTestModels(int count);

    @NonNull
    TestModel2 createTestModel();

    @NonNull
    TestModel2 createTestModel(int id);

    @NonNull
    String getValueForTestModel(int id);

    @NonNull
    String getPrefixForTestModel();

    int getRandomInt();

    @NonNull
    Class getDataClass();

    Class getDomainClass();

    void addTestModel(TestModel2 testModel2);

    int getPrimaryIdForAnyInsertedTestModel();

    String getTestInfo(int testModelId);

    RealmQuery getRealmQueryForValue(String query);

    RealmQuery getRealmQueryForId(int query);

    RealmQuery getRealmQueryForAnyId();

    List<Long> getListOfAllIds();

    int getItemCount();

    @NonNull
    TestSubscriber<Object> deleteAllExceptOneAfterAddingSome(DataStore diskDataStore);

    @NonNull
    TestSubscriber<Object> deleteAllAfterAddingSome(DataStore diskDataStore);

    JSONObject getTestModelJson();

    TestSubscriber<Object> postTestModelKeyValuePair(DataStore diskDataStore) throws JSONException;

    TestSubscriber<Object> putTestModelKeyValuePair(DataStore diskDataStore) throws JSONException;

    TestSubscriber<Object> postTestModelJsonObject(DataStore diskDataStore);

    Observable<?> dynamicDownloadFile(DataStore diskDataStore);
}
