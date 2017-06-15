package com.zeyad.usecases.app;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;

import com.jakewharton.espresso.OkHttp3IdlingResource;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author by ZIaDo on 6/15/17.
 */
public class OkHttpIdlingResourceRule implements TestRule {
    @Override
    public Statement apply(final Statement base, Description description) {
        TestGenericApplication app = (TestGenericApplication)
                InstrumentationRegistry.getTargetContext().getApplicationContext();
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                IdlingResource idlingResource = OkHttp3IdlingResource.create(
                        "okhttp", app.getOkHttpBuilder().build());
                Espresso.registerIdlingResources(idlingResource);
                base.evaluate();
                Espresso.unregisterIdlingResources(idlingResource);
            }
        };
    }
}