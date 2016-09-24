package com.zeyad.genericusecase.domain.interactors;

import android.support.annotation.NonNull;

import com.zeyad.genericusecase.UIThread;
import com.zeyad.genericusecase.data.db.realm_test_models.TestModel;
import com.zeyad.genericusecase.data.executor.JobExecutor;
import com.zeyad.genericusecase.data.mockable.ObjectObservable;
import com.zeyad.genericusecase.data.repository.DataRepository;

import junit.framework.Test;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mockito.Mockito;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import rx.Observable;
import rx.observers.TestSubscriber;

class GenericUseCaseTestRobot {

    private static final TestModel TEST_MODEL = new TestModel(1, "123");
    private static final JSONObject JSON_OBJECT = new JSONObject();
    private static final JSONArray JSON_ARRAY = new JSONArray();
    private static final HashMap<String, Object> HASH_MAP = new HashMap<>();
    private static final File MOCKED_FILE = Mockito.mock(File.class);
    private static final RealmQuery<TestModel> REALM_QUERY = Realm.getDefaultInstance().where(TestModel.class);
    private static final boolean IS_CHARGING = Mockito.anyBoolean();
    private static final boolean ON_WIFI = Mockito.anyBoolean();

    /**
     * At 0th index => getObjectDynamicallyById observable
     * At 1st index => map() observable
     * At 2nd index => compose() observable
     * At 3rd index => compose() on compose() observable
     *
     * @param genericUseCase
     * @param shouldPersist
     * @return
     */
    static TestSubscriber<Object> getObject(IGenericUseCase genericUseCase, boolean shouldPersist) {
        final TestSubscriber<Object> useCaseSubscriber = new TestSubscriber<>();
        genericUseCase.getObject(new GetObjectRequest(useCaseSubscriber, getUrl()
                , getIdColumnName(), getItemId(), getPresentationClass()
                , getDataClass(), shouldPersist, false))
                .subscribe(useCaseSubscriber);
        return useCaseSubscriber;
    }

    static boolean isCharging() {
        return IS_CHARGING;
    }

    static boolean isOnWifi() {
        return ON_WIFI;
    }

    static int getItemId() {
        return 1;
    }

    static String getIdColumnName() {
        return "id";
    }

    static Class getDataClass() {
        return TestModel.class;
    }

    static Class getPresentationClass() {
        return Test.class;
    }

    static String getUrl() {
        return "www.google.com";
    }

