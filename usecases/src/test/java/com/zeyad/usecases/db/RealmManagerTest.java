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

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Completable;
import rx.Observable;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * @author by ZIaDo on 2/15/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Realm.class, RealmQuery.class, RealmResults.class})
public class RealmManagerTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();
    private RealmManager mRealmManager;

    public Realm mockRealm() {
        PowerMockito.mockStatic(Realm.class);
        Realm mockRealm = mock(Realm.class);
        RealmQuery<TestRealmModel> realmQuery = mock(RealmQuery.class);
        RealmResults<TestRealmModel> realmResults = mock(RealmResults.class);
        Observable observable = Observable.just(realmResults);
        PowerMockito.when(mockRealm.where(TestRealmModel.class)).thenReturn(realmQuery);
        PowerMockito.when(mockRealm.where(TestRealmModel.class).equalTo("id", 1)).thenReturn(realmQuery);
        PowerMockito.when(mockRealm.where(TestRealmModel.class).equalTo("id", 1).findFirst()).thenReturn(new TestRealmModel());
        PowerMockito.when(mockRealm.where(TestRealmModel.class).findAll()).thenReturn(realmResults);
        PowerMockito.when(mockRealm.where(TestRealmModel.class).findAll().asObservable()).thenReturn(observable);
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
    public void getById() throws Exception {
        Observable observable = mRealmManager.getById("id", 1, TestRealmModel.class);

        applyTestSubscriber(observable);

        assertEquals(observable.toBlocking().first().getClass(), TestRealmModel.class);
//        verify(mockRealm, times(1)).where(TestRealmModel.class).equalTo("id", 1).findAll().asObservable();
    }

    @Test
    public void getAll() throws Exception {
        Observable observable = mRealmManager.getAll(TestRealmModel.class);

        applyTestSubscriber(observable);

        assertEquals(observable.toBlocking().first().getClass(), TestRealmModel.class);
    }

    private void applyTestSubscriber(Observable observable) {
        TestSubscriber testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
    }

    @Test
    public void getQuery() throws Exception {
        Observable observable = mRealmManager.getQuery(realm -> realm.where(TestRealmModel.class));

        applyTestSubscriber(observable);

        assertEquals(observable.toBlocking().first().getClass(), TestRealmModel.class);
    }

    @Test
    public void putJSONObject() throws Exception {
        Completable completable = mRealmManager.put(new JSONObject(new Gson().toJson(new TestRealmModel())),
                "id", TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    private void applyTestSubscriber(Completable completable) {
        TestSubscriber testSubscriber = new TestSubscriber();
        completable.subscribe(testSubscriber);
        testSubscriber.assertCompleted();
    }

    @Test
    public void putRealmModel() throws Exception {
        Completable completable = mRealmManager.put(new TestRealmModel(), TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    @Test
    public void putAllJSONArray() throws Exception {
        Completable completable = mRealmManager.putAll(new JSONArray(), "id", TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    @Test
    public void putAllRealmObject() throws Exception {
        Completable completable = mRealmManager.putAll(new ArrayList<>(), TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    @Test
    public void evictAll() throws Exception {
        Completable completable = mRealmManager.evictAll(TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    @Test
    public void evictCollection() throws Exception {
        Completable completable = mRealmManager.evictCollection("id", new ArrayList<>(), TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    @Test
    public void evictById() throws Exception {
//        assertEquals(mRealmManager.evictById(TestRealmModel.class, "id", 1), true);
    }
}
