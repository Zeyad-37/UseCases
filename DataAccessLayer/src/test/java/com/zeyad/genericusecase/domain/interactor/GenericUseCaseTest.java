package com.zeyad.genericusecase.domain.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.genericusecase.UIThread;
import com.zeyad.genericusecase.data.executor.JobExecutor;
import com.zeyad.genericusecase.data.repository.DataRepository;
import com.zeyad.genericusecase.data.requests.FileIORequest;
import com.zeyad.genericusecase.data.requests.GetRequest;
import com.zeyad.genericusecase.data.requests.PostRequest;
import com.zeyad.genericusecase.domain.interactors.generic.GenericUseCase;
import com.zeyad.genericusecase.domain.interactors.generic.IGenericUseCase;
import com.zeyad.genericusecase.domain.repositories.Repository;
import com.zeyad.genericusecase.realm_test_models.TestModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.io.File;
import java.util.HashMap;

import io.realm.RealmQuery;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.eq;

@RunWith(JUnit4.class)
public class GenericUseCaseTest {

    private Class PRESENTATION_CLASS = TestModel.class;
    private Class DATA_CLASS = TestModel.class;
    private TestModel TEST_MODEL = new TestModel(1, "123");
    private JSONObject JSON_OBJECT = new JSONObject();
    private JSONArray JSON_ARRAY = new JSONArray();
    private HashMap<String, Object> HASH_MAP = new HashMap<>();
    private File MOCKED_FILE = Mockito.mock(File.class);
    //    private  final RealmQuery<TestModel> REALM_QUERY = Realm.getDefaultInstance().where(TestModel.class);
    @Nullable
    private
    RealmQuery<TestModel> REALM_QUERY = null;
    @Nullable
    private
    Repository mDataRepository = getMockedDataRepo();
    @Nullable
    private
    JobExecutor mJobExecutor = getMockedJobExecuter();
    @Nullable
    private
    UIThread mUIThread = getMockedUiThread();
    private IGenericUseCase mGenericUse;
    private boolean mToPersist, mWhileCharging, mQueuable;

    @NonNull
    private TestSubscriber<Object> getObject(@NonNull IGenericUseCase genericUseCase, boolean shouldPersist) {
        final TestSubscriber<Object> useCaseSubscriber = new TestSubscriber<>();
        genericUseCase.getObject(new GetRequest(useCaseSubscriber, getUrl(), getIdColumnName(), getItemId(),
                getPresentationClass(), getDataClass(), shouldPersist, false))
                .subscribe(useCaseSubscriber);
        return useCaseSubscriber;
    }

    private int getItemId() {
        return 1;
    }

    private String getIdColumnName() {
        return "id";
    }

    @NonNull
    private Class getDataClass() {
        return TestModel.class;
    }

    @NonNull
    private Class getPresentationClass() {
        return junit.framework.Test.class;
    }

    String getUrl() {
        return "www.google.com";
    }

