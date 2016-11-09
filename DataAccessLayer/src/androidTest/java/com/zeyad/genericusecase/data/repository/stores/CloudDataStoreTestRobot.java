package com.zeyad.genericusecase.data.repository.stores;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.gson.Gson;
import com.zeyad.genericusecase.data.TestUtility;
import com.zeyad.genericusecase.data.ToStringArgumentMatcher;
import com.zeyad.genericusecase.data.db.DataBaseManager;
import com.zeyad.genericusecase.data.db.RealmManagerImplUtils;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.mockable.ResponseBodyObservable;
import com.zeyad.genericusecase.data.network.RestApi;
import com.zeyad.genericusecase.data.network.RestApiImpl;
import com.zeyad.genericusecase.data.services.realm_test_models.TestModel;
import com.zeyad.genericusecase.data.services.realm_test_models.TestModelViewModelMapper;
import com.zeyad.genericusecase.data.services.realm_test_models.TestViewModel;
import com.zeyad.genericusecase.data.utils.Utils;

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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;

@SuppressWarnings("WrongConstant")
class CloudDataStoreTestRobot {

    private static final TestModel TEST_MODEL = new TestModel(1, "123");
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

    @NonNull
    static Class getValidDomainClass() {
        return TestViewModel.class;
    }

    static int getValidColumnId() {
        return 12;
    }

    @NonNull
    static HashMap<String, Object> getValidHashmap() {
        return new HashMap<>();
    }


    static String getValidColumnName() {
        return "id";
    }

    static String getKey() {
        return "image";
    }


    @NonNull
    static Class getValidDataClass() {
        return TestModel.class;
    }

    static GoogleApiAvailability createMockedGoogleApiAvailability() {
        GoogleApiAvailability googleApiAvailaibility = Mockito.mock(GoogleApiAvailability.class);
        Mockito.when(googleApiAvailaibility.isGooglePlayServicesAvailable(Mockito.any())).thenReturn(ConnectionResult.SUCCESS);
        return googleApiAvailaibility;
    }

    static void changeStateOfGoogleApi(@NonNull GoogleApiAvailability googleApiAvailaibility, boolean state) {
        Mockito.when(googleApiAvailaibility.isGooglePlayServicesAvailable(Mockito.any()))
                .thenReturn(state ? ConnectionResult.SUCCESS : ConnectionResult.API_UNAVAILABLE);
    }

    static RestApi createMockedRestApi(boolean toCache) {
        final Observable<List> LIST_OBSERVABLE = getListObservable();
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
        Mockito.when(mock.upload(Mockito.any(), Mockito.any(RequestBody.class), Mockito.any(MultipartBody.Part.class))).thenReturn(OBJECT_OBSERVABLE);
        return mock;
    }

