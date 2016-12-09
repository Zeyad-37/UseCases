package com.zeyad.usecases.data.repository.stores;

import android.support.annotation.NonNull;
import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.exceptions.NetworkConnectionException;
import com.zeyad.usecases.data.mappers.EntityMapper;
import com.zeyad.usecases.data.network.RestApi;
import com.zeyad.usecases.utils.TestModel;
import com.zeyad.usecases.utils.TestUtility2;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmObject;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.observers.TestSubscriber;

import static com.zeyad.usecases.data.repository.stores.CloudDataStoreTestJUnitRobot.argThis;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Realm.class})
public class CloudDataStoreJUnitTest {

    private final boolean mToPersist;
    private final boolean mToCache;
    private final boolean mCallRealMethodsOfEntityMapper;
    @NonNull
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @NonNull
    @Rule
    public Timeout mTimeout = new Timeout(TestUtility2.TIMEOUT_TIME_VALUE, TestUtility2.TIMEOUT_TIME_UNIT);
    private CloudDataStore mCloudDataStore;
    private RestApi mMockedRestApi;
    private DataBaseManager mMockedDBManager;
    private EntityMapper<Object, Object> mEntityMapper;

    public CloudDataStoreJUnitTest(boolean callRealMethodsOfEntityMapper, boolean toPersist, boolean toCache) {
        mCallRealMethodsOfEntityMapper = callRealMethodsOfEntityMapper;
        mToPersist = toPersist;
        mToCache = toCache;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[]{true, true, false}
                , new Object[]{true, true, true}
                , new Object[]{true, false, false}
                , new Object[]{true, false, true}
                , new Object[]{false, true, false}
                , new Object[]{false, true, true}
                , new Object[]{false, false, false}
                , new Object[]{false, false, true}
        );
    }

    @Before
    public void setUp() throws Exception {
//        TestUtility2.performInitialSetupOfConfig(Mockito.mock(Context.class));
        mMockedRestApi = CloudDataStoreTestJUnitRobot.createMockedRestApi(mToCache);
        mMockedDBManager = CloudDataStoreTestJUnitRobot.createDBManagerWithMockedContext();
        if (mCallRealMethodsOfEntityMapper) {
            mEntityMapper = CloudDataStoreTestJUnitRobot.createMockedEntityMapperWithActualMethodCalls();
        } else
            mEntityMapper = CloudDataStoreTestJUnitRobot.createMockedEntityMapper();
        mCloudDataStore = new CloudDataStore(mMockedRestApi, mMockedDBManager, mEntityMapper);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDynamicGetList_ifRestApiGetListIsCalled_whenMethodIsCalled() {
        CloudDataStoreTestJUnitRobot.dynamicGetList(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedRestApi, times(1)).dynamicGetList(CloudDataStoreTestJUnitRobot.getValidUrl(), false);
    }

    @Test
    public void testDynamicGetList_ifRealmManagerPutAllIsCalledDependingOnPersistStatus_whenArgumentsArePassed() {
        CloudDataStoreTestJUnitRobot.dynamicGetList(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0))
                .putAll(Mockito.anyListOf(RealmObject.class)
                        , Mockito.any());
    }

    @Test
    public void testDynamicGetList_ifEntitiesAreMapped_whenResponseIsReceived() {
        CloudDataStoreTestJUnitRobot.dynamicGetList(mCloudDataStore, mToPersist);
        InOrder inorder = Mockito.inOrder(mEntityMapper, mEntityMapper);
        inorder.verify(mEntityMapper, times(mToPersist ? 1 : 0))
                .transformAllToRealm(Mockito.anyList(), Mockito.any());
        inorder.verify(mEntityMapper, times(1))
                .transformAllToDomain(Mockito.any()
                        , Mockito.any());
    }

    @Test
    public void testDynamicGetList_ifRestApiGetListIsCalled_whenMethodIsCalledCacheVersion() {
        CloudDataStoreTestJUnitRobot.dynamicGetList(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mMockedRestApi, times(1)).dynamicGetList(CloudDataStoreTestJUnitRobot.getValidUrl(), mToCache);
    }

    @Test
    public void testDynamicGetList_ifRealmManagerPutAllIsCalledDependingOnPersistStatus_whenArgumentsArePassedCacheVersion() {
        CloudDataStoreTestJUnitRobot.dynamicGetList(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0))
                .putAll(Mockito.anyListOf(RealmObject.class)
                        , Mockito.any());
    }

    @Test
    public void testDynamicGetList_ifEntitiesAreMapped_whenResponseIsReceivedCacheVersion() {
        CloudDataStoreTestJUnitRobot.dynamicGetList(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mEntityMapper, times(1))
                .transformAllToDomain(Mockito.any()
                        , Mockito.any());
    }

    @Test
    public void testDynamicGetList_ifDataClassIsSameAsSend_whenArgumentsArePassed() {
        CloudDataStoreTestJUnitRobot.dynamicGetList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(mToPersist ? 1 : 0)).transformAllToRealm(Mockito.anyList(), eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicGetList_ifDataClassIsSameAsSent_whenArgumentsArePassedCacheVersion() {
        CloudDataStoreTestJUnitRobot.dynamicGetList(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mEntityMapper, times(mToPersist ? 1 : 0)).transformAllToRealm(Mockito.anyList(), eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicGetList_ifDomainClassIsSameAsSend_whenArgumentsArePassed() {
        CloudDataStoreTestJUnitRobot.dynamicGetList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(1)).transformAllToDomain(Mockito.anyList(), eq(CloudDataStoreTestJUnitRobot.getValidDomainClass()));
    }

    @Test
    public void testDynamicGetList_ifDomainClassIsSameAsSent_whenArgumentsArePassedCacheVersion() {
        CloudDataStoreTestJUnitRobot.dynamicGetList(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mEntityMapper, times(1)).transformAllToDomain(Mockito.anyList(), eq(CloudDataStoreTestJUnitRobot.getValidDomainClass()));
    }

    @Test
    public void testDynamicGetList_ifNoErrorObservableIsReturned_whenArgumentsArePassed() {
        TestSubscriber<List> subscriber = CloudDataStoreTestJUnitRobot.dynamicGetList(mCloudDataStore, mToPersist);
        TestUtility2.assertNoErrors(subscriber);
    }

    @Test
    public void testDynamicGetList_ifNoErrorObservableIsReturned_whenArgumentsArePassedCacheVersion() {
        TestSubscriber<List> subscriber = CloudDataStoreTestJUnitRobot.dynamicGetList(mCloudDataStore, mToPersist, mToCache);
        TestUtility2.assertNoErrors(subscriber);
    }

    @Test
    public void testDynamicGetObject_ifDataClassIsSameAsSet_whenAreArgumentsArePassed() {
        CloudDataStoreTestJUnitRobot.dynamicGetObject(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(mToPersist ? 1 : 0)).transformToRealm(Mockito.anyObject(), eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicGetObject_ifIdColumnNameIsSameAsSet_whenAreArgumentsArePassed() {
        CloudDataStoreTestJUnitRobot.dynamicGetObject(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedDBManager
                , times(!mCallRealMethodsOfEntityMapper && mToPersist ? 1 : 0)).put(Mockito.any(), eq(CloudDataStoreTestJUnitRobot.getValidColumnName()), Mockito.any());
    }

    @Test
    public void testDynamicGetObject_ifDataClassIsSameAsSet_whenAreArgumentsArePassedCacheVersion() {
        CloudDataStoreTestJUnitRobot.dynamicGetObject(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mEntityMapper, times(mToPersist ? 1 : 0)).transformToRealm(Mockito.anyObject(), eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicGetObject_ifIdColumnNameIsSameAsSet_whenAreArgumentsArePassedCacheVersion() {
        CloudDataStoreTestJUnitRobot.dynamicGetObject(mCloudDataStore, mToPersist, mToCache);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist & !mCallRealMethodsOfEntityMapper ? 1 : 0));
        verify.put(Mockito.any(), eq(CloudDataStoreTestJUnitRobot.getValidColumnName()), Mockito.any());
    }

    @Test
    public void testDynamicGetObject_ifRestApiGetObjectIsCalled_whenMethodIsCalled() {
        CloudDataStoreTestJUnitRobot.dynamicGetObject(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedRestApi, times(1)).dynamicGetObject(CloudDataStoreTestJUnitRobot.getValidUrl(), false);
    }

    @Test
    public void testDynamicGetObject_ifRestApiGetObjectIsCalled_whenMethodIsCalledCacheVersion() {
        CloudDataStoreTestJUnitRobot.dynamicGetObject(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mMockedRestApi, times(1)).dynamicGetObject(CloudDataStoreTestJUnitRobot.getValidUrl(), mToCache);
    }

    @Test
    public void testDynamicGetObject_ifRealmManagerPutIsCalledDependingOnPersistState_whenObserverReturnsRealmObject() {
        CloudDataStoreTestJUnitRobot.dynamicGetObject(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (mCallRealMethodsOfEntityMapper) {
            verify.put(Mockito.isA(RealmObject.class), Mockito.any());
        } else {
            verify.put(argThis(CloudDataStoreTestJUnitRobot.createTestModelJSON())
                    , argThis(CloudDataStoreTestJUnitRobot.getValidColumnName())
                    , argThis(CloudDataStoreTestJUnitRobot.getValidDataClass()));
        }
    }

    @Test
    public void testDynamicGetObject_ifRealmManagerPutIsCalledDependingOnPersistState_whenObserverReturnsRealmObjectCacheVersion() {
        CloudDataStoreTestJUnitRobot.dynamicGetObject(mCloudDataStore, mToPersist, mToCache);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (mCallRealMethodsOfEntityMapper) {
            verify.put(Mockito.isA(RealmObject.class), Mockito.any());
        } else {
            verify.put(argThis(CloudDataStoreTestJUnitRobot.createTestModelJSON())
                    , argThis(CloudDataStoreTestJUnitRobot.getValidColumnName())
                    , argThis(CloudDataStoreTestJUnitRobot.getValidDataClass()));
        }
    }

    @Test
    public void testDynamicGetObject_ifEntitiesAreMapped_whenResponseIsReceived() {
        CloudDataStoreTestJUnitRobot.dynamicGetObject(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(1))
                .transformToDomain(Mockito.any()
                        , Mockito.any());
    }

    @Test
    public void testDynamicGetObject_ifEntitiesAreMapped_whenResponseIsReceivedCacheVersion() {
        CloudDataStoreTestJUnitRobot.dynamicGetObject(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mEntityMapper, times(1))
                .transformToDomain(Mockito.any()
                        , Mockito.any());
    }

    @Test
    public void testDynamicPostJsonObject_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonObject_ifDataIsNotPersistedIrrespectiveOfPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedDBManager, times(0))
                .put(Mockito.any(), Mockito.any(), Mockito.any());

    }

    @Test
    public void testDynamicPostJsonObject_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonObject_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (mCallRealMethodsOfEntityMapper) {
            verify.put(Mockito.isA(RealmObject.class), Mockito.any());
        } else {
            verify.put(Mockito.any(), Mockito.any(), Mockito.any());
        }
    }

    @Test
    public void testDynamicPostJsonObject_ifEntityMapperIsInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(mToPersist ? 1 : 0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPostJsonObject_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonObject_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (mCallRealMethodsOfEntityMapper) {
            verify.put(Mockito.isA(RealmObject.class), Mockito.any());
        } else {
            verify.put(Mockito.any(JSONObject.class), Mockito.anyString(), Mockito.any());
        }
    }

    @Test
    public void testDynamicPostJsonObject_ifEntityMapperIsInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(mToPersist ? 1 : 0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPostJsonObject_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonObject_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (mCallRealMethodsOfEntityMapper) {
            verify.put(Mockito.isA(RealmObject.class), Mockito.any());
        } else {
            verify.put(Mockito.any(), Mockito.any(), Mockito.any());
        }
    }

    @Test
    public void testDynamicPostJsonObject_ifEntityMapperIsInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(mToPersist ? 1 : 0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPostJsonObject_ifErrorObservableIsNotReturned_whenNetworkIsAvailableAndSDKHasLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicPostJsonObject_ifErrorObservableIsNotReturned_whenNetworkIsAvailable() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicPostJsonObject_ifCorrectPutMethodOfRealmManagerIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedRestApi, times(1))
                .dynamicPostObject(Mockito.anyString(), Mockito.any(RequestBody.class));
    }

    @Test
    public void testDynamicPostJsonObject_ifCorrectEntityMapperMethodIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(1))
                .transformToDomain(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testDynamicPostJsonObject_ifNoErrorIsThrown_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist)
                .assertNoErrors();
    }

    @Test
    public void testDynamicPostJsonArray_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<List> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonArray_ifDataIsNotPersistedIrrespectiveOfPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedDBManager, times(0)).put(Mockito.any(), Mockito.any(), Mockito.any());

    }

    @Test
    public void testDynamicPostJsonArray_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<List> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonArray_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        verify.putAll(Mockito.any(JSONArray.class), eq(CloudDataStoreTestJUnitRobot.getValidColumnName()), eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicPostJsonArray_ifEntityMapperIsNotInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPostJsonArray_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<List> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonArray_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        verify.putAll(Mockito.any(JSONArray.class), eq(CloudDataStoreTestJUnitRobot.getValidColumnName()), eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicPostJsonArray_ifEntityMapperIsNotInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPostJsonArray_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<List> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonArray_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        verify.putAll(Mockito.any(JSONArray.class), eq(CloudDataStoreTestJUnitRobot.getValidColumnName()), eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicPostJsonArray_ifEntityMapperIsNotInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPostJsonArray_ifErrorObservableIsNotReturned_whenNetworkIsAvailableAndSDKHasLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        TestSubscriber<List> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicPostJsonArray_ifErrorObservableIsNotReturned_whenNetworkIsAvailable() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<List> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        TestUtility2.assertNoErrors(testSubscriber);
    }

    @Test
    public void testDynamicPostJsonArray_ifCorrectPutMethodOfRealmManagerIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedRestApi, times(1))
                .dynamicPostList(eq(CloudDataStoreTestJUnitRobot.getValidUrl()), Mockito.any(RequestBody.class));
    }

    @Test
    public void testDynamicPostJsonArray_ifCorrectEntityMapperMethodIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(1))
                .transformAllToDomain(Mockito.any(), eq(CloudDataStoreTestJUnitRobot.getValidDomainClass()));
    }

    @Test
    public void testDynamicPostJsonArray_ifNoErrorIsThrown_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestUtility2.assertNoErrors(CloudDataStoreTestJUnitRobot.dynamicPostList(mCloudDataStore, mToPersist));
    }

    //
    @Test
    public void testDynamicPutHashMap_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutHashMap_ifDataIsNotPersistedIrrespectiveOfPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedDBManager, times(0))
                .put(Mockito.any(), Mockito.any(), Mockito.any());

    }

    @Test
    public void testDynamicPutHashMap_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutHashMap_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (mCallRealMethodsOfEntityMapper) {
            verify.put(Mockito.any(TestModel.class), Mockito.eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
        } else {
            verify.put(Mockito.any(JSONObject.class), Mockito.eq("id"), Mockito.eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
        }
    }

    @Test
    public void testDynamicPutHashMap_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutHashMap_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (mCallRealMethodsOfEntityMapper) {
            verify.put(Mockito.any(TestModel.class), Mockito.eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
        } else {
            verify.put(Mockito.any(JSONObject.class), Mockito.eq("id"), Mockito.eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
        }
    }

    @Test
    public void testDynamicPutHashMap_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutHashMap_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (!mCallRealMethodsOfEntityMapper && mToPersist) {
            verify.put(Mockito.any(JSONObject.class)
                    , Mockito.eq(CloudDataStoreTestJUnitRobot.getValidColumnName())
                    , Mockito.eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
        } else {
            verify.put(Mockito.any(TestModel.class)
                    , Mockito.eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
        }
    }

    @Test
    public void testDynamicPutHashMap_ifErrorObservableIsNotReturned_whenNetworkIsAvailableAndSDKHasLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicPutHashMap_ifErrorObservableIsNotReturned_whenNetworkIsAvailable() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicPutHashMap_ifCorrectPutMethodOfRealmManagerIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedRestApi, times(1))
                .dynamicPutObject(eq(CloudDataStoreTestJUnitRobot.getValidUrl()),
                        Mockito.any(RequestBody.class));
    }

    @Test
    public void testDynamicPutHashMap_ifCorrectEntityMapperMethodIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(1))
                .transformToDomain(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testDynamicPutHashMap_ifNoErrorIsThrown_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist)
                .assertNoErrors();
    }

    //
    @Test
    public void testDynamicPutList_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<List> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutList_ifDataIsNotPersistedIrrespectiveOfPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedDBManager, times(0))
                .put(Mockito.any(), Mockito.any(), Mockito.any());

    }

    @Test
    public void testDynamicPutList_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<List> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutList_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        verify.putAll(Mockito.any(JSONArray.class), eq(CloudDataStoreTestJUnitRobot.getValidColumnName()), eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicPutList_ifEntityMapperIsNotInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPutList_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<List> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutList_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        verify.putAll(Mockito.any(JSONArray.class), eq(CloudDataStoreTestJUnitRobot.getValidColumnName()), eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicPutList_ifEntityMapperIsNotInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPutList_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<List> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutList_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        verify.putAll(Mockito.any(JSONArray.class), eq(CloudDataStoreTestJUnitRobot.getValidColumnName()), eq(CloudDataStoreTestJUnitRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicPutList_ifEntityMapperIsNotInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPutList_ifErrorObservableIsNotReturned_whenNetworkIsAvailableAndSDKHasLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        TestSubscriber<List> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicPutList_ifErrorObservableIsNotReturned_whenNetworkIsAvailable() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<List> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        TestUtility2.assertNoErrors(testSubscriber);
    }

    @Test
    public void testDynamicPutList_ifCorrectPutMethodOfRealmManagerIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedRestApi, times(1))
                .dynamicPutList(eq(CloudDataStoreTestJUnitRobot.getValidUrl()), Mockito.any(RequestBody.class));
    }

    @Test
    public void testDynamicPutList_ifCorrectEntityMapperMethodIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(1))
                .transformAllToDomain(Mockito.any(), eq(CloudDataStoreTestJUnitRobot.getValidDomainClass()));
    }

    @Test
    public void testDynamicPutList_ifNoErrorIsThrown_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestUtility2.assertNoErrors(CloudDataStoreTestJUnitRobot.dynamicPutList(mCloudDataStore, mToPersist));
    }

    //upload file
    @Test
    public void testDynamicUploadFile_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicUploadFile_ifDataIsNotPersistedIrrespectiveOfPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedDBManager, times(0))
                .put(Mockito.any(), Mockito.any(), Mockito.any());

    }

    @Test
    public void testDynamicUploadFile_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicUploadFile_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicUploadFile_ifDataIsNotPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        Mockito.verifyNoMoreInteractions(mMockedDBManager);
    }

    @Test
    public void testDynamicUploadFile_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicUploadFile_ifDataIsNotPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(false);
        assumeFalse(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        Mockito.verifyNoMoreInteractions(mMockedDBManager);
    }

    @Test
    public void testDynamicUploadFile_ifErrorObservableIsNotReturned_whenNetworkIsAvailableAndSDKHasLollipop() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicUploadFile_ifErrorObservableIsNotReturned_whenNetworkIsAvailable() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestJUnitRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicUploadFile_ifCorrectPutMethodOfRealmManagerIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedRestApi, times(1))
                .upload(Mockito.anyString(), Mockito.any(Map.class), Mockito.any(MultipartBody.Part.class));
    }

    @Test
    public void testDynamicUploadFile_ifCorrectEntityMapperMethodIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(1))
                .transformToDomain(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testDynamicUploadFile_ifNoErrorIsThrown_whenRequiredThingsAreMet() {
        CloudDataStoreTestJUnitRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestJUnitRobot.isNetworkEnabled());
        CloudDataStoreTestJUnitRobot.dynamicUploadFile(mCloudDataStore, mToPersist)
                .assertNoErrors();
    }
}