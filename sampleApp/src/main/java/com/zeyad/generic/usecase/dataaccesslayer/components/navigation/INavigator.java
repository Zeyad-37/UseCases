package com.zeyad.generic.usecase.dataaccesslayer.components.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public interface INavigator {
    void navigateTo(Context context, Intent intent);

    void startForResult(Activity activity, Intent intent, int requestCode);
}
