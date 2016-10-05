package com.zeyad.genericusecase.domain.interactors.interactors;

import android.support.annotation.NonNull;

import com.zeyad.genericusecase.UIThread;
import com.zeyad.genericusecase.data.executor.JobExecutor;
import com.zeyad.genericusecase.data.mockable.ObjectObservable;
import com.zeyad.genericusecase.data.repository.DataRepository;
import com.zeyad.genericusecase.data.services.realm_test_models.TestModel;
import com.zeyad.genericusecase.data.services.realm_test_models.TestViewModel;
import com.zeyad.genericusecase.domain.interactors.GenericUseCase;
import com.zeyad.genericusecase.domain.interactors.IGenericUseCase;
import com.zeyad.genericusecase.domain.interactors.requests.FileIORequest;
import com.zeyad.genericusecase.domain.interactors.requests.GetObjectRequest;
import com.zeyad.genericusecase.domain.interactors.requests.PostRequest;
import com.zeyad.genericusecase.domain.repository.Repository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.eq;

@RunWith(JUnit4.class)
public class GenericUseCaseTest {

    public static final boolean ON_WIFI = Mockito.anyBoolean();
    public static final boolean WHILE_CHARGING = Mockito.anyBoolean();
    public static final Class DOMAIN_CLASS = TestModel.class;
    public static final Class DATA_CLASS = TestModel.class;
    private static final TestModel TEST_MODEL = new TestModel(1, "123");
    private static final JSONObject JSON_OBJECT = new JSONObject();
    private static final JSONArray JSON_ARRAY = new JSONArray();
    private static final HashMap<String, Object> HASH_MAP = new HashMap<>();
    private static final File MOCKED_FILE = Mockito.mock(File.class);
    //    private static final RealmQuery<TestModel> REALM_QUERY = Realm.getDefaultInstance().where(TestModel.class);
    private static final RealmQuery<TestModel> REALM_QUERY = null;
    private final Repository mDataRepository = null;//GenericUseCaseTestRobot.getMockedDataRepo();
    private final JobExecutor mJobExecutor = null;// GenericUseCaseTestRobot.getMockedJobExecuter();
    private final UIThread mUIThread = null;//GenericUseCaseTestRobot.getMockedUiThread();
    private IGenericUseCase mGenericUse;
    private boolean mToPersist, mWhileCharging;

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
        genericUseCase.getObject(new GetObjectRequest(useCaseSubscriber, getUrl(), getIdColumnName(),
                getItemId(), getPresentationClass(), getDataClass(), shouldPersist, false))
                .subscribe(useCaseSubscriber);
        return useCaseSubscriber;
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

    static Class getDomainClass() {
        return TestViewModel.class;
    }

    static Class getPresentationClass() {
        return junit.framework.Test.class;
    }

    static String getUrl() {
        return "www.google.com";
    }

    static Repository getMockedDataRepo() {
        final Repository dataRepository = Mockito.mock(Repository.class);
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
                .putListDynamically(Mockito.anyString(), Mockito.anyString(), Mockito.any(JSONArray.class),
                        Mockito.any(), Mockito.any(), Mockito.anyBoolean());
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
        return Observable.create(new Observable.OnSubscribe<List>() {
            @Override
            public void call(Subscriber<? super List> subscriber) {
                subscriber.onNext(Collections.singletonList(createTestModel()));
            }
        });
    }

    @NonNull
    private static Observable<Object> getObjectObservable() {
        //        final ObjectObservable mock1 = getMockedObjectObservable();
        //        final ObjectObservable mock2 = getMockedObjectObservable();
        //        final ObjectObservable mock3 = getMockedObjectObservable();
        //        final ObjectObservable mock4 = getMockedObjectObservable();
        //        Mockito.when(mock1.map(Mockito.any())).thenReturn(mock2);
        //        Mockito.when(mock2.compose(Mockito.any())).thenReturn(mock3);
        //        Mockito.when(mock3.compose(Mockito.any())).thenReturn(mock4);
        //        return mock1;
        return Observable.create(
                subscriber -> {
                    subscriber.onNext(createTestModel());
                });
    }

