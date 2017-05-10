package com.zeyad.usecases.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author zeyad on 8/30/16.
 */
public class TLSSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory internalSSLSocketFactory;

    public TLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{systemDefaultTrustManager()}, null);
        internalSSLSocketFactory = sslContext.getSocketFactory();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return internalSSLSocketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return internalSSLSocketFactory.getSupportedCipherSuites();
    }

    @Nullable
    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(s, host, port, autoClose));
    }

    @Nullable
    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port));
    }

    @Nullable
    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port, localHost, localPort));
    }

    @Nullable
    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port));
    }

    @Nullable
    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(address, port, localAddress, localPort));
    }

    @Nullable
    private Socket enableTLSOnSocket(@Nullable Socket socket) {
        if (socket != null && (socket instanceof SSLSocket))
            ((SSLSocket) socket).setEnabledProtocols(new String[]{"TLSv1.1", "TLSv1.2"});
        return socket;
    }

    @NonNull
    private X509TrustManager systemDefaultTrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory
                    .getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager))
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            return (X509TrustManager) trustManagers[0];
        } catch (GeneralSecurityException e) {
            throw new AssertionError(); // The system has no TLS. Just give up.
        }
    }
}
