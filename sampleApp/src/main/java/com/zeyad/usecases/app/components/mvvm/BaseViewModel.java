package com.zeyad.usecases.app.components.mvvm;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

import static android.os.Build.VERSION_CODES.M;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseViewModel<V extends LoadDataView, M extends BaseModel> implements IBaseViewModel<V> {
    private V view;
    private int itemId;
    private boolean isNewView;
    private Context applicationContext;
    private CompositeSubscription compositeSubscription;

    public abstract M reduce(M previous, M changes);

    public abstract Observable.Transformer<?, M> applyStates();

    @Override
    public void onViewAttached(V view, boolean isNew) {
        this.view = view;
        isNewView = isNew;
        setApplicationContext();
        if (compositeSubscription == null || compositeSubscription.isUnsubscribed()) {
            compositeSubscription = new CompositeSubscription();
        }
    }

    @Override
    public void onViewDetached() {
        view = null;
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
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
        Context context = getContextFromView();
        if (context == null)
            context = applicationContext;
        return context;
    }

    private void setApplicationContext() {
        Context context = getContextFromView();
        if (applicationContext == null && context != null)
            applicationContext = context.getApplicationContext();
    }

    private Context getContextFromView() {
        Context context = null;
        if (view instanceof LoadDataView) {
            context = view.getViewContext();
        } else if (view instanceof Context) {
            context = (Context) view;
        } else if (view instanceof Fragment) {
            if (Build.VERSION.SDK_INT < M) {
                Activity activity = ((Fragment) view).getActivity();
                if (activity != null) {
                    context = activity;
                }
            } else context = ((Fragment) view).getContext();
        }
        return context;
    }
}