    private static ObjectObservable getMockedObjectObservable() {
        return Mockito.mock(ObjectObservable.class);
    }

    static TestModel createTestModel() {
        return TEST_MODEL;
    }

    static void deleteAll(IGenericUseCase genericUse, boolean toPersist) {
        final PostRequest postRequest = new PostRequest.PostRequestBuilder(getDataClass(), toPersist)
                .url(getUrl()).build();
        genericUse.deleteAll(postRequest).subscribe(new TestSubscriber());
    }

    static void putList_JsonArray(IGenericUseCase genericUseCase, boolean toPersist) {
        final PostRequest postRequest = new PostRequest(new TestSubscriber(), getIdColumnName(),
                getUrl(), getJsonArray(), getPresentationClass(), getDataClass(), toPersist);
        genericUseCase.putList(postRequest);
    }

    static void putList_Hashmap(IGenericUseCase genericUseCase, boolean toPersist) {
        final PostRequest postRequest = getHashmapPostRequest(toPersist);
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

    static TestSubscriber uploadFile(IGenericUseCase genericUse, boolean onWifi, boolean whileCharging) {
        final TestSubscriber subscriber = new TestSubscriber();
        final FileIORequest fileIORequest = getUploadRequest(onWifi, whileCharging);
        genericUse.uploadFile(fileIORequest)
                .subscribe(subscriber);
        return subscriber;
    }

    static File getFile() {
        return MOCKED_FILE;
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

    static TestSubscriber<Object> postList(IGenericUseCase genericUse, boolean toPersist) {
        final PostRequest jsonArrayPostRequest = getJsonArrayPostRequest(toPersist);
        TestSubscriber<Object> testSubscriber = (TestSubscriber<Object>) jsonArrayPostRequest.getSubscriber();
        genericUse.postList(jsonArrayPostRequest).subscribe(testSubscriber);
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
    private static FileIORequest getUploadRequest(boolean onWifi, boolean whileCharging) {
        return new FileIORequest(getUrl(), getFile(), onWifi, whileCharging, getPresentationClass(), getDataClass());
    }

    @NonNull
    private static PostRequest getHashmapPostRequest(boolean toPersist) {
        return new PostRequest(new TestSubscriber(), getIdColumnName(), getUrl(), getHashmap(),
                getPresentationClass(), getDataClass(), toPersist);
    }

    @NonNull
    private static PostRequest getJsonArrayPostRequest(boolean toPersist) {
        return new PostRequest(new TestSubscriber(), getIdColumnName(), getUrl(), getJsonArray(),
                getPresentationClass(), getDataClass(), toPersist);
    }

    @NonNull
    private static PostRequest getJsonObjectPostRequest(boolean toPersist) {
        return new PostRequest(new TestSubscriber(), getIdColumnName(), getUrl(), getJSONObject(),
                getPresentationClass(), getDataClass(), toPersist);
    }

    public static RealmQuery getRealmQuery() {
        return REALM_QUERY;
    }

    @Before
    public void setUp() throws Exception {
        mGenericUse = getGenericUseImplementation((DataRepository) mDataRepository, mJobExecutor, mUIThread);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetObject_ifDataRepositoryMethodGetObjectDynamicallyIsCalled_whenArgumentsArePassedAsExpected() {
        getObject(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).getObjectDynamicallyById(eq(getUrl()), eq(getIdColumnName()),
                eq(getItemId()), eq(getDomainClass()), eq(getDataClass()), eq(mToPersist), eq(false));
    }

    @Test
    public void testExecuteDynamicPostObject_ifDataRepoCorrectMethodIsCalled_whenPostRequestOfHasmapIsPassed() {
        executeDynamicPostObject_PostRequestVersion_Hashmap(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).postObjectDynamically(eq(getUrl()), eq(getIdColumnName()),
                eq(getJSONObject()), eq(getDomainClass()), eq(getDataClass()), eq(mToPersist));
    }

    @Test
    public void testPostObject_ifDataRepoCorrectMethodIsCalled_whenPostRequestOfJsonObjectIsPassed() {
        postObject_JsonObject(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).postObjectDynamically(eq(getUrl()), eq(getIdColumnName()),
                eq(getJSONObject()), eq(getDomainClass()), eq(getDataClass()), eq(mToPersist));
    }

    @Test
    public void testPostObject_ifDataRepoCorrectMethodIsCalled_whenPostRequestOfHashMapIsPassed() {
        postObject_Hashmap(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).postObjectDynamically(eq(getUrl()), eq(getIdColumnName()),
                eq(getJSONObject()), eq(getDomainClass()), eq(getDataClass()), eq(mToPersist));
    }

    @Test
    public void testPostList_ifDataRepoCorrectMethodIsCalled_whenPostRequestIsPassed() {
        postList(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).postListDynamically(eq(getUrl()), eq(getIdColumnName()),
                eq(getJsonArray()), eq(getDomainClass()), eq(getDataClass()), eq(mToPersist));
    }

    @Test
    public void testDeleteCollection_ifDataRepoCorrectMethodIsCalled_whenPostRequestIsPassed() {
        deleteCollection(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).deleteListDynamically(eq(getUrl()), eq(getJsonArray()),
                eq(getDomainClass()), eq(getDataClass()), eq(mToPersist));
    }

    @Test
    public void testExecuteDynamicPutObject_ifDataRepoCorrectMethodIsCalled_whenNonPutRequestIsPassed() {
        putObject(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).putObjectDynamically(eq(getUrl()), eq(getIdColumnName()),
                eq(getJSONObject()), eq(getDomainClass()), eq(getDataClass()), eq(mToPersist));
    }

    @Test
    public void testPutObject_ifDataRepoCorrectMethodIsCalled_whenPostRequestIsPassed() {
        putObject(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).putObjectDynamically(eq(getUrl()), eq(getIdColumnName()),
                eq(getJSONObject()), eq(getDomainClass()), eq(getDataClass()), eq(mToPersist));
    }

    @Test
    public void testUploadFile_ifDataRepoCorrectMethodIsCalled_whenPutRequestIsPassed() {
        uploadFile(mGenericUse, mToPersist, mWhileCharging);
        Mockito.verify(mDataRepository).uploadFileDynamically(eq(getUrl()), eq(getFile()), eq(ON_WIFI),
                eq(WHILE_CHARGING), eq(DOMAIN_CLASS), eq(DATA_CLASS));
    }

    @Test
    public void testPutList_ifDataRepoCorrectMethodIsCalled_whenJsonArrayIsPassed() {
        putList_JsonArray(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).putListDynamically(eq(getUrl()), eq(getIdColumnName()),
                eq(getJsonArray()), eq(getDomainClass()), eq(getDataClass()), eq(mToPersist));
    }

    @Test
    public void testPutList_ifDataRepoCorrectMethodIsCalled_whenHashmapIsPassed() {
        putList_Hashmap(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).putListDynamically(eq(getUrl()), eq(getIdColumnName()),
                eq(getJsonArray()), eq(getDomainClass()), eq(getDataClass()), eq(mToPersist));
    }

    @Test
    public void testDeleteAll_ifDataRepositoryCorrectMethodIsCalled_whenPostRequestIsPassed() {
        deleteAll(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).deleteAllDynamically(eq(getUrl()), eq(getDataClass()),
                eq(mToPersist));
    }

    public IGenericUseCase getGenericUseImplementation(DataRepository datarepo, JobExecutor jobExecuter,
                                                       UIThread uithread) {
        GenericUseCase.init(datarepo, jobExecuter, uithread);
        return GenericUseCase.getInstance();
    }
}