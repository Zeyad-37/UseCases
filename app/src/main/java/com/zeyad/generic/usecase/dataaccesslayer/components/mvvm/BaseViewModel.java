package com.zeyad.generic.usecase.dataaccesslayer.components.mvvm;

import com.zeyad.generic.usecase.dataaccesslayer.components.mvp.LoadDataView;

import rx.subscriptions.CompositeSubscription;

/**
 * @author zeyad on 11/28/16.
 */

public abstract class BaseViewModel implements IBaseViewModel {
    public LoadDataView view;
    public boolean isNewView;
    private int itemId;
    private CompositeSubscription compositeSubscription;

    public BaseViewModel() {
        if (compositeSubscription == null || compositeSubscription.isUnsubscribed())
            compositeSubscription = new CompositeSubscription();
    }

    @Override
    public <V extends LoadDataView> void onViewAttached(V view, boolean isNew) {
        this.view = view;
        isNewView = isNew;
    }

    @Override
    public void onViewDetached() {
        if (compositeSubscription != null)
            compositeSubscription.unsubscribe();
    }

    @Override
    public int getItemId() {
        return itemId;
    }

    @Override
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
