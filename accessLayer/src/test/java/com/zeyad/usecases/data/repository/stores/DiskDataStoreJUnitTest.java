package com.zeyad.usecases.data.repository.stores;

import android.content.Context;
import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.db.DatabaseManagerFactory;
import com.zeyad.usecases.data.db.RealmManager;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.utils.TestUtility2;
import com.zeyad.usecases.utils.TestViewModel;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import rx.observers.TestSubscriber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

// FIXME: 2/10/17 Redo!
@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(constants = BuildConfig.class, sdk = 21)
public class DiskDataStoreJUnitTest {

    private DiskDataStoreJUnitRobotInterface mDiskDataStoreRobot;
    private DataStore mDiskDataStore;

    @Before
    public void setUp() throws Exception {
        Config.init(mock(Context.class));
        TestUtility2.performInitialSetupOfDb();
        DatabaseManagerFactory.initRealm();
        final DataBaseManager dbManager = mock(DataBaseManager.class);
        mDiskDataStoreRobot = DiskDataStoreJUnitRobot.newInstance(dbManager, mock(IDAOMapper.class));
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
        TestUtility2.assertNoErrors(subscriber);
    }

    @Test
    public void testGetAllCache_ifNoErrorIsThrown_whenGetMethodIsCalled() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber<List> subscriber = new TestSubscriber<>();
        mDiskDataStore.dynamicGetList(null, null, mDiskDataStoreRobot.getDataClass(), false, true)
                .subscribe(subscriber);
        TestUtility2.assertNoErrors(subscriber);
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
        TestUtility2.assertNoErrors(subscriber);
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
        TestUtility2.assertNoErrors(subscriber);
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

    @Test
    public void testSearchDiskRealmQuery_ifNoErrorIsThrown_whenDiskIsSearchedUsingValue() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        RealmManager.RealmQueryProvider realmQuery = mDiskDataStoreRobot
                .getRealmQueryForValue(mDiskDataStoreRobot.getPrefixForTestModel());
        mDiskDataStore.queryDisk(realmQuery, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        TestUtility2.assertNoErrors(subscriber);
    }

    @Test
    public void testSearchDiskRealmQuery_ifCorrectNumberOfItemsAreReturned_whenDiskIsSearchedUsingValue() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        RealmManager.RealmQueryProvider realmQuery = mDiskDataStoreRobot
                .getRealmQueryForValue(mDiskDataStoreRobot.getPrefixForTestModel());
        mDiskDataStore.queryDisk(realmQuery, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        assertThat(((List<Object>) subscriber.getOnNextEvents().get(0)), is(iterableWithSize(10)));
    }

    @Test
    public void testSearchDiskRealmQuery_ifCorrectItemsTypeIsReturned_whenDiskIsSearchedUsingValue() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        RealmManager.RealmQueryProvider realmQuery
                = mDiskDataStoreRobot
                .getRealmQueryForValue(mDiskDataStoreRobot.getPrefixForTestModel());
        mDiskDataStore.queryDisk(realmQuery, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        assertThat(((List<Object>) subscriber.getOnNextEvents().get(0)).get(0), is(instanceOf(mDiskDataStoreRobot.getDomainClass())));
    }

    @Test
    public void testSearchDiskRealmQuery_ifNoErrorIsThrown_whenDiskIsSearchedUsingId() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        RealmManager.RealmQueryProvider realmQuery = mDiskDataStoreRobot.getRealmQueryForAnyId();
        mDiskDataStore.queryDisk(realmQuery, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        TestUtility2.assertNoErrors(subscriber);
    }

    @Test
    public void testSearchDiskRealmQuery_ifCorrectNumberOfItemsAreReturned_whenDiskIsSearchedUsingId() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        RealmManager.RealmQueryProvider realmQuery = mDiskDataStoreRobot.getRealmQueryForAnyId();
        mDiskDataStore.queryDisk(realmQuery, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        assertThat(((List<Object>) subscriber.getOnNextEvents().get(0)), is(iterableWithSize(1)));
    }

    @Test
    public void testSearchDiskRealmQuery_ifCorrectItemsTypeIsReturned_whenDiskIsSearchedUsingId() {
        mDiskDataStoreRobot.insertTestModels(10);
        final TestSubscriber subscriber = new TestSubscriber<>();
        RealmManager.RealmQueryProvider realmQuery = mDiskDataStoreRobot
                .getRealmQueryForAnyId();
        mDiskDataStore.queryDisk(realmQuery, mDiskDataStoreRobot.getDataClass())
                .subscribe(subscriber);
        assertThat(((List<Object>) subscriber.getOnNextEvents().get(0)).get(0), is(instanceOf(mDiskDataStoreRobot.getDomainClass())));
    }

    @Test
    public void testDynamicDeleteCollection_ifNoErrorIsThrown_whenListContainingIdsIsSendToDelete() {
        final TestSubscriber<Object> subscriber = mDiskDataStoreRobot.deleteAllExceptOneAfterAddingSome(mDiskDataStore);
        TestUtility2.assertNoErrors(subscriber);
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
        TestUtility2.assertNoErrors(subscriber);
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
        TestUtility2.assertNoErrors(subscriber);
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
        TestUtility2.assertNoErrors(subscriber);
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
        TestUtility2.assertNoErrors(subscriber);
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