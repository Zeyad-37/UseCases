package com.zeyad.usecases.app;

import android.app.Application;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zeyad.usecases.app.presentation.models.AutoMap_DAOMapperUtil;
import com.zeyad.usecases.app.presentation.models.UserModelMapper;
import com.zeyad.usecases.app.presentation.models.UserRealm;
import com.zeyad.usecases.data.mappers.DAOMapperUtil;
import com.zeyad.usecases.data.mappers.DefaultDAOMapper;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseConfig;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.flowup.FlowUp;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.zeyad.usecases.app.utils.Constants.API_BASE_URL;

/**
 * @author by ZIaDo on 9/24/16.
 */
public class GenericApplication extends Application {

    private static final int TIME_OUT = 15;
    private static GenericApplication sInstance;

    public static GenericApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initializeRealm();
        DataUseCaseFactory.init(new DataUseCaseConfig.Builder(this)
                .baseUrl(API_BASE_URL)
                .withCache()
                .withRealm()
                .entityMapper(new AutoMap_DAOMapperUtil())
                .entityMapper(new DAOMapperUtil() {
                    @Override
                    public IDAOMapper getDataMapper(Class dataClass) {
                        if (dataClass == UserRealm.class) {
                            return UserModelMapper.getInstance();
                        }
                        return DefaultDAOMapper.getInstance();
                    }
                })
                .okHttpBuilder(provideOkHttpClientBuilder())
                .okhttpCache(provideCache())
                .build());
//        PrefsUseCaseFactory.init(this, "com.usecase.zeyad.PREFS");
        Fresco.initialize(this);
        initializeStetho();
        initializeFlowUp();
    }

    private OkHttpClient.Builder provideOkHttpClientBuilder() {
        return new OkHttpClient.Builder()
                .addInterceptor(provideHttpLoggingInterceptor())
                .addInterceptor(provideCacheInterceptor())
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS);
    }

    private HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor(message -> Log.d("NetworkInfo", message))
                .setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    private Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(getCacheDir(), "http-cache"), 10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }

    private Interceptor provideCacheInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());
            // re-write response header to force use of cache
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(2, TimeUnit.MINUTES)
                    .build();
            return response.newBuilder()
                    .header("Cache-Control", cacheControl.toString())
                    .build();
        };
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

    private void initializeStetho() {
//        Stetho.initialize(Stetho.newInitializerBuilder(this)
//                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
//                .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
//                .build());
//        RealmInspectorModulesProvider.builder(this)
//                .withFolder(getCacheDir())
////                .withEncryptionKey("encrypted.realm", key)
//                .withMetaTables()
//                .withDescendingOrder()
//                .withLimit(1000)
//                .databaseNamePattern(Pattern.compile(".+\\.realm"))
//                .build();
    }

    private void initializeFlowUp() {
        FlowUp.Builder.with(this)
                .apiKey(getString(R.string.flow_up_api_key))
                .forceReports(BuildConfig.DEBUG)
                .start();
    }
}
