package com.zeyad.genericusecase.data.db;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.data.TestUtility;
import com.zeyad.genericusecase.data.services.realm_test_models.ModelWithStringPrimaryKey2;
import com.zeyad.genericusecase.data.services.realm_test_models.RealmModelClass2;
import com.zeyad.genericusecase.data.services.realm_test_models.TestModel2;
import com.zeyad.genericusecase.data.utils.Utils;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
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

@RunWith(AndroidJUnit4.class)
public class GeneralRealmManagerImplTest {

    public static final String TEST_MODEL_PREFIX = "random value:";

    @Rule
    public Timeout globalTimeout = new Timeout(TestUtility.TIMEOUT_TIME_VALUE_LARGE, TestUtility.TIMEOUT_TIME_UNIT);

    private GenericRealmManager mRealmManager;
    @Rule
    public UiThreadTestRule mUiThreadTestRule = new UiThreadTestRule();
    @Rule
    public ExpectedException exception
            = ExpectedException.none();
    private Random mRandom;

    @Before
    public void before() {
        Config.init(InstrumentationRegistry.getTargetContext());
        TestUtility.performInitialSetupOfDb(InstrumentationRegistry.getTargetContext());
        mRandom = new Random();
        mRealmManager = getGeneralRealmManager();
        mRealmManager.evictAll(TestModel2.class).subscribe(new TestSubscriber<>());
        mRealmManager.evictAll(RealmModelClass2.class).subscribe(new TestSubscriber<>());
        mRealmManager.evictAll(ModelWithStringPrimaryKey2.class).subscribe(new TestSubscriber<>());
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
        GenericRealmManager realmManager = getGeneralRealmManager();
        runOnMainThread(() -> {
            putTestModel(realmManager);
        });
        realmManager.getAll(TestModel2.class);
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
        int previousTestModelCount = getQueryList(TestModel2.class).size();
        insertJsonObject();
        assertGetAllForSize(TestModel2.class, previousTestModelCount + 1);
    }

