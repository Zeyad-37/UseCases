package com.zeyad.generic.usecase.dataaccesslayer.components.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Class used to navigate through the application.
 */
class Navigator implements INavigator {

    private static INavigator mInstance;

    private Navigator() {
        // empty
    }

    protected static INavigator getInstance() {
        if (mInstance == null) {
            mInstance = new Navigator();
        }
        return mInstance;
    }

    @Override
    public void navigateTo(Context context, Intent intent) {
        context.startActivity(intent);
    }

    @Override
    public void startForResult(Activity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }
}