package com.zeyad.usecases.data.repository.stores;

import android.support.annotation.NonNull;

import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.db.RealmManager;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.repository.DataRepository;
import com.zeyad.usecases.data.utils.ModelConverters;
import com.zeyad.usecases.utils.TestModel;
import com.zeyad.usecases.utils.TestUtility2;
import com.zeyad.usecases.utils.TestViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.realm.RealmResults;
import rx.Observable;
import rx.observers.TestSubscriber;

class DiskDataStoreJUnitRobot implements DiskDataStoreJUnitRobotInterface {

    @NonNull
    private final Random mRandom;
    private final DataBaseManager mDBManager;
    private final IDAOMapper mIDAOMapper;

    private DiskDataStoreJUnitRobot(DataBaseManager dbManager, IDAOMapper enitityMapper) {
        mDBManager = dbManager;
        mIDAOMapper = enitityMapper;
        mRandom = new Random();
    }

    @NonNull
    public static DiskDataStoreJUnitRobotInterface newInstance(DataBaseManager dbManager, IDAOMapper enitityMapper) {
        return new DiskDataStoreJUnitRobot(dbManager, enitityMapper);
    }

    @Override
    public void tearDown() {
        mDBManager.evictAll(TestModel.class)
                .subscribe(new TestSubscriber<>());
    }

    @NonNull
    @Override
    public DataStore createDiskDataStore() {
        return new DiskDataStore(mDBManager, mIDAOMapper);
    }

    @Override
    public void insertTestModels(int count) {
        for (int i = 0; i < count; i++) {
            final TestModel model = createTestModel();
            addTestModel(model);
        }
    }

    @NonNull
    @Override
    public TestModel createTestModel() {
        final int randomInt = getRandomInt();
        return createTestModel(randomInt);
    }

    @NonNull
    @Override
    public TestModel createTestModel(int id) {
        return new TestModel(id, getValueForTestModel(id));
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
        return TestModel.class;
    }

    @NonNull
    @Override
    public Class getDomainClass() {
        return TestViewModel.class;
    }

    @Override
    public void addTestModel(TestModel model) {
        mDBManager.put(model, TestModel.class)
                .subscribe(new TestSubscriber<>());
    }

    @Override
    public int getPrimaryIdForAnyInsertedTestModel() {
        final TestSubscriber<List> subscriber = new TestSubscriber<>();
        mDBManager.getAll(getDataClass()).subscribe(subscriber);
        List<TestModel> testModelList = subscriber.getOnNextEvents().get(0);
        return testModelList.get(mRandom.nextInt(testModelList.size())).getId();
    }

    @NonNull
    @Override
    public String getTestInfo(int testModelId) {
        return testModelId + ":" + getValueForTestModel(testModelId);
    }

    @NonNull
    @Override
    public RealmManager.RealmQueryProvider getRealmQueryForValue(String query) {
        return realm -> realm.where(TestModel.class)
                .contains("value", query);
    }

    @NonNull
    @Override
    public RealmManager.RealmQueryProvider getRealmQueryForId(int query) {
        return realm -> realm.where(TestModel.class)
                .equalTo("id", query);
    }

    @NonNull
    @Override
    public RealmManager.RealmQueryProvider getRealmQueryForAnyId() {
        return realm -> realm.where(TestModel.class)
                .equalTo("id", getPrimaryIdForAnyInsertedTestModel());
    }

    @NonNull
    @Override
    public List<Long> getListOfAllIds() {
        final TestSubscriber<List> subscriber = new TestSubscriber<>();
        mDBManager.getAll(getDataClass()).subscribe(subscriber);
        final List<TestModel> list = ((RealmResults) subscriber.getOnNextEvents().get(0));
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
    public TestSubscriber<Object> deleteAllExceptOneAfterAddingSome(@NonNull DataStore diskDataStore) {
        insertTestModels(10);
        final List<Long> listOfAllIds = getListOfAllIds();
        listOfAllIds.remove(0);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        diskDataStore.dynamicDeleteCollection(null, DataRepository.DEFAULT_ID_KEY
                , ModelConverters.convertToJsonArray(listOfAllIds)
                , getDataClass(), false, false)
                .subscribe(subscriber);
        return subscriber;
    }

    @Override
    @NonNull
    public TestSubscriber<Object> deleteAllAfterAddingSome(@NonNull DataStore diskDataStore) {
        insertTestModels(10);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        diskDataStore.dynamicDeleteAll(null, TestModel.class, false)
                .subscribe(subscriber);
        return subscriber;
    }

    @Override
    public JSONObject getTestModelJson() {
        final JSONObject[] jsonObject = new JSONObject[1];
        TestUtility2.getJsonObjectFrom(jsonObject, createTestModel());
        return jsonObject[0];
    }

//    @Override
//    public HashMap<String, Object> getTestModelJsonKeyValuePair() throws JSONException {
//        final JSONObject[] jsonObjects = new JSONObject[1];
//        TestUtility2.getJsonObjectFrom(jsonObjects, createTestModel());
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

    @NonNull
    @Override
    public TestSubscriber<Object> postTestModelKeyValuePair(@NonNull DataStore diskDataStore) throws JSONException {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        diskDataStore.dynamicPostObject(null, "id", getTestModelJson(), getDomainClass(), getDataClass(), false, false)
                .subscribe(subscriber);
        return subscriber;
    }

    @NonNull
    @Override
    public TestSubscriber<Object> putTestModelKeyValuePair(@NonNull DataStore diskDataStore) throws JSONException {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        diskDataStore.dynamicPutObject(null, "id", getTestModelJson(), getDomainClass(), getDataClass(), false, false)
                .subscribe(subscriber);
        return subscriber;
    }

    @NonNull
    @Override
    public TestSubscriber<Object> postTestModelJsonObject(@NonNull DataStore diskDataStore) {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        diskDataStore.dynamicPostObject(null, "id", getTestModelJson(), getDomainClass(), getDataClass(), false, false)
                .subscribe(subscriber);
        return subscriber;
    }

    @NonNull
    @Override
    public Observable<?> dynamicDownloadFile(@NonNull DataStore diskDataStore) {
        return diskDataStore.dynamicDownloadFile(null, null, true, false, false);
    }
}
