package com.zeyad.usecases.app.components.mvvm;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import rx.subscriptions.CompositeSubscription;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseViewModel<V> implements IBaseViewModel<V> {
    private V view;
    private int itemId;
    private boolean isNewView;
    private CompositeSubscription compositeSubscription;

    @Override
    public void onViewAttached(V view, boolean isNew) {
        this.view = view;
        isNewView = isNew;
    }

    @Override
    public void onViewDetached() {
        view = null;
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }
    }

    @Override
    public Bundle getState() {
        return new Bundle(0);
    }

    @Override
    public void restoreState(Bundle state) {
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public LoadDataView getLoadDataView() {
        return (LoadDataView) view;
    }

    public V getView() {
        return view;
    }

    public void setView(V view) {
        this.view = view;
    }

    public boolean isNewView() {
        return isNewView;
    }

    public void setNewView(boolean newView) {
        isNewView = newView;
    }

    /**
     * Returns context from view or null if view is null.
     *
     * @return {@link Context}.
     */
    public Context getContext() {
        Context context = null;
        if (view instanceof LoadDataView) {
            context = ((LoadDataView) view).getViewContext();
        } else if (view instanceof Context) {
            context = (Context) view;
        } else if (view instanceof Fragment) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Activity activity = ((Fragment) view).getActivity();
                if (activity != null) {
                    context = activity;
                }
            } else context = ((Fragment) view).getContext();
        }
        return context;
    }

    /**
     * Returns current compositeSubscription or creates a new one, if null or un-subscribed.
     * @return {@link CompositeSubscription}.
     */
    public CompositeSubscription getCompositeSubscription() {
        if (compositeSubscription == null || compositeSubscription.isUnsubscribed()) {
            compositeSubscription = new CompositeSubscription();
        }
        return compositeSubscription;
    }
}
