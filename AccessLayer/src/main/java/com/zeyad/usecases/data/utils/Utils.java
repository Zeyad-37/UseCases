package com.zeyad.usecases.data.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
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

    public static Observable.Transformer<?, ?> logSources(final String source) {
        return observable -> observable.doOnNext(entities -> {
            if (entities == null)
                System.out.println(source + " does not have any data.");
            else
                System.out.println(source + " has the data you are looking for!");
        });
    }

    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
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

    public static boolean isNetworkDecent() {
        return ConnectionClassManager.getInstance().getCurrentBandwidthQuality()
                .compareTo(ConnectionQuality.MODERATE) >= 0;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static int getMaxId(Class clazz, String column) {
        Number currentMax = Realm.getDefaultInstance().where(clazz).max(column);
        if (currentMax != null)
            return currentMax.intValue();
        else return 0;
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
