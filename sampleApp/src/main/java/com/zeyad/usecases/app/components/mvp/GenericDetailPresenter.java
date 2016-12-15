package com.zeyad.usecases.app.components.mvp;

import android.support.annotation.NonNull;

import com.zeyad.usecases.app.components.exceptions.ErrorMessageFactory;

import rx.Subscriber;

/**
 * @author by zeyad on 17/05/16.
 */
public abstract class GenericDetailPresenter<M> extends BasePresenter {

    /**
     * id used to retrieve item details
     */
    public int mItemId;
    private GenericDetailView<M> mGenericDetailView;

    public GenericDetailPresenter() {
    }

    public void setView(@NonNull GenericDetailView<M> view) {
        mGenericDetailView = view;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    /**
     * Initializes the presenter by start retrieving item details.
     */
    public void initialize(int itemId) {
        mItemId = itemId;
        loadItemDetails();
    }

    /**
     * Loads item details.
     */
    private void loadItemDetails() {
        showViewLoading();
        getItemDetails();
    }

    public void showViewLoading() {
        mGenericDetailView.showLoading();
    }

    public void hideViewLoading() {
        mGenericDetailView.hideLoading();
    }

    public void showViewRetry() {
        mGenericDetailView.showErrorWithRetry("");
    }

    public void showErrorMessage(Throwable throwable) {
        mGenericDetailView.showError(ErrorMessageFactory.create((Exception) throwable));
    }

    void showUserDetailsInView(M m) {
        mGenericDetailView.renderItem(m);
    }

    public abstract void getItemDetails();

    public int getItemId() {
        return mItemId;
    }

    public void setItemId(int mItemId) {
        this.mItemId = mItemId;
    }

    public GenericDetailView<M> getGenericDetailView() {
        return mGenericDetailView;
    }

    public void setViewDetailsView(GenericDetailView<M> mViewDetailsView) {
        this.mGenericDetailView = mViewDetailsView;
    }

    public final class ItemDetailSubscriber extends Subscriber<M> {
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
        public void onNext(M m) {
            showUserDetailsInView(m);
        }
    }
}