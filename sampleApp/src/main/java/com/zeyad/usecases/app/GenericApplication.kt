package com.zeyad.usecases.app

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.StrictMode
import android.provider.Settings
import android.util.Base64
import android.util.Log
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import com.zeyad.rxredux.core.eventbus.RxEventBusFactory
import com.zeyad.usecases.app.di.myModule
import com.zeyad.usecases.app.utils.Constants.URLS.API_BASE_URL
import com.zeyad.usecases.network.ProgressInterceptor
import com.zeyad.usecases.network.ProgressListener
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.CertificatePinner
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.android.startKoin
import java.lang.reflect.InvocationTargetException
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * @author by ZIaDo on 9/24/16.
 */
open class GenericApplication : Application() {
    private var disposable: Disposable? = null
    var refwatcher: RefWatcher? = null
        private set

    internal val okHttpBuilder: OkHttpClient.Builder
        get() {
            val builder = OkHttpClient.Builder()
                    .addInterceptor(object : ProgressInterceptor(object : ProgressListener {
                        override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {

                        }
                    }) {
                        override fun isFileIO(originalResponse: Response): Boolean {
                            RxEventBusFactory.getInstance().send(originalResponse)
                            return false
                        }
                    })
                    .addInterceptor(HttpLoggingInterceptor { message -> Log.d("NetworkInfo", message) }
                            .setLevel(if (BuildConfig.DEBUG)
                                HttpLoggingInterceptor.Level.BODY
                            else
                                HttpLoggingInterceptor.Level.NONE))
                    .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                    .certificatePinner(CertificatePinner.Builder()
                            .add(API_BASE_URL,
                                    "sha256/6wJsqVDF8K19zxfLxV5DGRneLyzso9adVdUN/exDacw")
                            .add(API_BASE_URL,
                                    "sha256/k2v657xBsOVe1PQRwOsHsw3bsGT2VzIqz5K+59sNQws=")
                            .add(API_BASE_URL,
                                    "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=")
                            .build())
                    .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS,
                            ConnectionSpec.COMPATIBLE_TLS))
            if (sSlSocketFactory != null && x509TrustManager != null) {
                builder.sslSocketFactory(sSlSocketFactory!!, x509TrustManager!!)
            }
            return builder
        }

    internal open val apiBaseUrl: String
        get() = API_BASE_URL

    internal open val x509TrustManager: X509TrustManager?
        get() = null

    internal open val sSlSocketFactory: SSLSocketFactory?
        get() = null

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        startKoin(listOf(myModule))
//        initializeStrictMode()
        refwatcher = LeakCanary.install(this)
        disposable = Completable.fromAction {
            if (!checkAppTampering(this)) {
                throw IllegalAccessException("App might be tampered with!")
            }
            //            initializeFlowUp();
            //            Rollbar.init(this, "c8c8b4cb1d4f4650a77ae1558865ca87", BuildConfig.DEBUG ? "debug" : "production");
        }.subscribeOn(Schedulers.io())
                .subscribe(Action { }, Consumer<Throwable> { it.printStackTrace() })
        initializeRealm()
//        DataServiceFactory(DataServiceConfig(this, okHttpBuilder, baseUrl = apiBaseUrl,
//                withRealm = true, isWithCache = true, cacheDuration = 3))
//        DataServiceFactory.init(DataServiceConfig.Builder(this)
//                .baseUrl(apiBaseUrl)
//                .okHttpBuilder(okHttpBuilder)
//                .withCache(3, TimeUnit.MINUTES)
//                .withRealm()
//                .build())
    }

    override fun onTerminate() {
        disposable!!.dispose()
        super.onTerminate()
    }

    private fun initializeRealm() {
//        Realm.init(this)
//        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
//                .name("app.realm")
//                .modules(Realm.getDefaultModule(), LibraryModule())
//                .rxFactory(RealmObservableFactory())
//                .deleteRealmIfMigrationNeeded()
//                .build())
    }

    private fun checkAppTampering(context: Context): Boolean {
        return true
        //        return checkAppSignature(context)
        //                && verifyInstaller(context)
        //                && checkEmulator()
        //                && checkDebuggable(context);
    }

    private fun initializeStrictMode() {
        if (BuildConfig.DEBUG || "true" == Settings.System.getString(contentResolver, "firebase.test.lab")) {
            StrictMode.setThreadPolicy(
                    StrictMode.ThreadPolicy.Builder().detectAll().penaltyDeath().penaltyLog().build())
            StrictMode
                    .setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().penaltyDeath().build())
        }
    }

    companion object {
        private val TIME_OUT = 15

        @TargetApi(value = 24)
        private fun checkAppSignature(context: Context): Boolean {
            try {
                val packageInfo = context.packageManager
                        .getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
                for (signature in packageInfo.signatures) {
                    val signatureBytes = signature.toByteArray()
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signatureBytes)
                    val currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                    Log.d("REMOVE_ME", "Include this string as a value for SIGNATURE:$currentSignature")
                    //compare signatures
                    if (java.security.CryptoPrimitive.SIGNATURE.toString() == currentSignature) {
                        return true
                    }
                }
            } catch (e: Exception) {
                Log.e("GenericApplication", "checkAppSignature", e)
                //assumes an issue in checking signature., but we let the caller decide on what to do.
            }

            return false
        }

        private fun verifyInstaller(context: Context): Boolean {
            val installer = context.packageManager.getInstallerPackageName(context.packageName)
            return installer != null && installer.startsWith("com.android.vending")
        }

        private fun checkEmulator(): Boolean {
            try {
                val goldfish = getSystemProperty("ro.hardware").contains("goldfish")
                val emu = getSystemProperty("ro.kernel.qemu").length > 0
                val sdk = getSystemProperty("ro.product.model") == "sdk"
                if (emu || goldfish || sdk) {
                    return true
                }
            } catch (ignored: Exception) {
                Log.e("GenericApplication", "checkEmulator", ignored)
            }

            return false
        }

        @Throws(NoSuchMethodException::class, ClassNotFoundException::class, InvocationTargetException::class, IllegalAccessException::class)
        private fun getSystemProperty(name: String): String {
            val systemPropertyClazz = Class.forName("android.os.SystemProperties")
            return systemPropertyClazz.getMethod("get", *arrayOf<Class<*>>(String::class.java))
                    .invoke(systemPropertyClazz, name) as String
        }

        private fun checkDebuggable(context: Context): Boolean {
            return context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        }
    }
}
