package com.zeyad.usecases.integration;

import android.support.annotation.NonNull;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.TestLifecycle;

/**
 * @author by ZIaDo on 6/17/17.
 */
public class AndroidRobolectricRunner extends RobolectricTestRunner {

    public AndroidRobolectricRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @NonNull
    @Override
    public Class<? extends TestLifecycle> getTestLifecycleClass() {
        return AndroidSampleTestLifecycle.class;
    }
}

