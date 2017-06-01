package com.zeyad.usecases.db;

import android.os.HandlerThread;
import android.support.test.rule.BuildConfig;

import com.google.gson.Gson;
import com.zeyad.usecases.TestRealmModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;

/** @author by ZIaDo on 2/15/17. */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Realm.class, RealmQuery.class, RealmResults.class})
public class RealmManagerTest {
    @Rule public PowerMockRule rule = new PowerMockRule();
    private RealmManager mRealmManager;

    public Realm mockRealm() {
        PowerMockito.mockStatic(Realm.class);
        Realm mockRealm = mock(Realm.class);
        RealmQuery<TestRealmModel> realmQuery = mock(RealmQuery.class);
        RealmResults<TestRealmModel> realmResults = mock(RealmResults.class);
        Observable observable = Observable.just(realmResults);
        PowerMockito.when(mockRealm.where(TestRealmModel.class)).thenReturn(realmQuery);
        PowerMockito.when(mockRealm.where(TestRealmModel.class).equalTo("id", 1L))
                .thenReturn(realmQuery);
        PowerMockito.when(mockRealm.where(TestRealmModel.class).equalTo("id", 1L).findFirst())
                .thenReturn(new TestRealmModel());
        PowerMockito.when(mockRealm.where(TestRealmModel.class).findAll()).thenReturn(realmResults);
        PowerMockito.when(mockRealm.where(TestRealmModel.class).findAll().asObservable())
                .thenReturn(observable);
        PowerMockito.when(Realm.getDefaultInstance()).thenReturn(mockRealm);
        return mockRealm;
    }

    @Before
    public void before() {
        mockRealm();
        HandlerThread handlerThread = new HandlerThread("backgroundThread");
        handlerThread.start();
        mRealmManager = new RealmManager(handlerThread.getLooper());
    }

    @Test
    public void getByIdLessThanOne() throws Exception {
        Flowable flowable = mRealmManager.getById("id", 0L, null, TestRealmModel.class);

        TestSubscriber testSubscriber = new TestSubscriber();
        flowable.subscribe(testSubscriber);

        testSubscriber.assertError(IllegalArgumentException.class);
        testSubscriber.assertErrorMessage("Id can not be less than Zero.");
    }

    @Test
    public void getById() throws Exception {
        Flowable flowable = mRealmManager.getById("id", 1L, null, TestRealmModel.class);

        applyTestSubscriber(flowable);

        assertEquals(
                flowable.first(new TestRealmModel()).blockingGet().getClass(),
                TestRealmModel.class);
        //        verify(mockRealm, times(1)).where(TestRealmModel.class).equalTo("id", 1).findAll().asObservable();
    }

    @Test
    public void getAll() throws Exception {
        Flowable flowable = mRealmManager.getAll(TestRealmModel.class);

        applyTestSubscriber(flowable);

        assertEquals(
                flowable.first(new TestRealmModel()).blockingGet().getClass(),
                TestRealmModel.class);
    }

    private void applyTestSubscriber(Flowable flowable) {
        TestSubscriber testSubscriber = new TestSubscriber<>();
        flowable.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
    }

    @Test
    public void getQuery() throws Exception {
        Flowable flowable = mRealmManager.getQuery(realm -> realm.where(TestRealmModel.class));

        applyTestSubscriber(flowable);

        assertEquals(
                flowable.first(new TestRealmModel()).blockingGet().getClass(),
                TestRealmModel.class);
    }

    @Test
    public void putJSONObject() throws Exception {
        Single<Boolean> completable = mRealmManager.put(new JSONObject(new Gson().toJson(new TestRealmModel())),
                "id", TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    private void applyTestSubscriber(Completable completable) {
        TestObserver testSubscriber = new TestObserver();
        completable.subscribe(testSubscriber);
        testSubscriber.assertComplete();
    }

    private void applyTestSubscriber(Single<Boolean> single) {
        TestObserver<Boolean> testSubscriber = new TestObserver<>();
        single.subscribe(testSubscriber);
        testSubscriber.assertComplete();
    }

    @Test
    public void putRealmModel() throws Exception {
        Single<Boolean> completable = mRealmManager.put(new TestRealmModel(), TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    @Test
    public void putAllJSONArray() throws Exception {
        Single<Boolean> completable = mRealmManager.putAll(new JSONArray(), "id", TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    @Test
    public void putAllRealmObject() throws Exception {
        Single<Boolean> completable = mRealmManager.putAll(new ArrayList<>(), TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    @Test
    public void evictAll() throws Exception {
        Single<Boolean> completable = mRealmManager.evictAll(TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    @Test
    public void evictCollection() throws Exception {
        Flowable<Boolean> completable =
                mRealmManager.evictCollection("id", new ArrayList<>(), TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    @Test
    public void evictById() throws Exception {
        //        assertEquals(mRealmManager.evictById(TestRealmModel.class, "id", 1), true);
    }
}
