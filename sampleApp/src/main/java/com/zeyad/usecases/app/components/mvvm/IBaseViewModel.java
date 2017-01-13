package com.zeyad.usecases.app.components.mvvm;

import android.os.Bundle;

/**
 * @author zeyad on 11/28/16.
 */
interface IBaseViewModel<V> {
    Bundle getState();

    void restoreState(Bundle state);

    void onViewAttached(V view, boolean isNew);

    void onViewDetached();
}
