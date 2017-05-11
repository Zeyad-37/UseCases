//package com.zeyad.usecases.db;
//
//import android.content.Context;
//import android.support.test.rule.BuildConfig;
//
//import com.zeyad.usecases.TestRealmModel;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.powermock.core.classloader.annotations.PowerMockIgnore;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.annotation.Config;
//
//import java.util.ArrayList;
//
//import io.realm.Realm;
//import io.realm.RealmConfiguration;
//import io.realm.log.RealmLog;
//import io.realm.rx.RealmObservableFactory;
//import rx.Observable;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
///**
// * @author by ZIaDo on 2/15/17.
// */
//@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class, sdk = 19)
//@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
//@SuppressStaticInitializationFor("io.realm.internal.Util")
//@PrepareForTest({Realm.class, RealmLog.class})
//public class RealmManagerTest {
//    private RealmManager mRealmManager;
//    private Realm mockRealm;
//    private Observable observable;
//
//    @Before
//    public void before() {
//        mRealmManager = new RealmManager();
//
//        Realm.init(mock(Context.class));
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
//        Observable observable = mRealmManager.put(new JSONObject(), "", TestRealmModel.class);
//        assertEquals(observable.toBlocking().first().getClass(), TestRealmModel.class);
//    }
//
//    @Test
//    public void putRealmModel() throws Exception {
//        Observable observable = mRealmManager.put(new TestRealmModel(), TestRealmModel.class);
//        assertEquals(observable.toBlocking().first().getClass(), TestRealmModel.class);
//    }
//
//    @Test
//    public void putAllJSONArray() throws Exception {
//        Observable observable = mRealmManager.putAll(new JSONArray(), "", TestRealmModel.class);
//        assertEquals(observable.toBlocking().first().getClass(), TestRealmModel.class);
//    }
//
//    @Test
//    public void putAllRealmObject() throws Exception {
//        Observable observable = mRealmManager.putAll(new ArrayList<>(), TestRealmModel.class);
//        assertEquals(observable.toBlocking().first().getClass(), TestRealmModel.class);
//    }
//
//    @Test
//    public void evictAll() throws Exception {
//        Observable observable = mRealmManager.evictAll(TestRealmModel.class);
//        assertEquals(observable.toBlocking().first().getClass(), TestRealmModel.class);
//    }
//
//    @Test
//    public void evict() throws Exception {
////        Observable observable = mRealmManager.evict(new TestRealmModel(), TestRealmModel.class);
////        assertEquals(observable.toBlocking().first().getClass(),
////                TestRealmModel.class);
//    }
//
//    @Test
//    public void evictById() throws Exception {
//        assertEquals(mRealmManager.evictById(TestRealmModel.class, "", 0), true);
//    }
//
//    @Test
//    public void evictCollection() throws Exception {
//        assertEquals(mRealmManager.evictCollection("", new ArrayList<>(), TestRealmModel.class)
//                .toBlocking().first(), TestRealmModel.class);
//    }
//}