    @Test
    public void testPutJsonMethod_ifTrueIsReturnedInSubscriber_whenOperationIsPerformed() throws Throwable {
        TestModel2 testModel2 = createTestModelWithRandomId();
        String json = new Gson().toJson(testModel2);
        JSONObject jsonObject = new JSONObject(json);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObject, "id", TestModel2.class)
                .subscribe(subscriber);
        subscriber.assertValue(Boolean.TRUE);
    }

    @Test
    public void testPutRealmObject_ifNoExceptionIsThrown_whenOperationIsPerformed() throws Throwable {
        int previousTestModelCount = getQueryList(TestModel2.class).size();
        putTestModel(mRealmManager);
        assertGetAllForSize(TestModel2.class, previousTestModelCount + 1);
    }

    @Test
    public void testPutRealmObject_ifTrueIsReturnedInSubscriber_whenOperationIsPerformed() throws Throwable {
        Observable<?> observable
                = mRealmManager.put(createTestModelWithRandomId(), GeneralRealmManagerImplTest.class);
        TestSubscriber<Object> subscriber = TestSubscriber.create();
        observable.subscribe(subscriber);
        subscriber.assertValue(Boolean.TRUE);
    }

    @Test
    public void testPutJsonMethod_ifNoExceptionIsRaised_whenJsonObjectCreatedInBackgroundAndInsertedInUiThread()
            throws Throwable {
        TestModel2 testModel2 = createTestModelWithRandomId();
        String json = new Gson().toJson(testModel2);
        JSONObject jsonObject = new JSONObject(json);
        runOnMainThread(() -> {
            final TestSubscriber<Object> subscriber = new TestSubscriber<>();
            GenericRealmManager realmManager
                    = getGeneralRealmManager();
            realmManager.put(jsonObject, "id", TestModel2.class)
                    .subscribe(subscriber);
            subscriber.assertNoErrors();
        });

    }

    @Test
    public void testPutJsonMethod_ifNoExceptionIsGenerated_whenOperationIsPerformedOnUIThread()
            throws Throwable {
        final JSONObject[] jsonObject = new JSONObject[1];
        runOnMainThread(() -> TestUtility.getJsonObjectFrom(jsonObject
                , createTestModelWithRandomId()));
        TestUtility.assertConvertedJsonArray(jsonObject);
        GenericRealmManager realmManager
                = getGeneralRealmManager();
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        realmManager.put(jsonObject[0], "id", TestModel2.class)
                .subscribe(subscriber);
        subscriber.assertNoErrors();
    }

    @Test
    public void testPutJsonObject_ifNoExceptionIsRaised_whenJsonObjectIsInserted() {
        GenericRealmManager realmManager
                = getGeneralRealmManager();
        JSONObject[] jsonObject = new JSONObject[1];
        TestUtility.getJsonObjectFrom(jsonObject
                , createTestModelWithRandomId());
        TestUtility.assertConvertedJsonArray(jsonObject);
        Observable<?> observable
                = realmManager.put(jsonObject[0], "id", TestModel2.class);
        assertPutOperationObservable(observable);
    }

    @Test
    public void testPutJsonObject_ifNoExceptionIsRaised_whenJsonObjectsAreInsertedMultipleTimes() {
        TestUtility.executeMultipleTimes(TestUtility.EXECUTION_COUNT_SMALL, this::testPutJsonObject_ifNoExceptionIsRaised_whenJsonObjectIsInserted);
    }

    @Test
    public void testPutJsonObject_ifNoExceptionIsGenerated_whenJsonIsCreatedInWorkerAndInsertedInUI() {
        createJsonObjectInWorkerThreadAndInsertInUi();
    }

    @Test
    public void testPutJsonObject_ifNoExceptionIsGenerated_whenJsonIsCreatedInWorkerAndInsertedInUIMultipleTimes() {
        TestUtility.executeMultipleTimes(TestUtility.EXECUTION_COUNT_SMALL, this::createJsonObjectInWorkerThreadAndInsertInUi);
    }

    @Test
    public void testPutJsonObject_IfNoExceptionIsRaised_whenOrderModelJsonCreateInUIAndWriteInWorker() {
        GenericRealmManager realmManager
                = getGeneralRealmManager();
        JSONObject[] jsonObject
                = new JSONObject[1];
        runOnMainThread(() -> {
            TestUtility.getJsonObjectFrom(jsonObject, createTestModelWithRandomId());
            TestUtility.assertConvertedJsonArray(jsonObject);
        });
        Observable<?> observable = realmManager.put(jsonObject[0], "id", TestModel2.class);
        assertPutOperationObservable(observable);
    }

    @Test
    public void testEvictAllMethod_IfDeletesAllModels_whenSinglePut() {
        putTestModel(mRealmManager);
        Observable<Boolean> evictObservable
                = mRealmManager.evictAll(TestModel2.class);
        final TestSubscriber<Boolean> evictAllSubscriber = new TestSubscriber<>();
        evictObservable.subscribe(evictAllSubscriber);
        evictAllSubscriber.assertValue(Boolean.TRUE);
        assertGetAllForSize(TestModel2.class, 0);
    }

    @Test
    public void testEvictAllMethod_ifNoExceptionIsThrown_IfIncorrectClassnameIsPassed() {
        mRealmManager.evictAll(TestModel2.class).subscribe(new TestSubscriber<>());
    }

    @Test
    public void testEvictAllMethod_IfDeletesAllModels_whenMultiplePut() {
        TestUtility.executeMultipleTimes(TestUtility.EXECUTION_COUNT_SMALL, () -> putTestModel(mRealmManager));
        assertGetAllForSize(TestModel2.class, TestUtility.EXECUTION_COUNT_SMALL);
        Observable<Boolean> evictObservable
                = mRealmManager.evictAll(TestModel2.class);
        final TestSubscriber<Boolean> evictAllSubscriber = new TestSubscriber<>();
        evictObservable.subscribe(evictAllSubscriber);
        evictAllSubscriber.assertValue(Boolean.TRUE);
        assertGetAllForSize(TestModel2.class, 0);
    }


    @Test
    public void testEvictBySingleModel_ifManagedModelIsInsertedAndEvicted_thenWouldBeDeleted() {
        TestModel2 insertedTestModel2 = putTestModel(mRealmManager);
        mRealmManager.evict(getManagedObject(insertedTestModel2, mRealmManager), TestModel2.class);
        assertGetAllForSize(TestModel2.class, 0);
    }


    @Test
    public void testPutJsonObject_ifAddedSuccessfully_whenRealmObjectWithIntegerPrimaryKeyIsAdded() {
        ModelWithStringPrimaryKey2 modelWithStringPrimaryKey2 = new ModelWithStringPrimaryKey2(String.valueOf(getRandomInt())
                , TEST_MODEL_PREFIX);
        JSONObject[] jsonObjectArray = new JSONObject[1];
        TestUtility.getJsonObjectFrom(jsonObjectArray, modelWithStringPrimaryKey2);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObjectArray[0], "studentId", ModelWithStringPrimaryKey2.class)
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
        assertGetAllForSize(ModelWithStringPrimaryKey2.class, 1);
    }

    @Test(expected = java.lang.AssertionError.class)
    public void testPutJsonObject_ifAddedSuccessfully_whenRealmObjectWithIntegerPrimaryKeyIsAddedAndIdIsPassedAsPrimaryKey() {
        ModelWithStringPrimaryKey2 modelWithStringPrimaryKey2 = new ModelWithStringPrimaryKey2(String.valueOf(getRandomInt())
                , TEST_MODEL_PREFIX);
        JSONObject[] jsonObjectArray = new JSONObject[1];
        TestUtility.getJsonObjectFrom(jsonObjectArray, modelWithStringPrimaryKey2);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObjectArray[0], "id", ModelWithStringPrimaryKey2.class)
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
        assertGetAllForSize(ModelWithStringPrimaryKey2.class, 1);
    }

    @Test(expected = java.lang.AssertionError.class)
    public void testPutJsonObject_ifAddedSuccessfully_whenRealmObjectWithStringPrimaryKeyIsAdded() {
        ModelWithStringPrimaryKey2 modelWithStringPrimaryKey2
                = new ModelWithStringPrimaryKey2("abc"
                , TEST_MODEL_PREFIX);
        JSONObject[] jsonObjectArray = new JSONObject[1];
        TestUtility.getJsonObjectFrom(jsonObjectArray, modelWithStringPrimaryKey2);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObjectArray[0], "studentId", ModelWithStringPrimaryKey2.class)
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
        assertGetAllForSize(ModelWithStringPrimaryKey2.class, 1);
    }

    @Test
    public void testEvictBySingleModel_ifMultipleModelsAreInsertedAndEvictedSequentially() {
        List<TestModel2> insertedTestModel2List = new ArrayList<>();
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_SMALL; i++) {
            insertedTestModel2List.add(putTestModel(mRealmManager));
        }
        assertGetAllForSize(TestModel2.class, TestUtility.EXECUTION_COUNT_SMALL);
        for (TestModel2 testModel2 : insertedTestModel2List) {
            final TestModel2 managedObject = getManagedObject(testModel2, mRealmManager);
            mRealmManager.evict(managedObject, TestModel2.class);
        }
        assertGetAllForSize(TestModel2.class, 0);
    }

    @Test
    public void testGetManagedObject_ifReturnedObjectIsValid_whenNonManagedObjectIsProvided() {
        TestModel2 testModel2 = putTestModel(mRealmManager);
        TestModel2 managedTestModel2 = getManagedObject(testModel2, mRealmManager);
        assertThat("managed test model should be valid", managedTestModel2.isValid());
    }

    @Test
    public void testPutRealmObject_ifInsertedObjectIsNotvalid_whenRealmObjectIsInserted() {
        TestModel2 testModel2 = putTestModel(mRealmManager);
        assertThat("test model should not be valid", !testModel2.isValid());
    }

    @Test
    public void testAddingOrderModelJsonCreateInUIAndWriteInWorker_multipleTimes() {
        TestUtility.executeMultipleTimes(TestUtility.EXECUTION_COUNT_SMALL, this::testPutJsonObject_IfNoExceptionIsRaised_whenOrderModelJsonCreateInUIAndWriteInWorker);
    }

    @Test
    public void testPutRealmObject_ifCorrectNumberOfItemsAreInserted_whenSingleItemIsInserted() {
        Observable<?> observable = mRealmManager.put(createTestModelWithRandomId(), TestModel2.class);
        observable.subscribe(new TestSubscriber<>());
        assertGetAllForSize(TestModel2.class, 1);
    }

    @Test
    public void testPutRealmObject_ifCorrectNumberOfItemsAreInserted_whenMultipleItemAreInserted() {
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_SMALL; i++) {
            Observable<?> observable = mRealmManager.put(createTestModelWithRandomId(), TestModel2.class);
            observable.subscribe(new TestSubscriber<>());
        }
        assertGetAllForSize(TestModel2.class, TestUtility.EXECUTION_COUNT_SMALL);
    }

    @Test
    public void testPutRealmObject_ifModelIsUpdated_whenUpdatedModelIsInserted() {
        final TestModel2 testModel2Instance = createTestModelWithRandomId();
        mRealmManager.put(testModel2Instance, TestModel2.class)
                .subscribe(new TestSubscriber<>());
        testModel2Instance.setValue("test update");
        mRealmManager.put(testModel2Instance, TestModel2.class)
                .subscribe(new TestSubscriber<>());
        //check if no duplicates are added
        assertGetAllForSize(TestModel2.class, 1);
        //check if values are updated or not
        final TestSubscriber<Object> getItemByIdSubscriber = new TestSubscriber<>();
        mRealmManager.getById("id", testModel2Instance.getId(), TestModel2.class)
                .subscribe(getItemByIdSubscriber);
        TestModel2 currentModel
                = assertSubscriberGetSingleNextEventWithNoError(getItemByIdSubscriber
                , TestModel2.class);
        assertThat("two instances are not same", currentModel.equals(testModel2Instance));
    }

    /**
     * Test if we insert single test model and then make a query on value.
     * , returned observable should not be null.
     */
    @Test
    public void testGetWhere_ifReturnedObservableIsNotNull_whenSingleModelIsInsertedAndQueried() {
        TestModel2 insertedTestModel2 = putTestModel(mRealmManager);
        assertThat(mRealmManager.getWhere(TestModel2.class
                , insertedTestModel2.getValue()
                , "value"), is(notNullValue()));
    }

    /**
     * Test if we insert single test model and then make a query on value.
     * Returned Observable should return no error.
     */
    @Test
    public void testGetWhere_ifObservableShouldReturnNoError_whenSingleModelIsInsertedAndQueried() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        mRealmManager.getWhere(TestModel2.class
                , insertedTestModel2.getValue()
                , "value")
                .subscribe(querySubscriber);
        querySubscriber.assertNoErrors();
    }

    /**
     * Test if we insert single test model and then make a query on value.
     * Returned Observable should return size of 1 next event list.
     */
    @Test
    public void testGetWhere_ifOnNextEventListHasAtleastOneItem_whenSingleModelIsInsertedAndQueried() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        mRealmManager.getWhere(TestModel2.class
                , insertedTestModel2.getValue()
                , "value")
                .subscribe(querySubscriber);
        assertThat(querySubscriber.getOnNextEvents(), iterableWithSize(1));
    }

    /**
     * Test if we insert single test model and then make a query on value.
     * on next event list should contain query result at item index 1
     */
    @Test
    public void testGetWhere_ifNextEventListContainQueryResultAtIndex1_whenSingleModelIsInsertedAndQueried() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        mRealmManager.getWhere(TestModel2.class
                , insertedTestModel2.getValue()
                , "value")
                .subscribe(querySubscriber);
        assertThat(querySubscriber.getOnNextEvents().get(0), is(instanceOf(RealmResults.class)));
    }

    /**
     * Test if we insert single test model and then make a query on value.
     * query results are not empty
     */
    @Test
    public void testGetWhere_ifQueryResultsAreNotEmpty_whenSingleModelIsInsertedAndQueried() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        mRealmManager.getWhere(TestModel2.class
                , insertedTestModel2.getValue()
                , "value")
                .subscribe(querySubscriber);
        RealmResults<TestModel2> queryResults = (RealmResults<TestModel2>) querySubscriber.getOnNextEvents().get(0);
        assertThat(queryResults, is(iterableWithSize(1)));
    }

    /**
     * Test if we insert single test model and then make a query on value.
     * query results are instance of test model
     */
    @Test
    public void testGetWhere_ifQueryResultsInstanceOfTestModel_whenSingleModelIsInsertedAndQueried() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        mRealmManager.getWhere(TestModel2.class
                , insertedTestModel2.getValue()
                , "value")
                .subscribe(querySubscriber);
        RealmResults<?> queryResults = (RealmResults<?>) querySubscriber.getOnNextEvents().get(0);
        assertThat(queryResults.get(0), is(instanceOf(TestModel2.class)));
    }

    /**
     * Test if we insert single test model and then make a query on value
     * query results are correct, that is we getInstance what we inserted
     */
    @Test
    public void testGetWhere_ifQueryResultsHasCorrectModel_whenSingleModelIsInsertedAndQueried() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        mRealmManager.getWhere(TestModel2.class
                , insertedTestModel2.getValue()
                , "value")
                .subscribe(querySubscriber);
        RealmResults<TestModel2> queryResults = (RealmResults<TestModel2>) querySubscriber.getOnNextEvents().get(0);
        assertThat(queryResults.get(0), is(equalTo(insertedTestModel2)));
    }

    /**
     * Test if we insert single test model and then make a query on value using uppercase query.
     * query results are correct, that is we getInstance what we inserted
     */
    @Test
    public void testGetWhere_ifQueryResultsHasCorrectModel_whenSingleModelIsInsertedAndQueriedUsingUppercase() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        mRealmManager.getWhere(TestModel2.class
                , insertedTestModel2.getValue().toUpperCase()       //upper case the value of instance
                , "value")
                .subscribe(querySubscriber);
        RealmResults<TestModel2> queryResults = (RealmResults<TestModel2>) querySubscriber.getOnNextEvents().get(0);
        assertThat(queryResults.get(0), is(equalTo(insertedTestModel2)));
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * , returned observable should not be null.
     */
    @Test
    public void testGetById_ifReturnedObservableIsNotNull_whenSingleModelIsInsertedAndQueried() {
        TestModel2 insertedTestModel2 = putTestModel(mRealmManager);
        assertThat(mRealmManager.getById("id"
                , insertedTestModel2.getId()
                , TestModel2.class), is(notNullValue()));
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * Returned Observable should return no error.
     */
    @Test
    public void testGetById_ifObservableShouldReturnNoError_whenSingleModelIsInsertedAndQueried() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , insertedTestModel2.getId()
                , TestModel2.class)
                .subscribe(querySubscriber);
        querySubscriber.assertNoErrors();
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * Returned Observable should return size of 1 next event list.
     */
    @Test
    public void testGetById_ifOnNextEventListHasAtLeastOneItem_whenSingleModelIsInsertedAndQueried() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , insertedTestModel2.getId()
                , TestModel2.class)
                .subscribe(querySubscriber);
        assertThat(querySubscriber.getOnNextEvents(), iterableWithSize(1));
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * on next event list should contain  TestModel2 result at item index 1
     */
    @Test
    public void testGetById_ifQueryResultIsInstanceOfTestModel_whenSingleModelIsInsertedAndQueried() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , insertedTestModel2.getId()
                , TestModel2.class)
                .subscribe(querySubscriber);
        assertThat(querySubscriber.getOnNextEvents().get(0), is(instanceOf(TestModel2.class)));
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * returned test nmodel is same as inserted
     */
    @Test
    public void testGetById_ifQueryResultsHasCorrectModel_whenSingleModelIsInsertedAndQueried() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , insertedTestModel2.getId()
                , TestModel2.class)
                .subscribe(querySubscriber);
        TestModel2 returnedTestModel2 = (TestModel2) querySubscriber.getOnNextEvents().get(0);
        assertThat(returnedTestModel2, is(equalTo(insertedTestModel2)));
    }

    /**
     * Test if we insert single test model and then make a query using invalid id.
     * we should getInstance a test model with maximum id.
     */
    @Test
    public void testGetById_ifModelWithMaxIdIsReturned_whenQueriedUsingNegativeId() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , TestUtility.INVALID_ITEM_ID
                , TestModel2.class)
                .subscribe(querySubscriber);
        TestModel2 returnedTestModel2 = (TestModel2) querySubscriber.getOnNextEvents().get(0);
        int maxId = Utils.getMaxId(TestModel2.class, "id");
        assertThat(returnedTestModel2.getId(), is(equalTo(maxId)));
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * , returned observable should not be null.
     */
    @Test
    public void testGetWhere_ifReturnedObservableIsNotNull_whenRealmQueryIsMadeForSingleItem() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        RealmQuery<TestModel2> realmQuery = getExactTestModelRealmQuery(insertedTestModel2);
        assertThat(mRealmManager.getWhere(realmQuery), is(notNullValue()));
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * Returned Observable should return no error.
     */
    @Test
    public void testGetWhere_ifObservableShouldReturnNoError_whenRealmQueryIsMadeForSingleItem() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        RealmQuery<TestModel2> realmQuery = getExactTestModelRealmQuery(insertedTestModel2);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        mRealmManager.getWhere(realmQuery)
                .subscribe(querySubscriber);
        querySubscriber.assertNoErrors();
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * Returned Observable should return size of 1 next event list.
     */
    @Test
    public void testGetWhere_ifOnNextEventListHasAtleastOneItem_whenRealmQueryIsMadeForSingleItem() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        RealmQuery<TestModel2> realmQuery = getExactTestModelRealmQuery(insertedTestModel2);
        mRealmManager.getWhere(realmQuery)
                .subscribe(querySubscriber);
        assertThat(querySubscriber.getOnNextEvents(), iterableWithSize(1));
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * on next event list should contain query result at item index 1
     */
    @Test
    public void testGetWhere_ifNextEventListContainQueryResultAtIndex1_whenRealmQueryIsMadeForSingleItem() {
        final TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestModel2);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        final RealmQuery<TestModel2> realmQuery = getExactTestModelRealmQuery(insertedTestModel2);
        mRealmManager.getWhere(realmQuery)
                .subscribe(querySubscriber);
        assertThat(querySubscriber.getOnNextEvents().get(0), is(instanceOf(RealmResults.class)));
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * query results are not empty
     */
    @Test
    public void testGetWhere_ifQueryResultsAreNotEmpty_whenRealmQueryIsMadeForSingleItem() {
        TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        final RealmQuery<TestModel2> realmQuery = getExactTestModelRealmQuery(insertedTestModel2);
        putTestModel(mRealmManager, insertedTestModel2);
        mRealmManager.getWhere(realmQuery)
                .subscribe(querySubscriber);
        RealmResults<TestModel2> queryResults = (RealmResults<TestModel2>) querySubscriber.getOnNextEvents().get(0);
        assertThat(queryResults, is(iterableWithSize(1)));
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * query results are instance of test model
     */
    @Test
    public void testGetWhere_ifQueryResultsInstanceOfTestModel_whenRealmQueryIsMadeForSingleItem() {
        final TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        final RealmQuery<TestModel2> realmQuery = getExactTestModelRealmQuery(insertedTestModel2);
        putTestModel(mRealmManager, insertedTestModel2);
        mRealmManager.getWhere(realmQuery)
                .subscribe(querySubscriber);
        RealmResults<?> queryResults = (RealmResults<?>) querySubscriber.getOnNextEvents().get(0);
        assertThat(queryResults.get(0), is(instanceOf(TestModel2.class)));
    }

    /**
     * Test if we insert single test model and then make a realm query
     * query results are correct, that is we getInstance what we inserted
     */
    @Test
    public void testGetWhere_ifQueryResultsHasCorrectModel_whenRealmQueryIsMadeForSingleItem() {
        final TestModel2 insertedTestModel2 = createTestModelWithRandomId();
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        final RealmQuery<TestModel2> realmQuery = getExactTestModelRealmQuery(insertedTestModel2);
        putTestModel(mRealmManager, insertedTestModel2);
        mRealmManager.getWhere(realmQuery)
                .subscribe(querySubscriber);
        RealmResults<TestModel2> queryResults = (RealmResults<TestModel2>) querySubscriber.getOnNextEvents().get(0);
        assertThat(queryResults.get(0), is(equalTo(insertedTestModel2)));
    }

    @Test
    public void testGetAll_ifCorrectNumberOfRecordsAreFetched_whenMultipleRecordsAreInserted() {
        TestUtility.executeMultipleTimes(TestUtility.EXECUTION_COUNT_VERY_SMALL, () -> putTestModel(mRealmManager));
        final TestSubscriber<List> getAllSubscriber = new TestSubscriber<>();
        mRealmManager.getAll(TestModel2.class).subscribe(getAllSubscriber);
        assertThat(getAllSubscriber.getOnNextEvents().get(0).size()
                , is(equalTo(TestUtility.EXECUTION_COUNT_VERY_SMALL)));
    }

    @Test
    public void testGetAll_ifCorrectNumberOfRecordsAreFetched_whenSingleRecordIsInserted() {
        putTestModel(mRealmManager);
        final TestSubscriber<List> getAllSubscriber = new TestSubscriber<>();
        mRealmManager.getAll(TestModel2.class).subscribe(getAllSubscriber);
        assertThat(getAllSubscriber.getOnNextEvents().get(0).size()
                , is(equalTo(1)));
    }

    @Test
    public void testGetAll_ifCorrectNumberOfRecordsAreFetched_whenNoRecordsAreInserted() {
        final TestSubscriber<List> getAllSubscriber = new TestSubscriber<>();
        mRealmManager.getAll(TestModel2.class).subscribe(getAllSubscriber);
        assertThat(getAllSubscriber.getOnNextEvents().get(0).size()
                , is(equalTo(0)));
    }

    @Test
    public void testGetAll_ifExactlySameObjectsArezReturned_whenMultipleObjectsAreInserted() {
        List<TestModel2> listOfTestModelsInserted2 = new ArrayList<>();
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_VERY_SMALL; i++) {
            listOfTestModelsInserted2.add(putTestModel(mRealmManager));
        }
        final TestSubscriber<List> subscriber = new TestSubscriber<>();
        mRealmManager.getAll(TestModel2.class)
                .subscribe(subscriber);
        List<TestModel2> getModelList = subscriber.getOnNextEvents().get(0);
        assertThatTwoCollectionsContainSameElements(listOfTestModelsInserted2, getModelList);
    }

    @Test
    public void testEvictById_ifModelIsDeletedSuccessfully_whenSingleModelIsInserted() {
        TestModel2 insertedModel = putTestModel(mRealmManager);
        boolean isEvictSuccessful
                = mRealmManager.evictById(TestModel2.class, "id", insertedModel.getId());
        assertThat("", isEvictSuccessful);
    }

    @Test
    public void testEvictById_ifModelIsDeletedSuccessfully_whenMultipleModelAreInserted() {
        TestUtility.executeMultipleTimes(TestUtility.EXECUTION_COUNT_VERY_SMALL, () -> putTestModel(mRealmManager));
        TestModel2 lastTestModel2Inserted = putTestModel(mRealmManager);
        boolean isEvictSuccessful
                = mRealmManager.evictById(TestModel2.class, "id", lastTestModel2Inserted.getId());
        assertThat("", isEvictSuccessful);
    }

    @Test
    public void testEvictById_ifNoModelIsDeleted_whenIncorrectFieldValueIsMentioned() {
        putTestModel(mRealmManager);
        boolean isEvictSuccessful
                = mRealmManager.evictById(TestModel2.class, "id", TestUtility.INVALID_ITEM_ID);
        assertThat(isEvictSuccessful, is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEvictById_ifExceptionIsThrown_whenIncorrectFieldNameIsMentioned() {
        putTestModel(mRealmManager);
        boolean isEvictSuccessful
                = mRealmManager.evictById(TestModel2.class, "value", TestUtility.INVALID_ITEM_ID);
        assertThat(isEvictSuccessful, is(false));
    }

    @Test
    public void testEvict_ifRealmModelIsDeleted_whenManagedRealmObjectIsDeleted() {
        TestModel2 testModel2 = putTestModel(mRealmManager);
        Realm mRealm = Realm.getDefaultInstance();
        TestModel2 managedTestModel2 = getManagedObject(testModel2, mRealmManager);
        boolean originalValidity = managedTestModel2.isValid();
        mRealm.beginTransaction();
        managedTestModel2.deleteFromRealm();
        mRealm.commitTransaction();
        boolean newValidity = managedTestModel2.isValid();
        assertThat("original validity should be true," +
                        " and new validity should be false: originalValidity:"
                        + originalValidity + "newValidity:" + newValidity
                , originalValidity && !newValidity);
    }

    @Test
    public void testPutRealmModel_ifCorrectNumberOfItemsAreInserted_whenSingleItemIsInserted() {
        mRealmManager.put(createRealmModelInstanceWithRandomId(), RealmModelClass2.class)
                .subscribe(new TestSubscriber<>());
        assertGetAllForSize(RealmModelClass2.class, 1);
    }

    @Test
    public void testPutRealmModel_ifNoErrorAreRaised_whenSingleItemIsInserted() {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(createRealmModelInstanceWithRandomId(), RealmModelClass2.class)
                .subscribe(subscriber);
        subscriber.assertNoErrors();
    }

    @Test
    public void testBefore_ifEvictDeletesAllModelInstancesFromRealm_whenEvictAllIsInvokedInSetupMethod() {
        assertGetAllForSize(RealmModelClass2.class, 0);
        assertGetAllForSize(TestModel2.class, 0);
        assertGetAllForSize(ModelWithStringPrimaryKey2.class, 0);
    }

    @Test
    public void testPutRealmModel_ifCorrectNumberOfItemsAreInserted_whenMultipleItemAreInserted() {
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_SMALL; i++) {
            mRealmManager.put(createRealmModelInstanceWithRandomId(), RealmModelClass2.class)
                    .subscribe(new TestSubscriber<>());
        }
        assertGetAllForSize(RealmModelClass2.class, TestUtility.EXECUTION_COUNT_SMALL);
    }

    @Test
    public void testPutRealmModel_ifModelIsUpdated_whenUpdatedModelIsInserted() {
        final RealmModelClass2 testModelInstance = createRealmModelInstanceWithRandomId();
        mRealmManager.put(testModelInstance, RealmModelClass2.class)
                .subscribe(new TestSubscriber<>());
        testModelInstance.setValue("test update");
        mRealmManager.put(testModelInstance, RealmModelClass2.class)
                .subscribe(new TestSubscriber<>());
        //check if no duplicates are added
        assertGetAllForSize(RealmModelClass2.class, 1);
        //check if values are updated or not
        final TestSubscriber<Object> getItemByIdSubscriber = new TestSubscriber<>();
        mRealmManager.getById("id", testModelInstance.getId(), RealmModelClass2.class)
                .subscribe(getItemByIdSubscriber);
        RealmModelClass2 currentModel
                = assertSubscriberGetSingleNextEventWithNoError(getItemByIdSubscriber
                , RealmModelClass2.class);
        assertThat("two instances are not same", currentModel.equals(testModelInstance));
    }

    @Test
    public void testPutRealmModel_ifItemsAreInsertedCorrectly_whenTwoTypesOfItemsAreInserted() {
        //insert some test realm objects
        TestUtility.executeMultipleTimes(TestUtility.EXECUTION_COUNT_VERY_SMALL
                , () -> putTestModel(mRealmManager));
        //insert some realm model class
        TestUtility.executeMultipleTimes(TestUtility.EXECUTION_COUNT_VERY_SMALL
                , () -> mRealmManager.put(createRealmModelInstanceWithRandomId()
                        , RealmModelClass2.class).subscribe(new TestSubscriber<>()));
        //check if correct number of realm model classes are inserted or not
        assertGetAllForSize(RealmModelClass2.class, TestUtility.EXECUTION_COUNT_VERY_SMALL);
    }

    @Test
    public void testPutRealmModel_ifWeGetWhatWeInserted_whenWeInsertAndQueryById() {
        final RealmModelClass2 realmModelClass2 = createRealmModelInstanceWithRandomId();
        mRealmManager.put(realmModelClass2, RealmModelClass2.class)
                .subscribe(new TestSubscriber<>());
        assertThat(realmModelClass2, is(equalTo(getItemForId("id", realmModelClass2.getId(), RealmModelClass2.class))));
    }

    @Test
    public void testPutAll_ifCorrectNumberOfItemsAreInserted_whenMultipleItemsAreInserted() {
        List<RealmObject> testModelList = new ArrayList<>();
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_VERY_SMALL; i++) {
            testModelList.add(createTestModelWithRandomId());
        }
        mRealmManager.putAll(testModelList, TestModel2.class);
        assertGetAllForSize(TestModel2.class, TestUtility.EXECUTION_COUNT_VERY_SMALL);
    }

    @Test
    public void testPutAll_ifCorrectItemsAreInserted_whenMultipleItemsAreInserted() {

        final List<RealmObject> testModelList = new ArrayList<>();
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_VERY_SMALL; i++) {
            testModelList.add(createTestModelWithRandomId());
        }
        mRealmManager.putAll(testModelList, TestModel2.class);
        final TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        for (RealmObject testModel : testModelList) {
            mRealmManager.getById("id", ((TestModel2) testModel).getId(), TestModel2.class)
                    .subscribe(testSubscriber);
            assertThat(testSubscriber.getOnNextEvents(), allOf(iterableWithSize(1), notNullValue()));
        }
    }

    @Test
    public void testPutAll_ifCorrectItemIsInserted_whenSingleItemIsInserted() {
        final TestModel2 testModel2 = createTestModelWithRandomId();
        mRealmManager.putAll(Collections.singletonList(testModel2)
                , TestModel2.class);
        getItemForId("id", testModel2.getId(), TestModel2.class);
    }

    @Test
    public void testPutAll_ifSingleItemIsInserted_whenSingleItemIsInserted() {
        final TestModel2 testModel2 = createTestModelWithRandomId();
        mRealmManager.putAll(Collections.singletonList(testModel2)
                , TestModel2.class);
        assertGetAllForSize(TestModel2.class, 1);
    }

    @Test
    public void testPutAll_ifCorrectNumberOfItemsAreInserted_whenSingleItemIsInserted() {
        TestModel2 insertedTestModel2 = putTestModel(mRealmManager);
        final TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        mRealmManager.getById("id", (insertedTestModel2).getId(), TestModel2.class)
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
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_VERY_SMALL; i++) {
            final TestModel2 testModel2 = putTestModel(mRealmManager);
            listOfInsertedTestModels.add(testModel2);
            //but we will send only half of them
            if (i % 2 == 0) {
                listOfIdToDelete.add((long) testModel2.getId());
            }
        }
        //lets try deleting these
        final TestSubscriber<Object> deleteCollectionSubscriber = new TestSubscriber<>();
        mRealmManager.evictCollection("id"
                , listOfIdToDelete, TestModel2.class).subscribe(deleteCollectionSubscriber);
        deleteCollectionSubscriber.assertNoErrors();
        assertThat(deleteCollectionSubscriber.getOnNextEvents().get(0), is(equalTo(Boolean.TRUE)));
        RealmResults<TestModel2> testModel2List = getQueryList(TestModel2.class);
        for (TestModel2 testModel2 : testModel2List) {
            assertThat(listOfIdToDelete, not(IsCollectionContaining.hasItem((long) testModel2.getId())));
        }
    }

    @Test
    public void testEvictCollection_ifSpecifiedItemsAfterInvalidIdAreNotDeleted_whenMultipleIdsMixedWithInvalidIdIsPassed() {
        //lets put EXECUTION_COUNT_VERY_SMALL test models
        ArrayList<Object> listOfInsertedTestModels = new ArrayList<>();
        ArrayList<Long> listOfIdToDelete = new ArrayList<>();
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_VERY_SMALL; i++) {
            final TestModel2 testModel2 = putTestModel(mRealmManager);
            listOfInsertedTestModels.add(testModel2);
            //but we will send only half of them
            if (i % 2 == 0) {
                listOfIdToDelete.add((long) testModel2.getId());
            }
            if (i == TestUtility.EXECUTION_COUNT_VERY_SMALL / 2) {
                //add invalid id
                listOfIdToDelete.add((long) TestUtility.INVALID_ITEM_ID);
            }
        }
        //lets try deleting these
        final TestSubscriber<Object> deleteCollectionSubscriber = new TestSubscriber<>();
        mRealmManager.evictCollection("id"
                , listOfIdToDelete, TestModel2.class).subscribe(deleteCollectionSubscriber);
        deleteCollectionSubscriber.assertNoErrors();
        assertThat(deleteCollectionSubscriber.getOnNextEvents().get(0), is(equalTo(Boolean.FALSE)));
        RealmResults<TestModel2> testModel2List = getQueryList(TestModel2.class);
        //all ids before INVALID_ITEM_ID should be deleted
        int i = 0;
        while (listOfIdToDelete.get(i) != TestUtility.INVALID_ITEM_ID) {
            assertThat(getItemForId("id", listOfIdToDelete.get(i), TestModel2.class), is(nullValue()));
            i++;
        }
        i++;
        //all ids before INVALID_ITEM_ID should not be deleted
        while (i < listOfIdToDelete.size()) {
            assertThat(getItemForId("id", listOfIdToDelete.get(i), TestModel2.class), is(notNullValue()));
            i++;
        }
    }

    @Test
    public void testEvictCollection_ifItemsAreEvicted_whenSingleItemIdIsProvidedInList() {
        TestModel2 insertedTestModel2 = putTestModel(mRealmManager);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.evictCollection("id"
                , Collections.singletonList(Long.valueOf(insertedTestModel2.getId()))
                , TestModel2.class)
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
        subscriber.assertValue(Boolean.TRUE);
    }

    private <T> T getItemForId(String idFieldName, long idFieldValue, Class<T> clazz) {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.getById(idFieldName, (int) idFieldValue, clazz).subscribe(subscriber);
        return (T) subscriber.getOnNextEvents().get(0);
    }

    private TestModel2 getManagedObject(TestModel2 testModel2, GenericRealmManager realmManager) {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        realmManager.getById("id", testModel2.getId(), TestModel2.class).subscribe(subscriber);
        return (TestModel2) subscriber.getOnNextEvents().get(0);
    }

    private void createJsonObjectInWorkerThreadAndInsertInUi() {
        assertCurrentThreadIsWorkerThread();
        GenericRealmManager realmManager
                = getGeneralRealmManager();
        JSONObject[] jsonObject
                = new JSONObject[1];
        TestUtility.getJsonObjectFrom(jsonObject, createTestModelWithRandomId());
        TestUtility.assertConvertedJsonArray(jsonObject);
        runOnMainThread(() -> {
            Observable<?> observable
                    = realmManager.put(jsonObject[0], "id", TestModel2.class);
            assertPutOperationObservable(observable);
        });
    }

    private <T extends RealmModel> RealmResults<T> assertGetAllForSize(Class<T> clazz
            , int sizeExpected) {
        final RealmResults<T> queryResult = getQueryList(clazz);
        assertThat(queryResult.size(), is(equalTo(sizeExpected)));
        return queryResult;
    }

    @NonNull
    private <T extends RealmModel> RealmResults<T> getQueryList(Class<T> clazz) {
        Observable<List<?>> queryObservable = mRealmManager.getAll(clazz);
        assertThat(queryObservable, notNullValue());
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        queryObservable.subscribe(querySubscriber);
        TestUtility.assertNoErrors(querySubscriber);
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

    private <T> T assertSubscriberGetSingleNextEventWithNoError(TestSubscriber subscriber, Class<T> clazz) {
        subscriber.assertNoErrors();
        final List<List> onNextEvents = subscriber.getOnNextEvents();
        assertThat(onNextEvents, allOf(notNullValue(), iterableWithSize(1)));
        assertThat(onNextEvents.get(0), is(instanceOf(clazz)));
        return ((T) onNextEvents.get(0));
    }

    @NonNull
    private RealmQuery<TestModel2> getExactTestModelRealmQuery(TestModel2 insertedTestModel2) {
        return Realm.getDefaultInstance()
                .where(TestModel2.class)
                .beginsWith("value", TEST_MODEL_PREFIX, Case.INSENSITIVE)
                .equalTo("id", insertedTestModel2.getId())
                .equalTo("value", insertedTestModel2.getValue(), Case.INSENSITIVE);
    }

//    @NonNull
//    private OrdersRealmModel getOrdersRealmModel() {
//        OrdersRealmModel orm = new OrdersRealmModel();
//        orm.setId(getRandomInt());
//        return orm;
//    }

    private int getRandomInt() {
        return Math.abs(mRandom.nextInt()) + 1;
    }

    private void runOnMainThread(Runnable runnable) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(runnable);
    }

    private void assertPutOperationObservable(Observable<?> observable) {
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
    private GenericRealmManager getGeneralRealmManager() {
        return (GenericRealmManager) DatabaseManagerFactory.getInstance(getContext());
    }

    private Context getContext() {
        return InstrumentationRegistry.getContext();
    }

    private TestModel2 insertJsonObject() throws JSONException {
        TestModel2 testModel2 = createTestModelWithRandomId();
        String json = new Gson().toJson(testModel2);
        JSONObject jsonObject = new JSONObject(json);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObject, "id", TestModel2.class)
                .subscribe(subscriber);
        subscriber.assertNoErrors();
        return testModel2;
    }

    private TestModel2 putTestModel(DataBaseManager dataBaseManager) {
        return putTestModel(dataBaseManager, createTestModelWithRandomId());
    }

    private TestModel2 putTestModel(DataBaseManager dataBaseManager, TestModel2 realmModel) {
        Observable<?> observable
                = dataBaseManager.put(realmModel, GeneralRealmManagerImplTest.class);
        TestSubscriber<Object> subscriber = TestSubscriber.create();
        observable.subscribe(subscriber);
        TestUtility.printThrowables(subscriber.getOnErrorEvents());
        TestUtility.assertNoErrors(subscriber);
        return realmModel;
    }

    @NonNull
    private TestModel2 createTestModelWithRandomId() {
        final int randomInt = getRandomInt();
        return new TestModel2(randomInt, getStringValueForTestModel(randomInt));
    }

    @NonNull
    private RealmModelClass2 createRealmModelInstanceWithRandomId() {
        final int randomInt = getRandomInt();
        return new RealmModelClass2(randomInt, getStringValueForTestModel(randomInt));
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

    private <T> void assertThatTwoCollectionsContainSameElements(List<T> left, List<T> right) {
        assertThat(left, is(containsInAnyOrder(right.toArray())));
        assertThat(right, is(containsInAnyOrder(left.toArray())));
    }

}