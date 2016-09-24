package com.zeyad.generic.usecase.dataaccesslayer.components.mvp;

/**
 * @author by zeyad on 23/05/16.
 */
public interface GenericPostView<M> extends LoadDataView {

    void postSuccessful(M model);
}