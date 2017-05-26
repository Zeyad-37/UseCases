package com.zeyad.usecases.utils;

import android.support.annotation.NonNull;

import com.zeyad.usecases.db.DataBaseManager;

public interface DataBaseManagerUtil {
    @NonNull
    DataBaseManager getDataBaseManager(Class dataClass);
}
