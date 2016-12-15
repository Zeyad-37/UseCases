package com.zeyad.usecases.app.components.mvp;

import com.zeyad.usecases.app.components.mvvm.LoadDataView;

/**
 * @author by zeyad on 23/05/16.
 */
public interface GenericPostView<M> extends LoadDataView {

    void postSuccessful(M model);
}