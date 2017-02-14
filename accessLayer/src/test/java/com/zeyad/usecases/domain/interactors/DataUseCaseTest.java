package com.zeyad.usecases.domain.interactors;

import android.os.HandlerThread;
import android.os.Looper;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.db.RealmManager;
import com.zeyad.usecases.data.executor.JobExecutor;
import com.zeyad.usecases.data.repository.DataRepository;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.domain.executors.UIThread;
import com.zeyad.usecases.domain.interactors.data.DataUseCase;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;
import com.zeyad.usecases.domain.repositories.Data;
import com.zeyad.usecases.utils.TestRealmObject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.HashMap;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataUseCaseTest {

    private HashMap<String, Object> HASH_MAP = new HashMap<>();
    private JobExecutor mJobExecutor = getMockedJobExecuter();
    private UIThread mUIThread = getMockedUiThread();
    private IDataUseCase mDataUseCase;
    private Observable observable = Observable.just(true);
    private Data mData = getMockedDataRepo();

    Data getMockedDataRepo() {
        final DataRepository dataRepository = Mockito.mock(DataRepository.class);
        Mockito.doReturn(observable)
                .when(dataRepository)
                .getObjectDynamicallyById(Mockito.anyString(), Mockito.anyString()
                        , Mockito.anyInt(), any(), any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.doReturn(observable)
                .when(dataRepository)
                .getObjectDynamicallyById(Mockito.anyString(), Mockito.anyString()
                        , Mockito.anyInt(), any()
                        , any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.doReturn(observable)
                .when(dataRepository)
                .deleteAllDynamically(Mockito.anyString(), any(), Mockito.anyBoolean());
        Mockito.doReturn(observable)
                .when(dataRepository)
                .putListDynamically(Mockito.anyString(), Mockito.anyString(), any(JSONArray.class),
                        any(), any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.doReturn(observable)
                .when(dataRepository)
                .putListDynamically(Mockito.anyString(), Mockito.anyString(), any(JSONArray.class),
                        any(), any(), Mockito.anyBoolean(), Mockito.anyBoolean());
//        Mockito.doReturn(observable)
//                .when(dataRepository)
//                .uploadFileDynamically(Mockito.anyString(), Mockito.any(File.class), Mockito.anyString(),
//                        Mockito.any(HashMap.class), Mockito.anyBoolean(), Mockito.anyBoolean(),
//                        Mockito.anyBoolean(), Mockito.any(), Mockito.any());
        Mockito.doReturn(observable)
                .when(dataRepository)
                .putObjectDynamically(Mockito.anyString(), Mockito.anyString(), any(JSONObject.class),
                        any(), any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.doReturn(observable)
                .when(dataRepository)
                .deleteListDynamically(Mockito.anyString(), any(JSONArray.class), any(),
                        any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.doReturn(observable)
                .when(dataRepository)
                .queryDisk(any(RealmManager.RealmQueryProvider.class), any());
        Mockito.doReturn(observable)
                .when(dataRepository)
                .postListDynamically(Mockito.anyString(), Mockito.anyString(), any(JSONArray.class),
                        any(), any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.doReturn(observable)
                .when(dataRepository)
                .postObjectDynamically(Mockito.anyString(), Mockito.anyString(), any(JSONObject.class),
                        any(), any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.doReturn(observable)
                .when(dataRepository)
                .postObjectDynamically(Mockito.anyString(), Mockito.anyString(), any(JSONObject.class),
                        any(), any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        return dataRepository;
    }

    JobExecutor getMockedJobExecuter() {
        return Mockito.mock(JobExecutor.class);
    }

    UIThread getMockedUiThread() {
        return Mockito.mock(UIThread.class);
    }

    @Before
    public void setUp() throws Exception {
        HandlerThread handlerThread = mock(HandlerThread.class);
        when(handlerThread.getLooper()).thenReturn(mock(Looper.class));
        mDataUseCase = getGenericUseImplementation((DataRepository) mData, mJobExecutor, mUIThread,
                handlerThread);
        Config.setBaseURL("www.google.com");
    }

    @Test
    public void testGetObject() {
        when(mData.getObjectDynamicallyById(anyString(), anyString(), anyInt(),
                any(Class.class), any(Class.class), anyBoolean(), anyBoolean())).thenReturn(observable);

        mDataUseCase.getObject(new GetRequest("", "", 0, Object.class, Object.class, false, false));

        verify(mData, times(1)).getObjectDynamicallyById(anyString(), anyString(),
                anyInt(), any(Class.class), any(Class.class), anyBoolean(), anyBoolean());
    }

    @Test
    public void testGetList() {
        when(mData.getListDynamically(anyString(), any(Class.class), any(Class.class), anyBoolean(),
                anyBoolean())).thenReturn(observable);

        mDataUseCase.getList(new GetRequest("", "", 0, Object.class, Object.class, false, false));

        verify(mData, times(1)).getListDynamically(anyString(), any(Class.class), any(Class.class),
                anyBoolean(), anyBoolean());
    }

    @Test
    public void testExecuteDynamicPostObject() {
        when(mData.postObjectDynamically(anyString(), anyString(), any(JSONObject.class), any(Class.class),
                any(Class.class), anyBoolean(), anyBoolean())).thenReturn(observable);

        mDataUseCase.postObject(new PostRequest(new TestSubscriber(), "", "",
                new JSONArray(), Object.class, Object.class, false));

        verify(mData, times(1)).postObjectDynamically(anyString(), anyString(), any(JSONObject.class),
                any(Class.class), any(Class.class), anyBoolean(), anyBoolean());
    }

    @Test
    public void testPutObject() {
        when(mData.putObjectDynamically(anyString(), anyString(), any(JSONObject.class), any(Class.class),
                any(Class.class), anyBoolean(), anyBoolean())).thenReturn(observable);

        mDataUseCase.putObject(new PostRequest(new TestSubscriber(), "", "",
                new JSONArray(), Object.class, Object.class, false));

        verify(mData, times(1)).putObjectDynamically(anyString(), anyString(), any(JSONObject.class),
                any(Class.class), any(Class.class), anyBoolean(), anyBoolean());
    }

    @Test
    public void testPostList() {
        when(mData.postListDynamically(anyString(), anyString(), any(JSONArray.class), any(Class.class),
                any(Class.class), anyBoolean(), anyBoolean())).thenReturn(observable);
        mDataUseCase.postList(new PostRequest(new TestSubscriber(), "", "", HASH_MAP, Object.class,
                Object.class, false));

        verify(mData, times(1)).postListDynamically(anyString(), anyString(), any(JSONArray.class),
                any(Class.class), any(Class.class), anyBoolean(), anyBoolean());
    }

    @Test
    public void testPutList() {
        when(mData.putListDynamically(anyString(), anyString(), any(JSONArray.class), any(Class.class),
                any(Class.class), anyBoolean(), anyBoolean())).thenReturn(observable);

        mDataUseCase.putList(new PostRequest(new TestSubscriber(), "", "", HASH_MAP, Object.class,
                Object.class, false));

        verify(mData, times(1)).putListDynamically(anyString(), anyString(), any(JSONArray.class),
                any(Class.class), any(Class.class), anyBoolean(), anyBoolean());
    }

    @Test
    public void testExecuteSearch() {
        when(mData.queryDisk(any(RealmManager.RealmQueryProvider.class), any(Class.class))).thenReturn(observable);

        mDataUseCase.queryDisk(realm -> realm.where(TestRealmObject.class), Object.class);

        verify(mData, times(1)).queryDisk(any(RealmManager.RealmQueryProvider.class), any(Class.class));
    }

    @Test
    public void testDeleteCollection() {
        when(mData.deleteListDynamically(anyString(), any(JSONArray.class), any(Class.class),
                any(Class.class), anyBoolean(), anyBoolean())).thenReturn(observable);

        mDataUseCase.deleteCollection(new PostRequest(new TestSubscriber(), "", "", HASH_MAP, Object.class,
                Object.class, false));

        verify(mData, times(1)).deleteListDynamically(anyString(), any(JSONArray.class),
                any(Class.class), any(Class.class), anyBoolean(), anyBoolean());
    }

    @Test
    public void testDeleteAll_ifDataRepositoryCorrectMethodIsCalled_whenPostRequestIsPassed() {
        when(mData.deleteAllDynamically(anyString(), any(Class.class), anyBoolean())).thenReturn(observable);

        mDataUseCase.deleteAll(new PostRequest(new TestSubscriber(), "", "", HASH_MAP, Object.class,
                Object.class, false));

        verify(mData, times(1)).deleteAllDynamically(anyString(), any(Class.class), anyBoolean());
    }

    public IDataUseCase getGenericUseImplementation(DataRepository datarepo, JobExecutor jobExecuter
            , UIThread uithread, HandlerThread handlerThread) {
        DataUseCase.init(datarepo, jobExecuter, uithread, handlerThread);
        return DataUseCase.getInstance();
    }
}
