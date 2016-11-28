package com.zeyad.generic.usecase.dataaccesslayer.components.mvp;

import android.support.annotation.NonNull;

import com.zeyad.generic.usecase.dataaccesslayer.components.ErrorMessageFactory;

import java.util.HashMap;

import rx.Subscriber;

/**
 * @author by zeyad on 23/05/16.
 */
public abstract class GenericPostPresenter<M> extends BasePresenter {

    GenericPostView<M> mGenericPostView;

    public GenericPostPresenter() {
    }

    public void setView(@NonNull GenericPostView<M> view) {
        mGenericPostView = view;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    public void showViewLoading() {
        mGenericPostView.showLoading();
    }

    public void hideViewLoading() {
        mGenericPostView.hideLoading();
    }

    public void showErrorMessage(Throwable throwable) {
        mGenericPostView.showError(ErrorMessageFactory.create((Exception) throwable));
    }

    /**
     * Call showViewLoading() then execute Post Call.
     *
     * @param postBundle data to be posted.
     */
    public abstract void post(HashMap<String, Object> postBundle);

    public void postSuccess(M model) {
        mGenericPostView.postSuccessful(model);
    }

    public GenericPostView<M> getGenericPostView() {
        return mGenericPostView;
    }

    public final class PostSubscriber extends Subscriber<M> {
        @Override
        public void onCompleted() {
            hideViewLoading();
        }

        @Override
        public void onError(Throwable e) {
            hideViewLoading();
            showErrorMessage(e);
            e.printStackTrace();
        }

        @Override
        public void onNext(M model) {
            postSuccess(model);
        }
    }
}