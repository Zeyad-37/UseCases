package com.zeyad.genericusecase.data.utils;

public class Constants {
    public static final long EXPIRATION_TIME = 600000;
    public static final String APPLICATION_JSON = "application/json";
    public static final int COUNTER_START = 1;
    public static final int ATTEMPTS = 3;
    public static final String COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE = "collection_last_cache_update";
    public static final String DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE = "detail_last_cache_update";
    public static final String POST_TAG = "postObject";
    public static final String FILE_IO_TAG = "fileIOObject";

    // Errors
    public static final String NETWORK_ERROR_PERSISTED = "No Notwork but post request persisted to queue!\\n Request\n" +
            "        will be posted as soon as network is available.",
            NETWORK_ERROR_NOT_PERSISTED = "<![CDATA[No network & could not persist request to queue!" +
                    "\\nGoogle play services not available and android version less than 5.0!]]";
}