    Repository getMockedDataRepo() {
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
                        Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .putListDynamically(Mockito.anyString(), Mockito.anyString(), Mockito.any(JSONArray.class),
                        Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean());
//        Mockito.doReturn(getObjectObservable())
//                .when(dataRepository)
//                .uploadFileDynamically(Mockito.anyString(), Mockito.any(File.class), Mockito.anyString(),
//                        Mockito.mock(HashMap.class), Mockito.anyBoolean(), Mockito.anyBoolean(),
//                        Mockito.anyBoolean(), Mockito.any(), Mockito.any());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .putObjectDynamically(Mockito.anyString(), Mockito.anyString(), Mockito.any(JSONObject.class),
                        Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .deleteListDynamically(Mockito.anyString(), Mockito.any(JSONArray.class), Mockito.any(),
                        Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .searchDisk(Mockito.any(RealmQuery.class), Mockito.any());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .searchDisk(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .postListDynamically(Mockito.anyString(), Mockito.anyString(), Mockito.any(JSONArray.class),
                        Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .postObjectDynamically(Mockito.anyString(), Mockito.anyString(), Mockito.any(JSONObject.class),
                        Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .postObjectDynamically(Mockito.anyString(), Mockito.anyString(), Mockito.any(JSONObject.class),
                        Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        return dataRepository;
    }

    JobExecutor getMockedJobExecuter() {
        return Mockito.mock(JobExecutor.class);
    }

    UIThread getMockedUiThread() {
        return Mockito.mock(UIThread.class);
    }

    @NonNull
    private Observable<Object> getObjectObservable() {
        //        final ObjectObservable mock1 = getMockedObjectObservable();
        //        final ObjectObservable mock2 = getMockedObjectObservable();
        //        final ObjectObservable mock3 = getMockedObjectObservable();
        //        final ObjectObservable mock4 = getMockedObjectObservable();
        //        Mockito.when(mock1.map(Mockito.any())).thenReturn(mock2);
        //        Mockito.when(mock2.compose(Mockito.any())).thenReturn(mock3);
        //        Mockito.when(mock3.compose(Mockito.any())).thenReturn(mock4);
        //        return mock1;
        return Observable.just(createTestModel());
    }

    @NonNull
    private TestModel createTestModel() {
        return TEST_MODEL;
    }

    private void deleteAll(@NonNull IGenericUseCase genericUse, boolean toPersist) {
        final PostRequest postRequest = new PostRequest
                .PostRequestBuilder(getDataClass(), toPersist)
                .url(getUrl()).build();
        genericUse.deleteAll(postRequest).subscribe(new TestSubscriber());
    }

    private void putList_JsonArray(@NonNull IGenericUseCase genericUseCase, boolean toPersist) {
        final PostRequest postRequest = new PostRequest(new TestSubscriber(), getIdColumnName(),
                getUrl(), getJsonArray(), getPresentationClass(), getDataClass(), toPersist);
        genericUseCase.putList(postRequest);
    }

    private void putList_Hashmap(@NonNull IGenericUseCase genericUseCase, boolean toPersist) {
        final PostRequest postRequest = getHashmapPostRequest(toPersist);
        genericUseCase.putList(postRequest);
    }

    @NonNull
    JSONObject getJSONObject() {
        return JSON_OBJECT;
    }

    @NonNull
    private JSONArray getJsonArray() {
        return JSON_ARRAY;
    }

    @NonNull
    HashMap<String, Object> getHashmap() {
        return HASH_MAP;
    }

    @NonNull
    private TestSubscriber uploadFile(@NonNull IGenericUseCase genericUse, boolean onWifi, boolean whileCharging) {
        final TestSubscriber subscriber = new TestSubscriber();
        final FileIORequest fileIORequest = getUploadRequest(onWifi, whileCharging);
        genericUse.uploadFile(fileIORequest).subscribe(subscriber);
        return subscriber;
    }

    File getFile() {
        return MOCKED_FILE;
    }

    String getKey() {
        return "image";
    }

    @NonNull
    private TestSubscriber putObject(@NonNull IGenericUseCase genericUse, boolean toPersist) {
        final TestSubscriber subscriber = new TestSubscriber();
        genericUse.putObject(getHashmapPostRequest(toPersist)).subscribe(subscriber);
        return subscriber;
    }

    @NonNull
    private TestSubscriber deleteCollection(@NonNull IGenericUseCase genericUse, boolean toPersist) {
        final PostRequest deleteRequest = getHashmapPostRequest(toPersist);
        final TestSubscriber subscriber = (TestSubscriber) deleteRequest.getSubscriber();
        genericUse.deleteCollection(deleteRequest).subscribe(subscriber);
        return subscriber;
    }

    @NonNull
    private TestSubscriber executeSearch_RealmQuery(@NonNull IGenericUseCase genericUse) {
        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        genericUse.searchDisk(getRealmQuery(), getPresentationClass()).subscribe(testSubscriber);
        return testSubscriber;
    }

    @NonNull
    private TestSubscriber executeSearch_NonRealmQuery(@NonNull IGenericUseCase genericUse) {
        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        genericUse.searchDisk(getStringQuery(), getColumnQueryValue(), getPresentationClass(), getDataClass());
        return testSubscriber;
    }

    @NonNull
    private TestSubscriber<Object> postList(@NonNull IGenericUseCase genericUse, boolean toPersist) {

        final PostRequest jsonArrayPostRequest = getJsonArrayPostRequest(toPersist);
        TestSubscriber<Object> testSubscriber = (TestSubscriber<Object>) jsonArrayPostRequest.getSubscriber();
        genericUse.postList(jsonArrayPostRequest).subscribe(testSubscriber);
        return testSubscriber;
    }

    @NonNull
    private TestSubscriber<Object> postObject_JsonObject(@NonNull IGenericUseCase genericUse, boolean toPersist) {
        final PostRequest jsonObjectPostRequest = getJsonObjectPostRequest(toPersist);
        TestSubscriber<Object> subscriber = (TestSubscriber<Object>) jsonObjectPostRequest.getSubscriber();
        genericUse.postObject(jsonObjectPostRequest);
        return subscriber;
    }

    @NonNull
    private TestSubscriber<Object> postObject_Hashmap(@NonNull IGenericUseCase genericUse, boolean toPersist) {
        final PostRequest jsonObjectPostRequest = getHashmapPostRequest(toPersist);
        TestSubscriber<Object> subscriber = (TestSubscriber<Object>) jsonObjectPostRequest.getSubscriber();
        genericUse.postObject(jsonObjectPostRequest);
        return subscriber;
    }

    @NonNull
    private TestSubscriber<Object> executeDynamicPostObject_PostRequestVersion_Hashmap(@NonNull IGenericUseCase genericUse, boolean toPersist) {
        final PostRequest jsonObjectPostRequest = getHashmapPostRequest(toPersist);
        TestSubscriber<Object> subscriber = (TestSubscriber<Object>) jsonObjectPostRequest.getSubscriber();
        genericUse.postObject(jsonObjectPostRequest);
        return subscriber;
    }

    private String getColumnQueryValue() {
        return "1";
    }

    private String getStringQuery() {
        return "some query";
    }

    @NonNull
    private FileIORequest getUploadRequest(boolean onWifi, boolean whileCharging) {
        return new FileIORequest(getUrl(), getFile(), getKey(), getHashmap(), onWifi, whileCharging, getPresentationClass(), getDataClass());
    }

    @NonNull
    private PostRequest getHashmapPostRequest(boolean toPersist) {
        return new PostRequest(new TestSubscriber(), getIdColumnName(), getUrl(), getHashmap(),
                getPresentationClass(), getDataClass(), toPersist);
    }

    @NonNull
    private PostRequest getJsonArrayPostRequest(boolean toPersist) {
        return new PostRequest(new TestSubscriber(), getIdColumnName(), getUrl(), getJsonArray(),
                getPresentationClass(), getDataClass(), toPersist);
    }

    @NonNull
    private PostRequest getJsonObjectPostRequest(boolean toPersist) {
        return new PostRequest(new TestSubscriber(), getIdColumnName(), getUrl(), getJSONObject(),
                getPresentationClass(), getDataClass(), toPersist);
    }

    @Nullable
    public RealmQuery getRealmQuery() {
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
        Mockito.verify(getMockedDataRepo()).getObjectDynamicallyById(
                eq(getUrl()),
                eq(getIdColumnName()),
                eq(getItemId()),
                eq(getPresentationClass()),
                eq(getDataClass()),
                eq(mToPersist),
                eq(false));
    }

    @Test
    public void testExecuteDynamicPostObject_ifDataRepoCorrectMethodIsCalled_whenPostRequestOfHasmapIsPassed() {
        executeDynamicPostObject_PostRequestVersion_Hashmap(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).postObjectDynamically(
                eq(getUrl()),
                eq(getIdColumnName()),
                eq(getJSONObject()),
                eq(getPresentationClass()),
                eq(getDataClass()),
                eq(mToPersist),
                eq(mQueuable));
    }

    @Test
    public void testPostObject_ifDataRepoCorrectMethodIsCalled_whenPostRequestOfJsonObjectIsPassed() {
        postObject_JsonObject(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository)
                .postObjectDynamically(
                        eq(getUrl()),
                        eq(getIdColumnName()),
                        eq(getJSONObject()),
                        eq(getPresentationClass()),
                        eq(getDataClass()),
                        eq(mToPersist),
                        eq(mQueuable));
    }

    @Test
    public void testPostObject_ifDataRepoCorrectMethodIsCalled_whenPostRequestOfHashMapIsPassed() {
        postObject_Hashmap(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).postObjectDynamically(
                eq(getUrl()),
                eq(getIdColumnName()),
                eq(getJSONObject()),
                eq(getPresentationClass()),
                eq(getDataClass()),
                eq(mToPersist),
                eq(mQueuable));
    }

    @Test
    public void testPostList_ifDataRepoCorrectMethodIsCalled_whenPostRequestIsPassed() {
        postList(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository)
                .postListDynamically(
                        eq(getUrl()),
                        eq(getIdColumnName()),
                        eq(getJsonArray()),
                        eq(getPresentationClass()),
                        eq(getDataClass()),
                        eq(mToPersist),
                        eq(mQueuable));
    }

    @Test
    public void testExecuteSearch_ifDataRepoCorrectMethodIsCalled_whenRealmQueryIsPassed() {
        executeSearch_RealmQuery(mGenericUse);
        Mockito.verify(mDataRepository).searchDisk(eq(getRealmQuery()), eq(getPresentationClass()));
    }

    @Test
    public void testExecuteSearch_ifDataRepoCorrectMethodIsCalled_whenRealmQueryIsNotPassed() {
        executeSearch_NonRealmQuery(mGenericUse);
        Mockito.verify(mDataRepository)
                .searchDisk(eq(getStringQuery()),
                        eq(getColumnQueryValue()),
                        eq(getPresentationClass()),
                        eq(getDataClass()));
    }

    @Test
    public void testDeleteCollection_ifDataRepoCorrectMethodIsCalled_whenPostRequestIsPassed() {
        deleteCollection(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository)
                .deleteListDynamically(
                        eq(getUrl()),
                        eq(getJsonArray()),
                        eq(getPresentationClass()),
                        eq(getDataClass()),
                        eq(mToPersist),
                        eq(mQueuable));
    }

    @Test
    public void testExecuteDynamicPutObject_ifDataRepoCorrectMethodIsCalled_whenNonPutRequestIsPassed() {
        putObject(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).putObjectDynamically(
                eq(getUrl()),
                eq(getIdColumnName()),
                eq(getJSONObject()),
                eq(getPresentationClass()),
                eq(getDataClass()),
                eq(mToPersist),
                eq(mQueuable));
    }

    @Test
    public void testPutObject_ifDataRepoCorrectMethodIsCalled_whenPostRequestIsPassed() {
        putObject(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).putObjectDynamically(
                eq(getUrl()),
                eq(getIdColumnName()),
                eq(getJSONObject()),
                eq(getPresentationClass()),
                eq(getDataClass()),
                eq(mToPersist),
                eq(mQueuable));
    }

    @Test
    public void testUploadFile_ifDataRepoCorrectMethodIsCalled_whenPutRequestIsPassed() {
        uploadFile(mGenericUse, mToPersist, mWhileCharging);
        Mockito.verify(mDataRepository).uploadFileDynamically(eq(getUrl()),
                eq(getFile()),
                eq(getKey()),
                eq(getHashmap()),
                eq(true),
                eq(true),
                eq(mQueuable),
                eq(PRESENTATION_CLASS),
                eq(DATA_CLASS));
    }

    @Test
    public void testPutList_ifDataRepoCorrectMethodIsCalled_whenJsonArrayIsPassed() {
        putList_JsonArray(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).putListDynamically(eq(getUrl()),
                eq(getIdColumnName()),
                eq(getJsonArray()),
                eq(getPresentationClass()),
                eq(getDataClass()),
                eq(mToPersist),
                eq(mQueuable));
    }

    @Test
    public void testPutList_ifDataRepoCorrectMethodIsCalled_whenHashmapIsPassed() {
        putList_Hashmap(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).putListDynamically(eq(getUrl()),
                eq(getIdColumnName()),
                eq(getJsonArray()),
                eq(getPresentationClass()),
                eq(getDataClass()),
                eq(mToPersist),
                eq(mQueuable));
    }

    @Test
    public void testDeleteAll_ifDataRepositoryCorrectMethodIsCalled_whenPostRequestIsPassed() {
        deleteAll(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).deleteAllDynamically(eq(getUrl()), eq(getDataClass()), eq(mToPersist));
    }

    public IGenericUseCase getGenericUseImplementation(DataRepository datarepo, JobExecutor jobExecuter
            , UIThread uithread) {
        GenericUseCase.init(datarepo, jobExecuter, uithread);
        return GenericUseCase.getInstance();
    }
}
