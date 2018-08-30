package com.zeyad.usecases.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.zeyad.usecases.Config;
import com.zeyad.usecases.requests.FileIORequest;
import com.zeyad.usecases.requests.PostRequest;
import com.zeyad.usecases.services.GenericJobService;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static Utils instance;

    private Utils() {
    }

    public static synchronized Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    public boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = connectivityManager.getAllNetworks();
                for (Network network : networks) {
                    if (connectivityManager
                            .getNetworkInfo(network)
                            .getState()
                            .equals(NetworkInfo.State.CONNECTED)) {
                        return true;
                    }
                }
            } else {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void queuePostCore(@NonNull FirebaseJobDispatcher dispatcher, @NonNull PostRequest postRequest, int trailCount) {
        Bundle extras = new Bundle(3);
        extras.putString(GenericJobService.JOB_TYPE, GenericJobService.POST);
        extras.putParcelable(GenericJobService.PAYLOAD, postRequest);
        extras.putInt(GenericJobService.TRIAL_COUNT, trailCount);
        queueCore(dispatcher, extras, postRequest.getMethod(), postRequest.getOnWifi(), postRequest.getWhileCharging());
    }

    public void queueFileIOCore(@NonNull FirebaseJobDispatcher dispatcher, boolean isDownload,
                                @NonNull FileIORequest fileIORequest, int trailCount) {
        Bundle extras = new Bundle(3);
        extras.putString(GenericJobService.JOB_TYPE, isDownload ?
                GenericJobService.DOWNLOAD_FILE : GenericJobService.UPLOAD_FILE);
        extras.putParcelable(GenericJobService.PAYLOAD, fileIORequest);
        extras.putInt(GenericJobService.TRIAL_COUNT, trailCount);
        queueCore(dispatcher, extras, (isDownload ? "Download" : "Upload") + " file",
                fileIORequest.getOnWifi(), fileIORequest.getWhileCharging());
    }

    private void queueCore(@NonNull FirebaseJobDispatcher dispatcher, @NonNull Bundle bundle, String message,
                           boolean isOnWifi, boolean whileCharging) {
        int network = isOnWifi ? Constraint.ON_UNMETERED_NETWORK : Constraint.ON_ANY_NETWORK;
        int power = whileCharging ? Constraint.DEVICE_CHARGING : Constraint.DEVICE_IDLE;
        try {
            dispatcher.mustSchedule(dispatcher.newJobBuilder()
                    .setService(GenericJobService.class)
                    .setTag(bundle.getString("JOB_TYPE")
                            .concat(String.valueOf(System.currentTimeMillis())))
                    .setRecurring(false)
                    .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                    //                                          .setTrigger(Trigger.executionWindow(0, 10))
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .setExtras(bundle)
                    .setConstraints(network, power)
                    .build());
        } catch (Exception ignored) {

        }
        Log.d("FBJD", message + " request is queued successfully!");
    }

    public <T> List<T> convertToListOfId(@Nullable JSONArray jsonArray, Class<T> idType) {
        List<T> idList = new ArrayList<>();
        if (jsonArray != null && jsonArray.length() > 0) {
            int length = jsonArray.length();
            idList = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                try {
                    idList.add(idType.cast(jsonArray.get(i)));
                } catch (JSONException e) {
                    Log.e("Utils", "convertToListOfId", e);
                }
            }
        }
        return idList;
    }

    public List<String> convertToStringListOfId(@Nullable JSONArray jsonArray) {
        List<String> idList = new ArrayList<>();
        if (jsonArray != null && jsonArray.length() > 0) {
            idList = new ArrayList<>(jsonArray.length());
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                try {
                    idList.add(String.valueOf(jsonArray.get(i)));
                } catch (JSONException e) {
                    Log.e("Utils", "convertToListOfId", e);
                }
            }
        }
        return idList;
    }

    public boolean withDisk(boolean shouldPersist) {
        return Config.INSTANCE.isWithDisk() && shouldPersist;
    }

    public boolean withCache(boolean shouldCache) {
        return Config.INSTANCE.getWithCache() && shouldCache;
    }
}
