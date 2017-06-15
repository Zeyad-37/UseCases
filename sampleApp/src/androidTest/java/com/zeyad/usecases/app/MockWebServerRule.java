package com.zeyad.usecases.app;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import okhttp3.internal.tls.SslClient;
import okhttp3.mockwebserver.MockWebServer;

/**
 * @author by ZIaDo on 6/15/17.
 */

public class MockWebServerRule implements TestRule {
    public final MockWebServer server = new MockWebServer();

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                server.useHttps(SslClient.localhost().socketFactory, false);
                server.start();
                base.evaluate();
                server.shutdown();
            }
        };
    }

}
