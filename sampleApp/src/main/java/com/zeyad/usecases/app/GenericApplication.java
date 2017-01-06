package com.zeyad.usecases.app;

import android.app.Application;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zeyad.usecases.app.presentation.models.AutoMap_DAOMapperUtil;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseConfig;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;

import java.util.concurrent.TimeUnit;

import io.flowup.FlowUp;
import io.realm.Realm;
import okhttp3.OkHttpClient;
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
                .withCache(true)
                .withRealm(true)
                .entityMapper(new AutoMap_DAOMapperUtil())
                .okHttpBuilder(provideOkHttpClientBuilder())
                .build());
//        PrefsUseCaseFactory.init(this, "com.usecase.zeyad.PREFS");
        Fresco.initialize(this);
        initializeFlowUp();
    }

    private OkHttpClient.Builder provideOkHttpClientBuilder() {
        return new OkHttpClient.Builder()
                .addInterceptor(provideHttpLoggingInterceptor())
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS);
    }

    private HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor(message -> Log.d("NetworkInfo", message))
                .setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    private void initializeRealm() {
        Realm.init(this);
//        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
//                .name("app.realm")
//                .modules(Realm.getDefaultModule(), new LibraryModule())
//                .rxFactory(new RealmObservableFactory())
//                .deleteRealmIfMigrationNeeded()
//                .build());
    }

    private void initializeFlowUp() {
        FlowUp.Builder.with(this)
                .apiKey(getString(R.string.flow_up_api_key))
                .forceReports(BuildConfig.DEBUG)
                .start();
    }
}
