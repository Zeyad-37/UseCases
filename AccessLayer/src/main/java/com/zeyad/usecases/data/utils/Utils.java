package com.zeyad.usecases.data.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.gson.Gson;
import com.zeyad.usecases.data.requests.FileIORequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.data.services.GenericJobService;
import com.zeyad.usecases.data.services.GenericNetworkQueueIntentService;

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.DOWNLOAD_FILE;
import static com.zeyad.usecases.data.services.GenericNetworkQueueIntentService.UPLOAD_FILE;

public class Utils {
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

    public static boolean doesContextBelongsToApplication(Context mContext) {
        return !(mContext instanceof Activity || mContext instanceof Service);
    }

    public static int getNextId(Class clazz, String column) {
        return Utils.getMaxId(clazz, column) + 1;
    }

    // Simple logging to let us know what each source is returning
    public static Observable.Transformer<?, ?> logSources(final String source) {
        return observable -> observable.doOnNext(entities -> {
            if (entities == null)
                System.out.println(source + " does not have any data.");
            else
                System.out.println(source + " has the data you are looking for!");
        });
    }

    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (hasLollipop()) {
            Network[] networks = connectivityManager.getAllNetworks();
            for (Network network : networks)
                if (connectivityManager.getNetworkInfo(network).getState().equals(NetworkInfo.State.CONNECTED))
                    return true;
        } else if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED)
                        return true;
        }
        return false;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
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

    public static boolean isOnWifi(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isCharging(Context context) {
        boolean charging = false;
        final Intent batteryIntent = context.registerReceiver(null,
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

    public static RequestBody createPartFromString(Object descriptionString) {
        return RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), String.valueOf(descriptionString));
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static void queuePostCore(FirebaseJobDispatcher dispatcher, PostRequest postRequest, Gson gson) {
        Bundle extras = new Bundle(2);
        extras.putString(GenericNetworkQueueIntentService.JOB_TYPE, GenericNetworkQueueIntentService.POST);
        extras.putString(GenericNetworkQueueIntentService.PAYLOAD, gson.toJson(postRequest));
        dispatcher.mustSchedule(dispatcher.newJobBuilder()
                .setService(GenericJobService.class)
                .setTag(GenericNetworkQueueIntentService.POST)
                .setRecurring(false)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(Trigger.executionWindow(0, 60))
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(Constraint.ON_ANY_NETWORK, Constraint.DEVICE_CHARGING)
                .setExtras(extras)
                .build());
    }

    public static void queueFileIOCore(FirebaseJobDispatcher dispatcher, boolean isDownload,
                                       FileIORequest fileIORequest, Gson gson) {
        Bundle extras = new Bundle(2);
        extras.putString(GenericNetworkQueueIntentService.JOB_TYPE, isDownload ? DOWNLOAD_FILE : UPLOAD_FILE);
        extras.putString(GenericNetworkQueueIntentService.PAYLOAD, gson.toJson(fileIORequest));
        dispatcher.mustSchedule(dispatcher.newJobBuilder()
                .setService(GenericJobService.class)
                .setTag(isDownload ? DOWNLOAD_FILE : UPLOAD_FILE)
                .setRecurring(false)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(Trigger.executionWindow(0, 60))
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(fileIORequest.onWifi() ? Constraint.ON_UNMETERED_NETWORK : Constraint.ON_ANY_NETWORK,
                        fileIORequest.isWhileCharging() ? Constraint.DEVICE_CHARGING : 0)
                .setExtras(extras)
                .build());
    }
}
