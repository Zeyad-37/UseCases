package com.zeyad.usecases.data.db;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.BuildConfig;
import android.support.test.rule.UiThreadTestRule;

import com.google.gson.Gson;
import com.zeyad.usecases.data.utils.Utils;
import com.zeyad.usecases.utils.ModelWithStringPrimaryKey;
import com.zeyad.usecases.utils.RealmModelClass;
import com.zeyad.usecases.utils.TestModel;
import com.zeyad.usecases.utils.TestUtility2;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsCollectionContaining;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Realm.class})
public class RealmManagerImplTestJUnit {

    private static final String TEST_MODEL_PREFIX = "random value:";

    @NonNull
    @Rule
    public Timeout globalTimeout = new Timeout(TestUtility2.TIMEOUT_TIME_VALUE_LARGE, TestUtility2.TIMEOUT_TIME_UNIT);
    @NonNull
    @Rule
    public UiThreadTestRule mUiThreadTestRule = new UiThreadTestRule();
    @NonNull
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private RealmManager mRealmManager;
    private Random mRandom;

    @Before
    public void before() {
        com.zeyad.usecases.Config.init();
        TestUtility2.performInitialSetupOfDb();
        mRandom = new Random();
        mRealmManager = getGeneralRealmManager();
        mRealmManager.evictAll(TestModel.class).subscribe(new TestSubscriber<>());
        mRealmManager.evictAll(RealmModelClass.class).subscribe(new TestSubscriber<>());
        mRealmManager.evictAll(ModelWithStringPrimaryKey.class).subscribe(new TestSubscriber<>());
    }

    @After
    public void after() {
    }

    @Test
    public void testPutRealmObject_ifNoExceptionIsRaised_whenOperationIsDoneOnMainThread() {
        runOnMainThread(() -> putTestModel(mRealmManager));
    }

    @Test
    public void testPutRealmObject_ifNoExceptionIsRaised_whenOperationIsPerformedInWorkerThread() {
        putTestModel(mRealmManager);
    }

    @Test
    public void testPutRealmObject_ifNoExceptionIsRaised_WhenInsertInMainThreadAndReadInWorkerThread()
            throws InterruptedException {
        RealmManager realmManager = getGeneralRealmManager();
        runOnMainThread(() -> putTestModel(realmManager));
        realmManager.getAll(TestModel.class);
    }

    @Test
    public void testPutJsonMethod_ifNoExceptionIsRaised_whenOperationIsPerformedOnUiThread() throws Throwable {
        runOnMainThread(() -> {
            try {
                insertJsonObject();
            } catch (JSONException e) {
                assertThat("got exception:" + e.getMessage(), false);
            }
        });
    }

    @Test
    public void testPutJsonMethod_ifNewCountIsOneMoreThanPreviousCount_whenJsonObjectIsInserted() throws Exception {
        insertJsonObject();
    }

    @Test
    public void testPutJsonMethod_ifNoExceptionIsThrown_whenOperationIsPerformed() throws Throwable {
        int previousTestModelCount = getQueryList(TestModel.class).size();
        insertJsonObject();
        assertGetAllForSize(TestModel.class, previousTestModelCount + 1);
    }

