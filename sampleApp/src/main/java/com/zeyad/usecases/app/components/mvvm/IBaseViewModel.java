package com.zeyad.usecases.app.components.mvvm;

/**
 * @author zeyad on 11/28/16.
 */
interface IBaseViewModel<V> {
    void onViewAttached(V view, boolean isNew);

    void onViewDetached();
}
