package com.zeyad.usecases.db;

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

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

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
//        Observable observable = Observable.just(realmResults);
        PowerMockito.when(mockRealm.where(TestRealmModel.class)).thenReturn(realmQuery);
        PowerMockito.when(mockRealm.where(TestRealmModel.class).equalTo("id", 1L))
                .thenReturn(realmQuery);
        TestRealmModel value = new TestRealmModel();
        PowerMockito.when(mockRealm.where(TestRealmModel.class).equalTo("id", 1L).findFirst())
                .thenReturn(value);
        PowerMockito.when(mockRealm.where(TestRealmModel.class).findAll()).thenReturn(realmResults);
        //        PowerMockito.when(mockRealm.where(TestRealmModel.class).findAll().asObservable())
        //                .thenReturn(observable);
        PowerMockito.when(Realm.getDefaultInstance()).thenReturn(mockRealm);
        RealmConfiguration realmConfiguration = mock(RealmConfiguration.class);
        PowerMockito.when(mockRealm.getConfiguration()).thenReturn(realmConfiguration);
        PowerMockito.when(Realm.getInstance(realmConfiguration)).thenReturn(mockRealm);
        PowerMockito.when(mockRealm.copyFromRealm(value)).thenReturn(value);
        return mockRealm;
    }

    @Before
    public void before() {
        mockRealm();
        mRealmManager = new RealmManager();
    }

    @Test
    public void getById() throws Exception {
//        Flowable flowable = mRealmManager.getById("id", 1L, long.class, TestRealmModel.class);
//
//        applyTestSubscriber(flowable);
//
//        assertEquals(flowable.firstElement().blockingGet().getClass(), TestRealmModel.class);
    }

    @Test
    public void getAll() throws Exception {
//        Flowable flowable = mRealmManager.getAll(TestRealmModel.class);
//
//        applyTestSubscriber(flowable);
//
//        assertEquals(flowable.firstElement().blockingGet().getClass(), TestRealmModel.class);
    }

    private void applyTestSubscriber(Flowable flowable) {
        TestSubscriber testSubscriber = new TestSubscriber<>();
        flowable.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertSubscribed();
        testSubscriber.assertComplete();
//        testSubscriber.assertNotComplete();
//        testSubscriber.assertNotTerminated();
    }

    @Test
    public void getQuery() throws Exception {
//        Flowable flowable = mRealmManager.getQuery(realm -> realm.where(TestRealmModel.class));
//
//        applyTestSubscriber(flowable);
//
//        assertEquals(flowable.firstElement().blockingGet().getClass(), TestRealmModel.class);
    }

    @Test
    public void putJSONObject() throws Exception {
        Single<Boolean> completable = mRealmManager.put(new JSONObject(new Gson()
                .toJson(new TestRealmModel())), "id", int.class, TestRealmModel.class);
        applyTestSubscriber(completable);
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
        Single<Boolean> completable = mRealmManager.putAll(new JSONArray(), "id", int.class,
                TestRealmModel.class);
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
        Single<Boolean> completable =
                mRealmManager.evictCollection("id", new ArrayList<>(), String.class, TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    @Test
    public void evictById() throws Exception {
//        assertEquals(mRealmManager.evictById(TestRealmModel.class, "id", 1), true);
    }
}