    @Test
    public void testPutJsonMethod_ifTrueIsReturnedInSubscriber_whenOperationIsPerformed() throws Throwable {
        TestModel testModel = createTestModelWithRandomId();
        String json = new Gson().toJson(testModel);
        JSONObject jsonObject = new JSONObject(json);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObject, "id", TestModel.class)
                .subscribe(subscriber);
        subscriber.assertValue(Boolean.TRUE);
    }

    @Test
    public void testPutRealmObject_ifNoExceptionIsThrown_whenOperationIsPerformed() throws Throwable {
        int previousTestModelCount = getQueryList(TestModel.class).size();
        putTestModel(mRealmManager);
        assertGetAllForSize(TestModel.class, previousTestModelCount + 1);
    }

    @Test
    public void testPutRealmObject_ifTrueIsReturnedInSubscriber_whenOperationIsPerformed() throws Throwable {
        Observable<?> observable = mRealmManager.put(createTestModelWithRandomId(), RealmManagerImplTestJUnit.class);
        TestSubscriber<Object> subscriber = TestSubscriber.create();
        observable.subscribe(subscriber);
        subscriber.assertValue(Boolean.TRUE);
    }

    @Test
    public void testPutJsonMethod_ifNoExceptionIsRaised_whenJsonObjectCreatedInBackgroundAndInsertedInUiThread()
            throws Throwable {
        TestModel testModel = createTestModelWithRandomId();
        String json = new Gson().toJson(testModel);
        JSONObject jsonObject = new JSONObject(json);
        runOnMainThread(() -> {
            final TestSubscriber<Object> subscriber = new TestSubscriber<>();
            RealmManager realmManager
                    = getGeneralRealmManager();
            realmManager.put(jsonObject, "id", TestModel.class)
                    .subscribe(subscriber);
            subscriber.assertNoErrors();
        });
    }

    @Test
    public void testPutJsonMethod_ifNoExceptionIsGenerated_whenOperationIsPerformedOnUIThread()
            throws Throwable {
        final JSONObject[] jsonObject = new JSONObject[1];
        runOnMainThread(() -> TestUtility2.getJsonObjectFrom(jsonObject
                , createTestModelWithRandomId()));
        TestUtility2.assertConvertedJsonArray(jsonObject);
        RealmManager realmManager
                = getGeneralRealmManager();
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        realmManager.put(jsonObject[0], "id", TestModel.class)
                .subscribe(subscriber);
        subscriber.assertNoErrors();
    }

    @Test
    public void testPutJsonObject_ifNoExceptionIsRaised_whenJsonObjectIsInserted() {
        RealmManager realmManager
                = getGeneralRealmManager();
        JSONObject[] jsonObject = new JSONObject[1];
        TestUtility2.getJsonObjectFrom(jsonObject
                , createTestModelWithRandomId());
        TestUtility2.assertConvertedJsonArray(jsonObject);
        Observable<?> observable
                = realmManager.put(jsonObject[0], "id", TestModel.class);
        assertPutOperationObservable(observable);
    }

    @Test
    public void testPutJsonObject_ifNoExceptionIsRaised_whenJsonObjectsAreInsertedMultipleTimes() {
        TestUtility2.executeMultipleTimes(TestUtility2.EXECUTION_COUNT_SMALL, this::testPutJsonObject_ifNoExceptionIsRaised_whenJsonObjectIsInserted);
    }

    @Test
    public void testPutJsonObject_ifNoExceptionIsGenerated_whenJsonIsCreatedInWorkerAndInsertedInUI() {
        createJsonObjectInWorkerThreadAndInsertInUi();
    }

    @Test
    public void testPutJsonObject_ifNoExceptionIsGenerated_whenJsonIsCreatedInWorkerAndInsertedInUIMultipleTimes() {
        TestUtility2.executeMultipleTimes(TestUtility2.EXECUTION_COUNT_SMALL, this::createJsonObjectInWorkerThreadAndInsertInUi);
    }

    @Test
    public void testPutJsonObject_IfNoExceptionIsRaised_whenOrderModelJsonCreateInUIAndWriteInWorker() {
        RealmManager realmManager
                = getGeneralRealmManager();
        JSONObject[] jsonObject
                = new JSONObject[1];
        runOnMainThread(() -> {
            TestUtility2.getJsonObjectFrom(jsonObject, createTestModelWithRandomId());
            TestUtility2.assertConvertedJsonArray(jsonObject);
        });
        Observable<?> observable = realmManager.put(jsonObject[0], "id", TestModel.class);
        assertPutOperationObservable(observable);
    }

    @Test
    public void testEvictAllMethod_IfDeletesAllModels_whenSinglePut() {
        putTestModel(mRealmManager);
        Observable<Boolean> evictObservable
                = mRealmManager.evictAll(TestModel.class);
        final TestSubscriber<Boolean> evictAllSubscriber = new TestSubscriber<>();
        evictObservable.subscribe(evictAllSubscriber);
        evictAllSubscriber.assertValue(Boolean.TRUE);
        assertGetAllForSize(TestModel.class, 0);
    }

    @Test
    public void testEvictAllMethod_ifNoExceptionIsThrown_IfIncorrectClassnameIsPassed() {
        mRealmManager.evictAll(TestModel.class).subscribe(new TestSubscriber<>());
    }

    @Test
    public void testEvictAllMethod_IfDeletesAllModels_whenMultiplePut() {
        TestUtility2.executeMultipleTimes(TestUtility2.EXECUTION_COUNT_SMALL, () -> putTestModel(mRealmManager));
        assertGetAllForSize(TestModel.class, TestUtility2.EXECUTION_COUNT_SMALL);
        Observable<Boolean> evictObservable
                = mRealmManager.evictAll(TestModel.class);
        final TestSubscriber<Boolean> evictAllSubscriber = new TestSubscriber<>();
        evictObservable.subscribe(evictAllSubscriber);
        evictAllSubscriber.assertValue(Boolean.TRUE);
        assertGetAllForSize(TestModel.class, 0);
    }


    @Test
    public void testEvictBySingleModel_ifManagedModelIsInsertedAndEvicted_thenWouldBeDeleted() {
        TestModel insertedTestModel = putTestModel(mRealmManager);
        mRealmManager.evict(getManagedObject(insertedTestModel, mRealmManager), TestModel.class);
        assertGetAllForSize(TestModel.class, 0);
    }


    @Test
    public void testPutJsonObject_ifAddedSuccessfully_whenRealmObjectWithIntegerPrimaryKeyIsAdded() {
        ModelWithStringPrimaryKey modelWithStringPrimaryKey = new ModelWithStringPrimaryKey(String.valueOf(getRandomInt())
                , TEST_MODEL_PREFIX);
        JSONObject[] jsonObjectArray = new JSONObject[1];
        TestUtility2.getJsonObjectFrom(jsonObjectArray, modelWithStringPrimaryKey);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObjectArray[0], "studentId", ModelWithStringPrimaryKey.class)
                .subscribe(subscriber);
        TestUtility2.assertNoErrors(subscriber);
        assertGetAllForSize(ModelWithStringPrimaryKey.class, 1);
    }

    @Test(expected = AssertionError.class)
    public void testPutJsonObject_ifAddedSuccessfully_whenRealmObjectWithIntegerPrimaryKeyIsAddedAndIdIsPassedAsPrimaryKey() {
        ModelWithStringPrimaryKey modelWithStringPrimaryKey = new ModelWithStringPrimaryKey(String.valueOf(getRandomInt())
                , TEST_MODEL_PREFIX);
        JSONObject[] jsonObjectArray = new JSONObject[1];
        TestUtility2.getJsonObjectFrom(jsonObjectArray, modelWithStringPrimaryKey);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObjectArray[0], "id", ModelWithStringPrimaryKey.class)
                .subscribe(subscriber);
        TestUtility2.assertNoErrors(subscriber);
        assertGetAllForSize(ModelWithStringPrimaryKey.class, 1);
    }

    @Test(expected = AssertionError.class)
    public void testPutJsonObject_ifAddedSuccessfully_whenRealmObjectWithStringPrimaryKeyIsAdded() {
        ModelWithStringPrimaryKey modelWithStringPrimaryKey
                = new ModelWithStringPrimaryKey("abc"
                , TEST_MODEL_PREFIX);
        JSONObject[] jsonObjectArray = new JSONObject[1];
        TestUtility2.getJsonObjectFrom(jsonObjectArray, modelWithStringPrimaryKey);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObjectArray[0], "studentId", ModelWithStringPrimaryKey.class)
                .subscribe(subscriber);
        TestUtility2.assertNoErrors(subscriber);
        assertGetAllForSize(ModelWithStringPrimaryKey.class, 1);
    }

    @Test
    public void testEvictBySingleModel_ifMultipleModelsAreInsertedAndEvictedSequentially() {
        List<TestModel> insertedTestModelList = new ArrayList<>();
        for (int i = 0; i < TestUtility2.EXECUTION_COUNT_SMALL; i++) {
            insertedTestModelList.add(putTestModel(mRealmManager));
        }
        assertGetAllForSize(TestModel.class, TestUtility2.EXECUTION_COUNT_SMALL);
        for (TestModel testModel : insertedTestModelList) {
            final TestModel managedObject = getManagedObject(testModel, mRealmManager);
            mRealmManager.evict(managedObject, TestModel.class);
        }
        assertGetAllForSize(TestModel.class, 0);
    }

    @Test
    public void testGetManagedObject_ifReturnedObjectIsValid_whenNonManagedObjectIsProvided() {
        TestModel testModel = putTestModel(mRealmManager);
        TestModel managedTestModel = getManagedObject(testModel, mRealmManager);
        MatcherAssert.assertThat("managed test model should be valid", managedTestModel.isValid());
    }

    @Test
    public void testPutRealmObject_ifInsertedObjectIsNotvalid_whenRealmObjectIsInserted() {
        TestModel testModel = putTestModel(mRealmManager);
        assertThat("test model should not be valid", !testModel.isValid());
    }

    @Test
    public void testAddingOrderModelJsonCreateInUIAndWriteInWorker_multipleTimes() {
        TestUtility2.executeMultipleTimes(TestUtility2.EXECUTION_COUNT_SMALL, this::testPutJsonObject_IfNoExceptionIsRaised_whenOrderModelJsonCreateInUIAndWriteInWorker);
    }

    @Test
    public void testPutRealmObject_ifCorrectNumberOfItemsAreInserted_whenSingleItemIsInserted() {
        Observable<?> observable = mRealmManager.put(createTestModelWithRandomId(), TestModel.class);
        observable.subscribe(new TestSubscriber<>());
        assertGetAllForSize(TestModel.class, 1);
    }

    @Test
    public void testPutRealmObject_ifCorrectNumberOfItemsAreInserted_whenMultipleItemAreInserted() {
        for (int i = 0; i < TestUtility2.EXECUTION_COUNT_SMALL; i++) {
            Observable<?> observable = mRealmManager.put(createTestModelWithRandomId(), TestModel.class);
            observable.subscribe(new TestSubscriber<>());
        }
        assertGetAllForSize(TestModel.class, TestUtility2.EXECUTION_COUNT_SMALL);
    }

    @Test
    public void testPutRealmObject_ifModelIsUpdated_whenUpdatedModelIsInserted() {
        final TestModel testModelInstance = createTestModelWithRandomId();
        mRealmManager.put(testModelInstance, TestModel.class)
                .subscribe(new TestSubscriber<>());
        testModelInstance.setValue("test update");
        mRealmManager.put(testModelInstance, TestModel.class)
                .subscribe(new TestSubscriber<>());
        //check if no duplicates are added
        assertGetAllForSize(TestModel.class, 1);
        //check if values are updated or not
        final TestSubscriber<Object> getItemByIdSubscriber = new TestSubscriber<>();
        mRealmManager.getById("id", testModelInstance.getId(), TestModel.class)
                .subscribe(getItemByIdSubscriber);
        TestModel currentModel
                = assertSubscriberGetSingleNextEventWithNoError(getItemByIdSubscriber
                , TestModel.class);
        MatcherAssert.assertThat("two instances are not same", currentModel.equals(testModelInstance));
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * , returned observable should not be null.
     */
    @Test
    public void testGetById_ifReturnedObservableIsNotNull_whenSingleModelIsInsertedAndQueried() {
        TestModel insertedTestModel = putTestModel(mRealmManager);
        assertThat(mRealmManager.getById("id"
                , insertedTestModel.getId()
                , TestModel.class), is(notNullValue()));
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * Returned Observable should return no error.
     */
    @Test
    public void testGetById_ifObservableShouldReturnNoError_whenSingleModelIsInsertedAndQueried() {
        TestModel insertedTestModel = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , insertedTestModel.getId()
                , TestModel.class)
                .subscribe(querySubscriber);
        querySubscriber.assertNoErrors();
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * Returned Observable should return size of 1 next event list.
     */
    @Test
    public void testGetById_ifOnNextEventListHasAtLeastOneItem_whenSingleModelIsInsertedAndQueried() {
        TestModel insertedTestModel = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , insertedTestModel.getId()
                , TestModel.class)
                .subscribe(querySubscriber);
        assertThat(querySubscriber.getOnNextEvents(), iterableWithSize(1));
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * on next event list should contain  TestModel result at item index 1
     */
    @Test
    public void testGetById_ifQueryResultIsInstanceOfTestModel_whenSingleModelIsInsertedAndQueried() {
        TestModel insertedTestModel = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , insertedTestModel.getId()
                , TestModel.class)
                .subscribe(querySubscriber);
        assertThat(querySubscriber.getOnNextEvents().get(0), is(instanceOf(TestModel.class)));
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * returned test nmodel is same as inserted
     */
    @Test
    public void testGetById_ifQueryResultsHasCorrectModel_whenSingleModelIsInsertedAndQueried() {
        TestModel insertedTestModel = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , insertedTestModel.getId()
                , TestModel.class)
                .subscribe(querySubscriber);
        TestModel returnedTestModel = (TestModel) querySubscriber.getOnNextEvents().get(0);
        assertThat(returnedTestModel, is(equalTo(insertedTestModel)));
    }

    /**
     * Test if we insert single test model and then make a query using invalid id.
     * we should getInstance a test model with maximum id.
     */
    @Test
    public void testGetById_ifModelWithMaxIdIsReturned_whenQueriedUsingNegativeId() {
        TestModel insertedTestModel = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , TestUtility2.INVALID_ITEM_ID
                , TestModel.class)
                .subscribe(querySubscriber);
        TestModel returnedTestModel = (TestModel) querySubscriber.getOnNextEvents().get(0);
        int maxId = Utils.getMaxId(TestModel.class, "id");
        assertThat(returnedTestModel.getId(), is(equalTo(maxId)));
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * , returned observable should not be null.
     */
    @Test
    public void testGetWhere_ifReturnedObservableIsNotNull_whenRealmQueryIsMadeForSingleItem() {
        TestModel insertedTestModel = createTestModelWithRandomId();
        RealmManager.RealmQueryProvider realmQuery = getExactTestModelRealmQuery(insertedTestModel);
        assertThat(mRealmManager.getQuery(realmQuery), is(notNullValue()));
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * Returned Observable should return no error.
     */
    @Test
    public void testGetWhere_ifObservableShouldReturnNoError_whenRealmQueryIsMadeForSingleItem() {
        TestModel insertedTestModel = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel);
        RealmManager.RealmQueryProvider realmQuery = getExactTestModelRealmQuery(insertedTestModel);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        mRealmManager.getQuery(realmQuery)
                .subscribe(querySubscriber);
        querySubscriber.assertNoErrors();
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * Returned Observable should return size of 1 next event list.
     */
    @Test
    public void testGetWhere_ifOnNextEventListHasAtleastOneItem_whenRealmQueryIsMadeForSingleItem() {
        TestModel insertedTestModel = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        RealmManager.RealmQueryProvider realmQuery = getExactTestModelRealmQuery(insertedTestModel);
        mRealmManager.getQuery(realmQuery)
                .subscribe(querySubscriber);
        assertThat(querySubscriber.getOnNextEvents(), iterableWithSize(1));
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * on next event list should contain query result at item index 1
     */
    @Test
    public void testGetWhere_ifNextEventListContainQueryResultAtIndex1_whenRealmQueryIsMadeForSingleItem() {
        final TestModel insertedTestModel = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        final RealmManager.RealmQueryProvider realmQuery = getExactTestModelRealmQuery(insertedTestModel);
        mRealmManager.getQuery(realmQuery)
                .subscribe(querySubscriber);
        assertThat(querySubscriber.getOnNextEvents().get(0), is(instanceOf(RealmResults.class)));
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * query results are not empty
     */
    @Test
    public void testGetWhere_ifQueryResultsAreNotEmpty_whenRealmQueryIsMadeForSingleItem() {
        TestModel insertedTestModel = createTestModelWithRandomId();
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        final RealmManager.RealmQueryProvider realmQuery = getExactTestModelRealmQuery(insertedTestModel);
        putTestModel(mRealmManager, insertedTestModel);
        mRealmManager.getQuery(realmQuery)
                .subscribe(querySubscriber);
        RealmResults<TestModel> queryResults = (RealmResults<TestModel>) querySubscriber.getOnNextEvents().get(0);
        MatcherAssert.assertThat(queryResults, is(iterableWithSize(1)));
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * query results are instance of test model
     */
    @Test
    public void testGetWhere_ifQueryResultsInstanceOfTestModel_whenRealmQueryIsMadeForSingleItem() {
        final TestModel insertedTestModel = createTestModelWithRandomId();
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        final RealmManager.RealmQueryProvider realmQuery = getExactTestModelRealmQuery(insertedTestModel);
        putTestModel(mRealmManager, insertedTestModel);
        mRealmManager.getQuery(realmQuery)
                .subscribe(querySubscriber);
        RealmResults<?> queryResults = (RealmResults<?>) querySubscriber.getOnNextEvents().get(0);
        assertThat(queryResults.get(0), is(instanceOf(TestModel.class)));
    }

    /**
     * Test if we insert single test model and then make a realm query
     * query results are correct, that is we getInstance what we inserted
     */
    @Test
    public void testGetWhere_ifQueryResultsHasCorrectModel_whenRealmQueryIsMadeForSingleItem() {
        final TestModel insertedTestModel = createTestModelWithRandomId();
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        final RealmManager.RealmQueryProvider realmQuery = getExactTestModelRealmQuery(insertedTestModel);
        putTestModel(mRealmManager, insertedTestModel);
        mRealmManager.getQuery(realmQuery)
                .subscribe(querySubscriber);
        RealmResults<TestModel> queryResults = (RealmResults<TestModel>) querySubscriber.getOnNextEvents().get(0);
        assertThat(queryResults.get(0), is(equalTo(insertedTestModel)));
    }

    @Test
    public void testGetAll_ifCorrectNumberOfRecordsAreFetched_whenMultipleRecordsAreInserted() {
        TestUtility2.executeMultipleTimes(TestUtility2.EXECUTION_COUNT_VERY_SMALL, () -> putTestModel(mRealmManager));
        final TestSubscriber<List> getAllSubscriber = new TestSubscriber<>();
        mRealmManager.getAll(TestModel.class).subscribe(getAllSubscriber);
        assertThat(getAllSubscriber.getOnNextEvents().get(0).size()
                , is(equalTo(TestUtility2.EXECUTION_COUNT_VERY_SMALL)));
    }

    @Test
    public void testGetAll_ifCorrectNumberOfRecordsAreFetched_whenSingleRecordIsInserted() {
        putTestModel(mRealmManager);
        final TestSubscriber<List> getAllSubscriber = new TestSubscriber<>();
        mRealmManager.getAll(TestModel.class).subscribe(getAllSubscriber);
        assertThat(getAllSubscriber.getOnNextEvents().get(0).size()
                , is(equalTo(1)));
    }

    @Test
    public void testGetAll_ifCorrectNumberOfRecordsAreFetched_whenNoRecordsAreInserted() {
        final TestSubscriber<List> getAllSubscriber = new TestSubscriber<>();
        mRealmManager.getAll(TestModel.class).subscribe(getAllSubscriber);
        assertThat(getAllSubscriber.getOnNextEvents().get(0).size()
                , is(equalTo(0)));
    }

    @Test
    public void testGetAll_ifExactlySameObjectsArezReturned_whenMultipleObjectsAreInserted() {
        List<TestModel> listOfTestModelsInserted = new ArrayList<>();
        for (int i = 0; i < TestUtility2.EXECUTION_COUNT_VERY_SMALL; i++) {
            listOfTestModelsInserted.add(putTestModel(mRealmManager));
        }
        final TestSubscriber<List> subscriber = new TestSubscriber<>();
        mRealmManager.getAll(TestModel.class)
                .subscribe(subscriber);
        List<TestModel> getModelList = subscriber.getOnNextEvents().get(0);
        assertThatTwoCollectionsContainSameElements(listOfTestModelsInserted, getModelList);
    }

    @Test
    public void testEvictById_ifModelIsDeletedSuccessfully_whenSingleModelIsInserted() {
        TestModel insertedModel = putTestModel(mRealmManager);
        boolean isEvictSuccessful
                = mRealmManager.evictById(TestModel.class, "id", insertedModel.getId());
        assertThat("", isEvictSuccessful);
    }

    @Test
    public void testEvictById_ifModelIsDeletedSuccessfully_whenMultipleModelAreInserted() {
        TestUtility2.executeMultipleTimes(TestUtility2.EXECUTION_COUNT_VERY_SMALL, () -> putTestModel(mRealmManager));
        TestModel lastTestModelInserted = putTestModel(mRealmManager);
        boolean isEvictSuccessful
                = mRealmManager.evictById(TestModel.class, "id", lastTestModelInserted.getId());
        assertThat("", isEvictSuccessful);
    }

    @Test
    public void testEvictById_ifNoModelIsDeleted_whenIncorrectFieldValueIsMentioned() {
        putTestModel(mRealmManager);
        boolean isEvictSuccessful
                = mRealmManager.evictById(TestModel.class, "id", TestUtility2.INVALID_ITEM_ID);
        assertThat(isEvictSuccessful, is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEvictById_ifExceptionIsThrown_whenIncorrectFieldNameIsMentioned() {
        putTestModel(mRealmManager);
        boolean isEvictSuccessful
                = mRealmManager.evictById(TestModel.class, "value", TestUtility2.INVALID_ITEM_ID);
        assertThat(isEvictSuccessful, is(false));
    }

    @Test
    public void testEvict_ifRealmModelIsDeleted_whenManagedRealmObjectIsDeleted() {
        TestModel testModel = putTestModel(mRealmManager);
        Realm mRealm = Realm.getDefaultInstance();
        TestModel managedTestModel = getManagedObject(testModel, mRealmManager);
        boolean originalValidity = managedTestModel.isValid();
        mRealm.beginTransaction();
        managedTestModel.deleteFromRealm();
        mRealm.commitTransaction();
        boolean newValidity = managedTestModel.isValid();
        assertThat("original validity should be true," +
                        " and new validity should be false: originalValidity:"
                        + originalValidity + "newValidity:" + newValidity
                , originalValidity && !newValidity);
    }

    @Test
    public void testPutRealmModel_ifCorrectNumberOfItemsAreInserted_whenSingleItemIsInserted() {
        mRealmManager.put(createRealmModelInstanceWithRandomId(), RealmModelClass.class)
                .subscribe(new TestSubscriber<>());
        assertGetAllForSize(RealmModelClass.class, 1);
    }

    @Test
    public void testPutRealmModel_ifNoErrorAreRaised_whenSingleItemIsInserted() {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(createRealmModelInstanceWithRandomId(), RealmModelClass.class)
                .subscribe(subscriber);
        subscriber.assertNoErrors();
    }

    @Test
    public void testBefore_ifEvictDeletesAllModelInstancesFromRealm_whenEvictAllIsInvokedInSetupMethod() {
        assertGetAllForSize(RealmModelClass.class, 0);
        assertGetAllForSize(TestModel.class, 0);
        assertGetAllForSize(ModelWithStringPrimaryKey.class, 0);
    }

    @Test
    public void testPutRealmModel_ifCorrectNumberOfItemsAreInserted_whenMultipleItemAreInserted() {
        for (int i = 0; i < TestUtility2.EXECUTION_COUNT_SMALL; i++) {
            mRealmManager.put(createRealmModelInstanceWithRandomId(), RealmModelClass.class)
                    .subscribe(new TestSubscriber<>());
        }
        assertGetAllForSize(RealmModelClass.class, TestUtility2.EXECUTION_COUNT_SMALL);
    }

    @Test
    public void testPutRealmModel_ifModelIsUpdated_whenUpdatedModelIsInserted() {
        final RealmModelClass testModelInstance = createRealmModelInstanceWithRandomId();
        mRealmManager.put(testModelInstance, RealmModelClass.class)
                .subscribe(new TestSubscriber<>());
        testModelInstance.setValue("test update");
        mRealmManager.put(testModelInstance, RealmModelClass.class)
                .subscribe(new TestSubscriber<>());
        //check if no duplicates are added
        assertGetAllForSize(RealmModelClass.class, 1);
        //check if values are updated or not
        final TestSubscriber<Object> getItemByIdSubscriber = new TestSubscriber<>();
        mRealmManager.getById("id", testModelInstance.getId(), RealmModelClass.class)
                .subscribe(getItemByIdSubscriber);
        RealmModelClass currentModel
                = assertSubscriberGetSingleNextEventWithNoError(getItemByIdSubscriber
                , RealmModelClass.class);
        MatcherAssert.assertThat("two instances are not same", currentModel.equals(testModelInstance));
    }

    @Test
    public void testPutRealmModel_ifItemsAreInsertedCorrectly_whenTwoTypesOfItemsAreInserted() {
        //insert some test realm objects
        TestUtility2.executeMultipleTimes(TestUtility2.EXECUTION_COUNT_VERY_SMALL
                , () -> putTestModel(mRealmManager));
        //insert some realm model class
        TestUtility2.executeMultipleTimes(TestUtility2.EXECUTION_COUNT_VERY_SMALL
                , () -> mRealmManager.put(createRealmModelInstanceWithRandomId()
                        , RealmModelClass.class).subscribe(new TestSubscriber<>()));
        //check if correct number of realm model classes are inserted or not
        assertGetAllForSize(RealmModelClass.class, TestUtility2.EXECUTION_COUNT_VERY_SMALL);
    }

    @Test
    public void testPutRealmModel_ifWeGetWhatWeInserted_whenWeInsertAndQueryById() {
        final RealmModelClass realmModelClass = createRealmModelInstanceWithRandomId();
        mRealmManager.put(realmModelClass, RealmModelClass.class)
                .subscribe(new TestSubscriber<>());
        assertThat(realmModelClass, Matchers.is(equalTo(getItemForId("id", realmModelClass.getId(), RealmModelClass.class))));
    }

    @Test
    public void testPutAll_ifCorrectNumberOfItemsAreInserted_whenMultipleItemsAreInserted() {
        List<RealmObject> testModelList = new ArrayList<>();
        for (int i = 0; i < TestUtility2.EXECUTION_COUNT_VERY_SMALL; i++) {
            testModelList.add(createTestModelWithRandomId());
        }
        mRealmManager.putAll(testModelList, TestModel.class);
        assertGetAllForSize(TestModel.class, TestUtility2.EXECUTION_COUNT_VERY_SMALL);
    }

    @Test
    public void testPutAll_ifCorrectItemsAreInserted_whenMultipleItemsAreInserted() {

        final List<RealmObject> testModelList = new ArrayList<>();
        for (int i = 0; i < TestUtility2.EXECUTION_COUNT_VERY_SMALL; i++) {
            testModelList.add(createTestModelWithRandomId());
        }
        mRealmManager.putAll(testModelList, TestModel.class);
        final TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        for (RealmObject testModel : testModelList) {
            mRealmManager.getById("id", ((TestModel) testModel).getId(), TestModel.class)
                    .subscribe(testSubscriber);
            assertThat(testSubscriber.getOnNextEvents(), allOf(iterableWithSize(1), notNullValue()));
        }
    }

    @Test
    public void testPutAll_ifCorrectItemIsInserted_whenSingleItemIsInserted() {
        final TestModel testModel = createTestModelWithRandomId();
        mRealmManager.putAll(Collections.singletonList(testModel)
                , TestModel.class);
        getItemForId("id", testModel.getId(), TestModel.class);
    }

    @Test
    public void testPutAll_ifSingleItemIsInserted_whenSingleItemIsInserted() {
        final TestModel testModel = createTestModelWithRandomId();
        mRealmManager.putAll(Collections.singletonList(testModel)
                , TestModel.class);
        assertGetAllForSize(TestModel.class, 1);
    }

    @Test
    public void testPutAll_ifCorrectNumberOfItemsAreInserted_whenSingleItemIsInserted() {
        TestModel insertedTestModel = putTestModel(mRealmManager);
        final TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        mRealmManager.getById("id", (insertedTestModel).getId(), TestModel.class)
                .subscribe(testSubscriber);
        assertThat(testSubscriber.getOnNextEvents(), allOf(iterableWithSize(1), notNullValue()));
    }

    @Test
    public void testPutAll_ifCorrectItemsIsInserted_whenSingleItemIsInserted() {
        putTestModel(mRealmManager);
    }

    @Test
    public void testEvictCollection_ifonlySpecifiedItemsAreDeleted_whenMultipleIdsArePassed() {
        //lets put EXECUTION_COUNT_VERY_SMALL test models
        ArrayList<Object> listOfInsertedTestModels = new ArrayList<>();
        ArrayList<Long> listOfIdToDelete = new ArrayList<>();
        for (int i = 0; i < TestUtility2.EXECUTION_COUNT_VERY_SMALL; i++) {
            final TestModel testModel = putTestModel(mRealmManager);
            listOfInsertedTestModels.add(testModel);
            //but we will send only half of them
            if (i % 2 == 0) {
                listOfIdToDelete.add((long) testModel.getId());
            }
        }
        //lets try deleting these
        final TestSubscriber<Object> deleteCollectionSubscriber = new TestSubscriber<>();
        mRealmManager.evictCollection("id"
                , listOfIdToDelete, TestModel.class).subscribe(deleteCollectionSubscriber);
        deleteCollectionSubscriber.assertNoErrors();
        assertThat(deleteCollectionSubscriber.getOnNextEvents().get(0), is(equalTo(Boolean.TRUE)));
        RealmResults<TestModel> testModelList = getQueryList(TestModel.class);
        for (TestModel testModel : testModelList) {
            assertThat(listOfIdToDelete, not(IsCollectionContaining.hasItem((long) testModel.getId())));
        }
    }

    @Test
    public void testEvictCollection_ifSpecifiedItemsAfterInvalidIdAreNotDeleted_whenMultipleIdsMixedWithInvalidIdIsPassed() {
        //lets put EXECUTION_COUNT_VERY_SMALL test models
        ArrayList<Object> listOfInsertedTestModels = new ArrayList<>();
        ArrayList<Long> listOfIdToDelete = new ArrayList<>();
        for (int i = 0; i < TestUtility2.EXECUTION_COUNT_VERY_SMALL; i++) {
            final TestModel testModel = putTestModel(mRealmManager);
            listOfInsertedTestModels.add(testModel);
            //but we will send only half of them
            if (i % 2 == 0) {
                listOfIdToDelete.add((long) testModel.getId());
            }
            if (i == TestUtility2.EXECUTION_COUNT_VERY_SMALL / 2) {
                //add invalid id
                listOfIdToDelete.add((long) TestUtility2.INVALID_ITEM_ID);
            }
        }
        //lets try deleting these
        final TestSubscriber<Object> deleteCollectionSubscriber = new TestSubscriber<>();
        mRealmManager.evictCollection("id"
                , listOfIdToDelete, TestModel.class).subscribe(deleteCollectionSubscriber);
        deleteCollectionSubscriber.assertNoErrors();
        assertThat(deleteCollectionSubscriber.getOnNextEvents().get(0), is(equalTo(Boolean.FALSE)));
        RealmResults<TestModel> testModelList = getQueryList(TestModel.class);
        //all ids before INVALID_ITEM_ID should be deleted
        int i = 0;
        while (listOfIdToDelete.get(i) != TestUtility2.INVALID_ITEM_ID) {
            MatcherAssert.assertThat(getItemForId("id", listOfIdToDelete.get(i), TestModel.class), is(nullValue()));
            i++;
        }
        i++;
        //all ids before INVALID_ITEM_ID should not be deleted
        while (i < listOfIdToDelete.size()) {
            MatcherAssert.assertThat(getItemForId("id", listOfIdToDelete.get(i), TestModel.class), is(notNullValue()));
            i++;
        }
    }

    @Test
    public void testEvictCollection_ifItemsAreEvicted_whenSingleItemIdIsProvidedInList() {
        TestModel insertedTestModel = putTestModel(mRealmManager);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.evictCollection("id"
                , Collections.singletonList(Long.valueOf(insertedTestModel.getId()))
                , TestModel.class)
                .subscribe(subscriber);
        TestUtility2.assertNoErrors(subscriber);
        subscriber.assertValue(Boolean.TRUE);
    }

    @NonNull
    private <T> T getItemForId(@NonNull String idFieldName, long idFieldValue, Class<T> clazz) {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.getById(idFieldName, (int) idFieldValue, clazz).subscribe(subscriber);
        return (T) subscriber.getOnNextEvents().get(0);
    }

    @NonNull
    private TestModel getManagedObject(@NonNull TestModel testModel, @NonNull RealmManager realmManager) {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        realmManager.getById("id", testModel.getId(), TestModel.class).subscribe(subscriber);
        return (TestModel) subscriber.getOnNextEvents().get(0);
    }

    private void createJsonObjectInWorkerThreadAndInsertInUi() {
        assertCurrentThreadIsWorkerThread();
        RealmManager realmManager
                = getGeneralRealmManager();
        JSONObject[] jsonObject
                = new JSONObject[1];
        TestUtility2.getJsonObjectFrom(jsonObject, createTestModelWithRandomId());
        TestUtility2.assertConvertedJsonArray(jsonObject);
        runOnMainThread(() -> {
            Observable<?> observable
                    = realmManager.put(jsonObject[0], "id", TestModel.class);
            assertPutOperationObservable(observable);
        });
    }

    @NonNull
    private <T extends RealmModel> RealmResults<T> assertGetAllForSize(Class<T> clazz
            , int sizeExpected) {
        final RealmResults<T> queryResult = getQueryList(clazz);
        assertThat(queryResult.size(), is(equalTo(sizeExpected)));
        return queryResult;
    }

    @NonNull
    private <T extends RealmModel> RealmResults<T> getQueryList(Class<T> clazz) {
        Observable<List> queryObservable = mRealmManager.getAll(clazz);
        assertThat(queryObservable, notNullValue());
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        queryObservable.subscribe(querySubscriber);
        TestUtility2.assertNoErrors(querySubscriber);
        final List<List> onNextEvents = querySubscriber.getOnNextEvents();
        assertThat(onNextEvents, allOf(notNullValue(), iterableWithSize(1)));
        final List queryResult = onNextEvents.get(0);
        assertThat(queryResult, allOf(instanceOf(RealmResults.class)
                , notNullValue(List.class)
        ));
        if (queryResult.size() > 0) {
            assertThat(queryResult.get(0), is(instanceOf(clazz)));
        }
        return (RealmResults<T>) queryResult;
    }

    private void print(String str) {
        System.out.println(str);
    }

    @NonNull
    private <T> T assertSubscriberGetSingleNextEventWithNoError(@NonNull TestSubscriber subscriber, Class<T> clazz) {
        subscriber.assertNoErrors();
        final List<List> onNextEvents = subscriber.getOnNextEvents();
        assertThat(onNextEvents, allOf(notNullValue(), iterableWithSize(1)));
        assertThat(onNextEvents.get(0), is(instanceOf(clazz)));
        return ((T) onNextEvents.get(0));
    }

    @NonNull
    private RealmManager.RealmQueryProvider getExactTestModelRealmQuery(@NonNull TestModel insertedTestModel) {
        return realm -> realm.where(TestModel.class)
                .beginsWith("value", TEST_MODEL_PREFIX, Case.INSENSITIVE)
                .equalTo("id", insertedTestModel.getId())
                .equalTo("value", insertedTestModel.getValue(), Case.INSENSITIVE);
    }

    private int getRandomInt() {
        return Math.abs(mRandom.nextInt()) + 1;
    }

    private void runOnMainThread(Runnable runnable) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(runnable);
    }

    private void assertPutOperationObservable(@NonNull Observable<?> observable) {
        final TestSubscriber<Object> subscriber = TestSubscriber.create();
        observable.subscribe(subscriber);
        List<Object> onNextEvents = subscriber.getOnNextEvents();
        List<Throwable> onErrorEvents = subscriber.getOnErrorEvents();
        subscriber.assertNoErrors();
        assertThat(onNextEvents, allOf(Matchers.iterableWithSize(1)
                , Matchers.notNullValue()));
        assertThat(onNextEvents.get(0), allOf(Matchers.instanceOf(Boolean.class)
                , Matchers.equalTo(Boolean.TRUE)));
        assertThat(onErrorEvents, anyOf(Matchers.nullValue()
                , Matchers.iterableWithSize(0)));
    }


    @NonNull
    private RealmManager getGeneralRealmManager() {
        DatabaseManagerFactory.initRealm();
        return (RealmManager) DatabaseManagerFactory.getInstance();
    }

    @NonNull
    private TestModel insertJsonObject() throws JSONException {
        TestModel testModel = createTestModelWithRandomId();
        String json = new Gson().toJson(testModel);
        JSONObject jsonObject = new JSONObject(json);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObject, "id", TestModel.class)
                .subscribe(subscriber);
        subscriber.assertNoErrors();
        return testModel;
    }

    private TestModel putTestModel(@NonNull DataBaseManager dataBaseManager) {
        return putTestModel(dataBaseManager, createTestModelWithRandomId());
    }

    private TestModel putTestModel(@NonNull DataBaseManager dataBaseManager, TestModel realmModel) {
        Observable<?> observable
                = dataBaseManager.put(realmModel, RealmManagerImplTestJUnit.class);
        TestSubscriber<Object> subscriber = TestSubscriber.create();
        observable.subscribe(subscriber);
        TestUtility2.printThrowables(subscriber.getOnErrorEvents());
        TestUtility2.assertNoErrors(subscriber);
        return realmModel;
    }

    @NonNull
    private TestModel createTestModelWithRandomId() {
        final int randomInt = getRandomInt();
        return new TestModel(randomInt, getStringValueForTestModel(randomInt));
    }

    @NonNull
    private RealmModelClass createRealmModelInstanceWithRandomId() {
        final int randomInt = getRandomInt();
        return new RealmModelClass(randomInt, getStringValueForTestModel(randomInt));
    }

    @NonNull
    private String getStringValueForTestModel(int randomInt) {
        return TEST_MODEL_PREFIX + randomInt;
    }

    private void assertCurrentThreadIsWorkerThread() {
        assertThat(Thread.currentThread()
                , not(equalTo(Looper.getMainLooper().getThread())));
    }

    private void assertCurrentThreadIsMainThread() {
        assertThat(Thread.currentThread()
                , equalTo(Looper.getMainLooper().getThread()));
    }

    private <T> void assertThatTwoCollectionsContainSameElements(@NonNull List<T> left, @NonNull List<T> right) {
        assertThat(left, is(containsInAnyOrder(right.toArray())));
        assertThat(right, is(containsInAnyOrder(left.toArray())));
    }
}
