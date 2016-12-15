package com.zeyad.usecases.app.components.mvp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import rx.Subscriber;

/**
 * @author by zeyad on 19/05/16.
 */
public abstract class GenericListExtendedPresenter<M, H extends RecyclerView.ViewHolder> extends GenericListPresenter<M, H> {

    private List<M> mItemViewModels;

    public GenericListExtendedPresenter() {
        super();
    }

    public abstract void search(String query);

    public abstract void deleteCollection(List<Long> ids);

    public List<M> getItemsViewModels() {
        return mItemViewModels;
    }

    // TODO: 10/05/16 combine Search and List subscribers!
    public final class ItemListSubscriber extends Subscriber<List<M>> {
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
        public void onNext(List<M> models) {
            mItemViewModels = models;
            showItemsListInView(models);
        }
    }

    public final class SearchSubscriber extends Subscriber<List<M>> {
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
        public void onNext(List<M> response) {
            showItemsListInView(response);
        }
    }

    public final class DeleteSubscriber extends Subscriber<Boolean> {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            hideViewLoading();
            showErrorMessage(e);
            showViewRetry();
            e.printStackTrace();
        }

        // TODO: 4/17/16 Apply adapter method!
        @Override
        public void onNext(Boolean success) {
            if (success) {
                getItemList();
            } else Log.d("OnDelete", "Fail!");
        }
    }
}