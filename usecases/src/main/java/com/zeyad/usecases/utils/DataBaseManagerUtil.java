package com.zeyad.usecases.utils;

import com.zeyad.usecases.db.DataBaseManager;

public interface DataBaseManagerUtil {

    DataBaseManager getDataBaseManager(Class dataClass);
}
