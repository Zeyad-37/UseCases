package com.zeyad.genericusecase.data.repository.generalstore;

import android.support.annotation.NonNull;

import com.zeyad.genericusecase.data.TestUtility;
import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.repository.DataRepository;
import com.zeyad.genericusecase.data.services.realm_test_models.TestModel2;
import com.zeyad.genericusecase.data.services.realm_test_models.TestViewModel;
import com.zeyad.genericusecase.data.utils.ModelConverters;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.observers.TestSubscriber;

class DiskDataStoreRobot implements DiskDataStoreRobotInterface {

    private final Random mRandom;
    private final DataBaseManager mDBManager;
    private final EntityMapper mEntityMapper;

    private DiskDataStoreRobot(DataBaseManager dbManager, EntityMapper enitityMapper) {
        mDBManager = dbManager;
        mEntityMapper = enitityMapper;
        mRandom = new Random();
    }

    public static DiskDataStoreRobotInterface newInstance(DataBaseManager dbManager, EntityMapper enitityMapper) {
        return new DiskDataStoreRobot(dbManager, enitityMapper);
    }

    @Override
    public void tearDown() {
        mDBManager.evictAll(TestModel2.class)
                .subscribe(new TestSubscriber<>());
    }

    @Override
    public DataStore createDiskDataStore() {
        return new DiskDataStore(mDBManager, mEntityMapper);
    }

    @Override
    public void insertTestModels(int count) {
        for (int i = 0; i < count; i++) {
            final TestModel2 model = createTestModel();
            addTestModel(model);
        }
    }

    @NonNull
    @Override
    public TestModel2 createTestModel() {
        final int randomInt = getRandomInt();
        return createTestModel(randomInt);
    }

    @NonNull
    @Override
    public TestModel2 createTestModel(int id) {
        return new TestModel2(id, getValueForTestModel(id));
    }

    @NonNull
    @Override
    public String getValueForTestModel(int id) {
        return getPrefixForTestModel() + id;
    }

    @NonNull
    @Override
    public String getPrefixForTestModel() {
        return "some value:";
    }

    /**
     * @return positive and non zero integers
     */
    @Override
    public int getRandomInt() {
        return Math.abs(mRandom.nextInt()) + 1;
    }

    @Override
    @NonNull
    public Class getDataClass() {
        return TestModel2.class;
    }

    @Override
    public Class getDomainClass() {
        return TestViewModel.class;
    }

    @Override
    public void addTestModel(TestModel2 model) {
        mDBManager.put(model, TestModel2.class)
                .subscribe(new TestSubscriber<>());
    }

    @Override
    public int getPrimaryIdForAnyInsertedTestModel() {
        final TestSubscriber<List> subscriber = new TestSubscriber<>();
        mDBManager.getAll(getDataClass()).subscribe(subscriber);
        List<TestModel2> testModel2List = subscriber.getOnNextEvents().get(0);
        return testModel2List.get(mRandom.nextInt(testModel2List.size())).getId();
    }

    @Override
    public String getTestInfo(int testModelId) {
        return testModelId + ":" + getValueForTestModel(testModelId);
    }

    @Override
    public RealmQuery getRealmQueryForValue(String query) {
        return Realm.getDefaultInstance().where(TestModel2.class)
                .contains("value", query);
    }

    @Override
    public RealmQuery getRealmQueryForId(int query) {
        return Realm.getDefaultInstance().where(TestModel2.class)
                .equalTo("id", query);
    }

    @Override
    public RealmQuery getRealmQueryForAnyId() {
        return Realm.getDefaultInstance().where(TestModel2.class)
                .equalTo("id", getPrimaryIdForAnyInsertedTestModel());
    }

    @Override
    public List<Long> getListOfAllIds() {
        final TestSubscriber<List> subscriber = new TestSubscriber<>();
        mDBManager.getAll(getDataClass()).subscribe(subscriber);
        final List<TestModel2> list = ((RealmResults) subscriber.getOnNextEvents().get(0));
        int count = list.size();
        List<Long> listOfId = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            listOfId.add((long) list.get(i).getId());
        }
        return listOfId;
    }

    @Override
    public int getItemCount() {
        final TestSubscriber<List> subscriber = new TestSubscriber<>();
        mDBManager.getAll(getDataClass()).subscribe(subscriber);
        return subscriber.getOnNextEvents().get(0).size();
    }

    @Override
    @NonNull
    public TestSubscriber<Object> deleteAllExceptOneAfterAddingSome(DataStore diskDataStore) {
        insertTestModels(10);
        final List<Long> listOfAllIds = getListOfAllIds();
        listOfAllIds.remove(0);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        diskDataStore
                .dynamicDeleteCollection(null, DataRepository.DEFAULT_ID_TO_BE_REPLACED
                        , ModelConverters.convertToJsonArray(listOfAllIds)
                        , getDataClass(), false)
                .subscribe(subscriber);
        return subscriber;
    }

    @Override
    @NonNull
    public TestSubscriber<Object> deleteAllAfterAddingSome(DataStore diskDataStore) {
        insertTestModels(10);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        diskDataStore.dynamicDeleteAll(null, TestModel2.class, false)
                .subscribe(subscriber);
        return subscriber;
    }

    @Override
    public JSONObject getTestModelJson() {
        final JSONObject[] jsonObject = new JSONObject[1];
        TestUtility.getJsonObjectFrom(jsonObject, createTestModel());
        return jsonObject[0];
    }

//    @Override
//    public HashMap<String, Object> getTestModelJsonKeyValuePair() throws JSONException {
//        final JSONObject[] jsonObjects = new JSONObject[1];
//        TestUtility.getJsonObjectFrom(jsonObjects, createTestModel());
//        final JSONObject jsonObject = jsonObjects[0];
//        final Iterator<String> keys = jsonObject.keys();
//        HashMap<String, Object> keyValue = new HashMap<>();
//
//        while (keys.hasNext()) {
//            final String next = keys.next();
//            keyValue.put(next, jsonObject.get(next));
//        }
//        return keyValue;
//    }

    @Override
    public TestSubscriber<Object> postTestModelKeyValuePair(DataStore diskDataStore) throws JSONException {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        diskDataStore.dynamicPostObject(null, "id", getTestModelJson(), getDomainClass(), getDataClass(), false)
                .subscribe(subscriber);
        return subscriber;
    }

    @Override
    public TestSubscriber<Object> putTestModelKeyValuePair(DataStore diskDataStore) throws JSONException {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        diskDataStore.dynamicPutObject(null, "id", getTestModelJson(), getDomainClass(), getDataClass(), false)
                .subscribe(subscriber);
        return subscriber;
    }

    @Override
    public TestSubscriber<Object> postTestModelJsonObject(DataStore diskDataStore) {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        diskDataStore.dynamicPostObject(null, "id", getTestModelJson(), getDomainClass(), getDataClass(), false)
                .subscribe(subscriber);
        return subscriber;
    }

    @Override
    public Observable<?> dynamicDownloadFile(DataStore diskDataStore) {
        return diskDataStore.dynamicDownloadFile(null, null, true, false);
    }
}
