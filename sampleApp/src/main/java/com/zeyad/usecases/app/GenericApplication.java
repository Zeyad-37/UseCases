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

import com.rollbar.android.Rollbar;
import com.squareup.leakcanary.LeakCanary;
import com.zeyad.usecases.api.DataServiceConfig;
import com.zeyad.usecases.api.DataServiceFactory;

import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

import io.flowup.FlowUp;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;

import static com.zeyad.usecases.app.utils.Constants.URLS.API_BASE_URL;

/**
 * @author by ZIaDo on 9/24/16.
 */
public class GenericApplication extends Application {

    @TargetApi(value = 24)
    private static boolean checkAppSignature(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                byte[] signatureBytes = signature.toByteArray();
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signatureBytes);
                final String currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("REMOVE_ME", "Include this string as a value for SIGNATURE:" + currentSignature);
                //compare signatures
                if (java.security.CryptoPrimitive.SIGNATURE.toString().equals(currentSignature)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e("GenericApplication", "checkAppSignature", e);
            //assumes an issue in checking signature., but we let the caller decide on what to do.
        }
        return false;
    }

    private static boolean verifyInstaller(Context context) {
        final String installer =
                context.getPackageManager().getInstallerPackageName(context.getPackageName());
        return installer != null && installer.startsWith("com.android.vending");
    }

    private static boolean checkEmulator() {
        try {
            boolean goldfish = getSystemProperty("ro.hardware").contains("goldfish");
            boolean emu = getSystemProperty("ro.kernel.qemu").length() > 0;
            boolean sdk = getSystemProperty("ro.product.model").equals("sdk");
            if (emu || goldfish || sdk) {
                return true;
            }
        } catch (Exception ignored) {
            Log.e("GenericApplication", "checkEmulator", ignored);
        }
        return false;
    }

    private static String getSystemProperty(String name) throws Exception {
        Class systemPropertyClazz = Class.forName("android.os.SystemProperties");
        return (String) systemPropertyClazz.getMethod("get", new Class[]{String.class})
                .invoke(systemPropertyClazz, name);
    }

    private static boolean checkDebuggable(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    @Override
    public void onCreate() {
//        initializeStrictMode();
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        Completable.fromAction(() -> {
//            checkAppTampering(this);
            initializeFlowUp();
            Rollbar.init(this, "c8c8b4cb1d4f4650a77ae1558865ca87", "production");
        }).subscribeOn(Schedulers.io())
                .subscribe(() -> {
                }, Throwable::printStackTrace);
        initializeRealm();
        DataServiceFactory.init(new DataServiceConfig.Builder(this)
                .baseUrl(API_BASE_URL)
                .withCache(3, TimeUnit.MINUTES)
                .withRealm()
                .build());
    }

    private void initializeStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }
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

    private void initializeFlowUp() {
        FlowUp.Builder.with(this)
                .apiKey(getString(R.string.flow_up_api_key))
                .forceReports(BuildConfig.DEBUG)
                .start();
    }

    private boolean checkAppTampering(Context context) {
        return checkAppSignature(context)
                && verifyInstaller(context)
                && checkEmulator()
                && checkDebuggable(context);
    }
}
