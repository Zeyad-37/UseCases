package com.zeyad.usecases.app;

import android.app.Application;
import android.content.Context;

import io.appflate.restmock.android.RESTMockTestRunner;

/**
 * @author by ZIaDo on 6/15/17.
 */
public class UseCasesTestRunner extends RESTMockTestRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, TestGenericApplication.class.getName(), context);
    }
}
