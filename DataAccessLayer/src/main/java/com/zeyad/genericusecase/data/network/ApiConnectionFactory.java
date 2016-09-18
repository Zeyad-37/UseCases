package com.zeyad.genericusecase.data.network;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class ApiConnectionFactory {

    /**
     * This method will use default ok http and cache  to (re)initialize the instance of IApiConnection  implementation
     */
    public static void init() {
        ApiConnection.init();
    }

    /**
     * This method will use provided ok http and cache  to (re)initialize the instance of IApiConnection  implementation
     *
     * @param okhttpBuilder okhttp builder to use.
     * @param cache         cache to use
     */
    public static void init(OkHttpClient.Builder okhttpBuilder, Cache cache) {
        ApiConnection.init(okhttpBuilder, cache);
    }

    /**
     * This method will use provided ok http and default cache  to (re)initialize the instance of IApiConnection  implementation
     *
     * @param okhttpBuilder okhttp builder to use
     */
    public static void init(OkHttpClient.Builder okhttpBuilder) {
        ApiConnection.init(okhttpBuilder);
    }

    /**
     * @return the last instance created.
     */
    public static IApiConnection getInstance() {
        return ApiConnection.getInstance();
    }
}
