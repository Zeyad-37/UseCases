package com.zeyad.usecases.data.repository.stores;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.TestUtility;
import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.db.DatabaseManagerFactory;
import com.zeyad.usecases.data.services.realm_test_models.TestModelViewModelMapper;
import com.zeyad.usecases.data.services.realm_test_models.TestViewModel;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import io.realm.RealmQuery;
import rx.observers.TestSubscriber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class)
public class DiskDataStoreTest {

    @NonNull
    @Rule
    public Timeout mTimeout = new Timeout(TestUtility.TIMEOUT_TIME_VALUE, TestUtility.TIMEOUT_TIME_UNIT);
    @NonNull
    @Rule
    public ExpectedException mExpectedException = ExpectedException.none();
    private DiskDataStoreRobotInterface mDiskDataStoreRobot;
    private DataStore mDiskDataStore;

    @Before
    public void setUp() throws Exception {
        Config.init(InstrumentationRegistry.getTargetContext());
        TestUtility.performInitialSetupOfDb();
        DatabaseManagerFactory.initRealm();
        final DataBaseManager dbManager = DatabaseManagerFactory.getInstance();
        final TestModelViewModelMapper enitityMapper = new TestModelViewModelMapper();
        mDiskDataStoreRobot = DiskDataStoreRobot.newInstance(dbManager, enitityMapper);
        mDiskDataStore = mDiskDataStoreRobot.createDiskDataStore();
    }

    @After
    public void tearDown() throws Exception {
        if (mDiskDataStoreRobot != null) {
            mDiskDataStoreRobot.tearDown();
        }
    }

