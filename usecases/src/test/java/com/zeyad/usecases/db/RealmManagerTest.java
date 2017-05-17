//package com.zeyad.usecases.db;
//
//import android.support.test.InstrumentationRegistry;
//import android.support.test.runner.AndroidJUnit4;
//
//import com.zeyad.usecases.TestRealmModel;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.ArrayList;
//
//import io.realm.Realm;
//import io.realm.RealmConfiguration;
//import io.realm.rx.RealmObservableFactory;
//import rx.Completable;
//import rx.Observable;
//import rx.observers.TestSubscriber;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
///**
// * @author by ZIaDo on 2/15/17.
// */
////@RunWith(RobolectricTestRunner.class)
////@Config(constants = BuildConfig.class, sdk = 19)
//@RunWith(AndroidJUnit4.class)
//public class RealmManagerTest {
//    private RealmManager mRealmManager;
//    private Realm mockRealm;
//    private Observable observable;
//
//    @Before
//    public void before() {
//        mRealmManager = new RealmManager();
//
////        Realm.init(mock(Context.class));
//        Realm.init(InstrumentationRegistry.getContext());
//        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
//                .name("test.realm")
//                .rxFactory(new RealmObservableFactory())
//                .deleteRealmIfMigrationNeeded()
//                .build());
//
//        mockRealm = Realm.getDefaultInstance();
//
//        observable = Observable.just(new TestRealmModel());
//    }
//
//    @Test
//    public void getById() throws Exception {
//        when(mockRealm.where(TestRealmModel.class).equalTo("", 0).findAll().asObservable())
//                .thenReturn(observable);
//        Observable observable = mRealmManager.getById("", 0, TestRealmModel.class);
//
//        verify(mockRealm, times(1)).where(TestRealmModel.class).equalTo("", 0).findAll().asObservable();
////        assertEquals(observable.toBlocking().first().getClass(), TestRealmModel.class);
//    }
//
//    @Test
//    public void getAll() throws Exception {
//        Observable observable = mRealmManager.getAll(TestRealmModel.class);
//        assertEquals(observable.toBlocking().first().getClass(), TestRealmModel.class);
//    }
//
//    @Test
//    public void getQuery() throws Exception {
//        Observable observable = mRealmManager.getQuery(realm -> realm.where(TestRealmModel.class));
//        assertEquals(observable.toBlocking().first().getClass(), TestRealmModel.class);
//    }
//
//    @Test
//    public void putJSONObject() throws Exception {
//        Completable completable = mRealmManager.put(new JSONObject(), "", TestRealmModel.class);
//        TestSubscriber testSubscriber = new TestSubscriber();
//        completable.subscribe(testSubscriber);
//        testSubscriber.assertCompleted();
//    }
//
//    @Test
//    public void putRealmModel() throws Exception {
//        Completable completable = mRealmManager.put(new TestRealmModel(), TestRealmModel.class);
//        TestSubscriber testSubscriber = new TestSubscriber();
//        completable.subscribe(testSubscriber);
//        testSubscriber.assertCompleted();
//    }
//
//    @Test
//    public void putAllJSONArray() throws Exception {
//        Completable completable = mRealmManager.putAll(new JSONArray(), "", TestRealmModel.class);
//        TestSubscriber testSubscriber = new TestSubscriber();
//        completable.subscribe(testSubscriber);
//        testSubscriber.assertCompleted();
//    }
//
//    @Test
//    public void putAllRealmObject() throws Exception {
//        Completable completable = mRealmManager.putAll(new ArrayList<>(), TestRealmModel.class);
//        TestSubscriber testSubscriber = new TestSubscriber();
//        completable.subscribe(testSubscriber);
//        testSubscriber.assertCompleted();
//    }
//
//    @Test
//    public void evictAll() throws Exception {
//        Completable completable = mRealmManager.evictAll(TestRealmModel.class);
//        TestSubscriber testSubscriber = new TestSubscriber();
//        completable.subscribe(testSubscriber);
//        testSubscriber.assertCompleted();
//    }
//
//    @Test
//    public void evictCollection() throws Exception {
//        Completable completable = mRealmManager.evictCollection("", new ArrayList<>(), TestRealmModel.class);
//        TestSubscriber testSubscriber = new TestSubscriber();
//        completable.subscribe(testSubscriber);
//        testSubscriber.assertCompleted();
//    }
//
//    @Test
//    public void evictById() throws Exception {
//        assertEquals(mRealmManager.evictById(TestRealmModel.class, "", 0), true);
//    }
//}
