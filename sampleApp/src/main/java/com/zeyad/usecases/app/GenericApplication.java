package com.zeyad.usecases.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import com.zeyad.usecases.domain.interactors.data.DataUseCaseConfig;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;

import java.io.File;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

import io.flowup.FlowUp;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;
import jp.wasabeef.takt.Takt;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.zeyad.usecases.app.utils.Constants.URLS.API_BASE_URL;

/**
 * @author by ZIaDo on 9/24/16.
 */
public class GenericApplication extends Application {

    private static final int TIME_OUT = 15, CACHE_EXPIRY = 3;
    private static GenericApplication sInstance;

    public static GenericApplication getInstance() {
        return sInstance;
    }

    @TargetApi(value = 24)
    private static boolean checkAppSignature(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                byte[] signatureBytes = signature.toByteArray();
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("REMOVE_ME", "Include this string as a value for SIGNATURE:" + currentSignature);
                //compare signatures
                if (java.security.CryptoPrimitive.SIGNATURE.equals(currentSignature)) {
                    return true;
                }
            }
        } catch (Exception e) {
            //assumes an issue in checking signature., but we let the caller decide on what to do.
        }
        return false;
    }

    public static boolean verifyInstaller(Context context) {
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
        return installer != null && installer.startsWith("com.android.vending");
    }

    public static boolean checkEmulator() {
        try {
            boolean goldfish = getSystemProperty("ro.hardware").contains("goldfish");
            boolean emu = getSystemProperty("ro.kernel.qemu").length() > 0;
            boolean sdk = getSystemProperty("ro.product.model").equals("sdk");
            if (emu || goldfish || sdk) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private static String getSystemProperty(String name) throws Exception {
        Class systemPropertyClazz = Class.forName("android.os.SystemProperties");
        return (String) systemPropertyClazz.getMethod("get", new Class[]{String.class})
                .invoke(systemPropertyClazz, name);
    }

    public static boolean checkDebuggable(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    @Override
    public void onCreate() {
//        initializeStrickMode();
        super.onCreate();
        sInstance = this;
//        checkAppTampering(sInstance);
        if (BuildConfig.DEBUG)
            Takt.stock(this).play();
        initializeRealm();
        DataUseCaseFactory.init(new DataUseCaseConfig.Builder(this)
                .baseUrl(API_BASE_URL)
                .withCache(CACHE_EXPIRY, TimeUnit.MINUTES)
                .withRealm()
//                .entityMapper(new AutoMap_DAOMapperFactory())
                .okHttpBuilder(provideOkHttpClientBuilder())
//                .okhttpCache(provideCache())
                .build());
        initializeStetho();
        initializeFlowUp();
    }

    private void initializeStrickMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectNetwork()
//                    .detectCustomSlowCalls()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
//                    .detectLeakedClosableObjects()
                    .penaltyLog()
//                    .penaltyDeath()
                    .build());
        }
    }

    private OkHttpClient.Builder provideOkHttpClientBuilder() {
        return new OkHttpClient.Builder()
                .addInterceptor(provideHttpLoggingInterceptor())
//                .addInterceptor(provideCacheInterceptor())
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
                    .maxAge(CACHE_EXPIRY, TimeUnit.MINUTES)
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

    private boolean checkAppTampering(Context context) {
        return checkAppSignature(context) && verifyInstaller(context) && checkEmulator()
                && checkDebuggable(context);
    }
}
