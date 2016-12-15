package com.zeyad.usecases.app.components.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.zeyad.usecases.app.components.eventbus.IRxEventBus;
import com.zeyad.usecases.app.components.navigation.INavigator;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;
import com.zeyad.usecases.app.utils.Utils;

import rx.subscriptions.CompositeSubscription;

/**
 * Base {@link Fragment} class for every fragment in this application.
 */
public abstract class BaseFragment extends Fragment {

    public INavigator navigator;
    public IRxEventBus rxEventBus;
    public CompositeSubscription mCompositeSubscription;

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mCompositeSubscription = Utils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    public abstract void initialize();

    /**
     * Shows a {@link Toast} message.
     *
     * @param message An string representing a message to be shown.
     */
    protected void showSnackBarMessage(@SnackBarFactory.SnackBarType String typeSnackBar, View view,
                                       String message, int duration) {
        SnackBarFactory.getSnackBar(typeSnackBar, view, message, duration)
                .show();
    }

    protected void showSnackBarMessage(@SnackBarFactory.SnackBarType String typeSnackBar, View view,
                                       int messageId, int duration) {
        SnackBarFactory.getSnackBar(typeSnackBar, view, getString(messageId), duration)
                .show();
    }

    public Context getApplicationContext() {
        return getContext().getApplicationContext();
    }

    @Override
    public void onDestroyView() {
        Utils.unsubscribeIfNotNull(mCompositeSubscription);
        super.onDestroyView();
    }
}