    @Test
    public void testGetAll_ifNoErrorIsThrown_whenGetMethodIsCalled() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber<List> subscriber = new TestSubscriber<>();
        mDiskDataStore.dynamicGetList(null, null, mDiskDataStoreRobot.getDataClass(), false, false)
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testGetAllCache_ifNoErrorIsThrown_whenGetMethodIsCalled() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber<List> subscriber = new TestSubscriber<>();
        mDiskDataStore.dynamicGetList(null, null, mDiskDataStoreRobot.getDataClass(), false, true)
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testGetAll_ifCorrectNumberOfItemsAreReturned_whenGetMethodIsCalled() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber<List> subscriber = new TestSubscriber<>();
        mDiskDataStore.dynamicGetList(null, null, mDiskDataStoreRobot.getDataClass(), false, false)
                .subscribe(subscriber);
        final List<Object> list = subscriber.getOnNextEvents().get(0);
        assertThat(list, is(Matchers.iterableWithSize(10)));
    }

    @Test
    public void testGetAllCache_ifCorrectNumberOfItemsAreReturned_whenGetMethodIsCalled() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber<List> subscriber = new TestSubscriber<>();
        mDiskDataStore.dynamicGetList(null, null, mDiskDataStoreRobot.getDataClass(), false, true)
                .subscribe(subscriber);
        final List<Object> list = subscriber.getOnNextEvents().get(0);
        assertThat(list, is(Matchers.iterableWithSize(10)));
    }

    @Test
    public void testGetObject_ifNoErrorIsThrown_whenIdColumnIsUsed() {
        mDiskDataStoreRobot.insertTestModels(10);
        int testModelId = mDiskDataStoreRobot.getPrimaryIdForAnyInsertedTestModel();
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mDiskDataStore
                .dynamicGetObject(null, "id", testModelId, null, mDiskDataStoreRobot.getDataClass(), false, false)
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testGetObject_ifSubscriberReceivesEvent_whenIdColumnIsUsed() {
        mDiskDataStoreRobot.insertTestModels(10);
        int testModelId = mDiskDataStoreRobot.getPrimaryIdForAnyInsertedTestModel();
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mDiskDataStore
                .dynamicGetObject(null, "id", testModelId, mDiskDataStoreRobot.getDomainClass(), mDiskDataStoreRobot.getDataClass(), false, false)
                .subscribe(subscriber);
        assertThat("id searched for:" + testModelId, subscriber.getOnNextEvents(), allOf(notNullValue(), iterableWithSize(1)));
    }

    @Test
    public void testGetObject_ifTestModelIsReturned_whenIdColumnIsUsed() {
        mDiskDataStoreRobot.insertTestModels(10);
        int testModelId = mDiskDataStoreRobot.getPrimaryIdForAnyInsertedTestModel();
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mDiskDataStore
                .dynamicGetObject(null, "id", testModelId, null, mDiskDataStoreRobot.getDataClass(), false, false)
                .subscribe(subscriber);
        assertThat(subscriber.getOnNextEvents().get(0), allOf(notNullValue(), instanceOf(mDiskDataStoreRobot.getDomainClass())));
    }

    @Test
    public void testGetObject_ifTestModelHasIdAsExpected_whenIdColumnIsUsed() {
        mDiskDataStoreRobot.insertTestModels(10);
        int testModelId = mDiskDataStoreRobot.getPrimaryIdForAnyInsertedTestModel();
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mDiskDataStore
                .dynamicGetObject(null, "id", testModelId, null, mDiskDataStoreRobot.getDataClass(), false, false)
                .subscribe(subscriber);
        assertThat(((TestViewModel) subscriber.getOnNextEvents().get(0)).getTestInfo(), is(equalTo(mDiskDataStoreRobot.getTestInfo(testModelId))));
    }

    @Test
    public void testGetObjectCache_ifNoErrorIsThrown_whenIdColumnIsUsed() {
        mDiskDataStoreRobot.insertTestModels(10);
        int testModelId = mDiskDataStoreRobot.getPrimaryIdForAnyInsertedTestModel();
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mDiskDataStore
                .dynamicGetObject(null, "id", testModelId, null, mDiskDataStoreRobot.getDataClass(), false, true)
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testGetObjectCache_ifSubscriberReceivesEvent_whenIdColumnIsUsed() {
        mDiskDataStoreRobot.insertTestModels(10);
        int testModelId = mDiskDataStoreRobot.getPrimaryIdForAnyInsertedTestModel();
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mDiskDataStore
                .dynamicGetObject(null, "id", testModelId, null, mDiskDataStoreRobot.getDataClass(), false, true)
                .subscribe(subscriber);
        assertThat("id searched for:" + testModelId, subscriber.getOnNextEvents(), allOf(notNullValue(), iterableWithSize(1)));
    }

    @Test
    public void testGetObjectCache_ifTestModelIsReturned_whenIdColumnIsUsed() {
        mDiskDataStoreRobot.insertTestModels(10);
        int testModelId = mDiskDataStoreRobot.getPrimaryIdForAnyInsertedTestModel();
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mDiskDataStore
                .dynamicGetObject(null, "id", testModelId, null, mDiskDataStoreRobot.getDataClass(), false, true)
                .subscribe(subscriber);
        assertThat(subscriber.getOnNextEvents().get(0), allOf(notNullValue(), instanceOf(mDiskDataStoreRobot.getDomainClass())));
    }

    @Test
    public void testGetObjectCache_ifTestModelHasIdAsExpected_whenIdColumnIsUsed() {
        mDiskDataStoreRobot.insertTestModels(10);
        int testModelId = mDiskDataStoreRobot.getPrimaryIdForAnyInsertedTestModel();
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mDiskDataStore
                .dynamicGetObject(null, "id", testModelId, null, mDiskDataStoreRobot.getDataClass(), false, true)
                .subscribe(subscriber);
        assertThat(((TestViewModel) subscriber.getOnNextEvents().get(0)).getTestInfo(), is(equalTo(mDiskDataStoreRobot.getTestInfo(testModelId))));
    }

    @Test(expected = java.lang.AssertionError.class)
    public void testSearchDisk_ifNoErrorIsThrown_whenDiskIsSearchedUsingId() {
        mDiskDataStoreRobot.insertTestModels(10);
        int idToLookFor = mDiskDataStoreRobot.getPrimaryIdForAnyInsertedTestModel();
        final TestSubscriber subscriber = new TestSubscriber<>();
        mDiskDataStore.searchDisk(String.valueOf(idToLookFor), "id", null, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testSearchDisk_ifNoErrorIsThrown_whenDiskIsSearchedUsingValue() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        mDiskDataStore.searchDisk(mDiskDataStoreRobot.getPrefixForTestModel(), "value", null, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testSearchDisk_ifCorrectNumberOfItemsAreReturned_whenDiskIsSearchedUsingValue() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        mDiskDataStore.searchDisk(mDiskDataStoreRobot.getPrefixForTestModel(), "value", null, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        assertThat(((List<Object>) subscriber.getOnNextEvents().get(0)), is(iterableWithSize(10)));
    }

    @Test
    public void testSearchDisk_ifCorrectItemsTypeIsReturned_whenDiskIsSearchedUsingValue() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        mDiskDataStore.searchDisk(mDiskDataStoreRobot.getPrefixForTestModel(), "value", null, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        assertThat(((List<Object>) subscriber.getOnNextEvents().get(0)).get(0), is(instanceOf(mDiskDataStoreRobot.getDomainClass())));
    }

    @Test
    public void testSearchDiskRealmQuery_ifNoErrorIsThrown_whenDiskIsSearchedUsingValue() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        RealmQuery realmQuery
                = mDiskDataStoreRobot
                .getRealmQueryForValue(mDiskDataStoreRobot.getPrefixForTestModel());
        mDiskDataStore.searchDisk(realmQuery, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testSearchDiskRealmQuery_ifCorrectNumberOfItemsAreReturned_whenDiskIsSearchedUsingValue() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        RealmQuery realmQuery
                = mDiskDataStoreRobot
                .getRealmQueryForValue(mDiskDataStoreRobot.getPrefixForTestModel());
        mDiskDataStore.searchDisk(realmQuery, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        assertThat(((List<Object>) subscriber.getOnNextEvents().get(0)), is(iterableWithSize(10)));
    }

    @Test
    public void testSearchDiskRealmQuery_ifCorrectItemsTypeIsReturned_whenDiskIsSearchedUsingValue() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        RealmQuery realmQuery
                = mDiskDataStoreRobot
                .getRealmQueryForValue(mDiskDataStoreRobot.getPrefixForTestModel());
        mDiskDataStore.searchDisk(realmQuery, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        assertThat(((List<Object>) subscriber.getOnNextEvents().get(0)).get(0), is(instanceOf(mDiskDataStoreRobot.getDomainClass())));
    }

    @Test
    public void testSearchDiskRealmQuery_ifNoErrorIsThrown_whenDiskIsSearchedUsingId() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        RealmQuery realmQuery
                = mDiskDataStoreRobot
                .getRealmQueryForAnyId();
        mDiskDataStore.searchDisk(realmQuery, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testSearchDiskRealmQuery_ifCorrectNumberOfItemsAreReturned_whenDiskIsSearchedUsingId() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        RealmQuery realmQuery
                = mDiskDataStoreRobot
                .getRealmQueryForAnyId();
        mDiskDataStore.searchDisk(realmQuery, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        assertThat(((List<Object>) subscriber.getOnNextEvents().get(0)), is(iterableWithSize(1)));
    }

    @Test
    public void testSearchDiskRealmQuery_ifCorrectItemsTypeIsReturned_whenDiskIsSearchedUsingId() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        RealmQuery realmQuery
                = mDiskDataStoreRobot
                .getRealmQueryForAnyId();
        mDiskDataStore.searchDisk(realmQuery, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        assertThat(((List<Object>) subscriber.getOnNextEvents().get(0)).get(0), is(instanceOf(mDiskDataStoreRobot.getDomainClass())));
    }

    @Test
    public void testDynamicDeleteCollection_ifNoErrorIsThrown_whenListContainingIdsIsSendToDelete() {
        final TestSubscriber<Object> subscriber = mDiskDataStoreRobot.deleteAllExceptOneAfterAddingSome(mDiskDataStore);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testDynamicDeleteCollection_ifTrueIsReturned_whenListContainingIdsIsSendToDelete() {
        final TestSubscriber<Object> subscriber = mDiskDataStoreRobot.deleteAllExceptOneAfterAddingSome(mDiskDataStore);
        subscriber.assertValue(Boolean.TRUE);
    }

    @Test
    public void testDynamicDeleteCollection_ifItemsAreActuallyDeleted_whenListContainingIdsIsSendToDelete() {
        mDiskDataStoreRobot.deleteAllExceptOneAfterAddingSome(mDiskDataStore);
        assertThat(mDiskDataStoreRobot.getItemCount(), is(equalTo(1)));
    }

    @Test
    public void testDeleteAll_ifNoErrorIsThrown_whenTestModelIsPassed() {
        final TestSubscriber<Object> subscriber = mDiskDataStoreRobot.deleteAllAfterAddingSome(mDiskDataStore);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testDeleteAll_ifItemsAreDeletedOrNot_whenTestModelIsPassed() {
        mDiskDataStoreRobot.deleteAllAfterAddingSome(mDiskDataStore);
        assertThat(mDiskDataStoreRobot.getItemCount(), is(equalTo(0)));
    }

    @Test
    public void testDeleteAll_ifTrueIsReturned_whenTestModelIsPassed() {
        final TestSubscriber<Object> subscriber = mDiskDataStoreRobot.deleteAllAfterAddingSome(mDiskDataStore);
        subscriber.assertValue(Boolean.TRUE);
    }

    @Test
    public void testDynamicPostObject_ifNoErrorIsThrown_whenKeyValuePairIsPosted() throws Exception {
        TestSubscriber<Object> subscriber = mDiskDataStoreRobot.postTestModelKeyValuePair(mDiskDataStore);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testDynamicPostObject_ifTrueIsReturned_whenKeyValuePairIsPosted() throws Exception {
        TestSubscriber<Object> subscriber = mDiskDataStoreRobot.postTestModelKeyValuePair(mDiskDataStore);
        subscriber.assertValue(Boolean.TRUE);
    }

    @Test
    public void testDynamicPostObject_ifOneTestModelIsInserted_whenKeyValuePairIsPosted() throws Exception {
        mDiskDataStoreRobot.postTestModelKeyValuePair(mDiskDataStore);
        assertThat(mDiskDataStoreRobot.getItemCount(), is(equalTo(1)));
    }

    @Test
    public void testDynamicPostObject_ifNoErrorIsThrown_whenJsonObjectIsPosted() throws Exception {
        TestSubscriber<Object> subscriber = mDiskDataStoreRobot.postTestModelJsonObject(mDiskDataStore);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testDynamicPostObject_ifTrueIsReturned_whenJsonObjectIsPosted() throws Exception {
        TestSubscriber<Object> subscriber = mDiskDataStoreRobot.postTestModelJsonObject(mDiskDataStore);
        subscriber.assertValue(Boolean.TRUE);
    }

    @Test
    public void testDynamicPostObject_ifOneTestModelIsInserted_whenJsonObjectIsPosted() throws Exception {
        mDiskDataStoreRobot.postTestModelJsonObject(mDiskDataStore);
        assertThat(mDiskDataStoreRobot.getItemCount(), is(equalTo(1)));
    }

    @Test
    public void testDynamicPutObject_ifNoErrorIsThrown_whenKeyValuePairIsPosted() throws Exception {
        TestSubscriber<Object> subscriber = mDiskDataStoreRobot.putTestModelKeyValuePair(mDiskDataStore);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testDynamicPutObject_ifTrueIsReturned_whenKeyValuePairIsPosted() throws Exception {
        TestSubscriber<Object> subscriber = mDiskDataStoreRobot.putTestModelKeyValuePair(mDiskDataStore);
        subscriber.assertValue(Boolean.TRUE);
    }

    @Test
    public void testDynamicPutObject_ifOneTestModelIsInserted_whenKeyValuePairIsPosted() throws Exception {
        mDiskDataStoreRobot.putTestModelKeyValuePair(mDiskDataStore);
        assertThat(mDiskDataStoreRobot.getItemCount(), is(equalTo(1)));
    }

    @Test
    public void testDynamicDownloadFile_ifErrorIsThrown() throws Exception {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        mDiskDataStoreRobot.dynamicDownloadFile(mDiskDataStore)
                .subscribe(subscriber);
        subscriber.assertError(IllegalStateException.class);
    }

}