    @NonNull
    private static Observable<List> getListObservable() {
        return Observable.create(
                new Observable.OnSubscribe<List>() {
                    @Override
                    public void call(@NonNull Subscriber<? super List> subscriber) {
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
        final Observable LIST_OBSERVABLE = getListObservable();
        final Observable<Object> OBJECT_OBSERVABLE = getObjectObservable();
        final Observable<Object> TRUE_OBSERVABLE = Observable.create(subscriber -> subscriber.onNext(true));
        final DataBaseManager dbManagerWithMockedContext
                = RealmManagerImplUtils.createDBManagerWithMockedContext();
        Mockito.when(dbManagerWithMockedContext.getAll(Mockito.any())).thenReturn(LIST_OBSERVABLE);
        Mockito.doReturn(OBJECT_OBSERVABLE).when(dbManagerWithMockedContext).put(Mockito.any(RealmObject.class), Mockito.any());
        Mockito.doReturn(OBJECT_OBSERVABLE).when(dbManagerWithMockedContext).put(Mockito.any(RealmModel.class), Mockito.any());
        Mockito.doReturn(TRUE_OBSERVABLE).when(dbManagerWithMockedContext).put(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doNothing().when(dbManagerWithMockedContext)
                .putAll(Mockito.anyListOf(RealmObject.class), Mockito.any());
        Mockito.doReturn(OBJECT_OBSERVABLE).when(dbManagerWithMockedContext)
                .putAll(Mockito.any(JSONArray.class), eq(CloudDataStoreTestRobot.getValidColumnName()), Mockito.any());
        Mockito.when(dbManagerWithMockedContext.evictAll(Mockito.any())).thenReturn(any()); // was TRUE_OBSERVABLE
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

    private static void mockForString(@NonNull Context appContext, @NonNull Resources resources, int resId) {
        Mockito.when(appContext.getString(resId)).thenReturn("Hello");
        Mockito.when(resources.getString(resId)).thenReturn("Hello");
    }


    public static String getFileUrl() {
        return "";
    }

    @NonNull
    static TestSubscriber<List> dynamicGetList(@NonNull CloudDataStore cloudDataStore, boolean toPersist) {
        final TestSubscriber<List> listTestSubscriber = new TestSubscriber<>();
        cloudDataStore.dynamicGetList(getValidUrl()
                , getValidDomainClass()
                , getValidDataClass()
                , toPersist
                , false).subscribe(listTestSubscriber);
        return listTestSubscriber;
    }

    @NonNull
    static TestSubscriber<List> dynamicGetList(@NonNull CloudDataStore cloudDataStore, boolean toPersist, boolean toCahce) {
        final TestSubscriber<List> listTestSubscriber = new TestSubscriber<>();
        cloudDataStore.dynamicGetList(getValidUrl()
                , getValidDomainClass()
                , getValidDataClass()
                , toPersist, toCahce).subscribe(listTestSubscriber);
        return listTestSubscriber;
    }

    @NonNull
    static TestSubscriber<Object> dynamicGetObject(@NonNull CloudDataStore cloudDataStore, boolean toPersist) {
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

    @NonNull
    static TestSubscriber<Object> dynamicGetObject(@NonNull CloudDataStore cloudDataStore, boolean toPersist, boolean toCache) {
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

    @NonNull
    static TestSubscriber<Object> dynamicPostJsonObject(@NonNull CloudDataStore cloudDataStore, boolean toPersist) {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        cloudDataStore.dynamicPostObject(getValidUrl()
                , CloudDataStoreTestRobot.getValidColumnName()
                , createTestModelJSON()
                , getValidDomainClass()
                , getValidDataClass()
                , false
                , toPersist)
                .subscribe(subscriber);
        return subscriber;
    }

    @NonNull
    static TestSubscriber<Object> dynamicPutHashmapObject(@NonNull CloudDataStore cloudDataStore, boolean toPersist) {
        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        cloudDataStore.dynamicPutObject(getValidUrl()
                , getValidColumnName()
                , createTestModelJSON()
                , getValidDomainClass()
                , getValidDataClass()
                , false
                , toPersist)
                .subscribe(subscriber);
        return subscriber;
    }

    @NonNull
    public static TestModel createTestModel() {
        return TEST_MODEL;
    }

    @Nullable
    static JSONObject createTestModelJSON() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(createTestModel()));
        } catch (JSONException ignored) {
        }
        return jsonObject;
    }

    static Context changeNetworkState(boolean toEnable) {
        return TestUtility.changeStateOfNetwork(getMockedContext(), toEnable);
    }

    static void changeHasLollipop(@NonNull CloudDataStore cloudDataStore, boolean state) {
        cloudDataStore.setHasLollipop(state);
    }

    static boolean isNetworkEnabled() {
        return Utils.isNetworkAvailable(getMockedContext());
    }

    static boolean isGooglePlayerServicesEnabled(@NonNull CloudDataStore cloudDataStore) {
        return cloudDataStore.getGoogleApiAvailability().isGooglePlayServicesAvailable(getMockedContext())
                == CommonStatusCodes.SUCCESS;
    }

    static boolean hasLollipop(@NonNull CloudDataStore cloudDataStore) {
        return cloudDataStore.isHasLollipop();
    }

    static <T> T argThis(T arg) {
        return argThat(ToStringArgumentMatcher.newInstance(arg));
    }

    @NonNull
    static TestSubscriber<List> dynamicPostList(@NonNull CloudDataStore cloudDataStore, boolean toPersist) {
        final TestSubscriber subscriber = new TestSubscriber<>();
        cloudDataStore.dynamicPostList(getValidUrl()
                , getValidColumnName()
                , getValidJsonArray()
                , getValidDomainClass()
                , getValidDataClass()
                , false
                , toPersist)
                .subscribe(subscriber);
        return subscriber;
    }

    @NonNull
    static TestSubscriber dynamicPutList(@NonNull CloudDataStore cloudDataStore, boolean toPersist) {
        final TestSubscriber subscriber = new TestSubscriber<>();
        cloudDataStore.dynamicPutList(getValidUrl()
                , getValidColumnName()
                , getValidJsonArray()
                , getValidDomainClass()
                , getValidDataClass()
                , false
                , toPersist)
                .subscribe(subscriber);
        return subscriber;
    }

    @NonNull
    static TestSubscriber<Object> dynamicUploadFile(@NonNull CloudDataStore cloudDataStore, boolean onWifi) {

        final TestSubscriber<Object> subscriber = new TestSubscriber<>();
        cloudDataStore.dynamicUploadFile(getValidUrl(), getValidFile(), getKey(), onWifi, true, false, getValidDataClass())
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
