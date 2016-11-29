package com.zeyad.generic.usecase.dataaccesslayer.components.mvvm;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.zeyad.generic.usecase.dataaccesslayer.components.eventbus.IRxEventBus;
import com.zeyad.generic.usecase.dataaccesslayer.components.eventbus.RxEventBusFactory;
import com.zeyad.generic.usecase.dataaccesslayer.components.mvp.LoadDataView;
import com.zeyad.generic.usecase.dataaccesslayer.components.navigation.INavigator;
import com.zeyad.generic.usecase.dataaccesslayer.components.navigation.NavigatorFactory;
import com.zeyad.generic.usecase.dataaccesslayer.components.snackbar.SnackBarFactory;
import com.zeyad.generic.usecase.dataaccesslayer.utils.Utils;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author zeyad on 11/28/16.
 */

public abstract class BaseFragment extends Fragment implements LoadDataView {

    public INavigator navigator;
    public IRxEventBus rxEventBus;
    public CompositeSubscription compositeSubscription;
    boolean isNewActivity;
    IBaseViewModel viewModel;

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        navigator = NavigatorFactory.getInstance();
        rxEventBus = RxEventBusFactory.getInstance();
        isNewActivity = savedInstanceState == null;
        if (!isNewActivity && viewModel != null)
            viewModel.restoreState(savedInstanceState);
        compositeSubscription = Utils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
        if (savedInstanceState != null)
            viewModel.restoreState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null && viewModel != null) {
            outState.putAll(viewModel.getState());
        }
        super.onSaveInstanceState(outState);
    }

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
        compositeSubscription.add(loadData());
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.unsubscribeIfNotNull(compositeSubscription);
    }

    @Override
    public void onDestroyView() {
        Utils.unsubscribeIfNotNull(compositeSubscription);
        if (viewModel != null)
            viewModel.onViewDetached();
        super.onDestroyView();
    }
}
