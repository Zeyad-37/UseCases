package com.zeyad.generic.usecase.dataaccesslayer.components.mvp;

import android.support.annotation.NonNull;

import rx.Subscriber;

/**
 * @author by zeyad on 19/05/16.
 */
public abstract class GenericExtendedDetailPresenter<M> extends GenericDetailPresenter<M> {

    public GenericEditableItemView<M> mViewDetailsView;

    public GenericExtendedDetailPresenter() {
        super();
    }

    public void setView(@NonNull GenericEditableItemView<M> view) {
        mViewDetailsView = view;
    }

    private void showItemPutSuccess(@NonNull M model) {
        hideViewLoading();
//        mItemViewModel = model;
        mViewDetailsView.putItemSuccess(model);
    }

    public void setupEdit() {
//        mViewDetailsView.editItem(model);
    }

    public abstract void submitEdit();

    public final class PutSubscriber extends Subscriber<M> {
        @Override
        public void onCompleted() {
            hideViewLoading();
        }

        @Override
        public void onError(Throwable e) {
            hideViewLoading();
            showErrorMessage(e);
            showViewRetry();
            e.printStackTrace();
        }

        @Override
        public void onNext(M model) {
            showItemPutSuccess(model);
        }
    }
}