package com.zeyad.usecases.utils;

import android.app.Activity;
import android.app.Service;
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
import com.firebase.jobdispatcher.Trigger;
import com.zeyad.usecases.requests.FileIORequest;
import com.zeyad.usecases.requests.PostRequest;
import com.zeyad.usecases.services.GenericJobService;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Utils {

    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

    private static Utils instance;

    public Utils() {
        instance = this;
    }

    public static Utils getInstance() {
        if (instance == null)
            instance = new Utils();
        return instance;
    }

    public boolean doesContextBelongsToApplication(Context mContext) {
        return !(mContext instanceof Activity || mContext instanceof Service);
    }

    public int getNextId(Class clazz, String column) {
        return getMaxId(clazz, column) + 1;
    }

    public boolean isNetworkAvailable(@NonNull Context context) {
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

    public boolean isNotEmpty(List list) {
        return list != null && !list.isEmpty();
    }

    public boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public int getMaxId(Class clazz, String column) {
        Realm realm = Realm.getDefaultInstance();
        try {
            Number currentMax = realm.where(clazz).max(column);
            if (currentMax != null)
                return currentMax.intValue();
            else return 0;
        } finally {
            realm.close();
        }
    }

    public RequestBody createPartFromString(Object descriptionString) {
        return RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), String.valueOf(descriptionString));
    }

    public void queuePostCore(FirebaseJobDispatcher dispatcher, PostRequest postRequest) {
        Bundle extras = new Bundle(2);
        extras.putString(GenericJobService.JOB_TYPE, GenericJobService.POST);
        extras.putParcelable(GenericJobService.PAYLOAD, postRequest);
        dispatcher.mustSchedule(dispatcher.newJobBuilder()
                .setService(GenericJobService.class)
                .setTag(GenericJobService.POST)
                .setRecurring(false)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(Trigger.executionWindow(0, 60))
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(Constraint.ON_ANY_NETWORK, Constraint.DEVICE_CHARGING)
                .setExtras(extras)
                .build());
        Log.d("FBJDQ", postRequest.getMethod() + " request is queued successfully!");
    }

    public void queueFileIOCore(FirebaseJobDispatcher dispatcher, boolean isDownload, FileIORequest fileIORequest) {
        Bundle extras = new Bundle(2);
        extras.putString(GenericJobService.JOB_TYPE, isDownload ? GenericJobService.DOWNLOAD_FILE : GenericJobService.UPLOAD_FILE);
        extras.putParcelable(GenericJobService.PAYLOAD, fileIORequest);
        dispatcher.mustSchedule(dispatcher.newJobBuilder()
                .setService(GenericJobService.class)
                .setTag(isDownload ? GenericJobService.DOWNLOAD_FILE : GenericJobService.UPLOAD_FILE)
                .setRecurring(false)
                .setLifetime(Lifetime.FOREVER)
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(fileIORequest.onWifi() ? Constraint.ON_UNMETERED_NETWORK : Constraint.ON_ANY_NETWORK,
                        fileIORequest.isWhileCharging() ? Constraint.DEVICE_CHARGING : 0)
                .setExtras(extras)
                .build());
        Log.d("FBJDQ", String.format("%s file request is queued successfully!", isDownload ? "Download" : "Upload"));
    }

    @Nullable
    public List<Long> convertToListOfId(@Nullable JSONArray jsonArray) {
        List<Long> idList = new ArrayList<>();
        if (jsonArray != null && jsonArray.length() > 0) {
            try {
                jsonArray.getLong(0);
            } catch (JSONException e) {
                return null;
            }
            idList = new ArrayList<>(jsonArray.length());
            for (int i = 0, length = jsonArray.length(); i < length; i++)
                try {
                    idList.add(jsonArray.getLong(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
        return idList;
    }
}
