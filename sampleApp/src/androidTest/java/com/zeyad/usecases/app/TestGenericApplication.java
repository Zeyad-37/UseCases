package com.zeyad.usecases.app;

import android.support.annotation.NonNull;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import io.appflate.restmock.RESTMockServer;
import okhttp3.internal.tls.SslClient;

/**
 * @author by ZIaDo on 6/15/17.
 */
public class TestGenericApplication extends GenericApplication {
    @NonNull
    @Override
    public String getApiBaseUrl() {
        return RESTMockServer.getUrl();
    }

    @Override
    X509TrustManager getX509TrustManager() {
        return SslClient.localhost().trustManager;
    }

    @Override
    SSLSocketFactory getSSlSocketFactory() {
        return SslClient.localhost().socketFactory;
    }
}
