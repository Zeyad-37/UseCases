package com.zeyad.genericusecase.domain.interactors;

import com.zeyad.genericusecase.UIThread;
import com.zeyad.genericusecase.data.executor.JobExecutor;
import com.zeyad.genericusecase.data.repository.DataRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import static org.mockito.Matchers.eq;

@RunWith(Parameterized.class)
public abstract class IGenericUseCaseTest {

    private IGenericUseCase mGenericUse;
    private boolean mToPersist;
    private final DataRepository mDataRepository = GenericUseCaseTestRobot.getMockedDataRepo();
    private final JobExecutor mJobExecutor = GenericUseCaseTestRobot.getMockedJobExecuter();
    private final UIThread mUIThread = GenericUseCaseTestRobot.getMockedUiThread();

    public IGenericUseCaseTest(boolean toPersist) {
        this.mToPersist = toPersist;
    }

    @Parameterized.Parameters
    public static Object[][] provideParameters() {
        return new Object[][]{
                new Object[]{true},
                new Object[]{false}
        };
    }

    @Before
    public void setUp() throws Exception {
        mGenericUse = getGenericUseImplementation(mDataRepository, mJobExecutor, mUIThread);
    }

    @After
    public void tearDown() throws Exception {
    }

    //"getObject" related method tests starts
    @Test
    public void testGetObject_ifDataRepositoryMethodGetObjectDynamicallyIsCalled_whenArgumentsArePassedAsExpected() {
        GenericUseCaseTestRobot.getObject(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository)
                .getObjectDynamicallyById(
                        eq(GenericUseCaseTestRobot.getUrl())
                        , eq(GenericUseCaseTestRobot.getIdColumnName())
                        , eq(GenericUseCaseTestRobot.getItemId())
                        , eq(GenericUseCaseTestRobot.getPresentationClass())
                        , eq(GenericUseCaseTestRobot.getDataClass())
                        , eq(mToPersist)
                        , eq(false));
    }

    @Test
    public void testExecuteDynamicPostObject_ifDataRepoCorrectMethodIsCalled_whenPostRequestOfHasmapIsPassed() {
        GenericUseCaseTestRobot.executeDynamicPostObject_PostRequestVersion_Hashmap(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository)
                .postObjectDynamically(
                        eq(GenericUseCaseTestRobot.getUrl())
                        , eq(GenericUseCaseTestRobot.getIdColumnName())
                        , eq(GenericUseCaseTestRobot.getJSONObject())
                        , eq(GenericUseCaseTestRobot.getPresentationClass())
                        , eq(GenericUseCaseTestRobot.getDataClass())
                        , eq(mToPersist));
    }

    @Test
    public void testPostObject_ifDataRepoCorrectMethodIsCalled_whenPostRequestOfJsonObjectIsPassed() {
        GenericUseCaseTestRobot.postObject_JsonObject(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository)
                .postObjectDynamically(
                        eq(GenericUseCaseTestRobot.getUrl())
                        , eq(GenericUseCaseTestRobot.getIdColumnName())
                        , eq(GenericUseCaseTestRobot.getJSONObject())
                        , eq(GenericUseCaseTestRobot.getPresentationClass())
                        , eq(GenericUseCaseTestRobot.getDataClass())
                        , eq(mToPersist));
    }

    @Test
    public void testPostObject_ifDataRepoCorrectMethodIsCalled_whenPostRequestOfHashMapIsPassed() {
        GenericUseCaseTestRobot.postObject_Hashmap(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository)
                .postObjectDynamically(
                        eq(GenericUseCaseTestRobot.getUrl())
                        , eq(GenericUseCaseTestRobot.getIdColumnName())
                        , eq(GenericUseCaseTestRobot.getJSONObject())
                        , eq(GenericUseCaseTestRobot.getPresentationClass())
                        , eq(GenericUseCaseTestRobot.getDataClass())
                        , eq(mToPersist));
    }

    @Test
    public void testPostList_ifDataRepoCorrectMethodIsCalled_whenPostRequestIsPassed() {
        GenericUseCaseTestRobot.postList(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository)
                .postListDynamically(
                        eq(GenericUseCaseTestRobot.getUrl())
                        , eq(GenericUseCaseTestRobot.getIdColumnName())
                        , eq(GenericUseCaseTestRobot.getJsonArray())
                        , eq(GenericUseCaseTestRobot.getPresentationClass())
                        , eq(GenericUseCaseTestRobot.getDataClass())
                        , eq(mToPersist));
    }

    //"postList" related method tests ends

    //"executeSearch" related method tests starts
    @Test
    public void testExecuteSearch_ifDataRepoCorrectMethodIsCalled_whenRealmQueryIsPassed() {
        GenericUseCaseTestRobot.executeSearch_RealmQuery(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository)
                .searchDisk(eq(GenericUseCaseTestRobot.getRealmQuery()),
                        eq(GenericUseCaseTestRobot.getDataClass()));
    }

    @Test
    public void testExecuteSearch_ifDataRepoCorrectMethodIsCalled_whenRealmQueryIsNotPassed() {
        GenericUseCaseTestRobot.executeSearch_NonRealmQuery(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository)
                .searchDisk(eq(GenericUseCaseTestRobot.getStringQuery()),
                        eq(GenericUseCaseTestRobot.getColumnQueryValue()),
                        eq(GenericUseCaseTestRobot.getPresentationClass()),
                        eq(GenericUseCaseTestRobot.getDataClass()));
    }

