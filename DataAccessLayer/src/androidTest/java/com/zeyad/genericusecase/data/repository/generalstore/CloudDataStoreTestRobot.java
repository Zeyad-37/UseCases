package com.zeyad.genericusecase.data.repository.generalstore;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.gson.Gson;
import com.zeyad.genericusecase.data.TestUtility;
import com.zeyad.genericusecase.data.ToStringArgumentMatcher;
import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.db.GeneralRealmManagerImplUtils;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.mockable.ResponseBodyObservable;
import com.zeyad.genericusecase.data.network.RestApi;
import com.zeyad.genericusecase.data.network.RestApiImpl;
import com.zeyad.genericusecase.data.services.realm_test_models.TestModel2;
import com.zeyad.genericusecase.data.services.realm_test_models.TestModelViewModelMapper;
import com.zeyad.genericusecase.data.services.realm_test_models.TestViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.Mockito;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;


@SuppressWarnings("WrongConstant")
class CloudDataStoreTestRobot {

    private static final TestModel2 TEST_MODEL = new TestModel2(1, "123");
    private static final File MOCKED_FILE = Mockito.mock(File.class);
    private static final JobScheduler JOB_SCHEDULER = Mockito.mock(JobScheduler.class);


    static EntityMapper<Object, Object> createMockedEntityMapper() {
        final TestModelViewModelMapper viewModelMapper
                = Mockito.mock(TestModelViewModelMapper.class);
        Mockito.when(viewModelMapper.transformAllToDomain(Mockito.anyList()))
                .thenReturn(new ArrayList<>());
        Mockito.when(viewModelMapper.transformAllToDomain(Mockito.anyList(), Mockito.any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(viewModelMapper.transformAllToRealm(Mockito.anyList(), Mockito.any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(viewModelMapper.transformToRealm(Mockito.any(), Mockito.any()))
                .thenReturn("");
        Mockito.when(viewModelMapper.transformToDomain(Mockito.any()))
                .thenReturn("");
        Mockito.when(viewModelMapper.transformToDomain(Mockito.any(), Mockito.any()))
                .thenReturn("");
        return viewModelMapper;
    }

    static EntityMapper<Object, Object> createMockedEntityMapperWithActualMethodCalls() {
        final TestModelViewModelMapper viewModelMapper
                = Mockito.mock(TestModelViewModelMapper.class);
        Mockito.when(viewModelMapper.transformAllToDomain(Mockito.anyList()))
                .thenAnswer(invocation -> new TestModelViewModelMapper()
                        .transformAllToDomain((List<Object>) invocation.getArguments()[0]));
        Mockito.when(viewModelMapper.transformAllToDomain(Mockito.anyList(), Mockito.any()))
                .thenAnswer(invocation -> new TestModelViewModelMapper()
                        .transformAllToDomain((List<Object>) invocation.getArguments()[0], (Class) invocation.getArguments()[1]));
        Mockito.when(viewModelMapper.transformAllToRealm(Mockito.anyList(), Mockito.any()))
                .thenAnswer(invocation -> new TestModelViewModelMapper()
                        .transformAllToRealm((List<Object>) invocation.getArguments()[0], (Class) invocation.getArguments()[1]));
        Mockito.when(viewModelMapper.transformToRealm(Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> new TestModelViewModelMapper()
                        .transformToRealm(invocation.getArguments()[0], (Class) invocation.getArguments()[1]));
        Mockito.when(viewModelMapper.transformToDomain(Mockito.any()))
                .thenAnswer(invocation -> new TestModelViewModelMapper()
                        .transformToDomain(invocation.getArguments()[0]));
        Mockito.when(viewModelMapper.transformToDomain(Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> new TestModelViewModelMapper()
                        .transformToDomain(invocation.getArguments()[0], (Class) invocation.getArguments()[1]))
        ;
        return viewModelMapper;
    }

    static String getValidUrl() {
        return "http://www.google.com";
    }

    static Class getValidDomainClass() {
        return TestViewModel.class;
    }

    static int getValidColumnId() {
        return 12;
    }

    static HashMap<String, Object> getValidHashmap() {
        return new HashMap<>();
    }


    static String getValidColumnName() {
        return "id";
    }

    static Class getValidDataClass() {
        return TestModel2.class;
    }

    static GoogleApiAvailability createMockedGoogleApiAvailability() {
        GoogleApiAvailability googleApiAvailaibility = Mockito.mock(GoogleApiAvailability.class);
        Mockito.when(googleApiAvailaibility.isGooglePlayServicesAvailable(Mockito.any())).thenReturn(ConnectionResult.SUCCESS);
        return googleApiAvailaibility;
    }

    static void changeStateOfGoogleApi(GoogleApiAvailability googleApiAvailaibility, boolean state) {
        Mockito.when(googleApiAvailaibility.isGooglePlayServicesAvailable(Mockito.any()))
                .thenReturn(state ? ConnectionResult.SUCCESS : ConnectionResult.API_UNAVAILABLE);
    }

    static RestApi createMockedRestApi(boolean toCache) {
        final Observable<List<?>> LIST_OBSERVABLE = getListObservable();
        final Observable<Object> OBJECT_OBSERVABLE = getObjectObservable();
        final ResponseBodyObservable RESPONSE_BODY_OBSERVABLE = Mockito.mock(ResponseBodyObservable.class);
        final RestApiImpl mock = Mockito.mock(RestApiImpl.class);
        Mockito.doReturn(LIST_OBSERVABLE).when(mock).dynamicDeleteList(Mockito.anyString(), Mockito.any());
        Mockito.when(mock.dynamicGetObject(Mockito.any(), Mockito.anyBoolean())).thenReturn(OBJECT_OBSERVABLE);
        Mockito.when(mock.dynamicGetObject(Mockito.any())).thenReturn(OBJECT_OBSERVABLE);
        Mockito.doReturn(getListObservable()).when(mock).dynamicGetList(Mockito.any());
        Mockito.doReturn(getListObservable()).when(mock).dynamicGetList(Mockito.any(), Mockito.anyBoolean());
        Mockito.when(mock.dynamicPostObject(Mockito.any(), Mockito.any())).thenReturn(OBJECT_OBSERVABLE);
        Mockito.doReturn(LIST_OBSERVABLE).when(mock).dynamicPostList(Mockito.any(), Mockito.any());
        Mockito.when(mock.dynamicPutObject(Mockito.any(), Mockito.any())).thenReturn(OBJECT_OBSERVABLE);
        Mockito.doReturn(LIST_OBSERVABLE).when(mock).dynamicPutList(Mockito.any(), Mockito.any());
        Mockito.when(mock.dynamicDeleteObject(Mockito.any(), Mockito.any())).thenReturn(OBJECT_OBSERVABLE);
        Mockito.doReturn(LIST_OBSERVABLE).when(mock).dynamicDeleteList(Mockito.any(), Mockito.any());
        Mockito.when(mock.dynamicDownload(getFileUrl())).thenReturn(RESPONSE_BODY_OBSERVABLE);
        Mockito.when(mock.upload(Mockito.any(), Mockito.any(RequestBody.class))).thenReturn(OBJECT_OBSERVABLE);
        Mockito.when(mock.upload(Mockito.any(), Mockito.any(MultipartBody.Part.class))).thenReturn(RESPONSE_BODY_OBSERVABLE);
        Mockito.when(mock.upload(Mockito.any(), Mockito.any(MultipartBody.Part.class))).thenReturn(RESPONSE_BODY_OBSERVABLE);
        return mock;
    }

    @NonNull
    private static Observable<List<?>> getListObservable() {
        return Observable.create(
                new Observable.OnSubscribe<List<?>>() {
                    @Override
                    public void call(Subscriber<? super List<?>> subscriber) {
                        subscriber.onNext(Collections.singletonList(createTestModel()));
                    }
                });
    }

    @NonNull
    private static Observable<Object> getObjectObservable() {
        return Observable.create(
                subscriber -> {
                    subscriber.onNext(createTestModel());
                });
    }

    static DataBaseManager createDBManagerWithMockedContext() {
        final Observable<List<?>> LIST_OBSERVABLE = getListObservable();
        final Observable<Object> OBJECT_OBSERVABLE = getObjectObservable();
        final Observable<Boolean> TRUE_OBSERVABLE = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                subscriber.onNext(true);
            }
        });
        final DataBaseManager dbManagerWithMockedContext
                = GeneralRealmManagerImplUtils.createDBManagerWithMockedContext(getMockedContext());
        Mockito.when(dbManagerWithMockedContext.getAll(Mockito.any())).thenReturn(LIST_OBSERVABLE);
        Mockito.doReturn(OBJECT_OBSERVABLE).when(dbManagerWithMockedContext).put(Mockito.any(RealmObject.class), Mockito.any());
        Mockito.doReturn(OBJECT_OBSERVABLE).when(dbManagerWithMockedContext).put(Mockito.any(RealmModel.class), Mockito.any());
        Mockito.doReturn(TRUE_OBSERVABLE).when(dbManagerWithMockedContext).put(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doNothing().when(dbManagerWithMockedContext)
                .putAll(Mockito.anyListOf(RealmObject.class), Mockito.any());
        Mockito.doReturn(OBJECT_OBSERVABLE).when(dbManagerWithMockedContext)
                .putAll(Mockito.any(JSONArray.class), eq(CloudDataStoreTestRobot.getValidColumnName()), Mockito.any());
        Mockito.when(dbManagerWithMockedContext.isCached(Mockito.anyInt(), Mockito.anyString(), Mockito.any())).thenReturn(true);
        Mockito.when(dbManagerWithMockedContext.isItemValid(Mockito.anyInt(), Mockito.anyString(), Mockito.any())).thenReturn(true);
        Mockito.when(dbManagerWithMockedContext.areItemsValid(Mockito.anyString())).thenReturn(true);
        Mockito.when(dbManagerWithMockedContext.evictAll(Mockito.any()))
                .thenReturn(TRUE_OBSERVABLE);
        Mockito.doNothing()
                .when(dbManagerWithMockedContext).evict(Mockito.any(), Mockito.any());
        Mockito.when(dbManagerWithMockedContext.evictById(Mockito.any(), Mockito.anyString(), Mockito.anyLong()))
                .thenReturn(true);
        Mockito.doReturn(TRUE_OBSERVABLE).when(dbManagerWithMockedContext).evictCollection(Mockito.anyString(), Mockito.anyListOf(Long.class), Mockito.any());
        Mockito.when(dbManagerWithMockedContext.getWhere(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(LIST_OBSERVABLE);
        Mockito.when(dbManagerWithMockedContext.getWhere(Mockito.any(RealmQuery.class)))
                .thenReturn(LIST_OBSERVABLE);
        return dbManagerWithMockedContext;
    }

    public static Context getMockedContext() {
        final Context context = Mockito.mock(Context.class);
        Mockito.when(context.getApplicationContext()).thenReturn(context);
        final Resources resources = Mockito.mock(Resources.class);
        final PackageManager packageManager = Mockito.mock(PackageManager.class);
        Mockito.when(context.getApplicationContext())
                .thenReturn(context);
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(context.getPackageManager()).thenReturn(packageManager);
        Mockito.when(context.getPackageName()).thenReturn(".data.services.GenericJobService");
        Mockito.when(context.getSystemService(Context.JOB_SCHEDULER_SERVICE)).thenReturn(getMockedJobScheduler());
        return context;
    }

    private static JobScheduler getMockedJobScheduler() {
        return JOB_SCHEDULER;
    }

    private static void mockForString(Context appContext, Resources resources, int resId) {
        Mockito.when(appContext.getString(resId)).thenReturn("Hello");
        Mockito.when(resources.getString(resId)).thenReturn("Hello");
    }


    public static String getFileUrl() {
        return "";
    }

    static TestSubscriber<List> dynamicGetList(CloudDataStore cloudDataStore, boolean toPersist) {
        final TestSubscriber<List> listTestSubscriber = new TestSubscriber<>();
        cloudDataStore.dynamicGetList(getValidUrl()
                , getValidDomainClass()
                , getValidDataClass()
                , toPersist
                , false).subscribe(listTestSubscriber);
        return listTestSubscriber;
    }

    static TestSubscriber<List> dynamicGetList(CloudDataStore cloudDataStore, boolean toPersist, boolean toCahce) {
        final TestSubscriber<List> listTestSubscriber = new TestSubscriber<>();
        cloudDataStore.dynamicGetList(getValidUrl()
                , getValidDomainClass()
                , getValidDataClass()
                , toPersist, toCahce).subscribe(listTestSubscriber);
        return listTestSubscriber;
    }

    static TestSubscriber<Object> dynamicGetObject(CloudDataStore cloudDataStore, boolean toPersist) {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        cloudDataStore
                .dynamicGetObject(getValidUrl()
                        , getValidColumnName()
                        , getValidColumnId()
                        , getValidDomainClass()
                        , getValidDataClass()
                        , toPersist
                        , false)
                .subscribe(subscriber);
        return subscriber;
    }

    static TestSubscriber<Object> dynamicGetObject(CloudDataStore cloudDataStore, boolean toPersist, boolean toCache) {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        cloudDataStore
                .dynamicGetObject(getValidUrl()
                        , getValidColumnName()
                        , getValidColumnId()
                        , getValidDomainClass()
                        , getValidDataClass()
                        , toPersist
                        , toCache)
                .subscribe(subscriber);
        return subscriber;
    }

    static TestSubscriber<Object> dynamicPostJsonObject(CloudDataStore cloudDataStore, boolean toPersist) {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        cloudDataStore.dynamicPostObject(getValidUrl()
                , CloudDataStoreTestRobot.getValidColumnName()
                , createTestModelJSON()
                , getValidDomainClass()
                , getValidDataClass()
                , toPersist)
                .subscribe(subscriber);
        return subscriber;
    }

    static TestSubscriber<Object> dynamicPutHashmapObject(CloudDataStore cloudDataStore, boolean toPersist) {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        cloudDataStore.dynamicPutObject(getValidUrl()
                , getValidColumnName()
                , createTestModelJSON()
                , getValidDomainClass()
                , getValidDataClass()
                , toPersist)
                .subscribe(subscriber);
        return subscriber;
    }

    public static TestModel2 createTestModel() {
        return TEST_MODEL;
    }

    static JSONObject createTestModelJSON() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(createTestModel()));
        } catch (JSONException ignored) {
        }
        return jsonObject;
    }

    static Context changeNetworkState(DataBaseManager mockedDBManager, boolean toEnable) {
        return TestUtility.changeStateOfNetwork(mockedDBManager.getContext().getApplicationContext(), toEnable);
    }

    static void changeHasLollipop(CloudDataStore cloudDataStore, boolean state) {
        cloudDataStore.setHasLollipop(state);
    }

    static boolean isNetworkEnabled(DataBaseManager mockedDBManager) {
        return com.zeyad.genericusecase.data.utils.Utils.isNetworkAvailable(mockedDBManager.getContext().getApplicationContext());
    }

    static boolean isGooglePlayerServicesEnabled(DataBaseManager mockedDBManager, CloudDataStore cloudDataStore) {
        return cloudDataStore.getGoogleApiAvailability().isGooglePlayServicesAvailable(mockedDBManager.getContext().getApplicationContext())
                == CommonStatusCodes.SUCCESS;
    }

    static boolean hasLollipop(CloudDataStore cloudDataStore) {
        return cloudDataStore.isHasLollipop();
    }

    static <T> T argThis(T arg) {
        return argThat(ToStringArgumentMatcher.newInstance(arg));
    }

    static TestSubscriber<List> dynamicPostList(CloudDataStore cloudDataStore, boolean toPersist) {
        final TestSubscriber<List> subscriber = new TestSubscriber<>();
        cloudDataStore.dynamicPostList(getValidUrl()
                , getValidColumnName()
                , getValidJsonArray()
                , getValidDomainClass()
                , getValidDataClass()
                , toPersist)
                .subscribe(subscriber);
        return subscriber;
    }

    static TestSubscriber dynamicPutList(CloudDataStore cloudDataStore, boolean toPersist) {
        final TestSubscriber subscriber = new TestSubscriber<>();
        cloudDataStore.dynamicPutList(getValidUrl()
                , getValidColumnName()
                , getValidJsonArray()
                , getValidDomainClass()
                , getValidDataClass()
                , toPersist)
                .subscribe(subscriber);
        return subscriber;
    }

    static TestSubscriber<Object> dynamicUploadFile(CloudDataStore cloudDataStore, boolean onWifi) {

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        cloudDataStore.dynamicUploadFile(getValidUrl(), getValidFile(), onWifi, true, getValidDataClass())
                .subscribe(subscriber);
        return subscriber;
    }

    private static File getValidFile() {
        Mockito.when(MOCKED_FILE.getPath()).thenReturn("file://somedir/somepath.jpeg");
        return MOCKED_FILE;
    }

    static JSONArray getValidJsonArray() {
        final JSONArray jsonArray = new JSONArray();
        return jsonArray.put(createTestModelJSON());
    }

    static GcmNetworkManager getGcmNetworkManager() {
        return Mockito.mock(GcmNetworkManager.class);
    }
}
