package com.zeyad.usecases.integration;

import android.app.Application;

import org.robolectric.DefaultTestLifecycle;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;

import java.lang.reflect.Method;

import io.appflate.restmock.RESTMockServerStarter;
import io.appflate.restmock.android.AndroidLocalFileParser;
import io.appflate.restmock.android.AndroidLogger;

/**
 * @author by ZIaDo on 6/17/17.
 */
public class AndroidSampleTestLifecycle extends DefaultTestLifecycle {

    @Override
    public Application createApplication(Method method, AndroidManifest appManifest, Config config) {
        TestApplication app = (TestApplication) super.createApplication(method, appManifest, config);

        RESTMockServerStarter.startSync(new AndroidLocalFileParser(app), new AndroidLogger());

        return app;
    }
}