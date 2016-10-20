package com.zeyad.generic.usecase.dataaccesslayer;

import android.app.Application;
import android.util.Log;

import com.zeyad.generic.usecase.dataaccesslayer.di.components.ApplicationComponent;
import com.zeyad.generic.usecase.dataaccesslayer.di.components.DaggerApplicationComponent;
import com.zeyad.generic.usecase.dataaccesslayer.di.modules.ApplicationModule;
import com.zeyad.genericusecase.domain.interactors.GenericUseCaseFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author by ZIaDo on 9/24/16.
 */

public class GenericApplication extends Application {

    private static GenericApplication sInstance;
    private ApplicationComponent applicationComponent;

    public static GenericApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initializeInjector();
        initializeRealm();
        GenericUseCaseFactory.initWithoutDB(getApplicationContext());
    }

    private Cache provideCache() {
        try {
            return new Cache(new File(getCacheDir(), "http-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Interceptor provideCacheInterceptor() {
        return chain -> {
            // re-write response header to force use of cache
            return chain.proceed(chain.request())
                    .newBuilder()
                    .header("Cache-Control", new CacheControl.Builder()
                            .maxAge(2, TimeUnit.MINUTES)
                            .build().toString())
                    .build();
        };
    }

    private HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor(message -> Log.d("NetworkInfo", message))
                .setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    private void initializeRealm() {
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this)
                .name("app.realm")
                .modules(Realm.getDefaultModule(), new LibraryModule())
                .rxFactory(new RealmObservableFactory())
                .deleteRealmIfMigrationNeeded()
                .build());
    }

    private void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
