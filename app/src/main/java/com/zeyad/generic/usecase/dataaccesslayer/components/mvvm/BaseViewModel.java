package com.zeyad.generic.usecase.dataaccesslayer.components.mvvm;

import rx.subscriptions.CompositeSubscription;

/**
 * @author zeyad on 11/28/16.
 */

public abstract class BaseViewModel<V> implements IBaseViewModel<V> {
    public V view;
    public boolean isNewView;
    private int itemId;
    private CompositeSubscription compositeSubscription;

    public BaseViewModel() {
        if (compositeSubscription == null || compositeSubscription.isUnsubscribed())
            compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onViewAttached(V view, boolean isNew) {
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
