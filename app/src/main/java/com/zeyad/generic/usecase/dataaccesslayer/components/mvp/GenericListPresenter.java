package com.zeyad.generic.usecase.dataaccesslayer.components.mvp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.zeyad.generic.usecase.dataaccesslayer.components.ErrorMessageFactory;

import java.util.List;

import rx.Subscriber;

/**
 * @author by zeyad on 17/05/16.
 */
public abstract class GenericListPresenter<M, H extends RecyclerView.ViewHolder> extends BasePresenter {

    private GenericListView<M, H> mGenericListView;

    public GenericListPresenter() {
    }

    public void setView(@NonNull GenericListView<M, H> view) {
        mGenericListView = view;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    /**
     * Initializes the presenter by start retrieving the user list.
     */
    public void initialize() {
        loadItemList();
    }

    public void onItemClicked(M OrderHistoryViewModel, H holder) {
        mGenericListView.viewItemDetail(OrderHistoryViewModel, holder);
    }

    /**
     * Loads all users.
     */
    private void loadItemList() {
        hideViewRetry();
        showViewLoading();
        getItemList();
    }

    public void showViewLoading() {
        mGenericListView.showLoading();
    }

    public void hideViewLoading() {
        mGenericListView.hideLoading();
    }

    public void showViewRetry() {
        mGenericListView.showRetry();
    }

    public void hideViewRetry() {
        mGenericListView.hideRetry();
    }

    public void showErrorMessage(Throwable exception) {
        mGenericListView.showError(ErrorMessageFactory.create(mGenericListView.getApplicationContext(),
                (Exception) exception));
    }

    public void showItemsListInView(List<M> userViewModels) {
        mGenericListView.renderItemList(userViewModels);
    }

    public abstract void getItemList();

    public GenericListView<M, H> getGenericListView() {
        return mGenericListView;
    }

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
        public void onNext(List<M> mList) {
            showItemsListInView(mList);
        }
    }
}