    static DataRepository getMockedDataRepo() {
        final DataRepository dataRepository = Mockito.mock(DataRepository.class);
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .getObjectDynamicallyById(Mockito.anyString(), Mockito.anyString()
                        , Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .getObjectDynamicallyById(Mockito.anyString(), Mockito.anyString()
                        , Mockito.anyInt(), Mockito.any()
                        , Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .deleteAllDynamically(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .putListDynamically(Mockito.anyString(), Mockito.anyString(), Mockito.any(JSONArray.class),
                        Mockito.any(), Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .putListDynamically(Mockito.anyString(), Mockito.anyString(), Mockito.any(JSONArray.class), Mockito.any(),
                        Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(getListObservable())
                .when(dataRepository)
                .uploadFileDynamically(Mockito.anyString(), Mockito.any(File.class), Mockito.anyBoolean(),
                        Mockito.anyBoolean(), Mockito.any(), Mockito.any());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .putObjectDynamically(Mockito.anyString(), Mockito.anyString(), Mockito.any(JSONObject.class),
                        Mockito.any(), Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .deleteListDynamically(Mockito.anyString(), Mockito.any(JSONArray.class), Mockito.any(),
                        Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .searchDisk(Mockito.any(RealmQuery.class), Mockito.any());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .searchDisk(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .postListDynamically(Mockito.anyString(), Mockito.anyString(), Mockito.any(JSONArray.class),
                        Mockito.any(), Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .postObjectDynamically(Mockito.anyString(), Mockito.anyString(), Mockito.any(JSONObject.class),
                        Mockito.any(), Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .postObjectDynamically(Mockito.anyString(), Mockito.anyString(), Mockito.any(JSONObject.class),
                        Mockito.any(), Mockito.any(), Mockito.anyBoolean());
        return dataRepository;
    }

    static JobExecutor getMockedJobExecuter() {
        return Mockito.mock(JobExecutor.class);
    }

    static UIThread getMockedUiThread() {
        return Mockito.mock(UIThread.class);
    }

    @NonNull
    private static Observable<List> getListObservable() {
        return Observable.defer(() -> Observable.just(Collections.singletonList(createTestModel())));
    }

    @NonNull
    private static Observable<Object> getObjectObservable() {
        return Observable.defer(() -> Observable.just(createTestModel()));
    }

    private static ObjectObservable getMockedObjectObservable() {
        return Mockito.mock(ObjectObservable.class);
    }

    static TestModel createTestModel() {
        return TEST_MODEL;
    }

    static void deleteAll(IGenericUseCase genericUse, boolean toPersist) {
        final PostRequest postRequest = new PostRequest
                .PostRequestBuilder(GenericUseCaseTestRobot.getDataClass(), toPersist)
                .url(GenericUseCaseTestRobot.getUrl()).build();
        genericUse.deleteAll(postRequest).subscribe(new TestSubscriber());
    }


    static void putList_JsonArray(IGenericUseCase genericUseCase, boolean toPersist) {
        final PostRequest postRequest
                = new PostRequest(new TestSubscriber(), GenericUseCaseTestRobot.getIdColumnName()
                , GenericUseCaseTestRobot.getUrl(), getJsonArray(), GenericUseCaseTestRobot.getPresentationClass()
                , GenericUseCaseTestRobot.getDataClass(), toPersist);
        genericUseCase.putList(postRequest);
    }

    static void putList_Hashmap(IGenericUseCase genericUseCase, boolean toPersist) {
        final PostRequest postRequest
                = getHashmapPostRequest(toPersist);
        genericUseCase.putList(postRequest);
    }

    @NonNull
    static JSONObject getJSONObject() {
        return JSON_OBJECT;
    }

    static JSONArray getJsonArray() {
        return JSON_ARRAY;
    }

    static HashMap<String, Object> getHashmap() {
        return HASH_MAP;
    }

    static TestSubscriber uploadFile(IGenericUseCase genericUse) {
        final TestSubscriber subscriber = new TestSubscriber();
        genericUse.uploadFile(getUploadRequest())
                .subscribe(subscriber);
        return subscriber;
    }

    static File getFile() {
        return MOCKED_FILE;
    }

    static TestSubscriber executeUploadFile(IGenericUseCase genericUse) {
        final TestSubscriber subscriber = new TestSubscriber();
        genericUse.uploadFile(getUploadRequest()).subscribe(subscriber);
        return subscriber;
    }

    static TestSubscriber putObject(IGenericUseCase genericUse, boolean toPersist) {
        final TestSubscriber subscriber = new TestSubscriber();
        genericUse.putObject(getHashmapPostRequest(toPersist))
                .subscribe(subscriber);
        return subscriber;
    }

    static TestSubscriber deleteCollection(IGenericUseCase genericUse, boolean toPersist) {
        final PostRequest deleteRequest = getHashmapPostRequest(toPersist);
        final TestSubscriber subscriber = (TestSubscriber) deleteRequest.getSubscriber();
        genericUse.deleteCollection(deleteRequest)
                .subscribe(subscriber);
        return subscriber;
    }

    static void executeSearch_RealmQuery(IGenericUseCase genericUse, boolean toPersist) {
        genericUse.executeSearch(getRealmQuery(), getPresentationClass()).subscribe(new TestSubscriber());
    }

    static void executeSearch_NonRealmQuery(IGenericUseCase genericUse, boolean toPersist) {
        genericUse.executeSearch(getStringQuery(), getColumnQueryValue(), getPresentationClass(), getDataClass())
                .subscribe(new TestSubscriber());
    }

    static TestSubscriber<Object> postList(IGenericUseCase genericUse, boolean toPersist) {

        final PostRequest jsonArrayPostRequest = getJsonArrayPostRequest(toPersist);
        TestSubscriber<Object> testSubscriber = (TestSubscriber<Object>) jsonArrayPostRequest.getSubscriber();
        genericUse.postList(jsonArrayPostRequest)
                .subscribe(testSubscriber);
        return testSubscriber;
    }

    static TestSubscriber<Object> postObject_JsonObject(IGenericUseCase genericUse, boolean toPersist) {
        final PostRequest jsonObjectPostRequest = getJsonObjectPostRequest(toPersist);
        TestSubscriber<Object> subscriber = (TestSubscriber<Object>) jsonObjectPostRequest.getSubscriber();
        genericUse.postObject(jsonObjectPostRequest);
        return subscriber;
    }

    static TestSubscriber<Object> postObject_Hashmap(IGenericUseCase genericUse, boolean toPersist) {
        final PostRequest jsonObjectPostRequest = getHashmapPostRequest(toPersist);
        TestSubscriber<Object> subscriber = (TestSubscriber<Object>) jsonObjectPostRequest.getSubscriber();
        genericUse.postObject(jsonObjectPostRequest);
        return subscriber;
    }

    static TestSubscriber<Object> executeDynamicPostObject_PostRequestVersion_Hashmap(IGenericUseCase genericUse, boolean toPersist) {
        final PostRequest jsonObjectPostRequest = getHashmapPostRequest(toPersist);
        TestSubscriber<Object> subscriber = (TestSubscriber<Object>) jsonObjectPostRequest.getSubscriber();
        genericUse.postObject(jsonObjectPostRequest);
        return subscriber;
    }

    static String getColumnQueryValue() {
        return "1";
    }

    static String getStringQuery() {
        return "some query";
    }

    @NonNull
    private static PostRequest getHashmapPostRequest(boolean toPersist) {
        return new PostRequest(new TestSubscriber(), GenericUseCaseTestRobot.getIdColumnName()
                , GenericUseCaseTestRobot.getUrl(), getHashmap(), GenericUseCaseTestRobot.getPresentationClass()
                , GenericUseCaseTestRobot.getDataClass(), toPersist);
    }

    @NonNull
    private static PostRequest getJsonArrayPostRequest(boolean toPersist) {
        return new PostRequest(new TestSubscriber(), GenericUseCaseTestRobot.getIdColumnName()
                , GenericUseCaseTestRobot.getUrl(), getJsonArray(), GenericUseCaseTestRobot.getPresentationClass()
                , GenericUseCaseTestRobot.getDataClass(), toPersist);
    }

    @NonNull
    private static PostRequest getJsonObjectPostRequest(boolean toPersist) {
        return new PostRequest(new TestSubscriber(), GenericUseCaseTestRobot.getIdColumnName()
                , GenericUseCaseTestRobot.getUrl(), getJSONObject(), GenericUseCaseTestRobot.getPresentationClass()
                , GenericUseCaseTestRobot.getDataClass(), toPersist);
    }

    private static FileIORequest getUploadRequest() {
        return new FileIORequest(GenericUseCaseTestRobot.getUrl(), GenericUseCaseTestRobot.getFile(),
                GenericUseCaseTestRobot.ON_WIFI, GenericUseCaseTestRobot.IS_CHARGING,
                GenericUseCaseTestRobot.getPresentationClass(), GenericUseCaseTestRobot.getDataClass());
    }

    static RealmQuery getRealmQuery() {
        return REALM_QUERY;
    }
}
