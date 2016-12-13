package com.zeyad.usecases.data.network;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class ApiConnectionFactory {

    /**
     * This method will use default ok http and cache  to (re)initialize the instance of IApiConnection implementation
     */
    public static void init() {
        ApiConnection.init();
    }

    /**
     * This method will use provided ok http and cache to (re)initialize the instance of IApiConnection implementation
     *
     * @param okhttpBuilder okhttp builder to use.
     * @param cache         cache to use
     */
    public static void init(OkHttpClient.Builder okhttpBuilder, Cache cache) {
        ApiConnection.init(okhttpBuilder, cache);
    }

    /**
     * @return the last instance created.
     */
    public static IApiConnection getInstance() {
        return ApiConnection.getInstance();
    }
}
