package com.zeyad.usecases.data.db;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.BuildConfig;

import com.google.gson.Gson;
import com.zeyad.usecases.data.utils.Utils;
import com.zeyad.usecases.domain.interactors.data.DataUseCase;
import com.zeyad.usecases.utils.ModelWithStringPrimaryKey;
import com.zeyad.usecases.utils.RealmModelClass;
import com.zeyad.usecases.utils.TestRealmObject;
import com.zeyad.usecases.utils.TestUtility;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsCollectionContaining;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
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
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

// FIXME: 2/10/17 Redo!
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Realm.class})
public class RealmManagerImplTestJUnit {

    private static final String TEST_MODEL_PREFIX = "random value:";

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    private RealmManager mRealmManager;
    private Random mRandom;

    public static Realm mockRealm() {
        mockStatic(Realm.class);

        Realm mockRealm = PowerMockito.mock(Realm.class);

        when(mockRealm.createObject(TestRealmObject.class)).thenReturn(new TestRealmObject());
//        when(mockRealm.createObject(Offer.class)).thenReturn(new Offer());
//        when(mockRealm.createObject(Tasker.class)).thenReturn(new Tasker());
//        when(mockRealm.createObject(Job.class)).thenReturn(new Job());

        when(Realm.getDefaultInstance()).thenReturn(mockRealm);

        return mockRealm;
    }

    @Before
    public void before() {
        com.zeyad.usecases.Config.init();
        TestUtility.performInitialSetupOfDb();
        mRandom = new Random();
        mRealmManager = getGeneralRealmManager();
        mRealmManager.evictAll(TestRealmObject.class).subscribe(new TestSubscriber<>());
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
        realmManager.getAll(TestRealmObject.class);
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
        int previousTestModelCount = getQueryList(TestRealmObject.class).size();
        insertJsonObject();
        assertGetAllForSize(TestRealmObject.class, previousTestModelCount + 1);
    }

