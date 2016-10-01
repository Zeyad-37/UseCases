package com.zeyad.genericusecase.data.repository.generalstore;

import android.support.annotation.NonNull;

import com.zeyad.genericusecase.data.services.realm_test_models.TestModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;
import rx.observers.TestSubscriber;

public interface DiskDataStoreRobotInterface {

    void tearDown();

    @NonNull
    DataStore createDiskDataStore();

    void insertTestModels(int count);

    @NonNull
    TestModel createTestModel();

    @NonNull
    TestModel createTestModel(int id);

    @NonNull
    String getValueForTestModel(int id);

    @NonNull
    String getPrefixForTestModel();

    int getRandomInt();

    @NonNull
    Class getDataClass();

    @NonNull
    Class getDomainClass();

    void addTestModel(TestModel testModel);

    int getPrimaryIdForAnyInsertedTestModel();

    @NonNull
    String getTestInfo(int testModelId);

    @NonNull
    RealmQuery getRealmQueryForValue(String query);

    @NonNull
    RealmQuery getRealmQueryForId(int query);

    @NonNull
    RealmQuery getRealmQueryForAnyId();

    @NonNull
    List<Long> getListOfAllIds();

    int getItemCount();

    @NonNull
    TestSubscriber<Object> deleteAllExceptOneAfterAddingSome(DataStore diskDataStore);

    @NonNull
    TestSubscriber<Object> deleteAllAfterAddingSome(DataStore diskDataStore);

    JSONObject getTestModelJson();

    @NonNull
    TestSubscriber<Object> postTestModelKeyValuePair(DataStore diskDataStore) throws JSONException;

    @NonNull
    TestSubscriber<Object> putTestModelKeyValuePair(DataStore diskDataStore) throws JSONException;

    @NonNull
    TestSubscriber<Object> postTestModelJsonObject(DataStore diskDataStore);

    @NonNull
    Observable<?> dynamicDownloadFile(DataStore diskDataStore);
}
