package com.zeyad.genericusecase.data.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.zeyad.genericusecase.Config;
import com.zeyad.genericusecase.data.db.DataBaseManager;

import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class Utils {
    public static boolean doesContextBelongsToApplication(Context mContext) {
        return !(mContext instanceof Activity || mContext instanceof Service);
    }

    public static int getNextId(Class clazz, String column) {
        return Utils.getMaxId(clazz, column) + 1;
    }

    // Simple logging to let us know what each source is returning
    public static Observable.Transformer<List, List> logSources(final String source, @NonNull DataBaseManager realmManager) {
        return observable -> observable.doOnNext(entities -> {
            if (entities == null)
                System.out.println(source + " does not have any data.");
            else if (!realmManager.areItemsValid(DataBaseManager.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE))
                System.out.println(source + " has stale data.");
            else
                System.out.println(source + " has the data you are looking for!");
        });
    }

    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (hasLollipop()) {
            Network[] networks = connectivityManager.getAllNetworks();
            for (int i = 0, networksLength = networks.length; i < networksLength; i++)
                if (connectivityManager.getNetworkInfo(networks[i]).getState().equals(NetworkInfo.State.CONNECTED))
                    return true;
        } else if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null)
                for (int i = 0, infoLength = info.length; i < infoLength; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean scheduleJob(@NonNull Context context, JobInfo jobInfo) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (scheduler.schedule(jobInfo) == 1) {
            Log.d("JobScheduler", "Job scheduled successfully!");
            return true;
        } else {
            Log.d("JobScheduler", "Failed to scheduled Job!");
            return false;
        }
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isGooglePlayServicesAvailable(@NonNull Context context) {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    @Nullable
    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(@Nullable CompositeSubscription subscription) {
        if (subscription == null || subscription.isUnsubscribed())
            return new CompositeSubscription();
        return subscription;
    }

    public static void unsubscribeIfNotNull(@Nullable Subscription subscription) {
        if (subscription != null)
            subscription.unsubscribe();
    }

    public static int getMaxId(Class clazz, String column) {
        Number currentMax = Realm.getDefaultInstance().where(clazz).max(column);
        if (currentMax != null)
            return currentMax.intValue();
        else return 0;
    }

    public static boolean isOnWifi() {
        ConnectivityManager connManager = (ConnectivityManager) Config.getInstance().getContext().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }

    public static boolean isCharging() {
        boolean charging = false;
        final Intent batteryIntent = Config.getInstance().getContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean batteryCharge = status == BatteryManager.BATTERY_STATUS_CHARGING;

        int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        if (batteryCharge) charging = true;
        if (usbCharge) charging = true;
        if (acCharge) charging = true;

        return charging;

//        Intent intent = Config.getInstance().getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
//        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    public static boolean isChargingReqCompatible(boolean isChargingCurrently, boolean doWhileCharging) {
        return !doWhileCharging || isChargingCurrently;
    }
}
