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
import com.zeyad.genericusecase.domain.interactors.generic.GenericUseCaseFactory;
import com.zeyad.genericusecase.domain.interactors.generic.IGenericUseCase;
import com.zeyad.genericusecase.domain.repositories.Repository;
import com.zeyad.genericusecase.utils.TestModel;
import com.zeyad.genericusecase.utils.TestViewModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.HashMap;

import io.realm.RealmQuery;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({GenericUseCaseFactory.class})
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
        return TestViewModel.class;
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
        Mockito.doReturn(getObjectObservable())
                .when(dataRepository)
                .uploadFileDynamically(Mockito.anyString(), Mockito.any(File.class), Mockito.anyString(),
                        Mockito.any(HashMap.class), Mockito.anyBoolean(), Mockito.anyBoolean(),
                        Mockito.anyBoolean(), Mockito.any(), Mockito.any());
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
        PowerMockito.mockStatic(GenericUseCaseFactory.class);
        when(GenericUseCaseFactory.getBaseURL()).thenReturn("www.google.com");
    }

    @Test
    public void testGetObject_ifDataRepositoryMethodGetObjectDynamicallyIsCalled_whenArgumentsArePassedAsExpected() {
        final TestSubscriber<Object> useCaseSubscriber = new TestSubscriber<>();
        mGenericUse.getObject(new GetRequest(useCaseSubscriber, getUrl(), getIdColumnName(), getItemId(),
                getPresentationClass(), getDataClass(), mToPersist, false))
                .subscribe(useCaseSubscriber);

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
        final PostRequest jsonObjectPostRequest = getHashmapPostRequest(mToPersist);
        TestSubscriber<Object> subscriber = (TestSubscriber<Object>) jsonObjectPostRequest.getSubscriber();
        mGenericUse.postObject(jsonObjectPostRequest).subscribe(subscriber);

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
        final PostRequest jsonObjectPostRequest = getJsonObjectPostRequest(mToPersist);
        TestSubscriber<Object> subscriber = (TestSubscriber<Object>) jsonObjectPostRequest.getSubscriber();
        mGenericUse.postObject(jsonObjectPostRequest).subscribe(subscriber);

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
        final PostRequest jsonObjectPostRequest = getHashmapPostRequest(mToPersist);
        TestSubscriber<Object> subscriber = (TestSubscriber<Object>) jsonObjectPostRequest.getSubscriber();
        mGenericUse.postObject(jsonObjectPostRequest).subscribe(subscriber);

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
        final PostRequest jsonArrayPostRequest = getJsonArrayPostRequest(mToPersist);
        TestSubscriber<Object> testSubscriber = (TestSubscriber<Object>) jsonArrayPostRequest.getSubscriber();
        mGenericUse.postList(jsonArrayPostRequest).subscribe(testSubscriber);

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
        mGenericUse.searchDisk(getRealmQuery(), getPresentationClass()).subscribe(new TestSubscriber<>());
        Mockito.verify(mDataRepository).searchDisk(eq(getRealmQuery()), eq(getPresentationClass()));
    }

    @Test
    public void testExecuteSearch_ifDataRepoCorrectMethodIsCalled_whenRealmQueryIsNotPassed() {
        mGenericUse.searchDisk(getStringQuery(), getColumnQueryValue(), getPresentationClass(), getDataClass()).subscribe(new TestSubscriber());

        Mockito.verify(mDataRepository)
                .searchDisk(eq(getStringQuery()),
                        eq(getColumnQueryValue()),
                        eq(getPresentationClass()),
                        eq(getDataClass()));
    }

    @Test
    public void testDeleteCollection_ifDataRepoCorrectMethodIsCalled_whenPostRequestIsPassed() {
        final PostRequest deleteRequest = getHashmapPostRequest(mToPersist);
        final TestSubscriber subscriber = (TestSubscriber) deleteRequest.getSubscriber();
        mGenericUse.deleteCollection(deleteRequest).subscribe(subscriber);
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
        mGenericUse.uploadFile(getUploadRequest(mToPersist, mWhileCharging)).subscribe(new TestSubscriber());

        Mockito.verify(mDataRepository).uploadFileDynamically(eq(getUrl()),
                eq(getFile()),
                eq(getKey()),
                eq(getHashmap()),
                eq(false),
                eq(false),
                eq(mQueuable),
                eq(PRESENTATION_CLASS),
                eq(DATA_CLASS));
    }

    @Test
    public void testPutList_ifDataRepoCorrectMethodIsCalled_whenJsonArrayIsPassed() {
        final PostRequest postRequest = new PostRequest(new TestSubscriber(), getIdColumnName(),
                getUrl(), getJsonArray(), getPresentationClass(), getDataClass(), mToPersist);
        mGenericUse.putList(postRequest).subscribe(new TestSubscriber<>());

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
        mGenericUse.putList(getHashmapPostRequest(mToPersist)).subscribe(new TestSubscriber<>());
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
        final PostRequest postRequest = new PostRequest
                .PostRequestBuilder(getDataClass(), mToPersist)
                .url(getUrl()).build();
        mGenericUse.deleteAll(postRequest).subscribe(new TestSubscriber<>());
        Mockito.verify(mDataRepository).deleteAllDynamically(eq(getUrl()), eq(getDataClass()), eq(mToPersist));
    }

    public IGenericUseCase getGenericUseImplementation(DataRepository datarepo, JobExecutor jobExecuter
            , UIThread uithread) {
        GenericUseCase.init(datarepo, jobExecuter, uithread);
        return GenericUseCase.getInstance();
    }
}
