package com.zeyad.usecases.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zeyad.usecases.Config;

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
        return Config.isWithDisk() && shouldPersist;
    }

    public boolean withCache(boolean shouldCache) {
        return Config.isWithCache() && shouldCache;
    }
}
