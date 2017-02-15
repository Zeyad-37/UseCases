package com.zeyad.usecases.data.db;

import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.utils.TestRealmModel;
import com.zeyad.usecases.utils.TestRealmObject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.log.RealmLog;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author by ZIaDo on 2/15/17.
 */
//@RunWith(RobolectricGradleTestRunner.class)
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@SuppressStaticInitializationFor("io.realm.internal.Util")
@PrepareForTest({Realm.class, RealmLog.class})
public class RealmManagerTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();
    private RealmManager mRealmManager;
    private Realm mockRealm;

    @Before
    public void before() {
        mRealmManager = new RealmManager();
        mockStatic(RealmLog.class);
        mockStatic(Realm.class);

        Realm mockRealm = PowerMockito.mock(Realm.class);

        when(Realm.getDefaultInstance()).thenReturn(mockRealm);

        this.mockRealm = mockRealm;
    }

    @Test
    public void getById() throws Exception {
        Observable observable = mRealmManager.getById("", 0, TestRealmObject.class);
        assertEquals(observable.toBlocking().first().getClass().getSimpleName(),
                TestRealmObject.class.getSimpleName());
    }

    @Test
    public void getAll() throws Exception {
        Observable observable = mRealmManager.getAll(TestRealmObject.class);
        assertEquals(observable.toBlocking().first().getClass().getSimpleName(),
                TestRealmObject.class.getSimpleName());
    }

    @Test
    public void getQuery() throws Exception {
        Observable observable = mRealmManager.getQuery(realm -> realm.where(TestRealmObject.class));
        assertEquals(observable.toBlocking().first().getClass().getSimpleName(),
                TestRealmObject.class.getSimpleName());
    }

    @Test
    public void put() throws Exception {
        Observable observable = mRealmManager.put(new JSONObject(), "", TestRealmObject.class);
        assertEquals(observable.toBlocking().first().getClass().getSimpleName(),
                TestRealmObject.class.getSimpleName());
    }

    @Test
    public void put1() throws Exception {
        Observable observable = mRealmManager.put(new TestRealmObject(), TestRealmObject.class);
        assertEquals(observable.toBlocking().first().getClass().getSimpleName(),
                TestRealmObject.class.getSimpleName());
    }

    @Test
    public void put2() throws Exception {
        Observable observable = mRealmManager.put(new TestRealmModel(), TestRealmModel.class);
        assertEquals(observable.toBlocking().first().getClass().getSimpleName(),
                TestRealmObject.class.getSimpleName());
    }

    @Test
    public void putAll() throws Exception {
        Observable observable = mRealmManager.putAll(new JSONArray(), "", TestRealmModel.class);
        assertEquals(observable.toBlocking().first().getClass().getSimpleName(),
                TestRealmObject.class.getSimpleName());
    }

    @Test
    public void putAll1() throws Exception {
        Observable observable = mRealmManager.putAll(new ArrayList<>(), TestRealmModel.class);
        assertEquals(observable.toBlocking().first().getClass().getSimpleName(),
                TestRealmObject.class.getSimpleName());
    }

    @Test
    public void evictAll() throws Exception {
        Observable observable = mRealmManager.evictAll(TestRealmModel.class);
        assertEquals(observable.toBlocking().first().getClass().getSimpleName(),
                TestRealmObject.class.getSimpleName());
    }

    @Test
    public void evict() throws Exception {
//        Observable observable = mRealmManager.evict(new TestRealmObject(), TestRealmObject.class);
//        assertEquals(observable.toBlocking().first().getClass().getSimpleName(),
//                TestRealmObject.class.getSimpleName());
    }

    @Test
    public void evictById() throws Exception {
        assertEquals(mRealmManager.evictById(TestRealmObject.class, "", 0), true);
    }

    @Test
    public void evictCollection() throws Exception {
        assertEquals(mRealmManager.evictCollection("", new ArrayList<>(), TestRealmObject.class)
                .toBlocking().first(), TestRealmObject.class.getSimpleName());
    }
}
