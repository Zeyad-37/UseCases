package com.zeyad.generic.usecase.dataaccesslayer.components.mvvm;

import android.os.Bundle;

import com.zeyad.generic.usecase.dataaccesslayer.components.mvp.LoadDataView;

/**
 * @author zeyad on 11/28/16.
 */
public interface IBaseViewModel {
    Bundle getState();

    void restoreState(Bundle state);

    <V extends LoadDataView> void onViewAttached(V view, boolean isNew);

    void onViewDetached();
}
