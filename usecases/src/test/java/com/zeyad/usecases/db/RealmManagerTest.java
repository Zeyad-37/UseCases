//package com.zeyad.usecases.db;
//
//import android.content.Context;
//import android.os.HandlerThread;
//import android.support.test.rule.BuildConfig;
//
//import com.zeyad.usecases.TestRealmModel;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.RuntimeEnvironment;
//import org.robolectric.annotation.Config;
//
//import java.util.ArrayList;
//
//import io.reactivex.Completable;
//import io.reactivex.Flowable;
//import io.reactivex.observers.TestObserver;
//import io.realm.Realm;
//import io.realm.RealmConfiguration;
//import io.realm.rx.RealmObservableFactory;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
///**
// * @author by ZIaDo on 2/15/17.
// */
//@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class, sdk = 19)
//public class RealmManagerTest {
//    private RealmManager mRealmManager;
//    private Realm mockRealm;
//    private Flowable flowable;
//
//    @Before
//    public void before() {
//
//        HandlerThread handlerThread = new HandlerThread("backgroundThread");
//        handlerThread.start();
//        mRealmManager = new RealmManager(handlerThread.getLooper());
//
//        Context context = RuntimeEnvironment.application;
//        Realm.init(context);
//        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
//                .name("test.realm")
//                .rxFactory(new RealmObservableFactory())
//                .deleteRealmIfMigrationNeeded()
//                .build());
//
//        mockRealm = Realm.getDefaultInstance();
//
//        flowable = Flowable.just(new TestRealmModel());
//    }
//
//    @Test
//    public void getById() throws Exception {
////        when(mockRealm.where(TestRealmModel.class).equalTo("", 0).findAll().asObservable())
////                .thenReturn(flowable);
//        Flowable observable = mRealmManager.getById("", 0, TestRealmModel.class);
//
//        verify(mockRealm, times(1)).where(TestRealmModel.class).equalTo("", 0).findAll().asObservable();
////        assertEquals(flowable.first().getClass(), TestRealmModel.class);
//    }
//
//    @Test
//    public void getAll() throws Exception {
//        Flowable observable = mRealmManager.getAll(TestRealmModel.class);
//        assertEquals(observable.first(new TestRealmModel()).blockingGet().getClass(), TestRealmModel.class);
//    }
//
//    @Test
//    public void getQuery() throws Exception {
//        Flowable observable = mRealmManager.getQuery(realm -> realm.where(TestRealmModel.class));
//        assertEquals(observable.first(new TestRealmModel()).getClass(), TestRealmModel.class);
//    }
//
//    @Test
//    public void putJSONObject() throws Exception {
//        Completable completable = mRealmManager.put(new JSONObject(), "", TestRealmModel.class);
//        TestObserver testSubscriber = new TestObserver();
//        completable.subscribe(testSubscriber);
//        testSubscriber.assertComplete();
//    }
//
//    @Test
//    public void putRealmModel() throws Exception {
//        Completable completable = mRealmManager.put(new TestRealmModel(), TestRealmModel.class);
//        TestObserver testSubscriber = new TestObserver();
//        completable.subscribe(testSubscriber);
//        testSubscriber.assertComplete();
//    }
//
//    @Test
//    public void putAllJSONArray() throws Exception {
//        Completable completable = mRealmManager.putAll(new JSONArray(), "", TestRealmModel.class);
//        TestObserver testSubscriber = new TestObserver();
//        completable.subscribe(testSubscriber);
//        testSubscriber.assertComplete();
//    }
//
//    @Test
//    public void putAllRealmObject() throws Exception {
//        Completable completable = mRealmManager.putAll(new ArrayList<>(), TestRealmModel.class);
//        TestObserver testSubscriber = new TestObserver();
//        completable.subscribe(testSubscriber);
//        testSubscriber.assertComplete();
//    }
//
//    @Test
//    public void evictAll() throws Exception {
//        Completable completable = mRealmManager.evictAll(TestRealmModel.class);
//        TestObserver testSubscriber = new TestObserver();
//        completable.subscribe(testSubscriber);
//        testSubscriber.assertComplete();
//    }
//
//    @Test
//    public void evictCollection() throws Exception {
//        Completable completable = mRealmManager.evictCollection("", new ArrayList<>(), TestRealmModel.class);
//        TestObserver testSubscriber = new TestObserver();
//        completable.subscribe(testSubscriber);
//        testSubscriber.assertComplete();
//    }
//
//    @Test
//    public void evictById() throws Exception {
//        assertEquals(mRealmManager.evictById(TestRealmModel.class, "", 0), true);
//    }
//}
