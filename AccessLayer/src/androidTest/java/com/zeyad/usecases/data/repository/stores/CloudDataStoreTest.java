package com.zeyad.usecases.data.repository.stores;

import android.support.annotation.NonNull;

import com.google.android.gms.common.GoogleApiAvailability;
import com.zeyad.usecases.data.TestUtility;
import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.exceptions.NetworkConnectionException;
import com.zeyad.usecases.data.mappers.EntityMapper;
import com.zeyad.usecases.data.network.RestApi;
import com.zeyad.usecases.data.services.realm_test_models.TestModel;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.realm.RealmObject;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.observers.TestSubscriber;

import static com.zeyad.usecases.data.repository.stores.CloudDataStoreTestRobot.argThis;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

@RunWith(Parameterized.class)
public class CloudDataStoreTest {

    private final boolean mToPersist;
    private final boolean mToCache;
    private final boolean mCallRealMethodsOfEntityMapper;
    @NonNull
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @NonNull
    @Rule
    public Timeout mTimeout = new Timeout(TestUtility.TIMEOUT_TIME_VALUE, TestUtility.TIMEOUT_TIME_UNIT);
    private CloudDataStore mCloudDataStore;
    private RestApi mMockedRestApi;
    private DataBaseManager mMockedDBManager;
    private EntityMapper<Object, Object> mEntityMapper;
    private GoogleApiAvailability mMockedGoogleApiAvailability;


    public CloudDataStoreTest(boolean callRealMethodsOfEntityMapper, boolean toPersist, boolean toCache) {
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
//        TestUtility.performInitialSetupOfConfig(Mockito.mock(Context.class));
        mMockedRestApi = CloudDataStoreTestRobot.createMockedRestApi(mToCache);
        mMockedDBManager = CloudDataStoreTestRobot.createDBManagerWithMockedContext();
        if (mCallRealMethodsOfEntityMapper) {
            mEntityMapper = CloudDataStoreTestRobot.createMockedEntityMapperWithActualMethodCalls();
        } else
            mEntityMapper = CloudDataStoreTestRobot.createMockedEntityMapper();
        mCloudDataStore = new CloudDataStore(mMockedRestApi, mMockedDBManager, mEntityMapper);
        mMockedGoogleApiAvailability = CloudDataStoreTestRobot.createMockedGoogleApiAvailability();
        mCloudDataStore.setGoogleApiAvailability(mMockedGoogleApiAvailability);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDynamicGetList_ifRestApiGetListIsCalled_whenMethodIsCalled() {
        CloudDataStoreTestRobot.dynamicGetList(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedRestApi, times(1)).dynamicGetList(CloudDataStoreTestRobot.getValidUrl(), false);
    }

    @Test
    public void testDynamicGetList_ifRealmManagerPutAllIsCalledDependingOnPersistStatus_whenArgumentsArePassed() {
        CloudDataStoreTestRobot.dynamicGetList(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0))
                .putAll(Mockito.anyListOf(RealmObject.class)
                        , Mockito.any());
    }

    @Test
    public void testDynamicGetList_ifEntitiesAreMapped_whenResponseIsReceived() {
        CloudDataStoreTestRobot.dynamicGetList(mCloudDataStore, mToPersist);
        InOrder inorder = Mockito.inOrder(mEntityMapper, mEntityMapper);
        inorder.verify(mEntityMapper, times(mToPersist ? 1 : 0))
                .transformAllToRealm(Mockito.anyList(), Mockito.any());
        inorder.verify(mEntityMapper, times(1))
                .transformAllToDomain(Mockito.any()
                        , Mockito.any());
    }

    @Test
    public void testDynamicGetList_ifRestApiGetListIsCalled_whenMethodIsCalledCacheVersion() {
        CloudDataStoreTestRobot.dynamicGetList(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mMockedRestApi, times(1)).dynamicGetList(CloudDataStoreTestRobot.getValidUrl(), mToCache);
    }

