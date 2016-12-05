package com.zeyad.generic.usecase.dataaccesslayer;

import android.app.Application;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zeyad.generic.usecase.dataaccesslayer.mapper.RepoMapper;
import com.zeyad.generic.usecase.dataaccesslayer.models.data.RepoRealm;
import com.zeyad.genericusecase.data.mappers.EntityDataMapper;
import com.zeyad.genericusecase.data.mappers.EntityMapper;
import com.zeyad.genericusecase.data.utils.EntityMapperUtil;
import com.zeyad.genericusecase.domain.interactors.data.DataUseCaseFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.zeyad.generic.usecase.dataaccesslayer.utils.Constants.API_BASE_URL;

/**
 * @author by ZIaDo on 9/24/16.
 */

public class GenericApplication extends Application {

    private static GenericApplication sInstance;
    private static final int TIME_OUT = 15;

    public static GenericApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initializeRealm();
        DataUseCaseFactory.initWithRealm(this, new EntityMapperUtil() {
            @Override
            public EntityMapper getDataMapper(Class dataClass) {
                if (dataClass == RepoRealm.class)
                    return new RepoMapper();
                return new EntityDataMapper();
            }
        }, provideOkHttpClientBuilder(), null);
        DataUseCaseFactory.setBaseURL(API_BASE_URL);
        Fresco.initialize(this);
    }

    private OkHttpClient.Builder provideOkHttpClientBuilder() {
        return new OkHttpClient.Builder()
                .addInterceptor(provideHttpLoggingInterceptor())
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS);
    }

    private Cache provideCache() {
        try {
            return new Cache(new File(getCacheDir(), "http-cache"), 10 * 1024 * 1024); // 10 MB
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
        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .name("app.realm")
                .modules(Realm.getDefaultModule(), new LibraryModule())
                .rxFactory(new RealmObservableFactory())
                .deleteRealmIfMigrationNeeded()
                .build());
    }
}
