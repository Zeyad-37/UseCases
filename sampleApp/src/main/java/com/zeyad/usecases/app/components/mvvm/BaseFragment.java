package com.zeyad.usecases.app.components.mvvm;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxFragment;
import com.zeyad.usecases.app.components.eventbus.IRxEventBus;
import com.zeyad.usecases.app.components.eventbus.RxEventBusFactory;
import com.zeyad.usecases.app.components.navigation.INavigator;
import com.zeyad.usecases.app.components.navigation.NavigatorFactory;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;

import rx.Subscription;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseFragment extends RxFragment {

    public INavigator navigator;
    public IRxEventBus rxEventBus;
    public IBaseViewModel viewModel;
    public boolean isNewActivity;

    public BaseFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        navigator = NavigatorFactory.getInstance();
        rxEventBus = RxEventBusFactory.getInstance();
        isNewActivity = savedInstanceState == null;
        if (!isNewActivity)
            restoreState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
        if (!isNewActivity)
            restoreState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null)
            outState.putAll(saveState());
        super.onSaveInstanceState(outState);
    }

    /**
     * To implement! Saves the state of the current view. Do not return null!
     *
     * @return {@link Bundle}
     */
    public abstract Bundle saveState();

    /**
     * To implement! Restores the state of the view.
     *
     * @param outState a {@link Bundle} with saved state
     */
    public abstract void restoreState(Bundle outState);

    /**
     * Initialize any objects or any required dependencies.
     */
    public abstract void initialize();

    public abstract Subscription loadData();

    public void showToastMessage(String message) {
        showToastMessage(message, Toast.LENGTH_LONG);
    }

    public void showToastMessage(String message, int duration) {
        Toast.makeText(getContext(), message, duration).show();
    }

    /**
     * Shows a {@link android.support.design.widget.Snackbar} message.
     *
     * @param message An string representing a message to be shown.
     */
    public void showSnackBarMessage(View view, String message, int duration) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                if (view != null)
                    SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_INFO, view, message, duration).show();
                else throw new NullPointerException("view is null");
            });
    }

    public void showSnackBarWithAction(@SnackBarFactory.SnackBarType String typeSnackBar, View view, String message,
                                       String actionText, View.OnClickListener onClickListener) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                if (view != null)
                    SnackBarFactory.getSnackBarWithAction(typeSnackBar, view, message, actionText, onClickListener);
                else throw new NullPointerException("View is null");
            });
    }

    public void showSnackBarWithAction(@SnackBarFactory.SnackBarType String typeSnackBar, View view, String message,
                                       int actionText, View.OnClickListener onClickListener) {
        showSnackBarWithAction(typeSnackBar, view, message, getString(actionText), onClickListener);
    }

    /**
     * Shows a {@link android.support.design.widget.Snackbar} error message.
     *
     * @param message  An string representing a message to be shown.
     * @param duration Visibility duration.
     */
    public void showErrorSnackBar(String message, View view, int duration) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                if (view != null)
                    SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_ERROR, view, message, duration);
                else throw new NullPointerException("View is null");
            });
    }

    public Context getApplicationContext() {
        return getContext().getApplicationContext();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (viewModel != null)
            viewModel.onViewAttached(this, isNewActivity);
        isNewActivity = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onDestroyView() {
        if (viewModel != null)
            viewModel.onViewDetached();
        super.onDestroyView();
    }
}