    @Test
    public void testPutJsonMethod_ifTrueIsReturnedInSubscriber_whenOperationIsPerformed() throws Throwable {
        TestRealmObject testRealmObject = createTestModelWithRandomId();
        String json = new Gson().toJson(testRealmObject);
        JSONObject jsonObject = new JSONObject(json);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObject, "id", TestRealmObject.class)
                .subscribe(subscriber);
        subscriber.assertValue(Boolean.TRUE);
    }

    @Test
    public void testPutRealmObject_ifNoExceptionIsThrown_whenOperationIsPerformed() throws Throwable {
        int previousTestModelCount = getQueryList(TestRealmObject.class).size();
        putTestModel(mRealmManager);
        assertGetAllForSize(TestRealmObject.class, previousTestModelCount + 1);
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
        TestRealmObject testRealmObject = createTestModelWithRandomId();
        String json = new Gson().toJson(testRealmObject);
        JSONObject jsonObject = new JSONObject(json);
        runOnMainThread(() -> {
            final TestSubscriber<Object> subscriber = new TestSubscriber<>();
            RealmManager realmManager
                    = getGeneralRealmManager();
            realmManager.put(jsonObject, "id", TestRealmObject.class)
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
        RealmManager realmManager
                = getGeneralRealmManager();
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        realmManager.put(jsonObject[0], "id", TestRealmObject.class)
                .subscribe(subscriber);
        subscriber.assertNoErrors();
    }

    @Test
    public void testPutJsonObject_ifNoExceptionIsRaised_whenJsonObjectIsInserted() {
        RealmManager realmManager
                = getGeneralRealmManager();
        JSONObject[] jsonObject = new JSONObject[1];
        TestUtility.getJsonObjectFrom(jsonObject
                , createTestModelWithRandomId());
        TestUtility.assertConvertedJsonArray(jsonObject);
        Observable<?> observable
                = realmManager.put(jsonObject[0], "id", TestRealmObject.class);
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
        RealmManager realmManager
                = getGeneralRealmManager();
        JSONObject[] jsonObject
                = new JSONObject[1];
        runOnMainThread(() -> {
            TestUtility.getJsonObjectFrom(jsonObject, createTestModelWithRandomId());
            TestUtility.assertConvertedJsonArray(jsonObject);
        });
        Observable<?> observable = realmManager.put(jsonObject[0], "id", TestRealmObject.class);
        assertPutOperationObservable(observable);
    }

    @Test
    public void testEvictAllMethod_IfDeletesAllModels_whenSinglePut() {
        putTestModel(mRealmManager);
        Observable<Boolean> evictObservable
                = mRealmManager.evictAll(TestRealmObject.class);
        final TestSubscriber<Boolean> evictAllSubscriber = new TestSubscriber<>();
        evictObservable.subscribe(evictAllSubscriber);
        evictAllSubscriber.assertValue(Boolean.TRUE);
        assertGetAllForSize(TestRealmObject.class, 0);
    }

    @Test
    public void testEvictAllMethod_ifNoExceptionIsThrown_IfIncorrectClassnameIsPassed() {
        mRealmManager.evictAll(TestRealmObject.class).subscribe(new TestSubscriber<>());
    }

    @Test
    public void testEvictAllMethod_IfDeletesAllModels_whenMultiplePut() {
        TestUtility.executeMultipleTimes(TestUtility.EXECUTION_COUNT_SMALL, () -> putTestModel(mRealmManager));
        assertGetAllForSize(TestRealmObject.class, TestUtility.EXECUTION_COUNT_SMALL);
        Observable<Boolean> evictObservable
                = mRealmManager.evictAll(TestRealmObject.class);
        final TestSubscriber<Boolean> evictAllSubscriber = new TestSubscriber<>();
        evictObservable.subscribe(evictAllSubscriber);
        evictAllSubscriber.assertValue(Boolean.TRUE);
        assertGetAllForSize(TestRealmObject.class, 0);
    }


    @Test
    public void testEvictBySingleModel_ifManagedModelIsInsertedAndEvicted_thenWouldBeDeleted() {
        TestRealmObject insertedTestRealmObject = putTestModel(mRealmManager);
        mRealmManager.evict(getManagedObject(insertedTestRealmObject, mRealmManager), TestRealmObject.class);
        assertGetAllForSize(TestRealmObject.class, 0);
    }


    @Test
    public void testPutJsonObject_ifAddedSuccessfully_whenRealmObjectWithIntegerPrimaryKeyIsAdded() {
        ModelWithStringPrimaryKey modelWithStringPrimaryKey = new ModelWithStringPrimaryKey(String.valueOf(getRandomInt())
                , TEST_MODEL_PREFIX);
        JSONObject[] jsonObjectArray = new JSONObject[1];
        TestUtility.getJsonObjectFrom(jsonObjectArray, modelWithStringPrimaryKey);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObjectArray[0], "studentId", ModelWithStringPrimaryKey.class)
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
        assertGetAllForSize(ModelWithStringPrimaryKey.class, 1);
    }

    @Test(expected = AssertionError.class)
    public void testPutJsonObject_ifAddedSuccessfully_whenRealmObjectWithIntegerPrimaryKeyIsAddedAndIdIsPassedAsPrimaryKey() {
        ModelWithStringPrimaryKey modelWithStringPrimaryKey = new ModelWithStringPrimaryKey(String.valueOf(getRandomInt())
                , TEST_MODEL_PREFIX);
        JSONObject[] jsonObjectArray = new JSONObject[1];
        TestUtility.getJsonObjectFrom(jsonObjectArray, modelWithStringPrimaryKey);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObjectArray[0], "id", ModelWithStringPrimaryKey.class)
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
        assertGetAllForSize(ModelWithStringPrimaryKey.class, 1);
    }

    @Test(expected = AssertionError.class)
    public void testPutJsonObject_ifAddedSuccessfully_whenRealmObjectWithStringPrimaryKeyIsAdded() {
        ModelWithStringPrimaryKey modelWithStringPrimaryKey
                = new ModelWithStringPrimaryKey("abc"
                , TEST_MODEL_PREFIX);
        JSONObject[] jsonObjectArray = new JSONObject[1];
        TestUtility.getJsonObjectFrom(jsonObjectArray, modelWithStringPrimaryKey);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObjectArray[0], "studentId", ModelWithStringPrimaryKey.class)
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
        assertGetAllForSize(ModelWithStringPrimaryKey.class, 1);
    }

    @Test
    public void testEvictBySingleModel_ifMultipleModelsAreInsertedAndEvictedSequentially() {
        List<TestRealmObject> insertedTestRealmObjectList = new ArrayList<>();
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_SMALL; i++) {
            insertedTestRealmObjectList.add(putTestModel(mRealmManager));
        }
        assertGetAllForSize(TestRealmObject.class, TestUtility.EXECUTION_COUNT_SMALL);
        for (TestRealmObject testRealmObject : insertedTestRealmObjectList) {
            final TestRealmObject managedObject = getManagedObject(testRealmObject, mRealmManager);
            mRealmManager.evict(managedObject, TestRealmObject.class);
        }
        assertGetAllForSize(TestRealmObject.class, 0);
    }

    @Test
    public void testGetManagedObject_ifReturnedObjectIsValid_whenNonManagedObjectIsProvided() {
        TestRealmObject testRealmObject = putTestModel(mRealmManager);
        TestRealmObject managedTestRealmObject = getManagedObject(testRealmObject, mRealmManager);
        MatcherAssert.assertThat("managed test model should be valid", managedTestRealmObject.isValid());
    }

    @Test
    public void testPutRealmObject_ifInsertedObjectIsNotvalid_whenRealmObjectIsInserted() {
        TestRealmObject testRealmObject = putTestModel(mRealmManager);
        assertThat("test model should not be valid", !testRealmObject.isValid());
    }

    @Test
    public void testAddingOrderModelJsonCreateInUIAndWriteInWorker_multipleTimes() {
        TestUtility.executeMultipleTimes(TestUtility.EXECUTION_COUNT_SMALL, this::testPutJsonObject_IfNoExceptionIsRaised_whenOrderModelJsonCreateInUIAndWriteInWorker);
    }

    @Test
    public void testPutRealmObject_ifCorrectNumberOfItemsAreInserted_whenSingleItemIsInserted() {
        Observable<?> observable = mRealmManager.put(createTestModelWithRandomId(), TestRealmObject.class);
        observable.subscribe(new TestSubscriber<>());
        assertGetAllForSize(TestRealmObject.class, 1);
    }

    @Test
    public void testPutRealmObject_ifCorrectNumberOfItemsAreInserted_whenMultipleItemAreInserted() {
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_SMALL; i++) {
            Observable<?> observable = mRealmManager.put(createTestModelWithRandomId(), TestRealmObject.class);
            observable.subscribe(new TestSubscriber<>());
        }
        assertGetAllForSize(TestRealmObject.class, TestUtility.EXECUTION_COUNT_SMALL);
    }

    @Test
    public void testPutRealmObject_ifModelIsUpdated_whenUpdatedModelIsInserted() {
        final TestRealmObject testRealmObjectInstance = createTestModelWithRandomId();
        mRealmManager.put(testRealmObjectInstance, TestRealmObject.class)
                .subscribe(new TestSubscriber<>());
        testRealmObjectInstance.setValue("test update");
        mRealmManager.put(testRealmObjectInstance, TestRealmObject.class)
                .subscribe(new TestSubscriber<>());
        //check if no duplicates are added
        assertGetAllForSize(TestRealmObject.class, 1);
        //check if values are updated or not
        final TestSubscriber<Object> getItemByIdSubscriber = new TestSubscriber<>();
        mRealmManager.getById("id", testRealmObjectInstance.getId(), TestRealmObject.class)
                .subscribe(getItemByIdSubscriber);
        TestRealmObject currentModel
                = assertSubscriberGetSingleNextEventWithNoError(getItemByIdSubscriber
                , TestRealmObject.class);
        MatcherAssert.assertThat("two instances are not same", currentModel.equals(testRealmObjectInstance));
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * , returned observable should not be null.
     */
    @Test
    public void testGetById_ifReturnedObservableIsNotNull_whenSingleModelIsInsertedAndQueried() {
        TestRealmObject insertedTestRealmObject = putTestModel(mRealmManager);
        assertThat(mRealmManager.getById("id"
                , insertedTestRealmObject.getId()
                , TestRealmObject.class), is(notNullValue()));
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * Returned Observable should return no error.
     */
    @Test
    public void testGetById_ifObservableShouldReturnNoError_whenSingleModelIsInsertedAndQueried() {
        TestRealmObject insertedTestRealmObject = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestRealmObject);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , insertedTestRealmObject.getId()
                , TestRealmObject.class)
                .subscribe(querySubscriber);
        querySubscriber.assertNoErrors();
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * Returned Observable should return size of 1 next event list.
     */
    @Test
    public void testGetById_ifOnNextEventListHasAtLeastOneItem_whenSingleModelIsInsertedAndQueried() {
        TestRealmObject insertedTestRealmObject = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestRealmObject);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , insertedTestRealmObject.getId()
                , TestRealmObject.class)
                .subscribe(querySubscriber);
        assertThat(querySubscriber.getOnNextEvents(), iterableWithSize(1));
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * on next event list should contain  TestRealmObject result at item index 1
     */
    @Test
    public void testGetById_ifQueryResultIsInstanceOfTestModel_whenSingleModelIsInsertedAndQueried() {
        TestRealmObject insertedTestRealmObject = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestRealmObject);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , insertedTestRealmObject.getId()
                , TestRealmObject.class)
                .subscribe(querySubscriber);
        assertThat(querySubscriber.getOnNextEvents().get(0), is(instanceOf(TestRealmObject.class)));
    }

    /**
     * Test if we insert single test model and then make a query on id.
     * returned test nmodel is same as inserted
     */
    @Test
    public void testGetById_ifQueryResultsHasCorrectModel_whenSingleModelIsInsertedAndQueried() {
        TestRealmObject insertedTestRealmObject = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestRealmObject);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , insertedTestRealmObject.getId()
                , TestRealmObject.class)
                .subscribe(querySubscriber);
        TestRealmObject returnedTestRealmObject = (TestRealmObject) querySubscriber.getOnNextEvents().get(0);
        assertThat(returnedTestRealmObject, is(equalTo(insertedTestRealmObject)));
    }

    /**
     * Test if we insert single test model and then make a query using invalid id.
     * we should getInstance a test model with maximum id.
     */
    @Test
    public void testGetById_ifModelWithMaxIdIsReturned_whenQueriedUsingNegativeId() {
        TestRealmObject insertedTestRealmObject = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestRealmObject);
        final TestSubscriber<Object> querySubscriber = new TestSubscriber<>();
        mRealmManager.getById("id"
                , TestUtility.INVALID_ITEM_ID
                , TestRealmObject.class)
                .subscribe(querySubscriber);
        TestRealmObject returnedTestRealmObject = (TestRealmObject) querySubscriber.getOnNextEvents().get(0);
        int maxId = Utils.getInstance().getMaxId(TestRealmObject.class, "id");
        assertThat(returnedTestRealmObject.getId(), is(equalTo(maxId)));
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * , returned observable should not be null.
     */
    @Test
    public void testGetWhere_ifReturnedObservableIsNotNull_whenRealmQueryIsMadeForSingleItem() {
        TestRealmObject insertedTestRealmObject = createTestModelWithRandomId();
        RealmManager.RealmQueryProvider realmQuery = getExactTestModelRealmQuery(insertedTestRealmObject);
        assertThat(mRealmManager.getQuery(realmQuery), is(notNullValue()));
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * Returned Observable should return no error.
     */
    @Test
    public void testGetWhere_ifObservableShouldReturnNoError_whenRealmQueryIsMadeForSingleItem() {
        TestRealmObject insertedTestRealmObject = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestRealmObject);
        RealmManager.RealmQueryProvider realmQuery = getExactTestModelRealmQuery(insertedTestRealmObject);
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
        TestRealmObject insertedTestRealmObject = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestRealmObject);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        RealmManager.RealmQueryProvider realmQuery = getExactTestModelRealmQuery(insertedTestRealmObject);
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
        final TestRealmObject insertedTestRealmObject = createTestModelWithRandomId();
        putTestModel(mRealmManager, insertedTestRealmObject);
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        final RealmManager.RealmQueryProvider realmQuery = getExactTestModelRealmQuery(insertedTestRealmObject);
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
        TestRealmObject insertedTestRealmObject = createTestModelWithRandomId();
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        final RealmManager.RealmQueryProvider realmQuery = getExactTestModelRealmQuery(insertedTestRealmObject);
        putTestModel(mRealmManager, insertedTestRealmObject);
        mRealmManager.getQuery(realmQuery)
                .subscribe(querySubscriber);
        RealmResults<TestRealmObject> queryResults = (RealmResults<TestRealmObject>) querySubscriber.getOnNextEvents().get(0);
        MatcherAssert.assertThat(queryResults, is(iterableWithSize(1)));
    }

    /**
     * Test if we insert single test model and then make a realm query.
     * query results are instance of test model
     */
    @Test
    public void testGetWhere_ifQueryResultsInstanceOfTestModel_whenRealmQueryIsMadeForSingleItem() {
        final TestRealmObject insertedTestRealmObject = createTestModelWithRandomId();
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        final RealmManager.RealmQueryProvider realmQuery = getExactTestModelRealmQuery(insertedTestRealmObject);
        putTestModel(mRealmManager, insertedTestRealmObject);
        mRealmManager.getQuery(realmQuery)
                .subscribe(querySubscriber);
        RealmResults<?> queryResults = (RealmResults<?>) querySubscriber.getOnNextEvents().get(0);
        assertThat(queryResults.get(0), is(instanceOf(TestRealmObject.class)));
    }

    /**
     * Test if we insert single test model and then make a realm query
     * query results are correct, that is we getInstance what we inserted
     */
    @Test
    public void testGetWhere_ifQueryResultsHasCorrectModel_whenRealmQueryIsMadeForSingleItem() {
        final TestRealmObject insertedTestRealmObject = createTestModelWithRandomId();
        final TestSubscriber<List> querySubscriber = new TestSubscriber<>();
        final RealmManager.RealmQueryProvider realmQuery = getExactTestModelRealmQuery(insertedTestRealmObject);
        putTestModel(mRealmManager, insertedTestRealmObject);
        mRealmManager.getQuery(realmQuery)
                .subscribe(querySubscriber);
        RealmResults<TestRealmObject> queryResults = (RealmResults<TestRealmObject>) querySubscriber.getOnNextEvents().get(0);
        assertThat(queryResults.get(0), is(equalTo(insertedTestRealmObject)));
    }

    @Test
    public void testGetAll_ifCorrectNumberOfRecordsAreFetched_whenMultipleRecordsAreInserted() {
        TestUtility.executeMultipleTimes(TestUtility.EXECUTION_COUNT_VERY_SMALL, () -> putTestModel(mRealmManager));
        final TestSubscriber<List> getAllSubscriber = new TestSubscriber<>();
        mRealmManager.getAll(TestRealmObject.class).subscribe(getAllSubscriber);
        assertThat(getAllSubscriber.getOnNextEvents().get(0).size()
                , is(equalTo(TestUtility.EXECUTION_COUNT_VERY_SMALL)));
    }

    @Test
    public void testGetAll_ifCorrectNumberOfRecordsAreFetched_whenSingleRecordIsInserted() {
        putTestModel(mRealmManager);
        final TestSubscriber<List> getAllSubscriber = new TestSubscriber<>();
        mRealmManager.getAll(TestRealmObject.class).subscribe(getAllSubscriber);
        assertThat(getAllSubscriber.getOnNextEvents().get(0).size()
                , is(equalTo(1)));
    }

    @Test
    public void testGetAll_ifCorrectNumberOfRecordsAreFetched_whenNoRecordsAreInserted() {
        final TestSubscriber<List> getAllSubscriber = new TestSubscriber<>();
        mRealmManager.getAll(TestRealmObject.class).subscribe(getAllSubscriber);
        assertThat(getAllSubscriber.getOnNextEvents().get(0).size()
                , is(equalTo(0)));
    }

    @Test
    public void testGetAll_ifExactlySameObjectsArezReturned_whenMultipleObjectsAreInserted() {
        List<TestRealmObject> listOfTestModelsInserted = new ArrayList<>();
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_VERY_SMALL; i++) {
            listOfTestModelsInserted.add(putTestModel(mRealmManager));
        }
        final TestSubscriber<List> subscriber = new TestSubscriber<>();
        mRealmManager.getAll(TestRealmObject.class)
                .subscribe(subscriber);
        List<TestRealmObject> getModelList = subscriber.getOnNextEvents().get(0);
        assertThatTwoCollectionsContainSameElements(listOfTestModelsInserted, getModelList);
    }

    @Test
    public void testEvictById_ifModelIsDeletedSuccessfully_whenSingleModelIsInserted() {
        TestRealmObject insertedModel = putTestModel(mRealmManager);
        boolean isEvictSuccessful
                = mRealmManager.evictById(TestRealmObject.class, "id", insertedModel.getId());
        assertThat("", isEvictSuccessful);
    }

    @Test
    public void testEvictById_ifModelIsDeletedSuccessfully_whenMultipleModelAreInserted() {
        TestUtility.executeMultipleTimes(TestUtility.EXECUTION_COUNT_VERY_SMALL, () -> putTestModel(mRealmManager));
        TestRealmObject lastTestRealmObjectInserted = putTestModel(mRealmManager);
        boolean isEvictSuccessful
                = mRealmManager.evictById(TestRealmObject.class, "id", lastTestRealmObjectInserted.getId());
        assertThat("", isEvictSuccessful);
    }

    @Test
    public void testEvictById_ifNoModelIsDeleted_whenIncorrectFieldValueIsMentioned() {
        putTestModel(mRealmManager);
        boolean isEvictSuccessful
                = mRealmManager.evictById(TestRealmObject.class, "id", TestUtility.INVALID_ITEM_ID);
        assertThat(isEvictSuccessful, is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEvictById_ifExceptionIsThrown_whenIncorrectFieldNameIsMentioned() {
        putTestModel(mRealmManager);
        boolean isEvictSuccessful
                = mRealmManager.evictById(TestRealmObject.class, "value", TestUtility.INVALID_ITEM_ID);
        assertThat(isEvictSuccessful, is(false));
    }

    @Test
    public void testEvict_ifRealmModelIsDeleted_whenManagedRealmObjectIsDeleted() {
        TestRealmObject testRealmObject = putTestModel(mRealmManager);
        Realm mRealm = Realm.getDefaultInstance();
        TestRealmObject managedTestRealmObject = getManagedObject(testRealmObject, mRealmManager);
        boolean originalValidity = managedTestRealmObject.isValid();
        mRealm.beginTransaction();
        managedTestRealmObject.deleteFromRealm();
        mRealm.commitTransaction();
        boolean newValidity = managedTestRealmObject.isValid();
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
        assertGetAllForSize(TestRealmObject.class, 0);
        assertGetAllForSize(ModelWithStringPrimaryKey.class, 0);
    }

    @Test
    public void testPutRealmModel_ifCorrectNumberOfItemsAreInserted_whenMultipleItemAreInserted() {
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_SMALL; i++) {
            mRealmManager.put(createRealmModelInstanceWithRandomId(), RealmModelClass.class)
                    .subscribe(new TestSubscriber<>());
        }
        assertGetAllForSize(RealmModelClass.class, TestUtility.EXECUTION_COUNT_SMALL);
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
        TestUtility.executeMultipleTimes(TestUtility.EXECUTION_COUNT_VERY_SMALL
                , () -> putTestModel(mRealmManager));
        //insert some realm model class
        TestUtility.executeMultipleTimes(TestUtility.EXECUTION_COUNT_VERY_SMALL
                , () -> mRealmManager.put(createRealmModelInstanceWithRandomId()
                        , RealmModelClass.class).subscribe(new TestSubscriber<>()));
        //check if correct number of realm model classes are inserted or not
        assertGetAllForSize(RealmModelClass.class, TestUtility.EXECUTION_COUNT_VERY_SMALL);
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
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_VERY_SMALL; i++) {
            testModelList.add(createTestModelWithRandomId());
        }
        mRealmManager.putAll(testModelList, TestRealmObject.class);
        assertGetAllForSize(TestRealmObject.class, TestUtility.EXECUTION_COUNT_VERY_SMALL);
    }

    @Test
    public void testPutAll_ifCorrectItemsAreInserted_whenMultipleItemsAreInserted() {

        final List<RealmObject> testModelList = new ArrayList<>();
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_VERY_SMALL; i++) {
            testModelList.add(createTestModelWithRandomId());
        }
        mRealmManager.putAll(testModelList, TestRealmObject.class);
        final TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        for (RealmObject testModel : testModelList) {
            mRealmManager.getById("id", ((TestRealmObject) testModel).getId(), TestRealmObject.class)
                    .subscribe(testSubscriber);
            assertThat(testSubscriber.getOnNextEvents(), allOf(iterableWithSize(1), notNullValue()));
        }
    }

    @Test
    public void testPutAll_ifCorrectItemIsInserted_whenSingleItemIsInserted() {
        final TestRealmObject testRealmObject = createTestModelWithRandomId();
        mRealmManager.putAll(Collections.singletonList(testRealmObject)
                , TestRealmObject.class);
        getItemForId("id", testRealmObject.getId(), TestRealmObject.class);
    }

    @Test
    public void testPutAll_ifSingleItemIsInserted_whenSingleItemIsInserted() {
        final TestRealmObject testRealmObject = createTestModelWithRandomId();
        mRealmManager.putAll(Collections.singletonList(testRealmObject)
                , TestRealmObject.class);
        assertGetAllForSize(TestRealmObject.class, 1);
    }

    @Test
    public void testPutAll_ifCorrectNumberOfItemsAreInserted_whenSingleItemIsInserted() {
        TestRealmObject insertedTestRealmObject = putTestModel(mRealmManager);
        final TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        mRealmManager.getById("id", (insertedTestRealmObject).getId(), TestRealmObject.class)
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
            final TestRealmObject testRealmObject = putTestModel(mRealmManager);
            listOfInsertedTestModels.add(testRealmObject);
            //but we will send only half of them
            if (i % 2 == 0) {
                listOfIdToDelete.add((long) testRealmObject.getId());
            }
        }
        //lets try deleting these
        final TestSubscriber<Object> deleteCollectionSubscriber = new TestSubscriber<>();
        mRealmManager.evictCollection("id"
                , listOfIdToDelete, TestRealmObject.class).subscribe(deleteCollectionSubscriber);
        deleteCollectionSubscriber.assertNoErrors();
        assertThat(deleteCollectionSubscriber.getOnNextEvents().get(0), is(equalTo(Boolean.TRUE)));
        RealmResults<TestRealmObject> testRealmObjectList = getQueryList(TestRealmObject.class);
        for (TestRealmObject testRealmObject : testRealmObjectList) {
            assertThat(listOfIdToDelete, not(IsCollectionContaining.hasItem((long) testRealmObject.getId())));
        }
    }

    @Test
    public void testEvictCollection_ifSpecifiedItemsAfterInvalidIdAreNotDeleted_whenMultipleIdsMixedWithInvalidIdIsPassed() {
        //lets put EXECUTION_COUNT_VERY_SMALL test models
        ArrayList<Object> listOfInsertedTestModels = new ArrayList<>();
        ArrayList<Long> listOfIdToDelete = new ArrayList<>();
        for (int i = 0; i < TestUtility.EXECUTION_COUNT_VERY_SMALL; i++) {
            final TestRealmObject testRealmObject = putTestModel(mRealmManager);
            listOfInsertedTestModels.add(testRealmObject);
            //but we will send only half of them
            if (i % 2 == 0) {
                listOfIdToDelete.add((long) testRealmObject.getId());
            }
            if (i == TestUtility.EXECUTION_COUNT_VERY_SMALL / 2) {
                //add invalid id
                listOfIdToDelete.add((long) TestUtility.INVALID_ITEM_ID);
            }
        }
        //lets try deleting these
        final TestSubscriber<Object> deleteCollectionSubscriber = new TestSubscriber<>();
        mRealmManager.evictCollection("id"
                , listOfIdToDelete, TestRealmObject.class).subscribe(deleteCollectionSubscriber);
        deleteCollectionSubscriber.assertNoErrors();
        assertThat(deleteCollectionSubscriber.getOnNextEvents().get(0), is(equalTo(Boolean.FALSE)));
        RealmResults<TestRealmObject> testRealmObjectList = getQueryList(TestRealmObject.class);
        //all ids before INVALID_ITEM_ID should be deleted
        int i = 0;
        while (listOfIdToDelete.get(i) != TestUtility.INVALID_ITEM_ID) {
            MatcherAssert.assertThat(getItemForId("id", listOfIdToDelete.get(i), TestRealmObject.class), is(nullValue()));
            i++;
        }
        i++;
        //all ids before INVALID_ITEM_ID should not be deleted
        while (i < listOfIdToDelete.size()) {
            MatcherAssert.assertThat(getItemForId("id", listOfIdToDelete.get(i), TestRealmObject.class), is(notNullValue()));
            i++;
        }
    }

    @Test
    public void testEvictCollection_ifItemsAreEvicted_whenSingleItemIdIsProvidedInList() {
        TestRealmObject insertedTestRealmObject = putTestModel(mRealmManager);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.evictCollection("id"
                , Collections.singletonList(Long.valueOf(insertedTestRealmObject.getId()))
                , TestRealmObject.class)
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
        subscriber.assertValue(Boolean.TRUE);
    }

    @NonNull
    private <T> T getItemForId(@NonNull String idFieldName, long idFieldValue, Class<T> clazz) {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.getById(idFieldName, (int) idFieldValue, clazz).subscribe(subscriber);
        return (T) subscriber.getOnNextEvents().get(0);
    }

    @NonNull
    private TestRealmObject getManagedObject(@NonNull TestRealmObject testRealmObject, @NonNull RealmManager realmManager) {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        realmManager.getById("id", testRealmObject.getId(), TestRealmObject.class).subscribe(subscriber);
        return (TestRealmObject) subscriber.getOnNextEvents().get(0);
    }

    private void createJsonObjectInWorkerThreadAndInsertInUi() {
        assertCurrentThreadIsWorkerThread();
        RealmManager realmManager
                = getGeneralRealmManager();
        JSONObject[] jsonObject
                = new JSONObject[1];
        TestUtility.getJsonObjectFrom(jsonObject, createTestModelWithRandomId());
        TestUtility.assertConvertedJsonArray(jsonObject);
        runOnMainThread(() -> {
            Observable<?> observable
                    = realmManager.put(jsonObject[0], "id", TestRealmObject.class);
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

    @NonNull
    private <T> T assertSubscriberGetSingleNextEventWithNoError(@NonNull TestSubscriber subscriber, Class<T> clazz) {
        subscriber.assertNoErrors();
        final List<List> onNextEvents = subscriber.getOnNextEvents();
        assertThat(onNextEvents, allOf(notNullValue(), iterableWithSize(1)));
        assertThat(onNextEvents.get(0), is(instanceOf(clazz)));
        return ((T) onNextEvents.get(0));
    }

    @NonNull
    private RealmManager.RealmQueryProvider getExactTestModelRealmQuery(@NonNull TestRealmObject insertedTestRealmObject) {
        return realm -> realm.where(TestRealmObject.class)
                .beginsWith("value", TEST_MODEL_PREFIX, Case.INSENSITIVE)
                .equalTo("id", insertedTestRealmObject.getId())
                .equalTo("value", insertedTestRealmObject.getValue(), Case.INSENSITIVE);
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
        DatabaseManagerFactory.initRealm(DataUseCase.getHandlerThread().getLooper());
        return (RealmManager) DatabaseManagerFactory.getInstance();
    }

    @NonNull
    private TestRealmObject insertJsonObject() throws JSONException {
        TestRealmObject testRealmObject = createTestModelWithRandomId();
        String json = new Gson().toJson(testRealmObject);
        JSONObject jsonObject = new JSONObject(json);
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mRealmManager.put(jsonObject, "id", TestRealmObject.class)
                .subscribe(subscriber);
        subscriber.assertNoErrors();
        return testRealmObject;
    }

    private TestRealmObject putTestModel(@NonNull DataBaseManager dataBaseManager) {
        return putTestModel(dataBaseManager, createTestModelWithRandomId());
    }

    private TestRealmObject putTestModel(@NonNull DataBaseManager dataBaseManager, TestRealmObject realmModel) {
        Observable<?> observable
                = dataBaseManager.put(realmModel, RealmManagerImplTestJUnit.class);
        TestSubscriber<Object> subscriber = TestSubscriber.create();
        observable.subscribe(subscriber);
        TestUtility.printThrowables(subscriber.getOnErrorEvents());
        TestUtility.assertNoErrors(subscriber);
        return realmModel;
    }

    @NonNull
    private TestRealmObject createTestModelWithRandomId() {
        final int randomInt = getRandomInt();
        return new TestRealmObject(randomInt, getStringValueForTestModel(randomInt));
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