    @Test
    public void testDynamicGetList_ifRealmManagerPutAllIsCalledDependingOnPersistStatus_whenArgumentsArePassedCacheVersion() {
        CloudDataStoreTestRobot.dynamicGetList(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0))
                .putAll(Mockito.anyListOf(RealmObject.class)
                        , Mockito.any());
    }

    @Test
    public void testDynamicGetList_ifEntitiesAreMapped_whenResponseIsReceivedCacheVersion() {
        CloudDataStoreTestRobot.dynamicGetList(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mEntityMapper, times(1))
                .transformAllToDomain(Mockito.any()
                        , Mockito.any());
    }

    @Test
    public void testDynamicGetList_ifDataClassIsSameAsSend_whenArgumentsArePassed() {
        CloudDataStoreTestRobot.dynamicGetList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(mToPersist ? 1 : 0)).transformAllToRealm(Mockito.anyList(), eq(CloudDataStoreTestRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicGetList_ifDataClassIsSameAsSent_whenArgumentsArePassedCacheVersion() {
        CloudDataStoreTestRobot.dynamicGetList(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mEntityMapper, times(mToPersist ? 1 : 0)).transformAllToRealm(Mockito.anyList(), eq(CloudDataStoreTestRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicGetList_ifDomainClassIsSameAsSend_whenArgumentsArePassed() {
        CloudDataStoreTestRobot.dynamicGetList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(1)).transformAllToDomain(Mockito.anyList(), eq(CloudDataStoreTestRobot.getValidDomainClass()));
    }

    @Test
    public void testDynamicGetList_ifDomainClassIsSameAsSent_whenArgumentsArePassedCacheVersion() {
        CloudDataStoreTestRobot.dynamicGetList(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mEntityMapper, times(1)).transformAllToDomain(Mockito.anyList(), eq(CloudDataStoreTestRobot.getValidDomainClass()));
    }

    @Test
    public void testDynamicGetList_ifNoErrorObservableIsReturned_whenArgumentsArePassed() {
        TestSubscriber<List> subscriber = CloudDataStoreTestRobot.dynamicGetList(mCloudDataStore, mToPersist);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testDynamicGetList_ifNoErrorObservableIsReturned_whenArgumentsArePassedCacheVersion() {
        TestSubscriber<List> subscriber = CloudDataStoreTestRobot.dynamicGetList(mCloudDataStore, mToPersist, mToCache);
        TestUtility.assertNoErrors(subscriber);
    }

    @Test
    public void testDynamicGetObject_ifDataClassIsSameAsSet_whenAreArgumentsArePassed() {
        CloudDataStoreTestRobot.dynamicGetObject(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(mToPersist ? 1 : 0)).transformToRealm(Mockito.anyObject(), eq(CloudDataStoreTestRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicGetObject_ifIdColumnNameIsSameAsSet_whenAreArgumentsArePassed() {
        CloudDataStoreTestRobot.dynamicGetObject(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedDBManager
                , times(!mCallRealMethodsOfEntityMapper && mToPersist ? 1 : 0)).put(Mockito.any(), eq(CloudDataStoreTestRobot.getValidColumnName()), Mockito.any());
    }

    @Test
    public void testDynamicGetObject_ifDataClassIsSameAsSet_whenAreArgumentsArePassedCacheVersion() {
        CloudDataStoreTestRobot.dynamicGetObject(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mEntityMapper, times(mToPersist ? 1 : 0)).transformToRealm(Mockito.anyObject(), eq(CloudDataStoreTestRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicGetObject_ifIdColumnNameIsSameAsSet_whenAreArgumentsArePassedCacheVersion() {
        CloudDataStoreTestRobot.dynamicGetObject(mCloudDataStore, mToPersist, mToCache);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist & !mCallRealMethodsOfEntityMapper ? 1 : 0));
        verify.put(Mockito.any(), eq(CloudDataStoreTestRobot.getValidColumnName()), Mockito.any());
    }

    @Test
    public void testDynamicGetObject_ifRestApiGetObjectIsCalled_whenMethodIsCalled() {
        CloudDataStoreTestRobot.dynamicGetObject(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedRestApi, times(1)).dynamicGetObject(CloudDataStoreTestRobot.getValidUrl(), false);
    }

    @Test
    public void testDynamicGetObject_ifRestApiGetObjectIsCalled_whenMethodIsCalledCacheVersion() {
        CloudDataStoreTestRobot.dynamicGetObject(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mMockedRestApi, times(1)).dynamicGetObject(CloudDataStoreTestRobot.getValidUrl(), mToCache);
    }

    @Test
    public void testDynamicGetObject_ifRealmManagerPutIsCalledDependingOnPersistState_whenObserverReturnsRealmObject() {
        CloudDataStoreTestRobot.dynamicGetObject(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (mCallRealMethodsOfEntityMapper) {
            verify.put(Mockito.isA(RealmObject.class), Mockito.any());
        } else {
            verify.put(argThis(CloudDataStoreTestRobot.createTestModelJSON())
                    , argThis(CloudDataStoreTestRobot.getValidColumnName())
                    , argThis(CloudDataStoreTestRobot.getValidDataClass()));
        }
    }

    @Test
    public void testDynamicGetObject_ifRealmManagerPutIsCalledDependingOnPersistState_whenObserverReturnsRealmObjectCacheVersion() {
        CloudDataStoreTestRobot.dynamicGetObject(mCloudDataStore, mToPersist, mToCache);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (mCallRealMethodsOfEntityMapper) {
            verify.put(Mockito.isA(RealmObject.class), Mockito.any());
        } else {
            verify.put(argThis(CloudDataStoreTestRobot.createTestModelJSON())
                    , argThis(CloudDataStoreTestRobot.getValidColumnName())
                    , argThis(CloudDataStoreTestRobot.getValidDataClass()));
        }
    }

    @Test
    public void testDynamicGetObject_ifEntitiesAreMapped_whenResponseIsReceived() {
        CloudDataStoreTestRobot.dynamicGetObject(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(1))
                .transformToDomain(Mockito.any()
                        , Mockito.any());
    }

    @Test
    public void testDynamicGetObject_ifEntitiesAreMapped_whenResponseIsReceivedCacheVersion() {
        CloudDataStoreTestRobot.dynamicGetObject(mCloudDataStore, mToPersist, mToCache);
        Mockito.verify(mEntityMapper, times(1))
                .transformToDomain(Mockito.any()
                        , Mockito.any());
    }

    @Test
    public void testDynamicPostJsonObject_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonObject_ifDataIsNotPersistedIrrespectiveOfPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedDBManager, times(0))
                .put(Mockito.any(), Mockito.any(), Mockito.any());

    }

    @Test
    public void testDynamicPostJsonObject_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonObject_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (mCallRealMethodsOfEntityMapper) {
            verify.put(Mockito.isA(RealmObject.class), Mockito.any());
        } else {
            verify.put(Mockito.any(), Mockito.any(), Mockito.any());
        }
    }

    @Test
    public void testDynamicPostJsonObject_ifEntityMapperIsInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(mToPersist ? 1 : 0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPostJsonObject_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonObject_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (mCallRealMethodsOfEntityMapper) {
            verify.put(Mockito.isA(RealmObject.class), Mockito.any());
        } else {
            verify.put(Mockito.any(JSONObject.class), Mockito.anyString(), Mockito.any());
        }
    }

    @Test
    public void testDynamicPostJsonObject_ifEntityMapperIsInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(mToPersist ? 1 : 0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPostJsonObject_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonObject_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (mCallRealMethodsOfEntityMapper) {
            verify.put(Mockito.isA(RealmObject.class), Mockito.any());
        } else {
            verify.put(Mockito.any(), Mockito.any(), Mockito.any());
        }
    }

    @Test
    public void testDynamicPostJsonObject_ifEntityMapperIsInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(mToPersist ? 1 : 0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPostJsonObject_ifErrorObservableIsNotReturned_whenNetworkIsAvailableAndSDKHasLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicPostJsonObject_ifErrorObservableIsNotReturned_whenNetworkIsAvailable() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicPostJsonObject_ifCorrectPutMethodOfRealmManagerIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedRestApi, times(1))
                .dynamicPostObject(Mockito.anyString(), Mockito.any(RequestBody.class));
    }

    @Test
    public void testDynamicPostJsonObject_ifCorrectEntityMapperMethodIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(1))
                .transformToDomain(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testDynamicPostJsonObject_ifNoErrorIsThrown_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        CloudDataStoreTestRobot.dynamicPostJsonObject(mCloudDataStore, mToPersist)
                .assertNoErrors();
    }

    @Test
    public void testDynamicPostJsonArray_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<List> testSubscriber = CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonArray_ifDataIsNotPersistedIrrespectiveOfPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedDBManager, times(0)).put(Mockito.any(), Mockito.any(), Mockito.any());

    }

    @Test
    public void testDynamicPostJsonArray_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<List> testSubscriber = CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonArray_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        verify.putAll(Mockito.any(JSONArray.class), eq(CloudDataStoreTestRobot.getValidColumnName()), eq(CloudDataStoreTestRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicPostJsonArray_ifEntityMapperIsNotInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPostJsonArray_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<List> testSubscriber = CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonArray_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        verify.putAll(Mockito.any(JSONArray.class), eq(CloudDataStoreTestRobot.getValidColumnName()), eq(CloudDataStoreTestRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicPostJsonArray_ifEntityMapperIsNotInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPostJsonArray_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<List> testSubscriber = CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPostJsonArray_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        verify.putAll(Mockito.any(JSONArray.class), eq(CloudDataStoreTestRobot.getValidColumnName()), eq(CloudDataStoreTestRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicPostJsonArray_ifEntityMapperIsNotInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPostJsonArray_ifErrorObservableIsNotReturned_whenNetworkIsAvailableAndSDKHasLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<List> testSubscriber = CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicPostJsonArray_ifErrorObservableIsNotReturned_whenNetworkIsAvailable() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        TestSubscriber<List> testSubscriber = CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        TestUtility.assertNoErrors(testSubscriber);
    }

    @Test
    public void testDynamicPostJsonArray_ifCorrectPutMethodOfRealmManagerIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedRestApi, times(1))
                .dynamicPostList(eq(CloudDataStoreTestRobot.getValidUrl()), Mockito.any(RequestBody.class));
    }

    @Test
    public void testDynamicPostJsonArray_ifCorrectEntityMapperMethodIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(1))
                .transformAllToDomain(Mockito.any(), eq(CloudDataStoreTestRobot.getValidDomainClass()));
    }

    @Test
    public void testDynamicPostJsonArray_ifNoErrorIsThrown_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        TestUtility.assertNoErrors(CloudDataStoreTestRobot.dynamicPostList(mCloudDataStore, mToPersist));
    }

    //
    @Test
    public void testDynamicPutHashMap_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutHashMap_ifDataIsNotPersistedIrrespectiveOfPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedDBManager, times(0))
                .put(Mockito.any(), Mockito.any(), Mockito.any());

    }

    @Test
    public void testDynamicPutHashMap_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutHashMap_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (mCallRealMethodsOfEntityMapper) {
            verify.put(Mockito.any(TestModel.class), Mockito.eq(CloudDataStoreTestRobot.getValidDataClass()));
        } else {
            verify.put(Mockito.any(JSONObject.class), Mockito.eq("id"), Mockito.eq(CloudDataStoreTestRobot.getValidDataClass()));
        }
    }

    @Test
    public void testDynamicPutHashMap_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutHashMap_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (mCallRealMethodsOfEntityMapper) {
            verify.put(Mockito.any(TestModel.class), Mockito.eq(CloudDataStoreTestRobot.getValidDataClass()));
        } else {
            verify.put(Mockito.any(JSONObject.class), Mockito.eq("id"), Mockito.eq(CloudDataStoreTestRobot.getValidDataClass()));
        }
    }

    @Test
    public void testDynamicPutHashMap_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutHashMap_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        if (!mCallRealMethodsOfEntityMapper && mToPersist) {
            verify.put(Mockito.any(JSONObject.class)
                    , Mockito.eq(CloudDataStoreTestRobot.getValidColumnName())
                    , Mockito.eq(CloudDataStoreTestRobot.getValidDataClass()));
        } else {
            verify.put(Mockito.any(TestModel.class)
                    , Mockito.eq(CloudDataStoreTestRobot.getValidDataClass()));
        }
    }

    @Test
    public void testDynamicPutHashMap_ifErrorObservableIsNotReturned_whenNetworkIsAvailableAndSDKHasLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicPutHashMap_ifErrorObservableIsNotReturned_whenNetworkIsAvailable() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicPutHashMap_ifCorrectPutMethodOfRealmManagerIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        CloudDataStoreTestRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedRestApi, times(1))
                .dynamicPutObject(eq(CloudDataStoreTestRobot.getValidUrl()),
                        Mockito.any(RequestBody.class));
    }

    @Test
    public void testDynamicPutHashMap_ifCorrectEntityMapperMethodIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        CloudDataStoreTestRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(1))
                .transformToDomain(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testDynamicPutHashMap_ifNoErrorIsThrown_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        CloudDataStoreTestRobot.dynamicPutHashmapObject(mCloudDataStore, mToPersist)
                .assertNoErrors();
    }

    //
    @Test
    public void testDynamicPutList_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<List> testSubscriber = CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutList_ifDataIsNotPersistedIrrespectiveOfPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedDBManager, times(0))
                .put(Mockito.any(), Mockito.any(), Mockito.any());

    }

    @Test
    public void testDynamicPutList_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<List> testSubscriber = CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutList_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        verify.putAll(Mockito.any(JSONArray.class), eq(CloudDataStoreTestRobot.getValidColumnName()), eq(CloudDataStoreTestRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicPutList_ifEntityMapperIsNotInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPutList_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<List> testSubscriber = CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutList_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        verify.putAll(Mockito.any(JSONArray.class), eq(CloudDataStoreTestRobot.getValidColumnName()), eq(CloudDataStoreTestRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicPutList_ifEntityMapperIsNotInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPutList_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<List> testSubscriber = CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicPutList_ifDataIsPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        final DataBaseManager verify = Mockito.verify(mMockedDBManager, times(mToPersist ? 1 : 0));
        verify.putAll(Mockito.any(JSONArray.class), eq(CloudDataStoreTestRobot.getValidColumnName()), eq(CloudDataStoreTestRobot.getValidDataClass()));
    }

    @Test
    public void testDynamicPutList_ifEntityMapperIsNotInvokedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(0))
                .transformToRealm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testDynamicPutList_ifErrorObservableIsNotReturned_whenNetworkIsAvailableAndSDKHasLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<List> testSubscriber = CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicPutList_ifErrorObservableIsNotReturned_whenNetworkIsAvailable() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        TestSubscriber<List> testSubscriber = CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        TestUtility.assertNoErrors(testSubscriber);
    }

    @Test
    public void testDynamicPutList_ifCorrectPutMethodOfRealmManagerIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedRestApi, times(1))
                .dynamicPutList(eq(CloudDataStoreTestRobot.getValidUrl()), Mockito.any(RequestBody.class));
    }

    @Test
    public void testDynamicPutList_ifCorrectEntityMapperMethodIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(1))
                .transformAllToDomain(Mockito.any(), eq(CloudDataStoreTestRobot.getValidDomainClass()));
    }

    @Test
    public void testDynamicPutList_ifNoErrorIsThrown_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        TestUtility.assertNoErrors(CloudDataStoreTestRobot.dynamicPutList(mCloudDataStore, mToPersist));
    }

    //upload file
    @Test
    public void testDynamicUploadFile_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        testSubscriber.assertError(NetworkConnectionException.class);
    }

    @Test
    public void testDynamicUploadFile_ifDataIsNotPersistedIrrespectiveOfPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedDBManager, times(0))
                .put(Mockito.any(), Mockito.any(), Mockito.any());

    }

    @Test
    public void testDynamicUploadFile_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesNotHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, false);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeFalse(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicUploadFile_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicUploadFile_ifDataIsNotPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreDisabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, false);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeFalse(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        Mockito.verifyNoMoreInteractions(mMockedDBManager);
    }

    @Test
    public void testDynamicUploadFile_ifErrorObservableIsReturned_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicUploadFile_ifDataIsNotPersistedAccordingToPersistStatus_whenNetworkIsNotAvailableAndPlayServicesAreEnabledAndDeviceDoesHaveLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(false);
        CloudDataStoreTestRobot.changeStateOfGoogleApi(mMockedGoogleApiAvailability, true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeFalse(CloudDataStoreTestRobot.isNetworkEnabled());
        assumeTrue(CloudDataStoreTestRobot.isGooglePlayerServicesEnabled(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        CloudDataStoreTestRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        Mockito.verifyNoMoreInteractions(mMockedDBManager);
    }

    @Test
    public void testDynamicUploadFile_ifErrorObservableIsNotReturned_whenNetworkIsAvailableAndSDKHasLollipop() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        CloudDataStoreTestRobot.changeHasLollipop(mCloudDataStore, true);
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        assumeTrue(CloudDataStoreTestRobot.hasLollipop(mCloudDataStore));
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicUploadFile_ifErrorObservableIsNotReturned_whenNetworkIsAvailable() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        TestSubscriber<Object> testSubscriber = CloudDataStoreTestRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testDynamicUploadFile_ifCorrectPutMethodOfRealmManagerIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        CloudDataStoreTestRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        Mockito.verify(mMockedRestApi, times(1))
                .upload(Mockito.anyString(), Mockito.any(Map.class), Mockito.any(MultipartBody.Part.class));
    }

    @Test
    public void testDynamicUploadFile_ifCorrectEntityMapperMethodIsCalled_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        CloudDataStoreTestRobot.dynamicUploadFile(mCloudDataStore, mToPersist);
        Mockito.verify(mEntityMapper, times(1))
                .transformToDomain(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testDynamicUploadFile_ifNoErrorIsThrown_whenRequiredThingsAreMet() {
        CloudDataStoreTestRobot.changeNetworkState(true);
        assumeTrue(CloudDataStoreTestRobot.isNetworkEnabled());
        CloudDataStoreTestRobot.dynamicUploadFile(mCloudDataStore, mToPersist)
                .assertNoErrors();
    }
}