    @Test
    public void testDeleteCollection_ifDataRepoCorrectMethodIsCalled_whenPostRequestIsPassed() {
        GenericUseCaseTestRobot.deleteCollection(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository)
                .deleteListDynamically(
                        eq(GenericUseCaseTestRobot.getUrl())
                        , eq(GenericUseCaseTestRobot.getJsonArray())
                        , eq(GenericUseCaseTestRobot.getPresentationClass())
                        , eq(GenericUseCaseTestRobot.getDataClass())
                        , eq(mToPersist));
    }
    //"deleteCollection" related method tests ends

    //"putObject" related method tests starts
    @Test
    public void testExecuteDynamicPutObject_ifDataRepoCorrectMethodIsCalled_whenNonPutRequestIsPassed() {
        GenericUseCaseTestRobot.putObject(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository)
                .putObjectDynamically(
                        eq(GenericUseCaseTestRobot.getUrl())
                        , eq(GenericUseCaseTestRobot.getIdColumnName())
                        , eq(GenericUseCaseTestRobot.getJSONObject())
                        , eq(GenericUseCaseTestRobot.getPresentationClass())
                        , eq(GenericUseCaseTestRobot.getDataClass())
                        , eq(mToPersist));
    }

    @Test
    public void testPutObject_ifDataRepoCorrectMethodIsCalled_whenPostRequestIsPassed() {
        GenericUseCaseTestRobot.putObject(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository)
                .putObjectDynamically(
                        eq(GenericUseCaseTestRobot.getUrl())
                        , eq(GenericUseCaseTestRobot.getIdColumnName())
                        , eq(GenericUseCaseTestRobot.getJSONObject())
                        , eq(GenericUseCaseTestRobot.getPresentationClass())
                        , eq(GenericUseCaseTestRobot.getDataClass())
                        , eq(mToPersist));
    }

    @Test
    public void testExecuteUploadFile_ifDataRepoCorrectMethodIsCalled_whenPutRequestIsPassed() {
        GenericUseCaseTestRobot.executeUploadFile(mGenericUse);
        Mockito.verify(mDataRepository)
                .uploadFileDynamically(eq(GenericUseCaseTestRobot.getUrl())
                        , eq(GenericUseCaseTestRobot.getFile())
                        , eq(GenericUseCaseTestRobot.isOnWifi())
                        , eq(GenericUseCaseTestRobot.isCharging())
                        , eq(GenericUseCaseTestRobot.getPresentationClass())
                        , eq(GenericUseCaseTestRobot.getDataClass()));
    }

    @Test
    public void testUploadFile_ifDataRepoCorrectMethodIsCalled_whenPutRequestIsPassed() {
        GenericUseCaseTestRobot.uploadFile(mGenericUse);
        Mockito.verify(mDataRepository)
                .uploadFileDynamically(eq(GenericUseCaseTestRobot.getUrl())
                        , eq(GenericUseCaseTestRobot.getFile())
                        , eq(GenericUseCaseTestRobot.isOnWifi())
                        , eq(GenericUseCaseTestRobot.isCharging())
                        , eq(GenericUseCaseTestRobot.getPresentationClass())
                        , eq(GenericUseCaseTestRobot.getDataClass()));
    }

    @Test
    public void testPutList_ifDataRepoCorrectMethodIsCalled_whenJsonArrayIsPassed() {
        GenericUseCaseTestRobot.putList_JsonArray(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).putListDynamically(
                eq(GenericUseCaseTestRobot.getUrl())
                , eq(GenericUseCaseTestRobot.getIdColumnName())
                , eq(GenericUseCaseTestRobot.getJsonArray())
                , eq(GenericUseCaseTestRobot.getPresentationClass())
                , eq(GenericUseCaseTestRobot.getDataClass())
                , eq(mToPersist));
    }

    @Test
    public void testPutList_ifDataRepoCorrectMethodIsCalled_whenHashmapIsPassed() {
        GenericUseCaseTestRobot.putList_Hashmap(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).putListDynamically(
                eq(GenericUseCaseTestRobot.getUrl())
                , eq(GenericUseCaseTestRobot.getIdColumnName())
                , eq(GenericUseCaseTestRobot.getJsonArray())
                , eq(GenericUseCaseTestRobot.getPresentationClass())
                , eq(GenericUseCaseTestRobot.getDataClass())
                , eq(mToPersist));
    }

    @Test
    public void testDeleteAll_ifDataRepositoryCorrectMethodIsCalled_whenPostRequestIsPassed() {
        GenericUseCaseTestRobot.deleteAll(mGenericUse, mToPersist);
        Mockito.verify(mDataRepository).deleteAllDynamically(
                eq(GenericUseCaseTestRobot.getUrl())
                , eq(GenericUseCaseTestRobot.getDataClass())
                , eq(mToPersist));
    }
    //"delete all" related method tests ends

    public abstract IGenericUseCase getGenericUseImplementation(DataRepository datarepo,
                                                                JobExecutor jobExecuter, UIThread uithread);
}